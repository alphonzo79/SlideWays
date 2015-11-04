package rowley.slideways.activity;

import android.app.Activity;
import android.util.Log;

import javax.inject.Inject;

import rowley.slideways.SlideWaysApp;
import rowley.slideways.data.dao.IBestGamesDao;
import rowley.slideways.di.SlideWaysInjectionModule;

/**
 * Created by jrowley on 11/4/15.
 */
public class TestActivity extends Activity {
    @Inject
    IBestGamesDao bestGamesDao;

    public void setUpTestInjection(SlideWaysInjectionModule testModule) {
        ((SlideWaysApp)getApplication()).overrideInjectionModule(testModule);
        ((SlideWaysApp)getApplication()).applicationComponent().inject(this);
    }

    public IBestGamesDao getBestGamesDao() {
        return bestGamesDao;
    }
}
