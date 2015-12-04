package rowley.slideways.data.entity;

/**
 * Created by joe on 11/29/15.
 */
public class WordScore extends MovableEntity {
    private int score;
    private String scoreString;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getScoreString() {
        return scoreString;
    }

    public void setScoreString(String scoreString) {
        this.scoreString = scoreString;
    }
}
