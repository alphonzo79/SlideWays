package rowley.slideways.data.entity;

/**
 * Created by jrowley on 11/5/15.
 */
public class LetterTile extends Tile {
    private int lastStableLeft;
    private int lastStableTop;
    private int desiredLeft;
    private int desiredTop;

    private char letter;
    private char letterDisplay;

    private final int PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO = 21;
    private int pixelsPerSecondToMove;
    private int xDiff;
    private int yDiff;
    private double vectorLength;
    private int xComponent;
    private int yComponent;
    private double overallVelocity;

    public void setHeight(int height) {
        super.setHeight(height);
        pixelsPerSecondToMove = height * PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO;
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

    public void setLastStablePosition(int left, int top) {
        lastStableLeft = left;
        lastStableTop = top;
    }

    public int getLastStableLeft() {
        return lastStableLeft;
    }

    public int getLastStableTop() {
        return lastStableTop;
    }

    public boolean progressTowardLastStablePosition(float portionOfSecondDelta) {
        return progressTowardPosition(lastStableLeft, lastStableTop, portionOfSecondDelta);
    }

    public void setDesiredPosition(int desiredLeft, int desiredTop) {
        this.desiredLeft = desiredLeft;
        this.desiredTop = desiredTop;
    }

    public int getDesiredLeft() {
        return desiredLeft;
    }

    public int getDesiredTop() {
        return desiredTop;
    }

    public boolean progressTowardDesiredPosition(float portionOfSecondDelta) {
        return progressTowardPosition(desiredLeft, desiredTop, portionOfSecondDelta);
    }

    private boolean progressTowardPosition(int targetLeft, int targetTop, float portionOfSecondDelta) {
        xDiff = targetLeft - left;
        yDiff = targetTop - top;

        //normalize the vector
        vectorLength = Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
        overallVelocity = portionOfSecondDelta * pixelsPerSecondToMove;
        xComponent = (int) ((xDiff / vectorLength) * overallVelocity);
        yComponent = (int) ((yDiff / vectorLength) * overallVelocity);

        left += xComponent;
        top += yComponent;

        //Reuse xComponent and yComponent now that we're done with them to compare and see if
        //we passed the target and need to reset either value. If we multiply the two diff values
        //and find something less than 0 then we have passed the target -- If we were negative, and
        // are still negative it will come out positive. If we were positive and still positive it
        //will come out positive. If we were negative and now positive (Or vice-versa) it will come
        // out negative. If we hit it right on one value will be 0
        xComponent = targetLeft - left;
        yComponent = targetTop - top;
        if (xComponent * xDiff <= 0) {
            left = targetLeft;
        }
        if (yComponent * yDiff <= 0) {
            top = targetTop;
        }

        return left == targetLeft && top == targetTop;
    }
}
