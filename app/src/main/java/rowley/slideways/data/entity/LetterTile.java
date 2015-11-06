package rowley.slideways.data.entity;

/**
 * Created by jrowley on 11/5/15.
 */
public class LetterTile {
    private int left;
    private int top;
    private int width;
    private int height;

    private char letter;
    private char letterDisplay;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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
}
