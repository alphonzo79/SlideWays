package rowley.slideways;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import rowley.slideways.activity.TestActivity;
import rowley.slideways.di.SlideWaysInjectionModule;
import rowley.slideways.di.SlideWaysTestInjectionModule;

/**
 * Created by jrowley on 11/4/15.
 */
public abstract class BaseActivityTest extends ActivityInstrumentationTestCase2<TestActivity> {
    public BaseActivityTest() {
        super(TestActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity().setUpTestInjection(new SlideWaysTestInjectionModule(getActivity()));
    }
}
