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
public class SupplyLetterRail extends SlidingLetterRailBase {
    private ObjectPool<LetterTile> tilePool;
    private LetterTile[] onDeckTiles;

    private final int TILE_COUNT = 15;

    private boolean[] tilesToAdjust = new boolean[TILE_COUNT];

    public SupplyLetterRail(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        tilePool = new ObjectPool<>(new ObjectPool.PoolObjectFactory<LetterTile>() {
            @Override
            public LetterTile createObject() {
                LetterTile tile = new LetterTile();
                tile.setHeight(tileAttrs.getTileDimension());
                tile.setWidth(tileAttrs.getTileDimension());

                return tile;
            }
        }, TILE_COUNT * 2);

        onDeckTiles = new LetterTile[TILE_COUNT];
        for(int i = 0; i < onDeckTiles.length; i++) {
            onDeckTiles[i] = getNewTileForOnDeck();
        }

        int currentX = firstLetterLeftMax;
        for(int i = 0; i < letterTiles.length; i++) {
            letterTiles[i] = getTileFromOnDeck(currentX);
            currentX += (tileAttrs.getTileDimension() + Assets.padding);
        }
    }

    @Override
    public void update(float portionOfSecond, List<TouchEvent> touchEvents) {
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

        super.update(portionOfSecond, touchEvents);
    }

    @Override
    public void present(float portionOfASecond) {
        presentTiles();
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

    @Override
    protected int getTileCount() {
        return TILE_COUNT;
    }

    @Override
    protected int getLeftmostObjectLeftEdge() {
        return letterTiles[0].getLeft();
    }

    @Override
    protected int getRightmostObjectLeftEdge() {
        return letterTiles[getTileCount() - 1].getLeft();
    }
}
