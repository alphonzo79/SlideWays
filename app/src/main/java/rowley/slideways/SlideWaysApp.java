package rowley.slideways;

import android.app.Application;
import android.util.Log;

import javax.inject.Singleton;

import dagger.Component;
import rowley.slideways.activity.TestActivity;
import rowley.slideways.di.SlideWaysInjectionModule;
import rowley.slideways.screens.GameScreen;
import rowley.slideways.screens.LoadingScreen;
import rowley.slideways.screens.game_screen_sections.SlidingLetterRail;

/**
 * Created by jrowley on 11/4/15.
 */
public class SlideWaysApp extends Application {
    @Singleton
    @Component(modules = SlideWaysInjectionModule.class)
    public interface ApplicationComponent {
        void inject(LoadingScreen loadingScreen);
        void inject(TestActivity testActivity);
    }

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerSlideWaysApp_ApplicationComponent.builder().slideWaysInjectionModule(new SlideWaysInjectionModule(this)).build();
    }

    public ApplicationComponent applicationComponent() {
        return applicationComponent;
    }

    public void overrideInjectionModule(SlideWaysInjectionModule module) {
        applicationComponent = DaggerSlideWaysApp_ApplicationComponent.builder().slideWaysInjectionModule(module).build();
    }
}
