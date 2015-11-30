package rowley.slideways.data.entity;

/**
 * Created by jrowley on 11/5/15.
 */
public class LetterTile extends MovableEntity {

    private char letter;
    private char letterDisplay;

    public void setHeight(int height) {
        super.setHeight(height);
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
        this.letterDisplay = Character.toUpperCase(letter);
    }

    public char getLetterDisplay() {
        return letterDisplay;
    }

    public void detachFromStablePosition(int newLeft, int newTop) {
        setLastStablePosition(this.left, this.top);

        this.left = newLeft;
        this.top = newTop;
    }
}
