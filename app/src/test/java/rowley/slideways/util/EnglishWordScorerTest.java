package rowley.slideways.util;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by jrowley on 11/5/15.
 */
public class EnglishWordScorerTest {
    @Test
    public void testScoreWords() {
        LetterManager manager = new EnglishLetterManager();
        manager.initialize();
        EnglishWordScorer scorer = new EnglishWordScorer(manager);

        Assert.assertEquals(48, scorer.getScoreForWord(new char[]{'h', 'e', 'l', 'l', 'o'}));
        Assert.assertEquals(32, scorer.getScoreForWord(new char[]{'h', 'e', 'l', 'l'}));
        Assert.assertEquals(49, scorer.getScoreForWord(new char[]{'q', 'u', 'e', 'e', 'n'}));
        Assert.assertEquals(70, scorer.getScoreForWord(new char[]{'q', 'u', 'e', 'e', 'n', 's'}));
        Assert.assertEquals(5, scorer.getScoreForWord(new char[]{'a'}));
        Assert.assertEquals(12, scorer.getScoreForWord(new char[]{'a', 's'}));
        Assert.assertEquals(23, scorer.getScoreForWord(new char[]{'g', 'a', 's'}));
        Assert.assertEquals(206, scorer.getScoreForWord(new char[]{'m', 'a', 's', 'a', 'g', 'a', 's', 'c', 'a', 'r'}));
    }
}
