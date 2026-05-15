package internal.lib.math.points;

import internal.lib.math.vectors.Vector2F;
import org.jetbrains.annotations.NotNull;

public value record Point2F(float x, float y) {
    public static final Point2F ZERO = new Point2F(0, 0);

    public Point2F withX(float newX) {
        return new Point2F(newX, y);
    }

    public Point2F withY(float newY) {
        return new Point2F(x, newY);
    }

    public Point2F negate() {
        return new Point2F(-x, -y);
    }

    public float distanceTo(Point2F p) {
        return distanceTo(p.x, p.y);
    }

    public float distanceTo(float x, float y) {
        float dx = this.x - x;
        float dy = this.y - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float directionTo(float x, float y) {
        return (float) Math.atan2(y - this.y, x - this.x);
    }

    public float directionTo(Point2F p) {
        return directionTo(p.x, p.y);
    }

    public Point2F min(Point2F p) {
        return min(p.x, p.y);
    }

    public Point2F max(Point2F p) {
        return max(p.x, p.y);
    }

    public Point2F min(float x, float y) {
        return new Point2F(Math.min(this.x, x), Math.min(this.y, y));
    }

    public Point2F max(float x, float y) {
        return new Point2F(Math.max(this.x, x), Math.max(this.y, y));
    }

    public Point2F abs() {
        return new Point2F(Math.abs(x), Math.abs(y));
    }

    public Point2F floor() {
        return new Point2F((float) Math.floor(x), (float) Math.floor(y));
    }

    public Point2F ceil() {
        return new Point2F((float) Math.ceil(x), (float) Math.ceil(y));
    }

    public Point2F round() {
        return new Point2F(Math.round(x), Math.round(y));
    }

    @Override
    public @NotNull String toString() {
        return "(" + x + ", " + y + ")";
    }
}
