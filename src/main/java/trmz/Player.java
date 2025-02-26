package trmz;

import trmz.components.Sprite;
import trmz.renderer.SpriteManager;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

public class Player {
    public final Sprite playerObject;
    private static boolean instantiated = false;
    private double moveSpeed = 36 * Globals.globalScale * Globals.levelTileSize;
    private double deltaTime = 0;
    private double lastTime = 0;
    private moveState State = moveState.STILL;
    private double destX = 0;
    private double destY = 0;

    private enum moveState {
        MOVING,
        MOVING_WRAP,
        STILL;
    }

    public Player(int gridX, int gridY, int tileSize) {
        if (instantiated) {
            throw new AssertionError("Player instantiated twice!");
        }
        instantiated = true;
        playerObject = new SpriteManager.Builder("player")
                .setSize(tileSize * Globals.globalScale)
                .setPosition((gridX + 1) * tileSize * Globals.globalScale, (gridY + 1) * tileSize * Globals.globalScale)
                .setZ(1)
                .build();
    }

    // Whenever offscreen, the player is teleported to the opposite side
    private void doWrapChecks() {
        Vector2d absPos = playerObject.getAbsolutePosition();
        if (absPos.x < -Globals.levelTileSize) {
            this.playerObject.position.x = Globals.windowX;
            this.State = moveState.MOVING;
        }
        if (absPos.x > Globals.windowX) {
            this.playerObject.position.x = -Globals.levelTileSize;
            this.State = moveState.MOVING;
        }
        if (absPos.y < -Globals.levelTileSize) {
            this.playerObject.position.y = Globals.windowY;
            this.State = moveState.MOVING;
        }
        if (absPos.y > Globals.windowY) {
            this.playerObject.position.y = -Globals.levelTileSize;
            this.State = moveState.MOVING;
        }
    }

    public void update() {
        deltaTime = GLFW.glfwGetTime() - lastTime;
        lastTime = GLFW.glfwGetTime();
        if (this.moving()) {
            double playerX = this.playerObject.position.x;
            double playerY = this.playerObject.position.y;
            boolean wrappingAround = (this.State == moveState.MOVING_WRAP);

            double indepSpeed = moveSpeed * deltaTime; // framerate-independent

            boolean offX = (Math.abs(playerX - destX) > indepSpeed * 1.5);
            boolean offY = (Math.abs(playerY - destY) > indepSpeed * 1.5);
            if (offX) {
                int sign = (playerX > destX ^ wrappingAround) ? -1 : 1;
                this.playerObject.move(sign * indepSpeed, 0);
            } else {
                this.playerObject.position.x = destX;
            }
            if (offY) {
                int sign = (playerY > destY ^ wrappingAround) ? -1 : 1;
                this.playerObject.move(0, sign * indepSpeed);
            } else {
                this.playerObject.position.y = destY;
            }

            if (wrappingAround) { this.doWrapChecks(); }

            if (!offX && !offY) {
                this.State = moveState.STILL;
            }
        }
    }

    public void slideTo(double x, double y, boolean wrap) {
        if (!this.moving()) {
            this.destX = x; this.destY = y;
            if (wrap) {
                this.State = moveState.MOVING_WRAP;
            } else {
                this.State = moveState.MOVING;
            }
        }
    }

    public boolean moving() {
        return (this.State == moveState.MOVING || this.State == moveState.MOVING_WRAP);
    }

    public void forceStop() {
        this.State = moveState.STILL;
    }

    public void setMoveSpeed(double v) {
        this.moveSpeed = v;
    }
}
