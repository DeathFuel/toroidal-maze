package trmz.components;

import org.joml.Vector2d;

public class AABB extends GameObject {
    public double width = 0;
    public double height = 0;
    // Influences the AABB's anchor point. Zero-zero means a top-left anchor; (0.5, 0.5) will center the AABB wrt its position.
    public Vector2d offset = new Vector2d(0);

    public AABB(int w, int h) {
        super();
        this.width = w;
        this.height = h;
    }

    public void setOffset(double ox, double oy) {
        this.offset.x = ox;
        this.offset.y = oy;
    }
}
