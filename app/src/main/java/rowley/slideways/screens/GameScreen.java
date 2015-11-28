package rowley.slideways.screens;

import android.graphics.Color;

import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenController;
import rowley.slideways.data.entity.LetterTile;
import rowley.slideways.screens.game_screen_sections.BuilderRail;
import rowley.slideways.screens.game_screen_sections.SupplyLetterRail;
import rowley.slideways.util.Assets;
import rowley.slideways.util.MovingLetterTileAttributes;

/**
 * Created by jrowley on 11/4/15.
 */
public class GameScreen extends ScreenController implements LetterReceiver {
    private boolean hasBackBeenPressed = false;
    private int screenWidth;
    private SupplyLetterRail supplyLetterRail;
    private BuilderRail builderRail;
    private int dividerLineY;
    private float dividerLineHeight;
    private final float DIVIDER_HEIGHT_BASE = 1.5f;

    private final int TOP_COMPONENTS_HEIGHT_BASE = 60;
    private int topComponentsHeight;

    private LetterTile detachedTile;
    private boolean isDetachedTileReturningHome = false;

    private MovingLetterTileAttributes tileAttrs;

    private int lastX;
    private int lastY;

    public GameScreen(GameController gameController) {
        super(gameController);

        tileAttrs = MovingLetterTileAttributes.getInstance(gameController);

        screenWidth = gameController.getGraphics().getWidth();
        int screenHeight = gameController.getGraphics().getHeight();

        topComponentsHeight = (int) (TOP_COMPONENTS_HEIGHT_BASE * gameController.getGraphics().getScale());

        int supplyRailTop = screenHeight - tileAttrs.getTileDimension() - (Assets.padding * 2);
        int builderRailTop = supplyRailTop - tileAttrs.getTileDimension() - (Assets.padding * 2);

        builderRail = new BuilderRail(0, builderRailTop, screenWidth, supplyRailTop - builderRailTop, gameController);
        builderRail.setPickedUpLetterReceiver(this);

        supplyLetterRail = new SupplyLetterRail(0, supplyRailTop, screenWidth, screenHeight - supplyRailTop, gameController);
        supplyLetterRail.setPickedUpLetterReceiver(this);

        dividerLineY = supplyRailTop;
        dividerLineHeight = DIVIDER_HEIGHT_BASE * gameController.getGraphics().getScale();
    }

    @Override
    public void update(float portionOfSecond) {
        gameController.getFrameRateTracker().update(portionOfSecond);
        List<TouchEvent> touchEvents = gameController.getInput().getTouchEvents();

        if(hasBackBeenPressed) {
            gameController.setScreen(new HomeScreen(gameController));
            return;
        }

        if(detachedTile != null) {
            if(!isDetachedTileReturningHome) {
                for (TouchEvent event : touchEvents) {
                    if (event.getType() == TouchEvent.TOUCH_DRAGGED) {
                        //For now we will assume the touch is within the detached tile
                        //Careful management of the touch events will allow us to avoid a few calculations
                        int xDiff = event.getX() - lastX;
                        int yDiff = event.getY() - lastY;
                        detachedTile.setLeft(detachedTile.getLeft() + xDiff);
                        detachedTile.setTop(detachedTile.getTop() + yDiff);
                        supplyLetterRail.monitorDetachedTilePosition(detachedTile);

                        lastX = event.getX();
                        lastY = event.getY();
                    }

                    if (event.getType() == TouchEvent.TOUCH_UP) {
                        lastX = event.getX();
                        lastY = event.getY();

                        if(builderRail.tryReceiveControlOfLetter(detachedTile, lastX, lastY)) {
                            supplyLetterRail.onDetachedTileAcceptedByOther();
                            detachedTile = null;
                        } else if (supplyLetterRail.tryReceiveControlOfLetter(detachedTile, lastX, lastY)) {
                            builderRail.onDetachedTileAcceptedByOther();
                            detachedTile = null;
                        } else {
                            isDetachedTileReturningHome = true;
                        }
                    }
                }
            } else {
                if(detachedTile.progressTowardLastStablePosition(portionOfSecond)) {
                    // TODO: 11/14/15 What if it's not accepted? Does the letter belong to the supply or the builder?
                    if(supplyLetterRail.tryReceiveControlOfLetter(detachedTile, detachedTile.getLeft(), detachedTile.getTop())) {
                        builderRail.onDetachedTileAcceptedByOther();
                    } else if(builderRail.tryReceiveControlOfLetter(detachedTile, detachedTile.getLeft(), detachedTile.getTop())) {
                        supplyLetterRail.onDetachedTileAcceptedByOther();
                    }
                    detachedTile = null;
                    isDetachedTileReturningHome = false;
                }
            }
        }

        builderRail.update(portionOfSecond, touchEvents);
        supplyLetterRail.update(portionOfSecond, touchEvents);
    }

    @Override
    public void present(float portionOfSecond) {
        gameController.getGraphics().clear(Color.RED + 45);

        builderRail.present(portionOfSecond);
        supplyLetterRail.present(portionOfSecond);
        gameController.getGraphics().drawLine(Assets.padding, dividerLineY, screenWidth - Assets.padding, dividerLineY, dividerLineHeight, Color.BLACK);

        if(detachedTile != null) {
            gameController.getGraphics().drawRect(detachedTile.getLeft(), detachedTile.getTop(), detachedTile.getWidth(), detachedTile.getHeight(), tileAttrs.getTileBackgroundColor());
            gameController.getGraphics().writeText(String.valueOf(detachedTile.getLetterDisplay()),
                    detachedTile.getLeft() + (tileAttrs.getTileDimension() / 2),
                    detachedTile.getTop() + tileAttrs.getLetterBaselineFromTileTopOffset(), tileAttrs.getLetterTextColor(),
                    tileAttrs.getLetterTextSize(), tileAttrs.getLetterTypeface(), tileAttrs.getLetterAlignment());
        }

        gameController.getFrameRateTracker().writeFrameRate(gameController.getGraphics());
    }

    @Override
    public void pause() {
        supplyLetterRail.pause();
    }

    @Override
    public void resume() {
        supplyLetterRail.resume();
    }

    @Override
    public void dispose() {
        supplyLetterRail.dispose();
    }

    @Override
    public boolean onBackPressed() {
        hasBackBeenPressed = true;
        return true;
    }

    @Override
    public boolean tryReceiveControlOfLetter(LetterTile letter, int lastTouchX, int lastTouchY) {
        detachedTile = letter;
        lastX = lastTouchX;
        lastY = lastTouchY;

        return true;
    }
}
