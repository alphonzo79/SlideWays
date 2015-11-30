package rowley.slideways.data.entity;

import android.util.Log;

/**
 * Created by joe on 11/26/15.
 */
public class MovableEntity {
    protected int left;
    protected int top;
    protected int width;
    protected int height;

    private int lastStableLeft;
    private int lastStableTop;
    private int desiredLeft;
    private int desiredTop;

    public static final int PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO = 21;
    private int pixelsPerSecondToMove;
    private int xDiff;
    private int yDiff;
    private double vectorLength;
    private int xComponent;
    private int yComponent;
    private double overallVelocity;

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
        pixelsPerSecondToMove = height * PROGRESS_PIXELS_PER_SECOND_TO_HEIGHT_RATIO;
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

    public void overrideProgressPixelsPerSecondToHeightRatio(int pixelsPerSecondPerHeight) {
        pixelsPerSecondToMove = height * pixelsPerSecondPerHeight;
    }
}
