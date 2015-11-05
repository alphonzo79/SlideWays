package rowley.slideways.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rowley.slideways.SlideWaysApp;
import rowley.slideways.data.dao.BestGamesDao;
import rowley.slideways.data.dao.IBestGamesDao;
import rowley.slideways.data.dao.IDatabaseConfig;
import rowley.slideways.data.dao.ReleaseDatabaseConfig;
import rowley.slideways.util.EnglishLetterManager;
import rowley.slideways.util.EnglishWordScorer;
import rowley.slideways.util.LetterManager;
import rowley.slideways.util.WordScorer;

/**
 * Created by jrowley on 11/4/15.
 */
@Module
public class SlideWaysInjectionModule {
    private final Context context;

    public SlideWaysInjectionModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    IDatabaseConfig provideDatabaseconfig() {
        return new ReleaseDatabaseConfig();
    }

    @Provides
    @Singleton
    IBestGamesDao provideBestGamesDao(IDatabaseConfig config) {
        return new BestGamesDao(context, config);
    }

    @Provides
    @Singleton
    LetterManager provideLetterManager() {
        return new EnglishLetterManager();
    }

    @Provides
    @Singleton
    WordScorer provideWordScorer(LetterManager letterManager) {
        return new EnglishWordScorer(letterManager);
    }
}
