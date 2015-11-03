package rowley.slideways.util;

/**
 * Created by jrowley on 11/3/15.
 */
public class FrameRateTracker {
    private float[] deltaTimes;
    private int index;

    private int frameRate;
    private float deltaAccumulator;
    private int nonZeros;

    public FrameRateTracker() {
        deltaTimes = new float[100];
        index = 0;
    }

    public void update(float portionOfSecond) {
        deltaTimes[index++] = portionOfSecond;
        if(index >= deltaTimes.length) {
            index = 0;
        }
    }

    public int getFrameRate() {
        frameRate = 0;
        deltaAccumulator = 0;
        nonZeros = 0;

        for(int i = 0; i < deltaTimes.length; i++) {
            if(deltaTimes[i] > 0) {
                deltaAccumulator += deltaTimes[i];
                nonZeros++;
            }
        }

        if(deltaAccumulator > 0 && nonZeros > 0) {
            frameRate = (int) (nonZeros / deltaAccumulator);
        }

        return frameRate;
    }
}
