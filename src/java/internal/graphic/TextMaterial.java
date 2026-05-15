package internal.graphic;

import internal.data.Mesh;
import internal.data.Resources;
import internal.data.Texture;
import internal.lib.math.Matrix4;
import internal.objects.Camera2D;

public class TextMaterial extends Material {
    public TextMaterial(Mesh mesh) {
        super(Resources.getShader("text"), mesh);
    }

    public void use(Matrix4 transform, Color color, Texture texture, boolean ignoreCameraZoom) {
        shader.use();
        shader.setUniformColor("uColor", color);

        updateMatrixBuffer(transform, ignoreCameraZoom);
        shader.setUniformMat4f("uMVP", false, matrix_buffer);

        texture.bind(0);
        shader.setUniform("uTexture", 0);
    }

    public void use(Matrix4 transform, Color color, Texture texture) {
        use(transform, color, texture, false);
    }
}
