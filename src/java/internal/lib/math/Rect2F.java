package internal.lib.math;

import internal.lib.math.vectors.Vector2F;

public value record Rect2F(float x, float y, float width, float height) {
    public Rect2F(Vector2F pos, Vector2F size) {
        this(pos.x, pos.y, size.x, size.y);
    }

    public float left() {
        return x - width / 2f;
    }

    public float right() {
        return x + width / 2f;
    }

    public float top() {
        return y - height / 2f;
    }

    public float bottom() {
        return y + height / 2f;
    }

    public boolean intersects(Rect2F rect) {
        return intersects(rect.left(), rect.bottom()) || intersects(rect.right(), rect.bottom()) ||
                intersects(rect.left(), rect.top()) || intersects(rect.right(), rect.top());
    }

    public boolean intersects(float x, float y) {
        return x >= this.x - this.width / 2f && x <= this.x + width / 2f &&
                y >= this.y - this.height / 2f && y <= this.y + height / 2f;
    }

    @Override
    public String toString() {
        return "[left=" + left() + ", right=" + right() + ", top=" + top() + ", bottom=" + bottom() + "]";
    }
}
