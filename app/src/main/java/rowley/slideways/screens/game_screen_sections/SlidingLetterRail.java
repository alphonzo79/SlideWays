package rowley.slideways.screens.game_screen_sections;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.pooling.ObjectPool;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;
import rowley.slideways.data.entity.LetterTile;

/**
 * Created by jrowley on 11/5/15.
 */
public class SlidingLetterRail extends ScreenSectionController {
    private ObjectPool<LetterTile> tilePool;
    private LetterTile[] letterTiles;

    private final int LETTER_COUNT = 12;
    private final int letterDimension;
    private final float letterTextSize;
    private final float LETTER_TEXT_SIZE_RATIO = 0.8f;

    private final int PADDING_BASE = 30;
    private final int padding;

    public SlidingLetterRail(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);

        padding = (int) (PADDING_BASE * gameController.getGraphics().getScale());
        letterDimension = sectionHeight - (padding * 2);
        letterTextSize = letterDimension * LETTER_TEXT_SIZE_RATIO;

        tilePool = new ObjectPool<>(new ObjectPool.PoolObjectFactory<LetterTile>() {
            @Override
            public LetterTile createObject() {
                LetterTile tile = new LetterTile();
                tile.setHeight(letterDimension);
                tile.setWidth(letterDimension);

                return tile;
            }
        }, LETTER_COUNT * 2);

        letterTiles = new LetterTile[LETTER_COUNT];

        //todo finish initializing
    }

    @Override
    public void update(float v) {

    }

    @Override
    public void present(float v) {
        gameController.getGraphics().writeText("Sliding Letter Rail", sectionLeft + 120, sectionTop + 120,
                Color.CYAN, 24, Typeface.DEFAULT, Paint.Align.LEFT);
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
