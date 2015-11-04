package rowley.slideways.data.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jrowley on 11/4/15.
 */
public interface IBaseDao {
    public SQLiteDatabase getWritableDatabase();
}
