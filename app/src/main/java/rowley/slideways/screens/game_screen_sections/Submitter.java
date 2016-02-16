package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.R;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.data.entity.MovableEntity;
import rowley.slideways.data.entity.WordScore;
import rowley.slideways.screens.GameScreen;
import rowley.slideways.screens.HighScoresScreen;
import rowley.slideways.util.Assets;
import rowley.slideways.util.MovingLetterTileAttributes;

/**
 * Created by joe on 11/27/15.
 */
public class Submitter extends ScreenSectionController {
    private int sectionCenter;

    private int buttonLeft;
    private int buttonTop;
    private int buttonWidth;
    private int buttonHeight;

    private int buttonTextBaseline;
    private int buttonCenter;

    private final float BUTTON_HEIGHT_RATIO = 0.15f;

    private int buttonTextSize;
    private final float TEXT_SIZE_TO_BUTTON_RATIO = 0.6f;
    private final Typeface BUTTON_TYPEFACE = Typeface.DEFAULT_BOLD;
    private final Paint.Align BUTTON_ALIGNMENT = Paint.Align.CENTER;

    private String buttonText;

    private final int BUTTON_COLOR_STANDARD = Color.BLUE;
    private final int BUTTON_COLOR_PRESSED = Color.BLUE - 125;

    private boolean buttonPressed = false;
    private boolean pressInitiated = false;

    private List<OnRailLockListener> railLockListeners = new ArrayList<OnRailLockListener>(2);
    private OnSubmitPressedListener submitPressedListener;
    private OnWordScoredListener wordScoredListener;

    private LetterTile[] submittedTiles;
    private int desiredTileLeft;
    private MovingLetterTileAttributes tileAttrs;
    private char[] wordToSubmit;

    private SectionState sectionState = SectionState.DEFAULT;

    private String highestScoringWord = "";
    private int highestScore = 0;
    private String longestWord = "";

    public Submitter(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        buttonLeft = sectionLeft + (Assets.padding * 2);
        buttonWidth = sectionWidth - (Assets.padding * 4);
        buttonHeight = (int) (gameController.getGraphics().getHeight() * BUTTON_HEIGHT_RATIO);
        buttonTop = sectionTop + sectionHeight - buttonHeight;

        buttonTextSize = (int) (buttonHeight * TEXT_SIZE_TO_BUTTON_RATIO);
        buttonText = gameController.getStringResource(R.string.submit);

        Rect bounds = new Rect();
        gameController.getGraphics().getTextBounds(buttonText, buttonTextSize, BUTTON_TYPEFACE, BUTTON_ALIGNMENT, bounds);
        int textFromTopOffset = (buttonHeight / 2) + (bounds.height() / 2);

        buttonTextBaseline = buttonTop + textFromTopOffset;
        buttonCenter = buttonLeft + (buttonWidth / 2);

        sectionCenter = sectionLeft + (sectionWidth / 2);

        tileAttrs = MovingLetterTileAttributes.getInstance(gameController);
        desiredTileLeft = sectionCenter - (tileAttrs.getTileDimension() / 2);
    }

    @Override
    public void update(float portionOfSecond, List<TouchEvent> touchEvents) {
        for(TouchEvent event : touchEvents) {
            if(pressInitiated) {
                if (event.getType() == TouchEvent.TOUCH_UP) {
                    pressInitiated = false;
                    buttonPressed = false;
                    if (event.getY() > buttonTop && event.getY() < buttonTop + buttonHeight - 1
                            && event.getX() > buttonLeft && event.getX() < buttonLeft + buttonWidth - 1) {
                        for(OnRailLockListener listener : railLockListeners) {
                            listener.lock();
                        }
                        submittedTiles = submitPressedListener.takeControlOfBuiltTiles();
                        for(LetterTile tile : submittedTiles) {
                            if(tile != null) {
                                tile.setLastStablePosition(tile.getLeft(), tile.getTop());
                                tile.setDesiredPosition(desiredTileLeft, 0);
                                tile.overrideProgressPixelsPerSecondToHeightRatio((int) (MovableEntity.PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO * 0.65f));
                            }
                        }
                        sectionState = SectionState.SUBMITTING;
                        continue;
                    }
                }
            }

            if(event.getType() == TouchEvent.TOUCH_DOWN && event.getX() > buttonLeft
                    && event.getX() < buttonLeft + buttonWidth - 1 && event.getY() > buttonTop
                    && event.getY() < buttonTop + buttonHeight - 1) {
                pressInitiated = true;
                buttonPressed = true;
                continue;
            }

            if(event.getType() == TouchEvent.TOUCH_DRAGGED) {
                if(event.getX() > buttonLeft && event.getX() < buttonLeft + buttonWidth - 1
                        && event.getY() > buttonTop && event.getY() < buttonTop + buttonHeight - 1) {
                    buttonPressed = true;
                } else {
                    buttonPressed = false;
                }
            }

        }

        if(sectionState == SectionState.SUBMITTING) {
            boolean moveComplete = true;
            for(LetterTile tile : submittedTiles) {
                if(tile != null && !tile.progressTowardDesiredPosition(portionOfSecond)) {
                    moveComplete = false;
                }
            }

            if(moveComplete) {
                submitTilesForScore();
            }
        }

        if(sectionState == SectionState.RETURNING) {
            boolean moveComplete = true;
            for(LetterTile tile : submittedTiles) {
                if(tile != null && !tile.progressTowardLastStablePosition(portionOfSecond)) {
                    moveComplete = false;
                }
            }

            if(moveComplete) {
                for(OnRailLockListener listener : railLockListeners) {
                    listener.unlock();
                }
                resetTileVelocities();
                sectionState = SectionState.DEFAULT;
            }
        }
    }

    @Override
    public void present(float v) {
        int buttonColor = buttonPressed ? BUTTON_COLOR_PRESSED : BUTTON_COLOR_STANDARD;
        gameController.getGraphics().drawRect(buttonLeft, buttonTop, buttonWidth, buttonHeight, buttonColor);
        gameController.getGraphics().writeText(buttonText, buttonCenter, buttonTextBaseline, Color.WHITE, buttonTextSize, BUTTON_TYPEFACE, BUTTON_ALIGNMENT);

        //No need to draw tiles because we let the builder rail continue to draw -- we just handle moving them
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

    public void addRailLockListener(OnRailLockListener listener) {
        railLockListeners.add(listener);
    }

    public void setSubmitPressedListener(OnSubmitPressedListener submitPressedListener) {
        this.submitPressedListener = submitPressedListener;
    }

    public void setWordScoredListener(OnWordScoredListener wordScoredListener) {
        this.wordScoredListener = wordScoredListener;
    }

    private void submitTilesForScore() {
        //First, validate.
        boolean charFound = false;
        boolean gapFound = false;
        boolean valid = false;
        int wordStartIndexInclusive = 0;
        int wordEndIndexExclusive = 0;
        int index = 0;
        for(; index < submittedTiles.length; index++) {
            if(submittedTiles[index] != null) {
                if(!charFound) {
                    wordStartIndexInclusive = index;
                    charFound = true;
                    valid = true;
                } else if(gapFound) {
                    //We have recognized a gap between characters -- a break in the word
                    valid = false;
                }
            } else if(charFound && !gapFound) {
                wordEndIndexExclusive = index;
                gapFound = true;
            }
        }

        if(!valid) {
            returnTiles();
        } else {
            wordToSubmit = new char[wordEndIndexExclusive - wordStartIndexInclusive];
            index = 0;
            for(; index < wordToSubmit.length; index++) {
                wordToSubmit[index] = submittedTiles[index + wordStartIndexInclusive].getLetter();
            }

            int score = 0;

            if(Assets.wordTrie.isKnownWord(wordToSubmit)) {
                score = Assets.wordScorer.getScoreForWord(wordToSubmit);
            }
            if(score > 0) {
                handleWordScore(score, wordToSubmit);

                for(OnRailLockListener listener : railLockListeners) {
                    listener.unlock();
                }
                sectionState = SectionState.DEFAULT;
            } else {
                returnTiles();
            }
        }
    }

    private void returnTiles() {
        for(MovableEntity tile : submittedTiles) {
            if(tile != null) {
                tile.overrideProgressPixelsPerSecondToHeightRatio((int) (MovableEntity.PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO * 0.4f));
            }
        }
        sectionState = SectionState.RETURNING;
    }

    private void resetTileVelocities() {
        for(MovableEntity tile : submittedTiles) {
            if(tile != null) {
                tile.overrideProgressPixelsPerSecondToHeightRatio(MovableEntity.PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO);
            }
        }
    }

    private void handleWordScore(int score, char[] word) {
        WordScore wordScore = new WordScore();
        wordScore.setLeft(sectionCenter);
        wordScore.setTop(0);
        wordScore.setScore(score);
        wordScoredListener.onWordScored(wordScore);
        for(int index = 0; index < submittedTiles.length; index++) {
            submittedTiles[index] = null;
        }

        if(word.length > longestWord.length()) {
            longestWord = String.valueOf(word);
        }
        if(score > highestScore) {
            highestScore = score;
            highestScoringWord = String.valueOf(word);
        }
    }

    public String getHighestScoringWord() {
        return highestScoringWord;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public String getLongestWord() {
        return longestWord;
    }

    public interface OnRailLockListener {
        void lock();
        void unlock();
    }

    public interface OnSubmitPressedListener {
        LetterTile[] takeControlOfBuiltTiles();
    }

    public interface OnWordScoredListener {
        void onWordScored(WordScore score);
    }

    private enum SectionState {
        DEFAULT, SUBMITTING, RETURNING
    }
}
