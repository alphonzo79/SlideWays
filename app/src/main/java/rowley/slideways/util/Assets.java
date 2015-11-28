package rowley.slideways.util;

import rowley.slideways.data.entity.BestGameList;
import rowley.wordtrie.WordTrie;

/**
 * Created by jrowley on 11/3/15.
 */
public class Assets {
    public static final int MAX_WORD_LENGTH = 15;
    public static final int LETTERS_PER_SCREEN = 7;
    public static WordTrie wordTrie;
    public static final int MAX_BEST_GAMES = 12;
    public static BestGameList bestGameList;
    public static boolean isGameInProgress = false;
    public static LetterManager letterManager;
    public static WordScorer wordScorer;

    public static final int PADDING_BASE = 20;
    public static int padding;

    public static final int PLACEHOLDER_TILE_BACKGROUND_COLOR = 0x55ffffff;

    public static final int LABEL_TEXT_SIZE_BASE = 14;
    public static int labelTextSize;

    public static final int TIME_TEXT_SIZE_BASE = 24;
    public static int timeTextSize;

    public static final int SCORE_TEXT_SIZE_BASE = 24;
    public static int scoreTextSize;

    public static final int TRANSLUCENT_WHITE = 0xaaffffff;
}
