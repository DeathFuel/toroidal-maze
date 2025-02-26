package trmz;

import trmz.renderer.*;
import org.lwjgl.glfw.GLFW;

public class Main {
    public static Window window;

    public static void main(String[] args) {
        window = new Window(Globals.windowX, Globals.windowY, Globals.windowTitle);
        Render.init();
        SpriteManager.init();
        Level.init();
        GLFW.glfwSetKeyCallback(window.windowHandle, (w, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) { keyPressed(key); }
        });
        window.ready();
        while (!window.shouldClose()) {
            Render.drawBackground();
            SpriteManager.drawScene();
            Level.update();
            window.update();
        }
        window.exit();
    }

    public static void keyPressed(int key) {
        switch (key) {
            case GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_UP    -> Level.trySlidePlayer(0, -1);
            case GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT  -> Level.trySlidePlayer(-1, 0);
            case GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_DOWN  -> Level.trySlidePlayer(0, 1);
            case GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_RIGHT -> Level.trySlidePlayer(1, 0);
            case GLFW.GLFW_KEY_R -> Level.reloadLevel();
            case GLFW.GLFW_KEY_Z -> Level.loadAdjacentLevel(-1);
            case GLFW.GLFW_KEY_X -> Level.loadAdjacentLevel(1);
            case GLFW.GLFW_KEY_ESCAPE -> GLFW.glfwSetWindowShouldClose(Main.window.windowHandle, true);
        }
    }
}
