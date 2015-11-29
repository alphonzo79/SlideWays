package rowley.slideways.screens.game_screen_sections;

import java.util.List;

import jrowley.gamecontrollib.game_control.GameController;
import jrowley.gamecontrollib.input.TouchEvent;
import jrowley.gamecontrollib.screen_control.ScreenSectionController;

/**
 * Created by joe on 11/27/15.
 */
public class Score extends ScreenSectionController implements Submitter.OnWordScoredListener {
    public Score(int sectionLeft, int sectionTop, int sectionWidth, int sectionHeight, GameController gameController) {
        super(sectionLeft, sectionTop, sectionWidth, sectionHeight, gameController);
    }

    @Override
    public void update(float v, List<TouchEvent> list) {
        // TODO: 11/27/15
    }

    @Override
    public void present(float v) {
        // TODO: 11/27/15
        gameController.getGraphics().drawRect(sectionLeft, sectionTop, sectionWidth, sectionHeight, 0xff0000ff);
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

    @Override
    public void onWordScored(int score) {
        // TODO: 11/29/15  
    }
}
