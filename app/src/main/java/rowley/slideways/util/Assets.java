package rowley.slideways.util;

import rowley.slideways.data.entity.BestGameList;
import rowley.wordtrie.WordTrie;

/**
 * Created by jrowley on 11/3/15.
 */
public class Assets {
    public static final int MAX_WORD_LENGTH = 9;
    public static WordTrie wordTrie;
    public static final int MAX_BEST_GAMES = 12;
    public static BestGameList bestGameList;
    public static boolean isGameInProgress = false;
}
