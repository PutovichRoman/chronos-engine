package internal.lib.math.colliders;

import internal.lib.math.Matrix4;
import internal.lib.math.points.Point2F;

public class PolygonCollider2D extends Collider2D {
    Point2F[] localVertices;
    Point2F[] worldVertices;

    public PolygonCollider2D(Point2F[] points) {
        localVertices = points;
        worldVertices = points.clone();
    }

    public void updateWorldVertices() {
        for (int i = 0; i < localVertices.length; i++) {
            worldVertices[i] = applyMatrixToPoint(getWorldMatrix(), localVertices[i]);
        }
    }

    @Override
    public boolean collidesWith(Collider2D collider) {
        if (!(collider instanceof PolygonCollider2D polygonCollider)) {
            return false;
        }

        for (int i = 0; i < polygonCollider.worldVertices.length; i++) {
            if (isPointInPolygon(worldVertices, polygonCollider.worldVertices[i])) return true;
        }
        for (int i = 0; i < worldVertices.length; i++) {
            if (isPointInPolygon(polygonCollider.worldVertices, worldVertices[i])) return true;
        }
        return false;
    }

    public boolean hasPoint(Point2F point) {
        for (int i = 0; i < worldVertices.length; i++) {
            if (isPointInPolygon(worldVertices, point)) return true;
        }
        return false;
    }

    public boolean contains(PolygonCollider2D collider) {
        for (int i = 0; i < collider.worldVertices.length; i++) {
            if (!isPointInPolygon(worldVertices, collider.worldVertices[i])) return false;
        }
        return true;
    }

    public boolean intersects(PolygonCollider2D collider) {
        for (int i = 0; i < worldVertices.length; i++) {
            Point2F a1 = worldVertices[i];
            Point2F a2 = worldVertices[(i + 1) % worldVertices.length];

            for (int j = 0; j < collider.worldVertices.length; j++) {
                Point2F b1 = collider.worldVertices[j];
                Point2F b2 = collider.worldVertices[(j + 1) % collider.worldVertices.length];

                if (doLinesIntersect(a1, a2, b1, b2)) return true;
            }
        }
        return false;
    }

    /**
     * Применяет матрицу 4x4 к точке Vec2 (2D)
     */
    public static Point2F applyMatrixToPoint(Matrix4 mat, Point2F point) {
        // Для 2D нам нужны только компоненты трансляции и поворота/масштаба
        float x = mat.m00() * point.x() + mat.m01() * point.y() + mat.m03();
        float y = mat.m10() * point.x() + mat.m11() * point.y() + mat.m13();
        return new Point2F(x, y);
    }

    /**
     * Алгоритм проверки пересечения двух отрезков с помощью параметризации и определителя
     */
    public static boolean doLinesIntersect(Point2F a1, Point2F a2, Point2F b1, Point2F b2) {
        if (a1 == null || a2 == null || b1 == null || b2 == null) {
            return false;
        }
        double denom = (b2.y() - b1.y()) * (a2.x() - a1.x()) - (b2.x() - b1.x()) * (a2.y() - a1.y());
        if (Math.abs(denom) < 1e-9) return false; // параллельны

        double ua = ((b2.x() - b1.x()) * (a1.y() - b1.y()) - (b2.y() - b1.y()) * (a1.x() - b1.x())) / denom;
        double ub = ((a2.x() - a1.x()) * (a1.y() - b1.y()) - (a2.y() - a1.y()) * (a1.x() - b1.x())) / denom;

        return (ua >= 0 && ua <= 1) && (ub >= 0 && ub <= 1);
    }

    /**
     * Проверяет, находится ли точка внутри полигона (Ray Casting Algorithm)
     * Работает с выпуклыми и вогнутыми простыми полигонами.
     */
    public static boolean isPointInPolygon(Point2F[] polygon, Point2F point) {
        boolean inside = false;

        for (int i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
            Point2F a = polygon[i];
            Point2F b = polygon[j];

            // Если горизонтальная грань — пропускаем
            if ((a.y() > point.y()) != (b.y() > point.y()) &&
                    (point.x() < (b.x() - a.x()) * (point.y() - a.y()) / (b.y() - a.y()) + a.x())) {
                inside = !inside;
            }
        }
        return inside;
    }
}
