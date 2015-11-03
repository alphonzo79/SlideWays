package rowley.slideways.activity;

import jrowley.gamecontrollib.game_control.BaseGameControllerActivity;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.screens.LoadingScreen;

public class GameActivity extends BaseGameControllerActivity {

    @Override
    public ScreenController getStartScreen() {
        return new LoadingScreen(this);
    }
}
