package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.List;
import java.util.Locale;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.data.entity.WordScore;
import rowley.slideways.util.Assets;

/**
 * Created by joe on 11/27/15.
 */
public class Timer extends ScreenSectionController implements Score.OnScoreRecordedListener {
    private int seconds = 180;
    private String timeString = "";
    private int minutes;
    private int leftoverSeconds;
    private float incrementalElapsed = 0.0f;

    private Rect timeBounds;
    private final Typeface TYPEFACE = Typeface.DEFAULT_BOLD;
    private final Paint.Align ALIGNMENT = Paint.Align.LEFT;
    private int textSize;
    private final float TEXT_SIZE_TO_SECTION_HEIGHT_RATIO = 0.35f;
    private int textRight;
    private final int textLeft;
    private final int textBaseline;
    private final int textBaselineOffsetFromTop;

    private WordScore wordScore;
    private boolean incomingScore = false;

    private final float SECONDS_PER_POINT = 0.15f;
    private final int MIN_SECONDS_FOR_SCORE = 2;

    private OnTimeExpiredListener expiredListener;

    public Timer(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        textSize = (int) (sectionHeight * TEXT_SIZE_TO_SECTION_HEIGHT_RATIO);

        timeBounds = new Rect();

        textLeft = sectionLeft + Assets.padding;
        formatTimeString();
        gameController.getGraphics().getTextBounds(timeString, textSize, TYPEFACE, ALIGNMENT, timeBounds);
        textBaselineOffsetFromTop = timeBounds.height();
        textBaseline = sectionTop + Assets.padding + textBaselineOffsetFromTop;
        textRight = textLeft + timeBounds.width();
    }

    @Override
    public void update(float portionOfSecond, List<TouchEvent> list) {
        if(incomingScore && wordScore.progressTowardDesiredPosition(portionOfSecond)) {
            seconds += wordScore.getScore();
            timeBounds.setEmpty();
            formatTimeString();
            gameController.getGraphics().getTextBounds(timeString, textSize, TYPEFACE, ALIGNMENT, timeBounds);
            textRight = textLeft + timeBounds.width();
            incomingScore = false;
        }

        incrementalElapsed += portionOfSecond;
        if(incrementalElapsed > 1.0f) {
            incrementalElapsed -= 1.0f;
            seconds--;
            formatTimeString();
        }

        if(seconds < 1) {
            expiredListener.onTimeExpired();
        }
    }

    @Override
    public void present(float v) {
        gameController.getGraphics().writeText(timeString, textLeft, textBaseline, Color.WHITE, textSize, TYPEFACE, ALIGNMENT);
        if(incomingScore) {
            gameController.getGraphics().writeText(wordScore.getScoreString(), wordScore.getLeft(), wordScore.getTop() + textBaselineOffsetFromTop, Color.WHITE, textSize, TYPEFACE, Paint.Align.LEFT);
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

    @Override
    public void onScoreRecorded(WordScore score) {
        int secondsToAdd = (int) (score.getScore() * SECONDS_PER_POINT);
        if(secondsToAdd < MIN_SECONDS_FOR_SCORE) {
            secondsToAdd = MIN_SECONDS_FOR_SCORE;
        }
        score.setScore(secondsToAdd);
        score.setScoreString(String.format(Locale.getDefault(), "+:%02d", secondsToAdd));

        timeBounds.setEmpty();
        gameController.getGraphics().getTextBounds(score.getScoreString(), textSize, TYPEFACE, Paint.Align.RIGHT, timeBounds);
        score.setWidth(timeBounds.width());
        score.setHeight(timeBounds.height());
        score.setDesiredPosition(textRight, textBaseline - textBaselineOffsetFromTop);

        this.wordScore = score;
        incomingScore = true;
    }

    private void formatTimeString() {
        minutes = seconds / 60;
        leftoverSeconds = seconds % 60;
        timeString = String.format(Locale.US, "%d:%02d", minutes, leftoverSeconds);
    }

    public void setOnTimeExpiredListener(OnTimeExpiredListener listener) {
        this.expiredListener = listener;
    }

    public interface OnTimeExpiredListener {
        public void onTimeExpired();
    }
}
