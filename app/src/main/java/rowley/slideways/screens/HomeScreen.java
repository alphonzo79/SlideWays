package rowley.slideways.screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.R;
import rowley.slideways.util.Assets;

/**
 * Created by jrowley on 11/3/15.
 */
public class HomeScreen extends ScreenController {
    private boolean isGameInProgress;

    private int centerWidth;
    private int centerHeight;

    private int buttonWidth;
    private int buttonHeight;
    private int heightBetweenButtons;
    private int buttonsLeft;
    private int newButtonTop;
    private int continueButtonTop;
    private int highScoresButtonTop;

    private final float BUTTON_WIDTH_RATIO = 0.4f;
    private final float BUTTON_HEIGHT_RATIO = 0.25f;
    private final float HEIGHT_BETWEEN_BUTTONS_RATIO = 0.05f;
    private final float TEXT_SIZE_TO_BUTTON_RATIO = 0.4f;

    private String newGameString;
    private String continueGameString;
    private String highScoresString;
    private int buttonTextSize;
    private int newGameTextBaseline;
    private int continueGameTextBaseline;
    private int highScoreTextBaseline;

    private final int BUTTON_COLOR_STANDARD = Color.BLUE;
    private final int BUTTON_COLOR_PRESSED = Color.BLUE - 125;

    private boolean isNewPressed = false;
    private boolean isContinuePressed = false;
    private boolean isHighScoresPressed = false;

    public HomeScreen(GameController gameController) {
        super(gameController);
        isGameInProgress = Assets.isGameInProgress;

        int screenWidth = gameController.getGraphics().getWidth();
        int screenHeight = gameController.getGraphics().getHeight();
        centerWidth = screenWidth / 2;
        centerHeight = screenHeight / 2;

        buttonWidth = (int) (screenWidth * BUTTON_WIDTH_RATIO);
        buttonHeight = (int) (screenHeight * BUTTON_HEIGHT_RATIO);
        heightBetweenButtons = (int) (screenHeight * HEIGHT_BETWEEN_BUTTONS_RATIO);
        buttonsLeft = centerWidth - (buttonWidth / 2);
        buttonTextSize = (int) (buttonHeight * TEXT_SIZE_TO_BUTTON_RATIO);

        if(isGameInProgress) {
            continueButtonTop = centerHeight - (buttonHeight / 2);
            newButtonTop = continueButtonTop - heightBetweenButtons - buttonHeight;
            highScoresButtonTop = centerHeight + (buttonHeight / 2) + heightBetweenButtons;
        } else {
            newButtonTop = centerHeight - heightBetweenButtons - buttonHeight;
            highScoresButtonTop = centerHeight + heightBetweenButtons;
            continueButtonTop = screenHeight + buttonHeight;
        }

        newGameString = gameController.getStringResource(R.string.new_game);
        continueGameString = gameController.getStringResource(R.string.continue_game);
        highScoresString = gameController.getStringResource(R.string.high_scores);

        //I don't know why dividing by 3 works here instead of dividing by 2. Font metrics, I suspect.
        //But for now it works and we can move on. Maybe come back and figure it out later.
        int textFromTopOffset = (buttonHeight / 2) + (buttonTextSize / 3);
        newGameTextBaseline = newButtonTop + textFromTopOffset;
        continueGameTextBaseline = continueButtonTop + textFromTopOffset;
        highScoreTextBaseline = highScoresButtonTop + textFromTopOffset;
    }

    @Override
    public void update(float portionOfSecond) {
        gameController.getFrameRateTracker().update(portionOfSecond);

        List<TouchEvent> touchEvents = gameController.getInput().getTouchEvents();

        for(TouchEvent event : touchEvents) {
            if(event.getX() > buttonsLeft && event.getX() < buttonsLeft + buttonWidth - 1) {
                if (event.getType() == TouchEvent.TOUCH_UP) {
                    if (event.getY() > newButtonTop && event.getY() < newButtonTop + buttonHeight - 1) {
                        isNewPressed = false;
                        gameController.setScreen(new GameScreen(gameController));
                    }
                    if (event.getY() > continueButtonTop && event.getY() < continueButtonTop + buttonHeight - 1) {
                        isContinuePressed = false;
                        gameController.setScreen(new GameScreen(gameController));
                    }
                    if (event.getY() > highScoresButtonTop && event.getY() < highScoresButtonTop + buttonHeight - 1) {
                        isHighScoresPressed = false;
                        gameController.setScreen(new HighScoresScreen(gameController));
                    }
                }

                if (event.getType() == TouchEvent.TOUCH_DOWN || event.getType() == TouchEvent.TOUCH_DRAGGED) {
                    if (event.getY() > newButtonTop && event.getY() < newButtonTop + buttonHeight - 1) {
                        isNewPressed = true;
                    } else if(isNewPressed) {
                        isNewPressed = false;
                    }
                    if (event.getY() > continueButtonTop && event.getY() < continueButtonTop + buttonHeight - 1) {
                        isContinuePressed = true;
                    } else if(isContinuePressed) {
                        isContinuePressed = false;
                    }
                    if (event.getY() > highScoresButtonTop && event.getY() < highScoresButtonTop + buttonHeight - 1) {
                        isHighScoresPressed = true;
                    } else if(isHighScoresPressed) {
                        isHighScoresPressed = false;
                    }
                }
            } else {
                //reset buttons
                isNewPressed = false;
                isContinuePressed = false;
                isHighScoresPressed = false;
            }
        }
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.GREEN);

        int buttonColor = isNewPressed ? BUTTON_COLOR_PRESSED : BUTTON_COLOR_STANDARD;
        gameController.getGraphics().drawRect(buttonsLeft, newButtonTop, buttonWidth, buttonHeight, buttonColor);
        buttonColor = isContinuePressed ? BUTTON_COLOR_PRESSED : BUTTON_COLOR_STANDARD;
        gameController.getGraphics().drawRect(buttonsLeft, continueButtonTop, buttonWidth, buttonHeight, buttonColor);
        buttonColor = isHighScoresPressed ? BUTTON_COLOR_PRESSED : BUTTON_COLOR_STANDARD;
        gameController.getGraphics().drawRect(buttonsLeft, highScoresButtonTop, buttonWidth, buttonHeight, buttonColor);

        gameController.getGraphics().writeText(newGameString, centerWidth, newGameTextBaseline, Color.WHITE, buttonTextSize, Typeface.DEFAULT_BOLD, Paint.Align.CENTER);
        gameController.getGraphics().writeText(continueGameString, centerWidth, continueGameTextBaseline, Color.WHITE, buttonTextSize, Typeface.DEFAULT_BOLD, Paint.Align.CENTER);
        gameController.getGraphics().writeText(highScoresString, centerWidth, highScoreTextBaseline, Color.WHITE, buttonTextSize, Typeface.DEFAULT_BOLD, Paint.Align.CENTER);

        gameController.getFrameRateTracker().writeFrameRate(gameController.getGraphics());
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
