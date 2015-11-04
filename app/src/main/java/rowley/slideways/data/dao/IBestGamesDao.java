package rowley.slideways.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import rowley.slideways.data.entity.BestGame;
import rowley.slideways.data.entity.BestGameList;

/**
 * Created by jrowley on 11/4/15.
 */
public interface IBestGamesDao extends IBaseDao {
    public String getTableName();
    public boolean insertBestGame(BestGame bestGame);
    public BestGameList getBestGamesDecendingOrder(int maxGames);
}
