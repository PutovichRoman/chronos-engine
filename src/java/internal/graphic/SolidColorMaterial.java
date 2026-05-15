package internal.graphic;

import internal.data.Mesh;
import internal.data.Resources;
import internal.lib.math.Matrix4;
import internal.objects.Camera2D;

public class SolidColorMaterial extends Material {
    public SolidColorMaterial(Mesh mesh) {
        super(Resources.getShader("solid_color"), mesh);
    }

    public void use(Matrix4 transform, Color color, boolean ignoreCamera) {
        shader.use();
        shader.setUniformColor("uColor", color);

        updateMatrixBuffer(transform, ignoreCamera);
        shader.setUniformMat4f("uMVP", false, matrix_buffer);
        //GL46.glBindVertexArray(0);
    }

    public void use(Matrix4 transform, Color color) {
        use(transform, color, false);
    }
}
