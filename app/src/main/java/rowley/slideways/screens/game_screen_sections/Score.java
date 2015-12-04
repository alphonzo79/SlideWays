package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.data.entity.MovableEntity;
import rowley.slideways.data.entity.WordScore;
import rowley.slideways.util.Assets;

/**
 * Created by joe on 11/27/15.
 */
public class Score extends ScreenSectionController implements Submitter.OnWordScoredListener {
    private int score = 0;
    private Rect scoreBounds;
    private final Typeface TYPEFACE = Typeface.DEFAULT_BOLD;
    private final Paint.Align ALIGNMENT = Paint.Align.RIGHT;
    private int textSize;
    private final float TEXT_SIZE_TO_SECTION_HEIGHT_RATIO = 0.35f;
    private final int textRight;
    private int textLeft;
    private final int textBaseline;
    private final int textBaselineOffsetFromTop;

    private WordScore wordScore;
    private boolean incomingScore = false;

    private OnScoreRecordedListener scoreRecordedListener;

    public Score(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        textSize = (int) (sectionHeight * TEXT_SIZE_TO_SECTION_HEIGHT_RATIO);

        scoreBounds = new Rect();

        textRight = sectionLeft + sectionWidth - Assets.padding;
        gameController.getGraphics().getTextBounds(String.valueOf(score), textSize, TYPEFACE, ALIGNMENT, scoreBounds);
        textBaselineOffsetFromTop = scoreBounds.height();
        textBaseline = sectionTop + Assets.padding + textBaselineOffsetFromTop;
        textLeft = textRight - scoreBounds.width();
    }

    @Override
    public void update(float portionOfSecond, List<TouchEvent> list) {
        if(incomingScore && wordScore.progressTowardDesiredPosition(portionOfSecond)) {
            score += wordScore.getScore();
            scoreBounds.setEmpty();
            gameController.getGraphics().getTextBounds(String.valueOf(score), textSize, TYPEFACE, ALIGNMENT, scoreBounds);
            textLeft = textRight - scoreBounds.width();
            incomingScore = false;
            scoreRecordedListener.onScoreRecorded(wordScore);
        }
    }

    @Override
    public void present(float v) {
        gameController.getGraphics().writeText(String.valueOf(score), textRight, textBaseline, Color.WHITE, textSize, TYPEFACE, ALIGNMENT);
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
    public void onWordScored(WordScore score) {
        score.setScoreString(String.format(Locale.getDefault(), "+%d", score.getScore()));
        scoreBounds.setEmpty();
        gameController.getGraphics().getTextBounds(score.getScoreString(), textSize, TYPEFACE, Paint.Align.RIGHT, scoreBounds);
        score.setWidth(scoreBounds.width());
        score.setHeight(scoreBounds.height());
        score.setDesiredPosition(textLeft - scoreBounds.width(), textBaseline - textBaselineOffsetFromTop);
        score.overrideProgressPixelsPerSecondToHeightRatio((int) (MovableEntity.PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO * 0.8f));
        wordScore = score;
        incomingScore = true;
    }

    public void setOnSoreRecordedListener(OnScoreRecordedListener listener) {
        this.scoreRecordedListener = listener;
    }

    public interface OnScoreRecordedListener {
        public void onScoreRecorded(WordScore wordScore);
    }
}
