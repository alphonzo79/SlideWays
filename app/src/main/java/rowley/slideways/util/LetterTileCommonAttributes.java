package rowley.slideways.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by joe on 12/4/15.
 */
public abstract class LetterTileCommonAttributes {
    protected final int TILE_BACKGROUND_COLOR = 0x55ffffff;
    protected final int LETTER_TEXT_COLOR = Color.BLACK;
    protected final float LETTER_TEXT_SIZE_RATIO = 0.8f;
    protected final Typeface LETTER_TYPEFACE = Typeface.DEFAULT_BOLD;
    protected final Paint.Align LETTER_ALIGNMENT = Paint.Align.CENTER;
    protected int tileDimension;
    protected float letterTextSize;
    protected int letterBaselineFromTileTopOffset;

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
