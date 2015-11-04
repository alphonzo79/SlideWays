package rowley.slideways.data.entity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jrowley on 11/3/15.
 */
public class BestGameTest {
    @Test
    public void testGameCompare() {
        BestGame lowerScore = new BestGame("", 1, System.currentTimeMillis());
        BestGame higherScore = new BestGame("", 34, System.currentTimeMillis());
        BestGame higherNewer = new BestGame("", 34, System.currentTimeMillis() + 3);

        Assert.assertEquals(-1, higherScore.compareTo(lowerScore));
        Assert.assertEquals(1, lowerScore.compareTo(higherScore));
        Assert.assertEquals(1, higherNewer.compareTo(higherScore));
        Assert.assertEquals(-1, higherScore.compareTo(higherNewer));
        Assert.assertEquals(0, higherNewer.compareTo(higherNewer));
    }
}
