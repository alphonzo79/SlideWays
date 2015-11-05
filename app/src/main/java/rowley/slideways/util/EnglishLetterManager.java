package rowley.slideways.util;

import java.util.Random;

/**
 * Created by jrowley on 11/5/15.
 */
public class EnglishLetterManager implements LetterManager {
    private Random random;
    private boolean initialized = false;

    @Override
    public void initialize() {
        random = new Random(System.currentTimeMillis());
        initialized = true;
    }

    @Override
    public char getNextLetter() {
        if(!initialized) {
            throw new IllegalStateException("Must initialize before using");
        }
        return getLetterForNumber(random.nextInt(100000));
    }

    private char getLetterForNumber(int number) {
        //https://en.wikipedia.org/wiki/Letter_frequency
        if(number < 8166) {
            return 'a';
        } else if(number < 9658) {
            return 'b';
        } else if(number < 12440) {
            return 'c';
        } else if(number < 16693) {
            return 'd';
        } else if(number < 29395) {
            return 'e';
        } else if(number < 31623) {
            return 'f';
        } else if(number < 33638) {
            return 'g';
        } else if(number < 39732) {
            return 'h';
        } else if(number < 46698) {
            return 'i';
        } else if(number < 46851) {
            return 'j';
        } else if(number < 47623) {
            return 'k';
        } else if(number < 51648) {
            return 'l';
        } else if(number < 54054) {
            return 'm';
        } else if(number < 60803) {
            return 'n';
        } else if(number < 68310) {
            return 'o';
        } else if(number < 70239) {
            return 'p';
        } else if(number < 70334) {
            return 'q';
        } else if(number < 76321) {
            return 'r';
        } else if(number < 82648) {
            return 's';
        } else if(number < 91704) {
            return 't';
        } else if(number < 94462) {
            return 'u';
        } else if(number < 95440) {
            return 'v';
        } else if(number < 97801) {
            return 'w';
        } else if(number < 97951) {
            return 'x';
        } else if(number < 99925) {
            return 'y';
        } else if(number < 99999) {
            return 'z';
        }

        return 'e';
    }

    private final int SCORE_BASE_VALUE = 13;
    @Override
    public int getScoreForLetter(char letter) {
        if(!initialized) {
            throw new IllegalStateException("Must initialize before using");
        }
        letter = Character.toLowerCase(letter);
        return SCORE_BASE_VALUE - getScoreOffsetForLetter(letter);
    }

    private int getScoreOffsetForLetter(char letter) {
        //Based on the whole number portion of a letter's frequency percentage found at
        //https://en.wikipedia.org/wiki/Letter_frequency
        //except that all but the very smallest are at least 1 (only return 0 if the % is less
        // than 0.0...
        switch (letter) {
            case 'a':
                return 8;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 4;
            case 'e':
                return 12;
            case 'f':
                return 2;
            case 'g':
                return 2;
            case 'h':
                return 6;
            case 'i':
                return 6;
            case 'j':
                return 1;
            case 'k':
                return 1;
            case 'l':
                return 4;
            case 'm':
                return 2;
            case 'n':
                return 6;
            case 'o':
                return 7;
            case 'p':
                return 1;
            case 'q':
                return 0;
            case 'r':
                return 5;
            case 's':
                return 6;
            case 't':
                return 9;
            case 'u':
                return 2;
            case 'v':
                return 1;
            case 'w':
                return 2;
            case 'x':
                return 1;
            case 'y':
                return 1;
            case 'z':
                return 0;
            default:
                //Just go half
                return 6;
        }
    }
}
