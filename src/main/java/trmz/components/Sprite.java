package trmz.components;

import trmz.renderer.*;
import org.joml.Vector2d;

public class Sprite extends AABB {
    private int textureID = 0;

    public Sprite(Texture.TextureData td) {
        super(td.x, td.y);
        this.textureID = td.textureID;
    }

    public Sprite(String textureName) {
        this(Texture.get(textureName));
    }

    public void setTexture(String textureName) {
        Texture.TextureData td = Texture.get(textureName);
        this.width = td.x;
        this.height = td.y;
    }

    public void draw() {
        if (!this.enabled || this.textureID == 0) { return; }
        Vector2d absPos = this.getAbsolutePosition();
        Render.drawTexturedQuad(
                absPos.x - this.offset.x * this.scale.x * this.width,
                absPos.y - this.offset.y * this.scale.y * this.height,
                this.width * this.scale.x,
                this.height * this.scale.y,
                this.rotation, this.textureID
        );
    }
}
