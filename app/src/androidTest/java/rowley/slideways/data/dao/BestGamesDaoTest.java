package rowley.slideways.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import rowley.slideways.BaseActivityTest;
import rowley.slideways.data.entity.BestGame;
import rowley.slideways.data.entity.BestGameList;

/**
 * Created by jrowley on 11/4/15.
 */
public class BestGamesDaoTest extends BaseActivityTest {
    private IBestGamesDao dao;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dao = getActivity().getBestGamesDao();
    }

    @Override
    public void tearDown() throws Exception {
        SQLiteDatabase db = dao.getWritableDatabase();
        db.beginTransaction();
        try {
            int count = db.delete(dao.getTableName(), null, null);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        super.tearDown();
    }

    public void testSaveSingleGame() {
        BestGame game = new BestGame("TestUser", 56, System.currentTimeMillis());
        dao.insertBestGame(game);

        Cursor cursor = dao.getWritableDatabase().query(dao.getTableName(), null, null, null, null, null, null);
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        Assert.assertEquals(game.getUsername(), cursor.getString(1));
        Assert.assertEquals(game.getScore(), cursor.getInt(2));
        Assert.assertEquals(game.getTimestamp(), cursor.getLong(3));
    }

    public void testZeroMaxGamesOnList() {
        BestGameList zeroList = dao.getBestGamesDecendingOrder(0);
        Assert.assertNotNull(zeroList);
        Assert.assertNotNull(zeroList.getBestGames());
        Assert.assertEquals(0, zeroList.getBestGames().length);

        BestGameList tenList = dao.getBestGamesDecendingOrder(10);
        Assert.assertNotNull(tenList);
        Assert.assertNotNull(tenList.getBestGames());
        Assert.assertEquals(10, tenList.getBestGames().length);
    }

    public void testGetEmptyList() {
        BestGameList list = dao.getBestGamesDecendingOrder(10);
        Assert.assertNotNull(list);
        Assert.assertNotNull(list.getBestGames());
        Assert.assertEquals(10, list.getBestGames().length);
        for(BestGame game : list.getBestGames()) {
            Assert.assertNull(game);
        }
    }

    private List<BestGame> addABunchOfGames() {
        List<BestGame> result = new ArrayList<>(11);
        BestGame game = new BestGame("TestUser", 56, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 6, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 5, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 50, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 156, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 356, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 512, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 51, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 116, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 56, System.currentTimeMillis() - 2000000);
        dao.insertBestGame(game);
        result.add(game);
        game = new BestGame("TestUser", 560, System.currentTimeMillis());
        dao.insertBestGame(game);
        result.add(game);

        return result;
    }

    public void testBestGamesList() {
        addABunchOfGames();
        BestGameList list = dao.getBestGamesDecendingOrder(11);
        Assert.assertNotNull(list);
        Assert.assertNotNull(list.getBestGames());
        Assert.assertEquals(11, list.getBestGames().length);
        Assert.assertEquals(560, list.getBestGames()[0].getScore());
        Assert.assertEquals(512, list.getBestGames()[1].getScore());
        Assert.assertEquals(356, list.getBestGames()[2].getScore());
        Assert.assertEquals(156, list.getBestGames()[3].getScore());
        Assert.assertEquals(116, list.getBestGames()[4].getScore());
        Assert.assertEquals(56, list.getBestGames()[5].getScore());
        Assert.assertEquals(56, list.getBestGames()[6].getScore());
        Assert.assertTrue(list.getBestGames()[5].getTimestamp() < list.getBestGames()[6].getTimestamp());
        Assert.assertEquals(51, list.getBestGames()[7].getScore());
        Assert.assertEquals(50, list.getBestGames()[8].getScore());
        Assert.assertEquals(6, list.getBestGames()[9].getScore());
        Assert.assertEquals(5, list.getBestGames()[10].getScore());
    }

    public void testBestGamesListShort() {
        addABunchOfGames();
        BestGameList list = dao.getBestGamesDecendingOrder(3);
        Assert.assertNotNull(list);
        Assert.assertNotNull(list.getBestGames());
        Assert.assertEquals(3, list.getBestGames().length);
        Assert.assertEquals(560, list.getBestGames()[0].getScore());
        Assert.assertEquals(512, list.getBestGames()[1].getScore());
        Assert.assertEquals(356, list.getBestGames()[2].getScore());
    }

    public void testBestGamesListBestTieIsAtEnd() {
        List<BestGame> games = addABunchOfGames();
        BestGameList list = dao.getBestGamesDecendingOrder(6);
        Assert.assertNotNull(list);
        Assert.assertNotNull(list.getBestGames());
        Assert.assertEquals(6, list.getBestGames().length);
        Assert.assertEquals(560, list.getBestGames()[0].getScore());
        Assert.assertEquals(512, list.getBestGames()[1].getScore());
        Assert.assertEquals(356, list.getBestGames()[2].getScore());
        Assert.assertEquals(156, list.getBestGames()[3].getScore());
        Assert.assertEquals(116, list.getBestGames()[4].getScore());
        Assert.assertEquals(56, list.getBestGames()[5].getScore());
        Assert.assertEquals(games.get(9).getTimestamp(), list.getBestGames()[5].getTimestamp());
    }

    public void testBestGamesListExtraCapacity() {
        addABunchOfGames();
        BestGameList list = dao.getBestGamesDecendingOrder(25);
        Assert.assertNotNull(list);
        Assert.assertNotNull(list.getBestGames());
        Assert.assertEquals(25, list.getBestGames().length);
        Assert.assertNotNull(list.getBestGames()[0]);
        Assert.assertNotNull(list.getBestGames()[1]);
        Assert.assertNotNull(list.getBestGames()[2]);
        Assert.assertNotNull(list.getBestGames()[3]);
        Assert.assertNotNull(list.getBestGames()[4]);
        Assert.assertNotNull(list.getBestGames()[5]);
        Assert.assertNotNull(list.getBestGames()[6]);
        Assert.assertNotNull(list.getBestGames()[7]);
        Assert.assertNotNull(list.getBestGames()[8]);
        Assert.assertNotNull(list.getBestGames()[9]);
        Assert.assertNotNull(list.getBestGames()[10]);
        Assert.assertNull(list.getBestGames()[11]);
        Assert.assertNull(list.getBestGames()[12]);
        Assert.assertNull(list.getBestGames()[13]);
        Assert.assertNull(list.getBestGames()[14]);
        Assert.assertNull(list.getBestGames()[15]);
        Assert.assertNull(list.getBestGames()[16]);
        Assert.assertNull(list.getBestGames()[17]);
        Assert.assertNull(list.getBestGames()[18]);
        Assert.assertNull(list.getBestGames()[19]);
        Assert.assertNull(list.getBestGames()[20]);
        Assert.assertNull(list.getBestGames()[21]);
        Assert.assertNull(list.getBestGames()[22]);
        Assert.assertNull(list.getBestGames()[23]);
        Assert.assertNull(list.getBestGames()[24]);
    }
}
