package rowley.slideways.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jrowley on 11/5/15.
 */
public class EnglishLetterManagerTest {
    @Test
    public void testNewLetterFrequency() {
        Map<Character, Integer> lettersReturned = new HashMap<>(26);
        EnglishLetterManager manager = new EnglishLetterManager();
        manager.initialize();
        for(int i = 0; i < 100000; i++) {
            char letter = manager.getNextLetter();
            if(!lettersReturned.containsKey(letter)) {
                lettersReturned.put(letter, 0);
            }
            lettersReturned.put(letter, lettersReturned.get(letter) + 1);
        }

        //Base frequency expectations on https://en.wikipedia.org/wiki/Letter_frequency
        //Look for something withing +/- 1 percentage point
        Assert.assertTrue(lettersReturned.containsKey('a'));
        Assert.assertTrue("found " + lettersReturned.get('a'), lettersReturned.get('a') > 7167 && lettersReturned.get('a') < 9167);

        Assert.assertTrue(lettersReturned.containsKey('b'));
        Assert.assertTrue("found " + lettersReturned.get('b'), lettersReturned.get('b') > 492 && lettersReturned.get('b') < 2492);

        Assert.assertTrue(lettersReturned.containsKey('c'));
        Assert.assertTrue("found " + lettersReturned.get('c'), lettersReturned.get('c') > 1782 && lettersReturned.get('c') < 3782);

        Assert.assertTrue(lettersReturned.containsKey('d'));
        Assert.assertTrue("found " + lettersReturned.get('d'), lettersReturned.get('d') > 3253 && lettersReturned.get('d') < 5253);

        Assert.assertTrue(lettersReturned.containsKey('e'));
        Assert.assertTrue("found " + lettersReturned.get('e'), lettersReturned.get('e') > 11702 && lettersReturned.get('e') < 13702);

        Assert.assertTrue(lettersReturned.containsKey('f'));
        Assert.assertTrue("found " + lettersReturned.get('f'), lettersReturned.get('f') > 1228 && lettersReturned.get('f') < 3228);

        Assert.assertTrue(lettersReturned.containsKey('g'));
        Assert.assertTrue("found " + lettersReturned.get('g'), lettersReturned.get('g') > 1015 && lettersReturned.get('g') < 3015);

        Assert.assertTrue(lettersReturned.containsKey('h'));
        Assert.assertTrue("found " + lettersReturned.get('h'), lettersReturned.get('h') > 5094 && lettersReturned.get('h') < 7094);

        Assert.assertTrue(lettersReturned.containsKey('i'));
        Assert.assertTrue("found " + lettersReturned.get('i'), lettersReturned.get('i') > 5966 && lettersReturned.get('i') < 7966);

        Assert.assertTrue(lettersReturned.containsKey('j'));
        Assert.assertTrue("found " + lettersReturned.get('j'), lettersReturned.get('j') > 0 && lettersReturned.get('j') < 1153);

        Assert.assertTrue(lettersReturned.containsKey('k'));
        Assert.assertTrue("found " + lettersReturned.get('k'), lettersReturned.get('k') > 0 && lettersReturned.get('k') < 1772);

        Assert.assertTrue(lettersReturned.containsKey('l'));
        Assert.assertTrue("found " + lettersReturned.get('l'), lettersReturned.get('l') > 3025 && lettersReturned.get('l') < 5025);

        Assert.assertTrue(lettersReturned.containsKey('m'));
        Assert.assertTrue("found " + lettersReturned.get('m'), lettersReturned.get('m') > 1406 && lettersReturned.get('m') < 3406);

        Assert.assertTrue(lettersReturned.containsKey('n'));
        Assert.assertTrue("found " + lettersReturned.get('n'), lettersReturned.get('n') > 5749 && lettersReturned.get('n') < 7749);

        Assert.assertTrue(lettersReturned.containsKey('o'));
        Assert.assertTrue("found " + lettersReturned.get('o'), lettersReturned.get('o') > 6507 && lettersReturned.get('o') < 8507);

        Assert.assertTrue(lettersReturned.containsKey('p'));
        Assert.assertTrue("found " + lettersReturned.get('p'), lettersReturned.get('p') > 929 && lettersReturned.get('p') < 2929);

        Assert.assertTrue(lettersReturned.containsKey('q'));
        Assert.assertTrue("found " + lettersReturned.get('q'), lettersReturned.get('q') > 0 && lettersReturned.get('q') < 1095);

        Assert.assertTrue(lettersReturned.containsKey('r'));
        Assert.assertTrue("found " + lettersReturned.get('r'), lettersReturned.get('r') > 4987 && lettersReturned.get('r') < 6987);

        Assert.assertTrue(lettersReturned.containsKey('s'));
        Assert.assertTrue("found " + lettersReturned.get('s'), lettersReturned.get('s') > 5327 && lettersReturned.get('s') < 7327);

        Assert.assertTrue(lettersReturned.containsKey('t'));
        Assert.assertTrue("found " + lettersReturned.get('t'), lettersReturned.get('t') > 8056 && lettersReturned.get('t') < 10056);

        Assert.assertTrue(lettersReturned.containsKey('u'));
        Assert.assertTrue("found " + lettersReturned.get('u'), lettersReturned.get('u') > 1758 && lettersReturned.get('u') < 3758);

        Assert.assertTrue(lettersReturned.containsKey('v'));
        Assert.assertTrue("found " + lettersReturned.get('v'), lettersReturned.get('v') > 0 && lettersReturned.get('v') < 1978);

        Assert.assertTrue(lettersReturned.containsKey('w'));
        Assert.assertTrue("found " + lettersReturned.get('w'), lettersReturned.get('w') > 1361 && lettersReturned.get('w') < 3361);

        Assert.assertTrue(lettersReturned.containsKey('x'));
        Assert.assertTrue("found " + lettersReturned.get('x'), lettersReturned.get('x') > 0 && lettersReturned.get('x') < 1150);

        Assert.assertTrue(lettersReturned.containsKey('y'));
        Assert.assertTrue("found " + lettersReturned.get('y'), lettersReturned.get('y') > 974 && lettersReturned.get('y') < 2974);

        Assert.assertTrue(lettersReturned.containsKey('z'));
        Assert.assertTrue("found " + lettersReturned.get('z'), lettersReturned.get('z') > 0 && lettersReturned.get('z') < 1074);
    }

    @Test
    public void testLetterScoring() {
        EnglishLetterManager manager = new EnglishLetterManager();
        manager.initialize();

        Assert.assertEquals(5, manager.getScoreForLetter('a'));
        Assert.assertEquals(12, manager.getScoreForLetter('b'));
        Assert.assertEquals(11, manager.getScoreForLetter('c'));
        Assert.assertEquals(9, manager.getScoreForLetter('d'));
        Assert.assertEquals(1, manager.getScoreForLetter('e'));
        Assert.assertEquals(11, manager.getScoreForLetter('f'));
        Assert.assertEquals(11, manager.getScoreForLetter('g'));
        Assert.assertEquals(7, manager.getScoreForLetter('h'));
        Assert.assertEquals(7, manager.getScoreForLetter('i'));
        Assert.assertEquals(12, manager.getScoreForLetter('j'));
        Assert.assertEquals(12, manager.getScoreForLetter('k'));
        Assert.assertEquals(9, manager.getScoreForLetter('l'));
        Assert.assertEquals(11, manager.getScoreForLetter('m'));
        Assert.assertEquals(7, manager.getScoreForLetter('n'));
        Assert.assertEquals(6, manager.getScoreForLetter('o'));
        Assert.assertEquals(12, manager.getScoreForLetter('p'));
        Assert.assertEquals(13, manager.getScoreForLetter('q'));
        Assert.assertEquals(8, manager.getScoreForLetter('r'));
        Assert.assertEquals(7, manager.getScoreForLetter('s'));
        Assert.assertEquals(4, manager.getScoreForLetter('t'));
        Assert.assertEquals(11, manager.getScoreForLetter('u'));
        Assert.assertEquals(12, manager.getScoreForLetter('v'));
        Assert.assertEquals(11, manager.getScoreForLetter('w'));
        Assert.assertEquals(12, manager.getScoreForLetter('x'));
        Assert.assertEquals(12, manager.getScoreForLetter('y'));
        Assert.assertEquals(13, manager.getScoreForLetter('z'));
    }
}
