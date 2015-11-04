package rowley.slideways.screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.screen_control.ScreenController;

/**
 * Created by jrowley on 11/4/15.
 */
public class GameScreen extends ScreenController {
    public GameScreen(GameController gameController) {
        super(gameController);
    }

    @Override
    public void update(float portionOfSecond) {
        //nothing yet
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.RED + 45);
        gameController.getGraphics().writeText("Game Screen!", 120, 120, Color.WHITE, 36, Typeface.DEFAULT, Paint.Align.LEFT);
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
}
