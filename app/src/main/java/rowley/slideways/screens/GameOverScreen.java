package rowley.slideways.screens;

import android.graphics.Color;

import javax.inject.Inject;

import jrowley.gamecontrollib.game_control.BaseGameControllerActivity;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.R;
import rowley.slideways.SlideWaysApp;
import rowley.slideways.data.dao.IBestGamesDao;
import rowley.slideways.data.entity.BestGame;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.util.Assets;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by joe on 12/4/15.
 */
public class GameOverScreen extends ScreenController {
    private int score;
    private String longestWord;
    private int highestWordScore;
    private String highestScoringWord;

    private String gameOverLabel;
    private String longestWordLabel;
    private String highestScoringWordLabel;

    private LetterTile[] longestWordTiles;
    private LetterTile[] highestScoringWordTiles;

    private BestGame currentGame;
    private boolean isHighScore = false;

    private Subscription setupSubscription;
    private Subscription saveSubscription;

    @Inject
    IBestGamesDao bestGameDao;

    public GameOverScreen(final int score, String longestWord, int highestWordScore, String highestScoringWord, final BaseGameControllerActivity gameController) {
        super(gameController);

        this.score = score;
        this.longestWord = longestWord;
        this.highestWordScore = highestWordScore;
        this.highestScoringWord = highestScoringWord;

        gameOverLabel = gameController.getStringResource(R.string.game_over_label);
        longestWordLabel = gameController.getStringResource(R.string.longest_word_label);
        highestScoringWordLabel = gameController.getStringResource(R.string.highest_scoring_word_label_formatted);

        this.currentGame = new BestGame();
        currentGame.setScore(score);
        currentGame.setTimestamp(System.currentTimeMillis());

        ((SlideWaysApp)gameController.getApplication()).applicationComponent().inject(this);

        setupSubscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                BestGame[] bestGames = Assets.bestGameList.getBestGames();

                boolean isBest = false;
                for(BestGame game : bestGames) {
                    if(game != null && currentGame.compareTo(game) < 0) {
                        isBest = true;
                        break;
                    }
                }

                subscriber.onNext(isBest);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.immediate()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isBestGame) {
                isHighScore = isBestGame;
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
    }

    @Override
    public void update(float portionOfASecond) {

    }

    @Override
    public void present(float portionOfASecond) {
        gameController.getGraphics().clear(Color.WHITE);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        if(setupSubscription != null) {
            setupSubscription.unsubscribe();
        }
        if(saveSubscription != null) {
            saveSubscription.unsubscribe();
        }
    }
}
