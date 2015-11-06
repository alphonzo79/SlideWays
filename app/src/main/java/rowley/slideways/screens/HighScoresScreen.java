package rowley.slideways.screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.screen_control.ScreenController;

/**
 * Created by jrowley on 11/4/15.
 */
public class HighScoresScreen extends ScreenController {
    private boolean hasBackBeenPressed = false;

    public HighScoresScreen(GameController gameController) {
        super(gameController);
    }

    @Override
    public void update(float portionOfSecond) {
        gameController.getFrameRateTracker().update(portionOfSecond);

        if(hasBackBeenPressed) {
            gameController.setScreen(new HomeScreen(gameController));
            return;
        }
        //nothing yet. This is a stub.
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.RED);
        gameController.getGraphics().writeText("High Scores and Junk!", 120, 120, Color.BLACK, 36, Typeface.DEFAULT, Paint.Align.LEFT);

        gameController.getFrameRateTracker().writeFrameRate(gameController.getGraphics());
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

    @Override
    public boolean onBackPressed() {
        hasBackBeenPressed = true;
        return true;
    }
}
