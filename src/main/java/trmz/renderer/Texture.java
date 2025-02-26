package trmz.renderer;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

// Class that handles the loading of textures.
public class Texture {
    // Maps texture names to a data structure containing each texture's information.
    private static final HashMap<String, TextureData> textureMap = new HashMap<>();

    public static class TextureData {
        public int textureID, x, y, channels;
        public TextureData(int t, int x, int y, int c) {
            this.textureID = t;
            this.x = x;
            this.y = y;
            this.channels = c;
        }
    }

    // Load a texture or its cached information.
    public static TextureData get(String textureName) {
        if (textureMap.containsKey(textureName)) { return textureMap.get(textureName); }

        // Load image data into a buffer.
        ByteBuffer imgBuf;
        File f = new File("res/" + textureName + ".png");
        int[] textureWidth = new int[1], textureHeight = new int[1], channels = new int[1];
        imgBuf = STBImage.stbi_load(f.getAbsolutePath(), textureWidth, textureHeight, channels, 4);
        if (imgBuf == null) { throw new RuntimeException("Failed to load res/" + textureName); }

        // Generate a texture ID, create a 2D texture with buffered image data and the generated ID.
        int textureID = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, textureWidth[0], textureHeight[0], 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, imgBuf);

        STBImage.stbi_image_free(imgBuf);

        TextureData td = new TextureData(textureID, textureWidth[0], textureHeight[0], channels[0]);
        textureMap.put(textureName, td);
        return td;
    }
}
