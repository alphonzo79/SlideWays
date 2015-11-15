package rowley.slideways.screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import jrowley.gamecontrollib.game_control.BaseGameControllerActivity;
import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.SlideWaysApp;
import rowley.slideways.activity.GameActivity;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.screens.game_screen_sections.SlidingLetterRail;
import rowley.slideways.util.Assets;
import rowley.slideways.util.MovingLetterTileAttributes;

/**
 * Created by jrowley on 11/4/15.
 */
public class GameScreen extends ScreenController implements LetterReceiver {
    private boolean hasBackBeenPressed = false;
    private int screenWidth;
    private SlidingLetterRail letterRail;
    private int dividerLineY;
    private float dividerLineHeight;
    private final float DIVIDER_HEIGHT_BASE = 1.5f;

    private final int PADDING_BASE = 30;
    private final int padding;

    private LetterTile detachedTile;
    private boolean isDetachedTileReturningHome = false;

    private MovingLetterTileAttributes tileAttrs;

    private int lastX;
    private int lastY;

    public GameScreen(GameController gameController) {
        super(gameController);

        tileAttrs = MovingLetterTileAttributes.getInstance(gameController);

        screenWidth = gameController.getGraphics().getWidth();
        int screenHeight = gameController.getGraphics().getHeight();

        padding = (int) (PADDING_BASE * gameController.getGraphics().getScale());

        int letterRailTop = screenHeight - tileAttrs.getTileDimension() - (Assets.padding * 2);
        letterRail = new SlidingLetterRail(0, letterRailTop, screenWidth, screenHeight - letterRailTop, gameController);
        letterRail.setPickedUpLetterReceiver(this);

        dividerLineY = letterRailTop;
        dividerLineHeight = DIVIDER_HEIGHT_BASE * gameController.getGraphics().getScale();
    }

    @Override
    public void update(float portionOfSecond) {
        gameController.getFrameRateTracker().update(portionOfSecond);
        List<TouchEvent> touchEvents = gameController.getInput().getTouchEvents();

        if(hasBackBeenPressed) {
            gameController.setScreen(new HomeScreen(gameController));
            return;
        }

        if(detachedTile != null) {
            if(!isDetachedTileReturningHome) {
                for (TouchEvent event : touchEvents) {
                    if (event.getType() == TouchEvent.TOUCH_DRAGGED) {
                        //For now we will assume the touch is within the detached tile
                        //Careful management of the touch events will allow us to avoid a few calculations
                        int xDiff = event.getX() - lastX;
                        int yDiff = event.getY() - lastY;
                        detachedTile.setLeft(detachedTile.getLeft() + xDiff);
                        detachedTile.setTop(detachedTile.getTop() + yDiff);
                        letterRail.monitorDetachedTilePosition(detachedTile);

                        lastX = event.getX();
                        lastY = event.getY();
                    }

                    if (event.getType() == TouchEvent.TOUCH_UP) {
                        lastX = event.getX();
                        lastY = event.getY();

                        if (letterRail.tryReceiveControlOfLetter(detachedTile, lastX, lastY)) {
                            detachedTile = null;
                        } else {
                            isDetachedTileReturningHome = true;
                        }
                    }
                }
            } else {
                if(detachedTile.progressTowardLastStablePosition(portionOfSecond)) {
                    // TODO: 11/14/15 What if it's not accepted?
                    letterRail.tryReceiveControlOfLetter(detachedTile, detachedTile.getLeft(), detachedTile.getTop());
                    detachedTile = null;
                    isDetachedTileReturningHome = false;
                }
            }
        }

        letterRail.update(portionOfSecond, touchEvents);
        //nothing yet
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.RED + 45);

        letterRail.present(portionOfSecond);
        gameController.getGraphics().drawLine(padding, dividerLineY, screenWidth - padding, dividerLineY, dividerLineHeight, Color.BLACK);

        if(detachedTile != null) {
            gameController.getGraphics().drawRect(detachedTile.getLeft(), detachedTile.getTop(), detachedTile.getWidth(), detachedTile.getHeight(), tileAttrs.getTileBackgroundColor());
            gameController.getGraphics().writeText(String.valueOf(detachedTile.getLetterDisplay()),
                    detachedTile.getLeft() + (tileAttrs.getTileDimension() / 2),
                    detachedTile.getTop() + tileAttrs.getLetterBaselineFromTileTopOffset(), tileAttrs.getLetterTextColor(),
                    tileAttrs.getLetterTextSize(), tileAttrs.getLetterTypeface(), tileAttrs.getLetterAlignment());
        }

        gameController.getFrameRateTracker().writeFrameRate(gameController.getGraphics());
    }

    @Override
    public void pause() {
        letterRail.pause();
    }

    @Override
    public void resume() {
        letterRail.resume();
    }

    @Override
    public void dispose() {
        letterRail.dispose();
    }

    @Override
    public boolean onBackPressed() {
        hasBackBeenPressed = true;
        return true;
    }

    @Override
    public boolean tryReceiveControlOfLetter(LetterTile letter, int lastTouchX, int lastTouchY) {
        detachedTile = letter;
        lastX = lastTouchX;
        lastY = lastTouchY;
        //// TODO: 11/13/15  lett

        return true;
    }
}
