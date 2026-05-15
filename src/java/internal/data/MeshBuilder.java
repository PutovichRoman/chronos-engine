package internal.data;

import internal.lib.math.Mathf;
import internal.lib.math.points.Point2F;
import internal.lib.math.vectors.Vector2F;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public abstract class MeshBuilder {

    public static Mesh createBox(Vector2F position, Vector2F scale, boolean enable_uvs, boolean enable_normals) {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer normalBuffer = null;
            FloatBuffer uvBuffer = null;
            // Создание прямоугольника
            FloatBuffer vertexBuffer = memoryStack.mallocFloat(3 * 3 * 2);
            // 1 треугольник
            vertexBuffer.put(position.x() - scale.x() / 2f).put(position.y() - scale.y() / 2f).put(0f);
            vertexBuffer.put(position.x() - scale.x() / 2f).put(position.y() + scale.y() / 2f).put(0f);
            vertexBuffer.put(position.x() + scale.x() / 2f).put(position.y() + scale.y() / 2f).put(0f);
            // 2 треугольник
            vertexBuffer.put(position.x() - scale.x() / 2f).put(position.y() - scale.y() / 2f).put(0f);
            vertexBuffer.put(position.x() + scale.x() / 2f).put(position.y() - scale.y() / 2f).put(0f);
            vertexBuffer.put(position.x() + scale.x() / 2f).put(position.y() + scale.y() / 2f).put(0f);
            vertexBuffer.rewind();

            // Создание нормалей
            if (enable_normals) {
                normalBuffer = memoryStack.mallocFloat(3 * 3 * 2);
                for (int i = 0; i < 6; i++) {
                    normalBuffer.put(0f).put(0f).put(1f);
                }
                normalBuffer.rewind();
            }

            // Создание координат текстуры
            if (enable_uvs) {
                uvBuffer = memoryStack.mallocFloat(3 * 2 * 2);
                uvBuffer.put(0f).put(0f);
                uvBuffer.put(0f).put(1f);
                uvBuffer.put(1f).put(1f);
                uvBuffer.put(0f).put(0f);
                uvBuffer.put(1f).put(0f);
                uvBuffer.put(1f).put(1f);
                uvBuffer.rewind();
            }

            return new Mesh(vertexBuffer, normalBuffer, uvBuffer);
        }
    }

    public static Mesh createCircle(Vector2F center, float radius, int segments) {
        // Для круга из треугольников нужно segments * 3 вершины
        FloatBuffer vertices = MemoryUtil.memAllocFloat(segments * 3 * 3); // x, y, z

        float angleStep = Mathf.toRadians(360f) / segments;
        float currentAngle = 0f;

        for (int i = 0; i < segments; i++) {
            float nextAngle = currentAngle + angleStep;
            vertices.put(center.x).put(center.y()).put(0f);

            vertices.put(center.x + Mathf.cos(currentAngle) * radius)
                    .put(center.y + Mathf.sin(currentAngle) * radius)
                    .put(0f);

            vertices.put(center.x + Mathf.cos(nextAngle) * radius)
                    .put(center.y + Mathf.sin(nextAngle) * radius)
                    .put(0f);

            currentAngle = nextAngle;
        }

        vertices.rewind();

        Mesh mesh = new Mesh(vertices, null, null);
        MemoryUtil.memFree(vertices);   // очень важно освободить память!

        return mesh;
    }

    public static Mesh createLines(Point2F[] points, float thickness) {
        FloatBuffer vertices;
        int len;
        if (points.length == 2) {
            vertices = MemoryUtil.memAllocFloat(3 * 3 * points.length);
            len = points.length - 1;
        } else {
            vertices = MemoryUtil.memAllocFloat(3 * 3 * points.length * 2);
            len = points.length;
        }

        for (int i = 0; i < len; i++) {
            Point2F point1 = points[i];
            Point2F point2 = points[(i + 1) % points.length];

            float dirUp = point1.directionTo(point2) + Mathf.toRadians(90);
            float dirDown = point1.directionTo(point2) - Mathf.toRadians(90);
            float width = thickness * 0.5f;
            var up = new Point2F(Mathf.cos(dirUp) * width, Mathf.sin(dirUp) * width);
            var down = new Point2F(Mathf.cos(dirDown) * width, Mathf.sin(dirDown) * width);

            vertices.put(up.x() + point1.x()).put(up.y() + point1.y()).put(0f);
            vertices.put(down.x() + point1.x()).put(down.y() + point1.y()).put(0f);
            vertices.put(down.x() + point2.x()).put(down.y() + point2.y()).put(0f);

            vertices.put(up.x() + point1.x()).put(up.y() + point1.y()).put(0f);
            vertices.put(down.x() + point2.x()).put(down.y() + point2.y()).put(0f);
            vertices.put(up.x() + point2.x()).put(up.y() + point2.y()).put(0f);
        }

        vertices.flip();
        Mesh mesh = new Mesh(vertices, null, null);
        MemoryUtil.memFree(vertices);
        return mesh;
    }

    public static Mesh createConvexPolygon(Point2F[] points) {
        FloatBuffer vertices = MemoryUtil.memAllocFloat(3 * 3 * points.length);
        if (points.length == 2) {
            throw new IllegalArgumentException("Points count must be greater than 2");
        }
        for (int i = 0; i < points.length; i++) {
            Point2F point1 = points[i];
            Point2F point2 = points[(i + 1) % points.length];
            Point2F point3;
            if(i == 0){
                point3 = points[points.length - 1];
            } else {
                point3 = points[(i - 1) % points.length];
            }

            vertices.put(point1.x()).put(point1.y()).put(0f);
            vertices.put(point2.x()).put(point2.y()).put(0f);
            vertices.put(point3.x()).put(point3.y()).put(0f);
        }

        vertices.flip();
        Mesh mesh = new Mesh(vertices, null, null);
        MemoryUtil.memFree(vertices);
        return mesh;
    }

    /*public static Mesh createPolygonOutline(Point2F[] points, float thickness) {

    }*/
}
