package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.R;
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
    public void update(float v, List<TouchEvent> list) {
        // TODO: 11/27/15
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
}
