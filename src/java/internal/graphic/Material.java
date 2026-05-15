package internal.graphic;

import internal.data.Mesh;
import internal.data.Shader;
import internal.lib.math.Matrix4;
import internal.objects.Camera2D;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public abstract class Material {
    final FloatBuffer matrix_buffer = BufferUtils.createFloatBuffer(16);
    protected final Shader shader;
    protected final Mesh mesh;

    public Material(Shader shader, Mesh mesh) {
        this.shader = shader;
        this.mesh = mesh;
    }

    void updateMatrixBuffer(Matrix4 transform, boolean ignoreCameraZoom) {
        var camera = Camera2D.getCurrent();
        if(ignoreCameraZoom) {
            camera.toMatrix(true).mul(transform).get(matrix_buffer);
        } else {
            camera.toMatrix(false).mul(transform).get(matrix_buffer);
        }
    }

    public void drawMesh() {
        mesh.glDraw();
    }

    public Mesh getMesh() {
        return mesh;
    }
}
