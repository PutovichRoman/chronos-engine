package internal.graphic;

import internal.data.Mesh;
import internal.data.Texture;
import internal.lib.math.Matrix4;
import internal.objects.Camera2D;

import static internal.data.Resources.getShader;

public class TexturedMaterial extends Material {

    public TexturedMaterial(Mesh mesh) {
        super(getShader("textured"), mesh);
    }

    public void use(Matrix4 transform, Color color, Texture texture, boolean ignoreCamera) {
        shader.use();
        shader.setUniformColor("uColor", color);

        updateMatrixBuffer(transform, ignoreCamera);
        shader.setUniformMat4f("uMVP", false, matrix_buffer);

        texture.bind(0);
        shader.setUniform("uTexture", 0);
    }

    public void use(Matrix4 transform, Color color, Texture texture) {
        use(transform, color, texture, false);
    }
}
