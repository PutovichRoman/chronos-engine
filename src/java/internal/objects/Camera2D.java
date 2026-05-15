package internal.objects;

import internal.Engine;
import internal.Viewport;
import internal.lib.math.Matrix4;
import internal.lib.math.vectors.Vector2F;

public class Camera2D extends Node2D {
    public float zoom = 1f;

    static Camera2D current = new Camera2D();

    public Camera2D() {
        scale.set(1920, 1080);
    }

    public static Camera2D getCurrent() {
        return current;
    }

    public boolean isCurrent() {
        return current == this;
    }

    @Override
    public Matrix4 toMatrix() {
        return toMatrix(false);
    }

    public Matrix4 toMatrix(boolean ignoreZoom) {
        float _zoom;
        if (ignoreZoom) {
            _zoom = 1f;
        } else {
            _zoom = zoom;
        }
        Matrix4 view = Matrix4.identity()
                .translate(-position.x, -position.y, 0f)
                .rotateZ(-rotation)
                .scale(_zoom, _zoom, 1f);
        Viewport viewport = Engine.getWindow().getViewport();
        Matrix4 projection = Matrix4.ortho(
                -scale.x / 2f,
                scale.x / 2f,
                -scale.y / 2f,
                scale.y / 2f,
                -1f, 1f);
        return projection.mul(view);
    }

    public void makeCurrent() {
        current = this;
    }
}
