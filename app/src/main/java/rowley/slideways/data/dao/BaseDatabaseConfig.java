package rowley.slideways.data.dao;

/**
 * Created by jrowley on 11/4/15.
 */
public abstract class BaseDatabaseConfig implements IDatabaseConfig {
    @Override
    public int getDatabaseVersion() {
        return 1;
    }
}
