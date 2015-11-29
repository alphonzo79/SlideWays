package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.R;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.screens.GameScreen;
import rowley.slideways.screens.HighScoresScreen;
import rowley.slideways.util.Assets;

/**
 * Created by joe on 11/27/15.
 */
public class Submitter extends ScreenSectionController {
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
                        // TODO: 11/28/15 get the tiles
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
    }

    @Override
    public void present(float v) {
        int buttonColor = buttonPressed ? BUTTON_COLOR_PRESSED : BUTTON_COLOR_STANDARD;
        gameController.getGraphics().drawRect(buttonLeft, buttonTop, buttonWidth, buttonHeight, buttonColor);
        gameController.getGraphics().writeText(buttonText, buttonCenter, buttonTextBaseline, Color.WHITE, buttonTextSize, BUTTON_TYPEFACE, BUTTON_ALIGNMENT);
        // TODO: 11/27/15
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

    public interface OnRailLockListener {
        void lock();
        void unlock();
    }

    public interface OnSubmitPressedListener {
        LetterTile[] takeControlOfBuiltTiles();
    }

    public interface OnWordScoredListener {
        void onWordScored(int score);
    }

    private enum State {
        DEFAULT, SUBMITTING, RETURNING
    }
}
