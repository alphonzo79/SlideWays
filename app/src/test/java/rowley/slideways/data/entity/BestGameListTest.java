package rowley.slideways.data.entity;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by jrowley on 11/3/15.
 */
public class BestGameListTest {

    @Test
    public void testOrderOfGames() {
        BestGame lowerScore = new BestGame("", 1, System.currentTimeMillis());
        BestGame higherScore = new BestGame("", 34, System.currentTimeMillis());
        BestGame higherNewer = new BestGame("", 34, System.currentTimeMillis() + 3);

        BestGameList list = new BestGameList(3);
        list.addBestGame(lowerScore);
        Assert.assertNotNull(list.getBestGames()[0]);
        Assert.assertNull(list.getBestGames()[1]);

        list.addBestGame(higherScore);
        Assert.assertEquals(higherScore, list.getBestGames()[0]);
        Assert.assertEquals(lowerScore, list.getBestGames()[1]);
        Assert.assertNull(list.getBestGames()[2]);

        list.addBestGame(higherNewer);
        Assert.assertEquals(higherScore, list.getBestGames()[0]);
        Assert.assertEquals(higherNewer, list.getBestGames()[1]);
        Assert.assertEquals(lowerScore, list.getBestGames()[2]);
    }

    @Test
    public void testExtraCapacity() {
        BestGame lowerScore = new BestGame("", 1, System.currentTimeMillis());
        BestGame higherScore = new BestGame("", 34, System.currentTimeMillis());
        BestGame higherNewer = new BestGame("", 34, System.currentTimeMillis() + 3);

        BestGameList list = new BestGameList(4);
        list.addBestGame(lowerScore);
        list.addBestGame(higherScore);
        list.addBestGame(higherNewer);

        Assert.assertEquals(higherScore, list.getBestGames()[0]);
        Assert.assertEquals(higherNewer, list.getBestGames()[1]);
        Assert.assertEquals(lowerScore, list.getBestGames()[2]);
        Assert.assertNull(list.getBestGames()[3]);
    }

    @Test
    public void testMoreThanCapacity() {
        BestGame lowerScore = new BestGame("", 1, System.currentTimeMillis());
        BestGame higherScore = new BestGame("", 34, System.currentTimeMillis());
        BestGame higherNewer = new BestGame("", 34, System.currentTimeMillis() + 3);

        BestGameList list = new BestGameList(2);
        list.addBestGame(lowerScore);
        list.addBestGame(higherScore);
        list.addBestGame(higherNewer);

        Assert.assertEquals(higherScore, list.getBestGames()[0]);
        Assert.assertEquals(higherNewer, list.getBestGames()[1]);
    }

    @Test
    public void testSameAddedTwice() {
        BestGame lowerScore = new BestGame("", 1, System.currentTimeMillis());
        BestGame higherScore = new BestGame("", 34, System.currentTimeMillis());
        BestGame higherNewer = new BestGame("", 34, System.currentTimeMillis() + 3);

        BestGameList list = new BestGameList(4);
        list.addBestGame(lowerScore);
        list.addBestGame(higherScore);
        list.addBestGame(lowerScore);

        Assert.assertEquals(higherScore, list.getBestGames()[0]);
        Assert.assertEquals(lowerScore, list.getBestGames()[1]);
        Assert.assertNull(list.getBestGames()[2]);
        Assert.assertNull(list.getBestGames()[3]);
    }
}
