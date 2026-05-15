package internal.lib.math.colliders;

import internal.lib.math.Mathf;
import internal.lib.math.points.Point2F;

public class CircleCollider2D extends PolygonCollider2D {
    private static final Point2F[] vertices = createCircleVertices(1f, 32);

    private static Point2F[] createCircleVertices(float radius, int segments) {
        Point2F[] vertices = new Point2F[segments];
        float angleStep = Mathf.toRadians(360f) / segments;
        float currentAngle = 0;

        for (int i = 0; i < segments; i++) {
            vertices[i] = new Point2F(Mathf.cos(currentAngle) * radius, Mathf.sin(currentAngle) * radius);
            currentAngle += angleStep;
        }
        return vertices;
    }

    public CircleCollider2D(String name) {
        super(vertices);
    }

    public CircleCollider2D(String name, int segments) {
        super(createCircleVertices(1f, segments));
    }
}
