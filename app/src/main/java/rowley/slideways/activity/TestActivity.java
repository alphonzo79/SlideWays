package rowley.slideways.activity;

import android.app.Activity;

import rowley.slideways.SlideWaysApp;
import rowley.slideways.di.SlideWaysInjectionModule;

/**
 * Created by jrowley on 11/4/15.
 */
public class TestActivity extends Activity {

    public void setUpTestInjection(SlideWaysInjectionModule testModule) {
        ((SlideWaysApp)getApplication()).overrideInjectionModule(testModule);
        ((SlideWaysApp)getApplication()).applicationComponent().inject(this);
    }
}
