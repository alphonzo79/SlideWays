package rowley.slideways.data.entity;

/**
 * Created by jrowley on 11/3/15.
 */
public class BestGameList {
    private BestGame[] bestGames;

    public BestGameList(int capacity) {
        bestGames = new BestGame[capacity];
    }

    public void addBestGame(BestGame game) {
        int targetSlot = -1;
        for(int i = 0; i < bestGames.length; i++) {
            if(bestGames[i] == null) {
                targetSlot = i;
                break;
            }

            //Don't add the same one twice
            if(game.equals(bestGames[i])) {
                break;
            }

            int compared = game.compareTo(bestGames[i]);
            if(compared <= 0) {
                targetSlot = i;
                break;
            }
        }

        if(targetSlot >= 0) {
            for(int i = bestGames.length - 1; i > targetSlot; i--) {
                bestGames[i] = bestGames[i - 1];
            }
            bestGames[targetSlot] = game;
        }
    }

    public BestGame[] getBestGames() {
        return bestGames;
    }
}
