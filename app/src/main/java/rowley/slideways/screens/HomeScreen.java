package rowley.slideways.screens;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.screen_control.ScreenController;

/**
 * Created by jrowley on 11/3/15.
 */
public class HomeScreen extends ScreenController {
    public HomeScreen(GameController gameController) {
        super(gameController);
    }

    @Override
    public void update(float v) {
        // get touches, etc. But for now we're just going to show a static screen
    }

    @Override
    public void present(float v) {

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
