package rowley.slideways.data.dao;

/**
 * Created by jrowley on 11/4/15.
 */
public class TestDatabaseConfig extends BaseDatabaseConfig {
    @Override
    public String getDatabaseName() {
        return "SlideWaysTestData";
    }
}
