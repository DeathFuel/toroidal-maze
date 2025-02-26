package trmz.renderer;

import trmz.Globals;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFWErrorCallback.createPrint;

// Window class that handles GLFW plumbing
public class Window {
    public long windowHandle;

    public Window(int windowX, int windowY, String windowTitle) {
        createPrint(System.err).set();
        if (!glfwInit()) { throw new IllegalStateException("Failed to initialize GLFW"); }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        this.windowHandle = glfwCreateWindow(windowX, windowY, windowTitle, (Globals.fullscreen ? glfwGetPrimaryMonitor() : 0), 0);
        if (this.windowHandle == 0) { throw new RuntimeException("Window creation failed"); }
        glfwMakeContextCurrent(this.windowHandle);
        glfwSwapInterval(1);
        GL.createCapabilities();
        GL11.glClearColor(0.05f, 0.06f, 0.06f, 1);
    }

    public void ready() {
        glfwShowWindow(this.windowHandle);
    }

    public void update() {
        glfwSwapBuffers(this.windowHandle);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(this.windowHandle);
    }

    public void exit() {
        glfwFreeCallbacks(this.windowHandle);
        glfwDestroyWindow(this.windowHandle);
        glfwTerminate();
    }
}
