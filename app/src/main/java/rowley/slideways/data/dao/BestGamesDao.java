package rowley.slideways.data.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import rowley.slideways.data.entity.BestGame;
import rowley.slideways.data.entity.BestGameList;

/**
 * Created by jrowley on 11/4/15.
 */
public class BestGamesDao extends DaoBase implements IBestGamesDao {
    public static final String TABLE_NAME = "best_games";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "user_name";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_SCORE = "score";

    public BestGamesDao(Context context, IDatabaseConfig config) {
        super(context, config);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public boolean insertBestGame(BestGame bestGame) {
        boolean success = false;

        SQLiteDatabase db = getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement(String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?);", TABLE_NAME, COLUMN_USERNAME, COLUMN_SCORE, COLUMN_TIMESTAMP));
        stmt.bindString(1, bestGame.getUsername());
        stmt.bindLong(2, bestGame.getScore());
        stmt.bindLong(3, bestGame.getTimestamp());

        db.beginTransaction();
        try {
            stmt.execute();
            db.setTransactionSuccessful();
            success = true;
        }
        catch (SQLiteException ex) {
            ex.printStackTrace();
        }
        finally {
            db.endTransaction();
            stmt.close();
            db.close();
        }

        return success;
    }

    public BestGameList getBestGamesDecendingOrder(int maxGames) {
        BestGameList result = new BestGameList(maxGames);

        SQLiteDatabase db = getReadableDatabase();

        //Our real ordering is based on a combination of score and time to determine real precedence
        //Since we'll just be pulling from the DB based on score and letting the list Add method do the
        //rest of the sorting, let's pull some extra to make sure that if there are ties we really get
        //the top scores.
        //We know that doing it this way sacrifices a bit of performance, but we also expect this method
        //to be called on a background thread, and in reality during a loading initialization, so
        //we're willing to make this tradeoff.
        String limit = String.valueOf(maxGames * 2);
        String order = " DESC";

        String[] columns = new String[]{COLUMN_USERNAME, COLUMN_SCORE, COLUMN_TIMESTAMP};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, COLUMN_SCORE + order, limit);
        if(cursor != null && cursor.getCount() > 0) {
            try {
                while(cursor.moveToNext()) {
                    BestGame game = new BestGame(cursor.getString(0), cursor.getInt(1), cursor.getLong(2));
                    result.addBestGame(game);
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        db.close();

        return result;
    }
}
