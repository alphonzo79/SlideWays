package rowley.slideways.screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import javax.inject.Inject;

import jrowley.gamecontrollib.game_control.BaseGameControllerActivity;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.R;
import rowley.slideways.SlideWaysApp;
import rowley.slideways.data.dao.IBestGamesDao;
import rowley.slideways.util.Assets;
import rowley.wordtrie.WordTrie;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by jrowley on 11/2/15.
 */
public class LoadingScreen extends ScreenController {
    private final String TAG = "LoadingScreen";

    private float percentComplete = 0f;
    private int maxBarWidth;
    private int barHeight;
    private int centerWidth;
    private int centerHeight;
    private int barLeft;
    private int barTop;
    private final int LOADING_TEXT_SIZE = 40;
    private final int SECONDS_TO_LOOP = 5;

    private String loading;

    private Subscription subscription;
    private volatile boolean loadComplete = false;

    @Inject
    IBestGamesDao bestGameDao;

    public LoadingScreen(BaseGameControllerActivity gameController) {
        super(gameController);

        int screenWidth = gameController.getGraphics().getWidth();
        int screenHeight = gameController.getGraphics().getHeight();
        maxBarWidth = screenWidth / 2;
        barHeight = (int)(maxBarWidth * .1);
        centerWidth = screenWidth / 2;
        centerHeight = screenHeight / 2;

        barLeft = centerWidth - (maxBarWidth / 2);
        barTop = centerHeight - (barHeight / 2);

        loading = gameController.getStringResource(R.string.loading);

        ((SlideWaysApp)gameController.getApplication()).applicationComponent().inject(this);

        subscription = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Assets.wordTrie = new WordTrie.WordTrieBuilder().addSowpodWords(Assets.MAX_WORD_LENGTH).addEnableWords(Assets.MAX_WORD_LENGTH).build();
                Assets.bestGameList = bestGameDao.getBestGamesDecendingOrder(Assets.MAX_BEST_GAMES);
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
        float progress = portionOfSecond / SECONDS_TO_LOOP;
        if(percentComplete < 1) {
            percentComplete += progress;
        }
        if (percentComplete > 1) {
            percentComplete = 0;
        }
        gameController.getFrameRateTracker().update(portionOfSecond);

        //Clear out the touch events
        gameController.getInput().getTouchEvents();
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.BLUE);
        gameController.getGraphics().drawRect(barLeft, barTop, (int) (maxBarWidth * percentComplete), barHeight, Color.GREEN);
        gameController.getGraphics().writeText(loading, centerWidth, centerHeight + barHeight, Color.WHITE, LOADING_TEXT_SIZE, Typeface.SANS_SERIF, Paint.Align.CENTER);
        gameController.getFrameRateTracker().writeFrameRate(gameController.getGraphics());
        if(loadComplete) {
            gameController.getGraphics().writeText(Assets.wordTrie.getWordCount() + " words loaded", centerWidth, (int)(centerHeight + barHeight + (LOADING_TEXT_SIZE * gameController.getGraphics().getScale())), Color.WHITE, 25, Typeface.DEFAULT_BOLD, Paint.Align.CENTER);
            gameController.setScreen(new HomeScreen(gameController));
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
        if(subscription != null) {
            subscription.unsubscribe();
        }
    }
}
