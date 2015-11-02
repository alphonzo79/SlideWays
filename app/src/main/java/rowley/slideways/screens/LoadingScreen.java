package rowley.slideways.screens;

import android.graphics.Color;
import android.util.Log;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.screen_control.ScreenController;

/**
 * Created by jrowley on 11/2/15.
 */
public class LoadingScreen extends ScreenController {
    private float percentComplete = 0f;
    private int maxWidth = 200;
    private int barHeight = 30;
    private int screenWidth = 480;
    private int screenHeight = 320;
    private int centerWidth = 240;
    private int centerHeight = 160;
    private int barLeft;
    private int barTop;
    private final int SECONDS_TO_LOAD = 5;

    public LoadingScreen(GameController gameController) {
        super(gameController);

        barLeft = centerWidth - (maxWidth / 2);
        barTop = centerHeight - 15;
    }

    @Override
    public void update(float portionOfSecond) {
        float progress = portionOfSecond / SECONDS_TO_LOAD;
        if(percentComplete < 1) {
            percentComplete += progress;
        }
        if (percentComplete > 1) {
            percentComplete = 0;
        }
        Log.d("LoadingScreen", "portionOfSecond: " + portionOfSecond + " progress: " + progress + " PercentComplete: " + percentComplete);
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().drawRect(0, 0, screenWidth, screenHeight, Color.BLUE);
        gameController.getGraphics().drawRect(barLeft, barTop, (int)(maxWidth * percentComplete), barHeight, Color.GREEN);
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
