package internal.lib.math.colliders;

import internal.lib.math.points.Point2F;

public class RectangleCollider2D extends PolygonCollider2D {
    private static final Point2F[] rect = new Point2F[]{
            new Point2F(0.5f, -0.5f),
            new Point2F(-0.5f, -0.5f),
            new Point2F(-0.5f, 0.5f),
            new Point2F(0.5f, 0.5f)
    };

    public RectangleCollider2D(String name) {
        super(rect);
    }
}
