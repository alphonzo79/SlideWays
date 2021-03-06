package rowley.slideways.util;

import android.graphics.Rect;

import jrowley.gamecontrollib.game_control.GameController;

/**
 * Created by joe on 11/14/15.
 */
public class MovingLetterTileAttributes extends LetterTileCommonAttributes {

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
        tileDimension = ((gameController.getGraphics().getWidth() - Assets.padding) / Assets.MOVING_LETTERS_PER_SCREEN) - Assets.padding;
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
}
