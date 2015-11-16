package rowley.slideways.screens.game_screen_sections;

import java.util.Arrays;
import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.pooling.ObjectPool;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.screens.DetachedTileMonitor;
import rowley.slideways.screens.LetterReceiver;
import rowley.slideways.util.Assets;
import rowley.slideways.util.MovingLetterTileAttributes;

/**
 * Created by jrowley on 11/5/15.
 */
public class SlidingLetterRail extends ScreenSectionController implements DetachedTileMonitor, LetterReceiver {
    private ObjectPool<LetterTile> tilePool;
    private LetterTile[] letterTiles;
    private LetterTile[] onDeckTiles;

    private final int TILE_COUNT = 15;

    private MovingLetterTileAttributes tileAttrs;

    private final int firstLetterLeftMax;
    private final int lastLetterLeftMin;

    private int lastX;
    private int lastY;
    private RailState railState = RailState.RESTING;
    private RailState targetRailStateAfterAdjustment = RailState.RESTING;
    private float flingVelocity;
    private final float FLING_FRICTION = 0.95f;
    private float timeSinceLastTouchEvent;
    private final float LONG_TOUCH_THRESHOLD = 0.4f;
    private int selectedLetterIndex = -1;
    private final int LETTER_PICKUP_OFFSET_BASE = 10;
    private final int letterPickupOffset;

    private LetterReceiver pickedUpLetterReceiver;

    private boolean[] tilesToAdjust = new boolean[TILE_COUNT];

    public SlidingLetterRail(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        tileAttrs = MovingLetterTileAttributes.getInstance(gameController);

        firstLetterLeftMax = sectionLeft + Assets.padding;
        lastLetterLeftMin = sectionLeft + sectionWidth - Assets.padding - tileAttrs.getTileDimension();

        tilePool = new ObjectPool<>(new ObjectPool.PoolObjectFactory<LetterTile>() {
            @Override
            public LetterTile createObject() {
                LetterTile tile = new LetterTile();
                tile.setHeight(tileAttrs.getTileDimension());
                tile.setWidth(tileAttrs.getTileDimension());

                return tile;
            }
        }, TILE_COUNT * 2);

        letterTiles = new LetterTile[TILE_COUNT];
        onDeckTiles = new LetterTile[TILE_COUNT];
        for(int i = 0; i < onDeckTiles.length; i++) {
            onDeckTiles[i] = getNewTileForOnDeck();
        }
        int currentX = firstLetterLeftMax;
        for(int i = 0; i < letterTiles.length; i++) {
            letterTiles[i] = getTileFromOnDeck(currentX);
            currentX += (tileAttrs.getTileDimension() + Assets.padding);
        }

        letterPickupOffset = (int) (LETTER_PICKUP_OFFSET_BASE * gameController.getGraphics().getScale());
    }

    @Override
    public void update(float portionOfSecond, List<TouchEvent> touchEvents) {
        if(railState == RailState.FLUNG) {
            int offset = (int) (flingVelocity * portionOfSecond);
            offset = adjustCalculatedOffsetToStayWithinBounds(offset);
            updateTilesWithOffset(offset);
            flingVelocity = flingVelocity * FLING_FRICTION;

            if(flingVelocity < 5 && flingVelocity > -5) {
                railState = RailState.RESTING;
            }
        }

        timeSinceLastTouchEvent += portionOfSecond;

        if(timeSinceLastTouchEvent > LONG_TOUCH_THRESHOLD && (railState == RailState.TOUCH_INITIATED || railState == RailState.SLIDING)) {
            tryToPickUpLetter();
        }

        if(railState == RailState.ADJUSTING) {
            boolean doneAdjusting = true;
            for(int i = 0; i < tilesToAdjust.length; i++) {
                if(tilesToAdjust[i] && !letterTiles[i].progressTowardDesiredPosition(portionOfSecond)) {
                    doneAdjusting = false;
                }
            }

            if(doneAdjusting) {
                Arrays.fill(tilesToAdjust, false);
                railState = targetRailStateAfterAdjustment;
            }
        }

        int lastTouchOffset = 0;
        for(TouchEvent event : touchEvents) {
            if(event.getType() == TouchEvent.TOUCH_DOWN && railState == RailState.RESTING && touchIsInsideRail(event)) {
                railState = RailState.TOUCH_INITIATED;

                lastX = event.getX();
                lastY = event.getY();

                timeSinceLastTouchEvent = 0;
            }

            if(event.getType() == TouchEvent.TOUCH_DRAGGED) {
                if(railState == RailState.TOUCH_INITIATED || railState == RailState.SLIDING) {
                    if (railState != RailState.SLIDING) {
                        railState = RailState.SLIDING;
                    }

                    lastTouchOffset = getRailSlideOffset(event);
                    updateTilesWithOffset(lastTouchOffset);
                }

                //some devices register drags even if there is no change. Others don't register a drag without change
                if(lastTouchOffset != 0) {
                    timeSinceLastTouchEvent = 0;
                }
            }

            if(event.getType() == TouchEvent.TOUCH_UP) {
                if(railState == RailState.SLIDING) {
                    //Generally an up is accompanied by a drag first. But what it if isn't?
                    if (lastTouchOffset == 0) {
                        lastTouchOffset = getRailSlideOffset(event);
                    }
                    if (lastTouchOffset != 0) {
                        railState = RailState.FLUNG;
                        updateTilesWithOffset(lastTouchOffset);
                        flingVelocity = lastTouchOffset / portionOfSecond;
                    } else {
                        railState = RailState.RESTING;
                    }
                } else if(railState != RailState.LETTER_SELECTED && railState != RailState.ADJUSTING) {
                    railState = RailState.RESTING;
                }

                timeSinceLastTouchEvent = 0;
            }
        }
    }

    private void tryToPickUpLetter() {
        for(int i = 0; i < letterTiles.length; i++) {
            LetterTile tile = letterTiles[i];
            if(lastX > tile.getLeft() && lastX < tile.getLeft() + tileAttrs.getTileDimension() - 1
                    && lastY > tile.getTop() && lastY < tile.getTop() + tileAttrs.getTileDimension() - 1) {
                pickUpLetter(i);
                break;
            }
        }
    }

    private void pickUpLetter(int letterIndex) {
        railState = RailState.LETTER_SELECTED;
        if(pickedUpLetterReceiver != null && pickedUpLetterReceiver.tryReceiveControlOfLetter(letterTiles[letterIndex], lastX, lastY)) {
            letterTiles[letterIndex].detachFromStablePosition(letterTiles[letterIndex].getLeft(), letterTiles[letterIndex].getTop() - letterPickupOffset);
            selectedLetterIndex = letterIndex;

            letterTiles[selectedLetterIndex] = null;
        }
    }

    private boolean touchIsInsideRail(TouchEvent event) {
        return event.getX() > sectionLeft
                && event.getX() < sectionLeft + sectionWidth
                && event.getY() > sectionTop + Assets.padding
                && event.getY() < sectionTop + sectionHeight - Assets.padding;
    }

    private int getRailSlideOffset(TouchEvent event) {
        int offset = event.getX() - lastX;

        lastX = event.getX();
        lastY = event.getY();

        offset = adjustCalculatedOffsetToStayWithinBounds(offset);

        return offset;
    }

    private int adjustCalculatedOffsetToStayWithinBounds(int offset) {
        if(offset > 0 && (letterTiles[0].getLeft() + offset) > firstLetterLeftMax) {
            offset = firstLetterLeftMax - letterTiles[0].getLeft();
        }
        if(offset < 0 && (letterTiles[TILE_COUNT - 1].getLeft() + offset) < lastLetterLeftMin) {
            offset = lastLetterLeftMin - letterTiles[TILE_COUNT - 1].getLeft();
        }

        return offset;
    }

    private void updateTilesWithOffset(int offset) {
        for(LetterTile tile : letterTiles) {
            tile.setLeft(tile.getLeft() + offset);
        }
    }

    @Override
    public void present(float v) {
        for(LetterTile tile : letterTiles) {
            if(tile != null) {
                gameController.getGraphics().drawRect(tile.getLeft(), tile.getTop(), tileAttrs.getTileDimension(),
                        tileAttrs.getTileDimension(), tileAttrs.getTileBackgroundColor());
                gameController.getGraphics().writeText(String.valueOf(tile.getLetterDisplay()), tile.getLeft() + (tileAttrs.getTileDimension() / 2),
                        tile.getTop() + tileAttrs.getLetterBaselineFromTileTopOffset(), tileAttrs.getLetterTextColor(),
                        tileAttrs.getLetterTextSize(), tileAttrs.getLetterTypeface(), tileAttrs.getLetterAlignment());
            }
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    private LetterTile getNewTileForOnDeck() {
        LetterTile tile = tilePool.newObject();
        tile.setLetter(Assets.letterManager.getNextLetter());

        return tile;
    }

    private LetterTile getTileFromOnDeck(int xPosition) {
        LetterTile tile = onDeckTiles[0];
        tile.setLeft(xPosition);
        tile.setTop(sectionTop + Assets.padding);

        for(int i = 0; i < onDeckTiles.length - 1; i++) {
            onDeckTiles[i] = onDeckTiles[i + 1];
        }
        onDeckTiles[onDeckTiles.length - 1] = getNewTileForOnDeck();

        return tile;
    }

    private void pushTileBackIntoOnDeck(LetterTile tile) {
        for(int i = onDeckTiles.length - 1; i > 0; i--) {
            onDeckTiles[i] = onDeckTiles[i - 1];
        }
        onDeckTiles[0] = tile;
    }

    public void setPickedUpLetterReceiver(LetterReceiver receiver) {
        this.pickedUpLetterReceiver = receiver;
    }

    @Override
    public void monitorDetachedTilePosition(LetterTile tile) {
        //Do we need to do anything in response -- is it even in our bounds:
        if(tile.getTop() < sectionTop + sectionHeight - Assets.padding
                && tile.getTop() + tile.getHeight() > sectionTop + Assets.padding) {
            if(railState != RailState.ADJUSTING) {
                int targetIndex = findTargetIndexForMovingTile(tile);

                if(railState == RailState.LETTER_SELECTED) {
                    //Reset the selected state to match the new state if needed
                    if(targetIndex < selectedLetterIndex) {
                        requestTilesShiftRight(targetIndex, selectedLetterIndex);
                        tile.setLastStablePosition(letterTiles[targetIndex + 1].getDesiredLeft() - tile.getWidth() - Assets.padding,
                                letterTiles[targetIndex + 1].getDesiredTop());
                        selectedLetterIndex = targetIndex;

                        targetRailStateAfterAdjustment = railState;
                        railState = RailState.ADJUSTING;
                    } else if(targetIndex > selectedLetterIndex) {
                        requestTilesShiftLeft(selectedLetterIndex, targetIndex);
                        tile.setLastStablePosition(letterTiles[targetIndex - 1].getDesiredLeft() + tile.getWidth() + Assets.padding,
                                letterTiles[targetIndex - 1].getDesiredTop());
                        selectedLetterIndex = targetIndex;

                        targetRailStateAfterAdjustment = railState;
                        railState = RailState.ADJUSTING;
                    }
                    //If target matches selected, then do nothing
                } else {
                    pushTileBackIntoOnDeck(letterTiles[letterTiles.length - 1]);
                    requestTilesShiftRight(targetIndex, letterTiles.length - 1);
                    selectedLetterIndex = targetIndex;

                    targetRailStateAfterAdjustment = railState;
                    railState = RailState.ADJUSTING;
                }
            }

        } else if(railState == RailState.RESTING && selectedLetterIndex != -1) {
            requestTilesShiftLeft(selectedLetterIndex, letterTiles.length - 1);
            letterTiles[letterTiles.length - 1] = getTileFromOnDeck(letterTiles[letterTiles.length - 2].getLeft()
                    + tileAttrs.getTileDimension() + Assets.padding);
            letterTiles[selectedLetterIndex].setDesiredPosition(letterTiles[letterTiles.length - 2].getLeft(), letterTiles[selectedLetterIndex - 1].getTop());
            tilesToAdjust[letterTiles.length - 1] = true;

            selectedLetterIndex = -1;

            targetRailStateAfterAdjustment = railState;
            railState = RailState.ADJUSTING;
        }
    }
    
    @Override
    public void onDetachedTileAcceptedByOther() {
        if(selectedLetterIndex > -1) {
            for (; selectedLetterIndex < letterTiles.length - 1; selectedLetterIndex++) {
                letterTiles[selectedLetterIndex] = letterTiles[selectedLetterIndex + 1];
                letterTiles[selectedLetterIndex].setDesiredPosition(letterTiles[selectedLetterIndex].getLeft() - letterTiles[selectedLetterIndex].getWidth() - Assets.padding, letterTiles[selectedLetterIndex].getTop());
                tilesToAdjust[selectedLetterIndex] = true;
            }
            letterTiles[selectedLetterIndex] = getTileFromOnDeck(letterTiles[selectedLetterIndex - 1].getLeft()
                    + tileAttrs.getTileDimension() + Assets.padding);
            letterTiles[selectedLetterIndex].setDesiredPosition(letterTiles[selectedLetterIndex - 1].getLeft(), letterTiles[selectedLetterIndex - 1].getTop());
            tilesToAdjust[selectedLetterIndex] = true;

            selectedLetterIndex = -1;

            railState = RailState.ADJUSTING;
            targetRailStateAfterAdjustment = RailState.RESTING;
        }
    }

    @Override
    public boolean tryReceiveControlOfLetter(LetterTile letter, int lastTouchX, int lastTouchY) {
        //first, can we consider accepting it?
        if(letter.getTop() < sectionTop + sectionHeight - Assets.padding
                && letter.getTop() + letter.getHeight() > sectionTop + Assets.padding) {
            //We're inside the vertical bounds of our letters, let's figure out where it needs to go
            int targetIndex = findTargetIndexForMovingTile(letter);

            if(targetIndex < selectedLetterIndex) {
                requestTilesShiftRight(targetIndex, selectedLetterIndex);
                letter.setDesiredPosition(letterTiles[targetIndex + 1].getDesiredLeft() - letter.getWidth() - Assets.padding,
                        letterTiles[targetIndex + 1].getDesiredTop());
            } else if(targetIndex > selectedLetterIndex) {
                requestTilesShiftLeft(selectedLetterIndex, targetIndex);
                letter.setDesiredPosition(letterTiles[targetIndex - 1].getDesiredLeft() + letter.getWidth() + Assets.padding,
                        letterTiles[targetIndex - 1].getDesiredTop());
            } else {
                letter.setDesiredPosition(letter.getLastStableLeft(), letter.getLastStableTop());
            }

            letterTiles[targetIndex] = letter;
            tilesToAdjust[targetIndex] = true;
            railState = RailState.ADJUSTING;
            targetRailStateAfterAdjustment = RailState.RESTING;
            selectedLetterIndex = -1;

            return true;
        }
        return false;
    }

    /**
     * Shift tiles right (using a desired position and animated move). If a tile currently lives in the toIndex
     * It will be overwritten. When the operation is complete there will be a gap at fromIndex (it was moved one right)
     * @param fromIndex - inclusive
     * @param toIndex - inclusive
     */
    private void requestTilesShiftRight(int fromIndex, int toIndex) {
        for(int i = toIndex; i > fromIndex; i--) {
            letterTiles[i] = letterTiles[i - 1];
            letterTiles[i].setDesiredPosition(letterTiles[i].getLeft() + letterTiles[i].getWidth() + Assets.padding, letterTiles[i].getTop());
            letterTiles[i - 1] = null;
            tilesToAdjust[i] = true;
        }
    }

    /**
     * Shift tiles left (using a desired position and animated move). If a tile currently lives in the toIndex
     * It will be overwritten. When the operation is complete there will be a gap at fromIndex (it was moved one left)
     * @param toIndex - Inclusive
     * @param fromIndex - Inclusive
     */
    private void requestTilesShiftLeft(int toIndex, int fromIndex) {
        for(int i = toIndex; i < fromIndex; i++) {
            letterTiles[i] = letterTiles[i + 1];
            letterTiles[i].setDesiredPosition(letterTiles[i].getLeft() - letterTiles[i].getWidth() - Assets.padding, letterTiles[i].getTop());
            letterTiles[i + 1] = null;
            tilesToAdjust[i] = true;
        }
    }

    private int findTargetIndexForMovingTile(LetterTile movingTile) {
        int targetIndex = selectedLetterIndex;
        for(int i = 0; i < letterTiles.length - 1; i++) {
            if(i == selectedLetterIndex) {
                continue;
            }

            if((movingTile.getLeft() > letterTiles[i].getLeft()
                    && movingTile.getLeft() < letterTiles[i].getLeft() + (letterTiles[i].getWidth() / 2))
                    || (movingTile.getLeft() + movingTile.getWidth() < letterTiles[i].getLeft() + letterTiles[i].getWidth()
                    && movingTile.getLeft() + movingTile.getWidth() > letterTiles[i].getLeft() + (letterTiles[i].getWidth() / 2))) {
                targetIndex = i;
                break;
            }
        }

        return targetIndex;
    }

    private enum RailState {
        RESTING, TOUCH_INITIATED, SLIDING, FLUNG, LETTER_SELECTED, ADJUSTING
    }
}
