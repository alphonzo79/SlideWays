package rowley.slideways.data.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import rowley.slideways.R;

/**
 * Created by jrowley on 11/3/15.
 */
public class DaoBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "SlideWaysData";
    private static final int VERSION = 1;
    private final Context slidewaysContext;

    public DaoBase(Context context) {
        super(context, DB_NAME, null, VERSION);
        slidewaysContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();

        try {
            sqLiteDatabase.execSQL(slidewaysContext.getString(R.string.build_best_games_table));
            sqLiteDatabase.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
