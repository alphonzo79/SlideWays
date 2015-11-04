package rowley.slideways;

import android.test.ActivityInstrumentationTestCase2;

import rowley.slideways.activity.TestActivity;
import rowley.slideways.di.SlideWaysInjectionModule;

/**
 * Created by jrowley on 11/4/15.
 */
public abstract class BaseActivityTest extends ActivityInstrumentationTestCase2<TestActivity> {
    public BaseActivityTest() {
        super(TestActivity.class);
        getActivity().setUpTestInjection(new SlideWaysInjectionModule(getActivity()));
    }
}
