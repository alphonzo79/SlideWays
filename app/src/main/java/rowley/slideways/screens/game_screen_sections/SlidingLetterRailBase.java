package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.screens.DetachedTileMonitor;
import rowley.slideways.screens.LetterReceiver;
import rowley.slideways.util.Assets;
import rowley.slideways.util.MovingLetterTileAttributes;

/**
 * Created by joe on 11/25/15.
 */
public abstract class SlidingLetterRailBase extends ScreenSectionController implements DetachedTileMonitor,
        LetterReceiver, Submitter.OnRailLockListener {
    protected LetterTile[] letterTiles;
    protected boolean[] tilesToAdjust;

    protected MovingLetterTileAttributes tileAttrs;

    private LetterReceiver pickedUpLetterReceiver;

    protected final int firstLetterLeftMax;
    protected final int lastLetterLeftMin;

    protected int lastX;
    protected int lastY;
    protected RailState railState = RailState.RESTING;
    protected RailState targetRailStateAfterAdjustment = RailState.RESTING;
    protected float flingVelocity;
    protected final float FLING_FRICTION = 0.95f;
    protected float timeSinceLastTouchEvent;
    protected final float LONG_TOUCH_THRESHOLD = 0.4f;
    protected int selectedLetterIndex = -1;
    protected final int LETTER_PICKUP_OFFSET_BASE = 10;
    protected final int letterPickupOffset;

    protected String label;
    private int centerWidth;
    private int labelBaseline;
    private final Typeface LABEL_TYPEFACE = Typeface.DEFAULT;
    private final Paint.Align LABEL_ALIGNMENT = Paint.Align.CENTER;

    public SlidingLetterRailBase(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        tileAttrs = MovingLetterTileAttributes.getInstance(gameController);

        firstLetterLeftMax = sectionLeft + Assets.padding;
        lastLetterLeftMin = sectionLeft + sectionWidth - Assets.padding - tileAttrs.getTileDimension();

        letterTiles = new LetterTile[getTileCount()];
        tilesToAdjust = new boolean[getTileCount()];

        letterPickupOffset = (int) (LETTER_PICKUP_OFFSET_BASE * gameController.getGraphics().getScale());

        label = getLabel();
        centerWidth = sectionWidth / 2 + sectionLeft;
        labelBaseline = sectionTop + sectionHeight - Assets.padding;

        initializeTiles();
    }

    @Override
    public void update(float portionOfSecond, List<TouchEvent> touchEvents) {
        if(railState != RailState.LOCKED) {
            if (railState == RailState.ADJUSTING) {
                boolean doneAdjusting = true;
                for (int i = 0; i < tilesToAdjust.length; i++) {
                    if (tilesToAdjust[i] && !letterTiles[i].progressTowardDesiredPosition(portionOfSecond)) {
                        doneAdjusting = false;
                    }
                }

                if (doneAdjusting) {
                    Arrays.fill(tilesToAdjust, false);
                    railState = targetRailStateAfterAdjustment;
                }
            }

            if (railState == RailState.FLUNG) {
                int offset = (int) (flingVelocity * portionOfSecond);
                offset = adjustCalculatedOffsetToStayWithinBounds(offset);
                updateTilesWithOffset(offset);
                flingVelocity = flingVelocity * FLING_FRICTION;

                if (flingVelocity < 5 && flingVelocity > -5) {
                    railState = RailState.RESTING;
                }
            }

            timeSinceLastTouchEvent += portionOfSecond;

            if (timeSinceLastTouchEvent > LONG_TOUCH_THRESHOLD && (railState == RailState.TOUCH_INITIATED || railState == RailState.SLIDING)) {
                tryToPickUpLetter();
            }

            int lastTouchOffset = 0;
            for (TouchEvent event : touchEvents) {
                if (event.getType() == TouchEvent.TOUCH_DOWN && railState == RailState.RESTING && touchIsInsideRail(event)) {
                    railState = RailState.TOUCH_INITIATED;

                    lastX = event.getX();
                    lastY = event.getY();

                    timeSinceLastTouchEvent = 0;
                }

                if (event.getType() == TouchEvent.TOUCH_DRAGGED) {
                    if (railState == RailState.TOUCH_INITIATED || railState == RailState.SLIDING) {
                        if (railState != RailState.SLIDING) {
                            railState = RailState.SLIDING;
                        }

                        lastTouchOffset = getRailSlideOffset(event);
                        updateTilesWithOffset(lastTouchOffset);
                    }

                    //some devices register drags even if there is no change. Others don't register a drag without change
                    if (lastTouchOffset != 0) {
                        timeSinceLastTouchEvent = 0;
                    }
                }

                if (event.getType() == TouchEvent.TOUCH_UP) {
                    if (railState == RailState.SLIDING) {
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
                    } else if (railState != RailState.LETTER_SELECTED && railState != RailState.ADJUSTING) {
                        railState = RailState.RESTING;
                    }

                    timeSinceLastTouchEvent = 0;
                }
            }
        }
    }

    @Override
    public void present(float portionOfASecond) {
        presentTiles();
    }

    private void tryToPickUpLetter() {
        for(int i = 0; i < letterTiles.length; i++) {
            LetterTile tile = letterTiles[i];
            if(tile != null) {
                if (lastX > tile.getLeft() && lastX < tile.getLeft() + tileAttrs.getTileDimension() - 1
                        && lastY > tile.getTop() && lastY < tile.getTop() + tileAttrs.getTileDimension() - 1) {
                    pickUpLetter(i);
                    break;
                }
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
        if(offset > 0 && (getLeftmostObjectLeftEdge() + offset) > firstLetterLeftMax) {
            offset = firstLetterLeftMax - getLeftmostObjectLeftEdge();
        }
        if(offset < 0 && (getRightmostObjectLeftEdge() + offset) < lastLetterLeftMin) {
            offset = lastLetterLeftMin - getRightmostObjectLeftEdge();
        }

        return offset;
    }

    protected void updateTilesWithOffset(int offset) {
        for(LetterTile tile : letterTiles) {
            if(tile != null) {
                tile.setLeft(tile.getLeft() + offset);
            }
        }
    }

    protected void presentTiles() {
        for(LetterTile tile : letterTiles) {
            if(tile != null) {
                gameController.getGraphics().drawRect(tile.getLeft(), tile.getTop(), tileAttrs.getTileDimension(),
                        tileAttrs.getTileDimension(), tileAttrs.getTileBackgroundColor());
                gameController.getGraphics().writeText(String.valueOf(tile.getLetterDisplay()), tile.getLeft() + (tileAttrs.getTileDimension() / 2),
                        tile.getTop() + tileAttrs.getLetterBaselineFromTileTopOffset(), tileAttrs.getLetterTextColor(),
                        tileAttrs.getLetterTextSize(), tileAttrs.getLetterTypeface(), tileAttrs.getLetterAlignment());
            }
        }

        if(!TextUtils.isEmpty(label)) {
            gameController.getGraphics().writeText(label, centerWidth, labelBaseline, Assets.TRANSLUCENT_WHITE, Assets.labelTextSize, LABEL_TYPEFACE, LABEL_ALIGNMENT);
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

    public void setPickedUpLetterReceiver(LetterReceiver receiver) {
        this.pickedUpLetterReceiver = receiver;
    }

    @Override
    public void lock() {
        targetRailStateAfterAdjustment = railState;
        railState = RailState.LOCKED;
    }

    @Override
    public void unlock() {
        railState = targetRailStateAfterAdjustment;
    }

    protected abstract int getTileCount();
    protected abstract void initializeTiles();
    protected abstract int getLeftmostObjectLeftEdge();
    protected abstract int getRightmostObjectLeftEdge();
    protected abstract String getLabel();

    protected enum RailState {
        RESTING, TOUCH_INITIATED, SLIDING, FLUNG, LETTER_SELECTED, ADJUSTING, LOCKED
    }
}
