package rowley.slideways.util;

/**
 * Created by jrowley on 11/5/15.
 */
public interface LetterManager {
    public void initialize();
    public char getNextLetter();
    public int getScoreForLetter(char letter);
}
