package rowley.slideways.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import jrowley.gamecontrollib.game_control.GameController;

/**
 * Created by joe on 11/14/15.
 */
public class MovingLetterTileAttributes {
    private final int TILE_BACKGROUND_COLOR = 0x55ffffff;
    private final int LETTER_TEXT_COLOR = Color.BLACK;
    private final float LETTER_TEXT_SIZE_RATIO = 0.8f;
    private final Typeface LETTER_TYPEFACE = Typeface.DEFAULT_BOLD;
    private final Paint.Align LETTER_ALIGNMENT = Paint.Align.CENTER;
    private final int tileDimension;
    private final float letterTextSize;
    private final int letterBaselineFromTileTopOffset;

    private static MovingLetterTileAttributes instance;
    private static Object syncLock = new Object();

    private MovingLetterTileAttributes() {
        //We'll never use this, but making this call to the other constructor allows us to keep
        //certain fields final without compiler errors
        this(null);
    }

    private MovingLetterTileAttributes(GameController gameController) {
        //remove one padding from width, then base tile dimen on the # of tiles we want to fit across
        //the screen, then subtract one padding from the width. This should leave us with MAX_WORD_LENGTH
        //tiles and left padding and one more right padding to fill the screen.
        tileDimension = ((gameController.getGraphics().getWidth() - Assets.padding) / Assets.LETTERS_PER_SCREEN) - Assets.padding;
        letterTextSize = tileDimension * LETTER_TEXT_SIZE_RATIO;
        Rect bounds = new Rect();
        gameController.getGraphics().getTextBounds("A", letterTextSize, LETTER_TYPEFACE, LETTER_ALIGNMENT, bounds);
        letterBaselineFromTileTopOffset = (tileDimension / 2) + (bounds.height() / 2);
    }

    public static MovingLetterTileAttributes getInstance(GameController gameController) {
        if(instance == null) {
            synchronized (syncLock) {
                if(instance == null) {
                    instance = new MovingLetterTileAttributes(gameController);
                }
            }
        }

        return instance;
    }

    public int getTileBackgroundColor() {
        return TILE_BACKGROUND_COLOR;
    }

    public int getLetterTextColor() {
        return LETTER_TEXT_COLOR;
    }

    public Typeface getLetterTypeface() {
        return LETTER_TYPEFACE;
    }

    public Paint.Align getLetterAlignment() {
        return LETTER_ALIGNMENT;
    }

    public int getTileDimension() {
        return tileDimension;
    }

    public float getLetterTextSize() {
        return letterTextSize;
    }

    public int getLetterBaselineFromTileTopOffset() {
        return letterBaselineFromTileTopOffset;
    }
}
