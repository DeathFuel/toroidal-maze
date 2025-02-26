package trmz.renderer;

import trmz.components.Sprite;

import java.util.ArrayList;

// Keeps track of sprites and draws them in the correct order. Also features a sprite builder.
public class SpriteManager {
    private final static int layers = 4; // Very primitive z-layer implementation but we don't need anything better here
    private static final ArrayList<ArrayList<Sprite>> sprites = new ArrayList<>(layers);

    public static void init() {
        for (int i = 0; i < layers; i++) {
            sprites.add(new ArrayList<>());
        }
    }

    static public void drawScene() {
        for (int i = 0; i < layers; i++) {
            sprites.get(i).forEach(Sprite::draw);
        }
    }

    public static class Builder {
        private final Sprite s;
        private int zLayer = 1;

        public Builder(String textureName) { this.s = new Sprite(textureName); }

        public Builder setName(String name) { this.s.name = name; return this; }

        public Builder setPosition(double px, double py) { this.s.setPosition(px, py); return this; }

        public Builder setScale(double sx, double sy) { this.s.setScale(sx, sy); return this; }

        public Builder setScale(double d) { return this.setScale(d, d); }

        public Builder setSize(double sx, double sy) { this.s.setScale(sx / this.s.width, sy / this.s.height); return this; }

        public Builder setSize(double d) { return this.setSize(d,d); }

        public Builder setRotation(double rotation) { this.s.rotation = rotation; return this; }

        public Builder setOffset(double ox, double oy) { this.s.offset.x = ox; this.s.offset.y = oy; return this; }

        public Builder setZ(int z) { assert(0 <= z && z < SpriteManager.layers); this.zLayer = z; return this; }

        public Sprite build() { SpriteManager.sprites.get(zLayer).add(this.s); return this.s; }
    }
}
