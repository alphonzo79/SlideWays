package rowley.slideways.util;

/**
 * Created by jrowley on 11/5/15.
 */
public class EnglishWordScorer implements WordScorer {
    private LetterManager letterManager;

    public EnglishWordScorer(LetterManager letterManager) {
        this.letterManager = letterManager;
    }

    @Override
    public int getScoreForWord(char[] word) {
        int accumulator = 0;
        for(char character : word) {
            accumulator += letterManager.getScoreForLetter(character);
        }
        return (int) (accumulator * getMultiplierForWordLength(word.length));
    }

    private float getMultiplierForWordLength(int wordLength) {
        if(wordLength > 3) {
            return 1 + ((wordLength - 3) * 0.25f);
        }
        return 1;
    }
}
