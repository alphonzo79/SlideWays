package rowley.slideways.data.dao;

import android.content.Context;

/**
 * Created by jrowley on 11/4/15.
 */
public class BestGamesDao extends DaoBase implements IBestGamesDao {
    public BestGamesDao(Context context, IDatabaseConfig config) {
        super(context, config);
    }
}
