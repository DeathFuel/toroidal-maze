package trmz.renderer;

import trmz.Globals;
import org.joml.Matrix4d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import static org.joml.Math.toRadians;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.List;

// Static-only class that does the OpenGL work
public class Render {
    private static int quadShaderProg;
    private static int bgShaderProg;
    private static int fxShaderProg;
    private static int quadVAO;

    // Working objects that might reduce the number of memory allocations
    private static final Matrix4d transform = new Matrix4d();
    private static final FloatBuffer floatBuf16 = BufferUtils.createFloatBuffer(16);

    public static void init() {
        quadShaderProg = linkShaderProg("quad-vs", "quad-fs");
        bgShaderProg = linkShaderProg("bg-vs", "bg-fs");
        fxShaderProg = linkShaderProg("fx-vs", "fx-fs");

        float[] vertices = new float[]{
                0, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        quadVAO = GL30.glGenVertexArrays();
        int VBO = GL30.glGenBuffers();

        GL30.glBindVertexArray(quadVAO);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, VBO);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);

        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static int compileShader(String filename, int shaderType) {
        String shaderPath = "src/main/java/trmz/renderer/shaders/" + filename + ".glsl";
        File f = new File(shaderPath);
        List<String> strings;
        try {
            strings = Files.readAllLines(f.toPath());
        } catch (Exception e) {
            System.err.println("Failed to load " + shaderPath);
            throw new RuntimeException(e);
        }
        StringBuilder shaderProgram = new StringBuilder();
        for (String s : strings) { shaderProgram.append(s).append("\n"); }
        int shader = GL30.glCreateShader(shaderType);
        GL30.glShaderSource(shader, shaderProgram);
        GL30.glCompileShader(shader);
        if (GL30.glGetShaderi(shader, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
            System.err.println(GL30.glGetShaderInfoLog(shader));
            throw new RuntimeException("Failed to compile " + shaderPath);
        }
        return shader;
    }

    public static int linkShaderProg(String vs, String fs) {
        int vsc = compileShader(vs, GL30.GL_VERTEX_SHADER);
        int fsc = compileShader(fs, GL30.GL_FRAGMENT_SHADER);
        int shaderProg = GL30.glCreateProgram();
        GL30.glAttachShader(shaderProg, vsc);
        GL30.glAttachShader(shaderProg, fsc);
        GL30.glLinkProgram(shaderProg);
        if (GL30.glGetProgrami(shaderProg, GL30.GL_LINK_STATUS) == GL30.GL_FALSE) {
            System.err.println(GL30.glGetProgramInfoLog(shaderProg));
            throw new RuntimeException("Failed to link shader program.");
        }
        GL30.glDeleteShader(vsc);
        GL30.glDeleteShader(fsc);
        return shaderProg;
    }

    public static void drawTexturedQuad(double posX, double posY, double scaleX, double scaleY, double rotationAngle, int textureID) {
        transform
                .identity()
                .ortho2D(0, Globals.windowX, Globals.windowY, 0)
                .translate(posX, posY, 0)
                .translate(scaleX/2, scaleY/2, 0)
                .rotate(toRadians(rotationAngle), 0, 0, 1)
                .translate(-scaleX/2, -scaleY/2, 0)
                .scale(scaleX, scaleY, 1);

        transform.get(floatBuf16);

        GL30.glUseProgram(quadShaderProg);
        GL30.glUniformMatrix4fv(GL30.glGetUniformLocation(quadShaderProg, "transform"), false, floatBuf16);
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 0, 0L);
        GL30.glEnableVertexAttribArray(0);

        GL30.glBindVertexArray(quadVAO);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 6);
    }

    public static void drawBackground() {
        GL30.glUseProgram(bgShaderProg);

        GL30.glUniform1f(GL30.glGetUniformLocation(bgShaderProg, "_time"), (float) GLFW.glfwGetTime());
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 0, 0L);
        GL30.glEnableVertexAttribArray(0);

        GL30.glBindVertexArray(quadVAO);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 2);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 6);
    }

    public static void drawEffects(double exitX, double exitY, int tileSize) {
        GL30.glUseProgram(fxShaderProg);

        GL30.glUniform1f(GL30.glGetUniformLocation(fxShaderProg, "_time"), (float) GLFW.glfwGetTime());
        GL30.glUniform2f(GL30.glGetUniformLocation(fxShaderProg, "_exitPos"), (float)exitX, (float)exitY);
        GL30.glUniform1f(GL30.glGetUniformLocation(fxShaderProg, "_tileSize"), tileSize);
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 0, 0L);
        GL30.glEnableVertexAttribArray(0);

        GL30.glBindVertexArray(quadVAO);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 2);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, 6);
    }
}
