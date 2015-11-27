package rowley.slideways.screens.game_screen_sections;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.pooling.ObjectPool;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.data.entity.Tile;
import rowley.slideways.util.Assets;

/**
 * Created by joe on 11/26/15.
 */
public class BuilderRail extends SlidingLetterRailBase {
    private Tile[] placeHolders;

    private final int TILE_COUNT = 15;

    public BuilderRail(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);
    }

    @Override
    protected void initializeTiles() {
        placeHolders = new Tile[TILE_COUNT];
        int currentX = firstLetterLeftMax;
        for(int i = 0; i < placeHolders.length; i++) {
            placeHolders[i] = new Tile();
            placeHolders[i].setLeft(currentX);
            placeHolders[i].setTop(sectionTop + Assets.padding);

            currentX += (tileAttrs.getTileDimension() + Assets.padding);
        }
    }

    @Override
    protected int getTileCount() {
        return TILE_COUNT;
    }

    @Override
    protected void updateTilesWithOffset(int offset) {
        super.updateTilesWithOffset(offset);
        for(Tile tile : placeHolders) {
            tile.setLeft(tile.getLeft() + offset);
        }
    }

    @Override
    protected int getLeftmostObjectLeftEdge() {
        return placeHolders[0].getLeft();
    }

    @Override
    protected int getRightmostObjectLeftEdge() {
        return placeHolders[getTileCount() - 1].getLeft();
    }

    @Override
    public void monitorDetachedTilePosition(LetterTile tile) {
// TODO: 11/26/15
    }

    @Override
    public void onDetachedTileAcceptedByOther() {
        if(selectedLetterIndex > -1) {
            selectedLetterIndex = -1;
            railState = RailState.RESTING;
        }
    }

    @Override
    public boolean tryReceiveControlOfLetter(LetterTile letter, int lastTouchX, int lastTouchY) {
        // TODO: 11/26/15
        return false;
    }

    @Override
    protected void presentTiles() {
        for(Tile tile : placeHolders) {
            if(tile != null) {
                gameController.getGraphics().drawRect(tile.getLeft(), tile.getTop(), tileAttrs.getTileDimension(),
                        tileAttrs.getTileDimension(), Assets.PLACEHOLDER_TILE_BACKGROUND_COLOR);
            }
        }
        super.presentTiles();
    }
}
