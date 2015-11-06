package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.pooling.ObjectPool;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.util.Assets;

/**
 * Created by jrowley on 11/5/15.
 */
public class SlidingLetterRail extends ScreenSectionController {
    private ObjectPool<LetterTile> tilePool;
    private LetterTile[] letterTiles;

    private final int TILE_COUNT = 12;
    private final int tileDimension;
    private final int TILE_BACKGROUND_COLOR = 0x55ffffff;
    private final int LETTER_TEXT_COLOR = Color.BLACK;
    private final float letterTextSize;
    private final float LETTER_TEXT_SIZE_RATIO = 0.8f;
    private final Typeface LETTER_TYPEFACE = Typeface.DEFAULT_BOLD;
    private final Paint.Align LETTER_ALIGNMENT = Paint.Align.CENTER;
    private final int letterBaselineFromTileTopOffset;

    private final int PADDING_BASE = 20;
    private final int padding;

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

    public SlidingLetterRail(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        padding = (int) (PADDING_BASE * gameController.getGraphics().getScale());
        tileDimension = sectionHeight - (padding * 2);
        letterTextSize = tileDimension * LETTER_TEXT_SIZE_RATIO;
        Rect bounds = new Rect();
        gameController.getGraphics().getTextBounds("A", letterTextSize, LETTER_TYPEFACE, LETTER_ALIGNMENT, bounds);
        letterBaselineFromTileTopOffset = (tileDimension / 2) + (bounds.height() / 2);

        firstLetterLeftMax = sectionLeft + padding;
        lastLetterLeftMin = sectionLeft + sectionWidth - padding - tileDimension;

        tilePool = new ObjectPool<>(new ObjectPool.PoolObjectFactory<LetterTile>() {
            @Override
            public LetterTile createObject() {
                LetterTile tile = new LetterTile();
                tile.setHeight(tileDimension);
                tile.setWidth(tileDimension);

                return tile;
            }
        }, TILE_COUNT * 2);

        letterTiles = new LetterTile[TILE_COUNT];
        int currentX = firstLetterLeftMax;
        for(int i = 0; i < letterTiles.length; i++) {
            LetterTile tile = tilePool.newObject();
            tile.setLeft(currentX);
            tile.setTop(sectionTop + padding);
            tile.setLetter(Assets.letterManager.getNextLetter());

            letterTiles[i] = tile;

            currentX += (tileDimension + padding);
        }
    }

    @Override
    public void update(float portionOfSecond) {
        if(railState == RailState.FLUNG) {
            int offset = (int) (flingVelocity * portionOfSecond);
            offset = adjustCalculatedOffsetToStayWithinBounds(offset);
            updateTilesWithOffset(offset);
            flingVelocity = flingVelocity * FLING_FRICTION;

            if(flingVelocity < 5 && flingVelocity > -5) {
                railState = RailState.RESTING;
            }
        }

        List<TouchEvent> touchEvents = gameController.getInput().getTouchEvents();

        timeSinceLastTouchEvent += portionOfSecond;

        if(timeSinceLastTouchEvent > LONG_TOUCH_THRESHOLD && (railState == RailState.TOUCH_INITIATED || railState == RailState.SLIDING)) {
            tryToPickUpLetter();
        }

        int lastTouchOffset = 0;
        for(TouchEvent event : touchEvents) {
            if(event.getType() == TouchEvent.TOUCH_DOWN && touchIsInsideRail(event)) {
                railState = RailState.TOUCH_INITIATED;

                lastX = event.getX();
                lastY = event.getY();
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
                } else {
                    railState = RailState.RESTING;
                }
            }

            timeSinceLastTouchEvent = 0;
        }
    }

    private void tryToPickUpLetter() {
        for(int i = 0; i < letterTiles.length; i++) {
            LetterTile tile = letterTiles[i];
            if(lastX > tile.getLeft() && lastX < tile.getLeft() + tileDimension - 1
                    && lastY > tile.getTop() && lastY < tile.getTop() + tileDimension - 1) {
                pickUpLetter(i);
                break;
            }
        }
    }

    private void pickUpLetter(int letterIndex) {
        railState = RailState.LETTER_SELECTED;
        letterTiles[letterIndex].setTop(letterTiles[letterIndex].getTop() - 25);
        selectedLetterIndex = letterIndex;

        //todo more?
    }

    private boolean touchIsInsideRail(TouchEvent event) {
        return event.getX() > sectionLeft
                && event.getX() < sectionLeft + sectionWidth
                && event.getY() > sectionTop + padding
                && event.getY() < sectionTop + sectionHeight - padding;
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
            gameController.getGraphics().drawRect(tile.getLeft(), tile.getTop(), tileDimension, tileDimension, TILE_BACKGROUND_COLOR);
            gameController.getGraphics().writeText(String.valueOf(tile.getLetterDisplay()), tile.getLeft() + (tileDimension / 2),
                    tile.getTop() + letterBaselineFromTileTopOffset, LETTER_TEXT_COLOR, letterTextSize, LETTER_TYPEFACE, LETTER_ALIGNMENT);
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

    private enum RailState {
        RESTING, TOUCH_INITIATED, SLIDING, FLUNG, LETTER_SELECTED
    }
}
