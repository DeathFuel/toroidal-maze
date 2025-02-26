package trmz;

public class Globals {
    /* Quick reference for how globalScale maps to different resolutions
      1    -> 1280x720
      1.25 -> 1600x900
      1.5  -> 1920x1080
      2    -> 2560x1440
      3    -> 3840x2160
    */
    public static final String windowTitle = "toroidal-maze";
    public static final boolean fullscreen = false;
    public static final double globalScale = 1.25;
    public static final int levelTileSize = 47;

    public static final int windowX = (int) (1280 * globalScale);
    public static final int windowY = windowX * 9 / 16;
}
