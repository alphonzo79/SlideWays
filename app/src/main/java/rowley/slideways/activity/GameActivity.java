package rowley.slideways.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import jrowley.gamecontrollib.game_control.BaseGameControllerActivity;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.screens.LoadingScreen;

public class GameActivity extends BaseGameControllerActivity {

    @Override
    public ScreenController getStartScreen() {
        return new LoadingScreen(this);
    }
}
