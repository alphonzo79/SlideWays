package rowley.slideways.di;

import javax.inject.Singleton;

import dagger.Provides;
import rowley.slideways.SlideWaysApp;
import rowley.slideways.data.dao.IDatabaseConfig;
import rowley.slideways.data.dao.ReleaseDatabaseConfig;
import rowley.slideways.data.dao.TestDatabaseConfig;

/**
 * Created by jrowley on 11/4/15.
 */
public class SlideWaysTestInjectionModule extends SlideWaysInjectionModule {
    public SlideWaysTestInjectionModule(SlideWaysApp app) {
        super(app);
    }

    @Override
    @Provides
    @Singleton
    IDatabaseConfig provideDatabaseconfig() {
        return new TestDatabaseConfig();
    }
}
