package rowley.slideways.data.entity;

import android.text.TextUtils;

/**
 * Created by jrowley on 11/3/15.
 */
public class BestGame implements Comparable {
    private String username;
    private int score;
    private long timestamp;

    public BestGame() {

    }

    public BestGame(String username, int score, long timestamp) {
        this.username = username;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Object another) {
        if(!(another instanceof BestGame)) {
            throw new IllegalArgumentException("Not an instance of BestGame");
        }

        if(score > ((BestGame) another).score) {
            return -1;
        }
        if(score < ((BestGame) another).score) {
            return 1;
        }

        //Score is equal, so Oldest takes precedence
        if(timestamp < ((BestGame) another).timestamp) {
            return -1;
        }
        if(timestamp > ((BestGame) another).timestamp) {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object another) {
        if(!(another instanceof BestGame)) {
            return false;
        }

        if(username != null && !(username.equals(((BestGame) another).username))) {
            return false;
        }
        if(username == null && ((BestGame) another).username != null) {
            return false;
        }

        if(score != ((BestGame) another).score) {
            return false;
        }

        if(timestamp != ((BestGame) another).timestamp) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 35;

        if(username != null) {
            hashCode += 35 * username.hashCode();
        }
        hashCode += 35 * score;
        hashCode += 35 * timestamp;

        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(username != null) {
            builder.append(username);
        }
        builder.append(" scored ").append(score).append(" at ").append(timestamp);
        return builder.toString();
    }
}
