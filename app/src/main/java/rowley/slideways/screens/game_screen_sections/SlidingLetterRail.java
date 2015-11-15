package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import jrowley.gamecontrollib.game_control.BaseGameControllerActivity;
import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.pooling.ObjectPool;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.SlideWaysApp;
import rowley.slideways.activity.GameActivity;
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

    private final int TILE_COUNT = 15;

    private MovingLetterTileAttributes tileAttrs;

    private final int firstLetterLeftMax;
    private final int lastLetterLeftMin;

    private int lastX;
    private int lastY;
    private RailState railState = RailState.RESTING;
    private float flingVelocity;
    private final float FLING_FRICTION = 0.95f;
    private float timeSinceLastTouchEvent;
    private final float LONG_TOUCH_THRESHOLD = 0.4f;
    private int selectedLetterIndex;
    private final int LETTER_PICKUP_OFFSET_BASE = 10;
    private final int letterPickupOffset;

    private LetterReceiver pickedUpLetterReceiver;

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
        int currentX = firstLetterLeftMax;
        for(int i = 0; i < letterTiles.length; i++) {
            LetterTile tile = getNewLetterTile(currentX);
            letterTiles[i] = tile;
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
                } else if(railState == RailState.LETTER_SELECTED) {
                    //todo
                } else if(touchIsInsideRail(event)) {
                    railState = RailState.TOUCH_INITIATED;

                    lastX = event.getX();
                    lastY = event.getY();

                    timeSinceLastTouchEvent = 0;
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
                } else if(railState != RailState.LETTER_SELECTED) {
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

            for(; letterIndex < letterTiles.length - 1; letterIndex++) {
                letterTiles[letterIndex] = letterTiles[letterIndex + 1];
            }
            letterTiles[letterIndex] = getNewLetterTile(letterTiles[letterIndex - 1].getLeft() + tileAttrs.getTileDimension() + Assets.padding);
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
            gameController.getGraphics().drawRect(tile.getLeft(), tile.getTop(), tileAttrs.getTileDimension(),
                    tileAttrs.getTileDimension(), tileAttrs.getTileBackgroundColor());
            gameController.getGraphics().writeText(String.valueOf(tile.getLetterDisplay()), tile.getLeft() + (tileAttrs.getTileDimension() / 2),
                    tile.getTop() + tileAttrs.getLetterBaselineFromTileTopOffset(), tileAttrs.getLetterTextColor(),
                    tileAttrs.getLetterTextSize(), tileAttrs.getLetterTypeface(), tileAttrs.getLetterAlignment());
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

    private LetterTile getNewLetterTile(int xPosition) {
        LetterTile tile = tilePool.newObject();
        tile.setLeft(xPosition);
        tile.setTop(sectionTop + Assets.padding);
        tile.setLetter(Assets.letterManager.getNextLetter());

        return tile;
    }

    public void setPickedUpLetterReceiver(LetterReceiver receiver) {
        this.pickedUpLetterReceiver = receiver;
    }

    @Override
    public void monitorDetachedTilePosition(LetterTile tile) {
        //// TODO: 11/13/15 Determine if we need to nudge any of the other tiles 
    }
    
    @Override
    public void onDetachedTileAcceptedByOther() {
        // TODO: 11/14/15  
    }

    @Override
    public boolean tryReceiveControlOfLetter(LetterTile letter, int lastTouchX, int lastTouchY) {
        for(int i = letterTiles.length - 1; i > selectedLetterIndex; i--) {
            letterTiles[i] = letterTiles[i - 1];
        }
        letterTiles[selectedLetterIndex] = letter;
        railState = RailState.RESTING;
        // TODO: 11/13/15  
        return false;
    }

    private enum RailState {
        RESTING, TOUCH_INITIATED, SLIDING, FLUNG, LETTER_SELECTED
    }
}
