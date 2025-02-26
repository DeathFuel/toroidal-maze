package trmz.components;

import org.joml.Vector2d;
import java.util.ArrayList;

public class GameObject {
    // Object position in pixels, relative to its parent.
    public Vector2d position = new Vector2d(0);
    // Object scale.
    public Vector2d scale = new Vector2d(1);
    // Object rotation in degrees.
    public double rotation = 0;
    // Internal object name.
    public String name;
    // Decides whether the GameObject can influence other things. (Visibility, collision)
    public boolean enabled = true;

    private GameObject parent = null;
    private ArrayList<GameObject> children = null;

    public Vector2d getAbsolutePosition() {
        if (parent == null) { return this.position; }
        return getAbsolutePosition(new Vector2d(0, 0));
    }

    private Vector2d getAbsolutePosition(Vector2d s) {
        s.add(this.position);
        if (parent != null) {
            return parent.getAbsolutePosition(s);
        }
        return s;

    }

    public void move(double px, double py) {
        this.position.x += px; this.position.y += py;
    }

    public void setPosition(double px, double py) {
        this.position.x = px; this.position.y = py;
    }

    public void setScale(double sx, double sy) {
        this.scale.x = sx; this.scale.y = sy;
    }

    public void setParent(GameObject other) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        this.parent = other;
        parent.addChild(this);
    }

    public GameObject getParent() {
        return this.parent;
    }

    public void addChild(GameObject child) {
        if (children == null) { children = new ArrayList<>(); }
        children.add(child);
    }

    public ArrayList<GameObject> getChildren() {
        return children;
    }

    public GameObject getChild() {
        if (children == null) { return null; }
        return children.getFirst();
    }
}
