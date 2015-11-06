package rowley.slideways.screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.screens.game_screen_sections.SlidingLetterRail;

/**
 * Created by jrowley on 11/4/15.
 */
public class GameScreen extends ScreenController {
    private boolean hasBackBeenPressed = false;
    private int screenWidth;
    private SlidingLetterRail letterRail;
    private int dividerLineY;
    private int dividerLineHeight;
    private final int DIVIDER_HEIGHT_BASE = 2;

    private final int PADDING_BASE = 30;
    private final int padding;

    public GameScreen(GameController gameController) {
        super(gameController);

        screenWidth = gameController.getGraphics().getWidth();
        int screenHeight = gameController.getGraphics().getHeight();

        padding = (int) (PADDING_BASE * gameController.getGraphics().getScale());

        int letterRailTop = (int) (screenHeight * 0.6);
        letterRail = new SlidingLetterRail(0, letterRailTop, screenWidth, screenHeight - letterRailTop, gameController);

        dividerLineY = letterRailTop;
        dividerLineHeight = (int) (DIVIDER_HEIGHT_BASE * gameController.getGraphics().getScale());
    }

    @Override
    public void update(float portionOfSecond) {
        gameController.getFrameRateTracker().update(portionOfSecond);

        if(hasBackBeenPressed) {
            gameController.setScreen(new HomeScreen(gameController));
            return;
        }

        letterRail.update(portionOfSecond);
        //nothing yet
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.RED + 45);

        letterRail.present(portionOfSecond);
        gameController.getGraphics().drawLine(padding, dividerLineY, screenWidth - padding, dividerLineY, dividerLineHeight, Color.BLACK);

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
}
