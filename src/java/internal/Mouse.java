package internal;

import internal.lib.math.points.Point2F;
import internal.objects.Camera2D;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

public abstract class Mouse {
    private static GLFWCursorPosCallback cursorCallback;
    // Сырые координаты на экране
    static float x, y;

    static @NotNull Point2F localPos2 = Point2F.ZERO;
    static @NotNull Point2F globalPos2 = Point2F.ZERO;

    public static void setupCallback() {
        cursorCallback = glfwSetCursorPosCallback(Engine.window.id, (w, xpos, ypos) -> {
            var vp = Engine.window.viewport;
            var camera = Camera2D.getCurrent();

            Mouse.x = (float) xpos;
            Mouse.y = (float) ypos;

            localPos2 = new Point2F(
                    (float) Math.clamp((xpos - vp.getX()) * (camera.scale.x / (float) vp.getWidth()) - camera.scale.x / 2f, -camera.scale.x / 2f, camera.scale.x / 2f),
                    (float) -Math.clamp((ypos - vp.getY()) * (camera.scale.y / (float) vp.getHeight()) - camera.scale.y / 2f, -camera.scale.y / 2f, camera.scale.y / 2f));
        });
    }

    public static void update() {
        var camera = Camera2D.getCurrent();

        var minX = -camera.scale.x / 2f;
        var maxX = camera.scale.x / 2f;
        var minY = -camera.scale.y / 2f;
        var maxY = camera.scale.y / 2f;
        globalPos2 = new Point2F(
                Math.clamp(localPos2.x() * (1f / camera.zoom), minX, maxX),
                Math.clamp(localPos2.y() * (1f / camera.zoom), minY, maxY)
        );
    }

    public static Point2F getLocalPosition2F() {
        return localPos2;
    }

    public static Point2F getPosition2F() {
        return globalPos2;
    }

    public GLFWCursorPosCallback getCursorCallback() {
        return cursorCallback;
    }
}
