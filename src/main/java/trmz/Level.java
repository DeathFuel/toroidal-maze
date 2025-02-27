package trmz;

import trmz.components.*;
import trmz.renderer.Render;
import trmz.renderer.SpriteManager;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// A class that's in charge of the game board; it loads levels, places objects and controls the player position.
public class Level {
    private static Player player = null;
    private static int playerTileX = 0, playerTileY = 0;
    private static int exitX = 0, exitY = 0;
    private static final GameObject world = new GameObject();
    private static final ArrayList<Sprite> tiles = new ArrayList<>();
    private static String currentLevel = null;
    private static List<String> levels; // Holds level strings loaded from a file.
    private static int levelIndex = 0;

    private static final int tileSize = Globals.levelTileSize;
    public static int getTilesH() { return 1280 / tileSize; }
    public static int getTilesV() { return 720 / tileSize; }
    public static double getMarginH() { return (1280 - tileSize * getTilesH()) / 2.0; }
    public static double getMarginV() { return (720  - tileSize * getTilesV()) / 2.0; }

    public static void init() {
        // This GameObject acts as a parent for the entire level, centering its objects wrt the display (position is cumulatively inherited)
        world.setPosition(
                Globals.globalScale * getMarginH(),
                Globals.globalScale * getMarginV()
        );

        // Pre-generate walls
        for (int row = 0; row < getTilesV(); row++) {
            for (int col = 0; col < getTilesH(); col++) {
                Sprite s = new SpriteManager.Builder("wall")
                        .setSize(tileSize * Globals.globalScale)
                        .setPosition(
                                col * tileSize * Globals.globalScale,
                                row * tileSize * Globals.globalScale
                        )
                        .setZ(0)
                        .build();
                s.setParent(world);
                s.enabled = false;
                tiles.add(s);
            }
        }

        player = new Player(0, 0, tileSize);
        player.playerObject.setParent(world);
        levels = loadLevelArray("res/levels.txt");
        loadLevel(levelIndex);
    }

    public static void loadLevel(int index) {
        player.forceStop();
        try {
            createLevel(levels.get(index));
        } catch (IndexOutOfBoundsException e) {
            createLevel("");
        } catch (Exception e) {
            throw new RuntimeException("Could not load level %d:\n%s".formatted(index, e));
        }
    }

    public static List<String> loadLevelArray(String path) {
        File f = new File(path);
        List<String> strings;
        try {
            strings = Files.readAllLines(f.toPath());
        } catch (Exception e) {
            System.err.println("Failed to load " + path);
            throw new RuntimeException(e);
        }
        return strings;
    }

    public static void reloadLevel() {
        loadLevel(levelIndex);
    }

    public static void loadAdjacentLevel(int delta) {
        levelIndex += delta;
        loadLevel(levelIndex);
    }

    public static void createLevel(String level) { // Turns level layout strings into levels
        // Ensure the level string has correct size. Trim or pad if necessary.
        int levelSize = getTilesH() * getTilesV();
        if (level.length() < levelSize) {
            level += "#".repeat(levelSize - level.length());
        } else if (level.length() > levelSize) {
            level = level.substring(0, levelSize);
        }
        // Parse level string
        for (int row = 0; row < getTilesV(); row++) {
            for (int col = 0; col < getTilesH(); col++) {
                int tilePos = row * getTilesH() + col;
                char tileChar = level.charAt(tilePos);
                if (tileChar == ' ') { continue; }
                Sprite thisTile = tiles.get(tilePos);
                thisTile.enabled = (tileChar == '#');
                switch (tileChar) {
                    case '#':
                        thisTile.setTexture("wall");
                        break;
                    case 'p':
                        playerTileX = col;
                        playerTileY = row;
                        player.playerObject.setPosition(
                                tileSize * Globals.globalScale * playerTileX,
                                tileSize * Globals.globalScale * playerTileY
                        );
                        break;
                    case 'e':
                        exitX = col;
                        exitY = row;
                        break;
                    default:
                        break;
                }
            }
        }
        currentLevel = level; // Remember loaded level
    }

    public static void trySlidePlayer(int dx, int dy) {
        if (player.moving()) { return; }
        // Calculate the longest line of empty tiles in a direction given by the vector (dx, dy). Can wrap around the screen.
        int distance = -1;
        int cx = playerTileX; int cy = playerTileY; // Position currently being examined
        int lastX = playerTileX; int lastY = playerTileY; // Last recorded free position
        boolean wrapAround = false; boolean lastWrapped = false;
        while (currentLevel.charAt(cy * getTilesH() + cx) != '#') {
            lastWrapped = wrapAround; // Account for a literal edge case

            if (dx != 0) {
                lastX = cx;
                cx += dx;
                if (cx >= getTilesH()) {
                    cx -= getTilesH(); wrapAround = true;
                } else if (cx < 0) {
                    cx += getTilesH(); wrapAround = true;
                }
            }

            if (dy != 0) {
                lastY = cy;
                cy += dy;
                if (cy >= getTilesV()) {
                    cy -= getTilesV(); wrapAround = true;
                } else if (cy < 0) {
                    cy += getTilesV(); wrapAround = true;
                }
            }

            distance++;
            if ((dx != 0 && distance > getTilesH()) || (dy != 0 && distance > getTilesV())) {
                return; // Break loop when infinite wrapping would occur
            }
        }
        if (distance <= 0) { return; }
        // Slide the player to the last free position
        playerTileX = lastX; playerTileY = lastY;
        player.slideTo(tileSize * Globals.globalScale * playerTileX, tileSize * Globals.globalScale * playerTileY, lastWrapped);
    }

    public static void update() {
        if (player != null) {
            player.update();
            if (!player.moving() && playerTileX == exitX && playerTileY == exitY) {
                loadAdjacentLevel(1);
            }
        }
        // Get the position of the exit in pixels. (globalScale is ignored)
        double x = exitX * tileSize + getMarginH();
        double y = exitY * tileSize + getMarginV();
        // Transform pixels to shader space: the rectangle (0,0)x(1280,720) is transformed into (-16/9,1)x(16/9,-1)
        // (0,0)x(1280,720)
        x -= 640;
        y = 360 - y;
        // (-640,360)x(640,-360)
        x /= 360;
        y /= 360;
        // (-16/9,1)x(16/9,-1)
        Render.drawEffects(x, y, tileSize);
    }

    public static Player getPlayer() {
        return player;
    }
}
