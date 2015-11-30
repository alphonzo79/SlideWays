package rowley.slideways.screens.game_screen_sections;

import jrowley.gamecontrollib.game_control.GameController;
import rowley.slideways.R;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.data.entity.MovableEntity;
import rowley.slideways.util.Assets;

/**
 * Created by joe on 11/26/15.
 */
public class BuilderRail extends SlidingLetterRailBase implements Submitter.OnSubmitPressedListener {
    private MovableEntity[] placeHolders;

    private final int TILE_COUNT = 15;

    public BuilderRail(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);
    }

    @Override
    protected void initializeTiles() {
        placeHolders = new MovableEntity[TILE_COUNT];
        int currentX = firstLetterLeftMax;
        for(int i = 0; i < placeHolders.length; i++) {
            placeHolders[i] = new MovableEntity();
            placeHolders[i].setLeft(currentX);
            placeHolders[i].setTop(sectionTop + Assets.padding);
            placeHolders[i].setHeight(tileAttrs.getTileDimension());
            placeHolders[i].setWidth(tileAttrs.getTileDimension());

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
        for(MovableEntity tile : placeHolders) {
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
        //first, can we consider accepting it?
        if(letter.getTop() < sectionTop + sectionHeight - Assets.padding
                && letter.getTop() + letter.getHeight() > sectionTop + Assets.padding) {
            //We're inside the vertical bounds of our letters, let's figure out where it needs to go
            int targetIndex = findTargetIndexForMovingTile(letter);

            if(targetIndex != -1) {
                letter.setDesiredPosition(placeHolders[targetIndex].getLeft(), placeHolders[targetIndex].getTop());

                letterTiles[targetIndex] = letter;
                tilesToAdjust[targetIndex] = true;
                railState = RailState.ADJUSTING;
                targetRailStateAfterAdjustment = RailState.RESTING;
                selectedLetterIndex = -1;

                return true;
            }
        }
        return false;
    }

    private int findTargetIndexForMovingTile(LetterTile movingTile) {
        int targetIndex = selectedLetterIndex;
        for(int i = 0; i < placeHolders.length; i++) {
            if(i == selectedLetterIndex) {
                continue;
            }

            if((movingTile.getLeft() > placeHolders[i].getLeft()
                    && movingTile.getLeft() < placeHolders[i].getLeft() + (placeHolders[i].getWidth() / 2))
                    || (movingTile.getLeft() + movingTile.getWidth() < placeHolders[i].getLeft() + placeHolders[i].getWidth()
                    && movingTile.getLeft() + movingTile.getWidth() > placeHolders[i].getLeft() + (placeHolders[i].getWidth() / 2))
                    && letterTiles[i] == null) {
                targetIndex = i;
                break;
            }
        }

        return targetIndex;
    }

    @Override
    protected void presentTiles() {
        for(MovableEntity tile : placeHolders) {
            if(tile != null) {
                gameController.getGraphics().drawRect(tile.getLeft(), tile.getTop(), tileAttrs.getTileDimension(),
                        tileAttrs.getTileDimension(), Assets.PLACEHOLDER_TILE_BACKGROUND_COLOR);
            }
        }
        super.presentTiles();
    }

    @Override
    protected String getLabel() {
        return gameController.getStringResource(R.string.builder_rail);
    }

    @Override
    public LetterTile[] takeControlOfBuiltTiles() {
        return letterTiles;
    }
}
