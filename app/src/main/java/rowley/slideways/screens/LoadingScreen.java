package rowley.slideways.screens;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.R;
import rowley.slideways.util.Assets;
import rowley.slideways.util.FrameRateTracker;
import rowley.wordtrie.WordTrie;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by jrowley on 11/2/15.
 */
public class LoadingScreen extends ScreenController {
    private final String TAG = "LoadingScreen";

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
    private FrameRateTracker frameRateTracker;

    private volatile boolean loadComplete = false;

    public LoadingScreen(GameController gameController) {
        super(gameController);

        barLeft = centerWidth - (maxWidth / 2);
        barTop = centerHeight - 15;

        frameRateTracker = new FrameRateTracker();

        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Assets.wordTrie = new WordTrie.WordTrieBuilder().addSowpodWords(Assets.MAX_WORD_LENGTH).addEnableWords(Assets.MAX_WORD_LENGTH).build();
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.immediate()).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        }, new Action0() {
            @Override
            public void call() {
                loadComplete = true;
            }
        });
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
        frameRateTracker.update(portionOfSecond);
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.BLUE);
        gameController.getGraphics().drawRect(barLeft, barTop, (int) (maxWidth * percentComplete), barHeight, Color.GREEN);
        gameController.getGraphics().writeText(gameController.getStringResource(R.string.loading), centerWidth, centerHeight + barHeight, Color.WHITE, 40f, Typeface.SANS_SERIF);
        gameController.getGraphics().writeText(String.valueOf(frameRateTracker.getFrameRate()) + " fps", 100, 25, Color.WHITE, 10f, Typeface.SANS_SERIF);
        if(loadComplete) {
            gameController.getGraphics().writeText(Assets.wordTrie.getWordCount() + " words loaded", centerWidth, centerHeight + barHeight + 45, Color.WHITE, 25f, Typeface.DEFAULT_BOLD);
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
}
