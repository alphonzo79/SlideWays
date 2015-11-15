package rowley.slideways.screens;

import rowley.slideways.data.entity.LetterTile;

/**
 * Created by joe on 11/13/15.
 */
public interface DetachedTileMonitor {
    public void monitorDetachedTilePosition(LetterTile tile);
    public void onDetachedTileAcceptedByOther();
}
