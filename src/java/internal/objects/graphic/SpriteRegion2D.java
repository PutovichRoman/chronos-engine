package internal.objects.graphic;

import internal.data.Disposable;
import internal.data.DynamicMesh;
import internal.graphic.Color;
import internal.graphic.TexturedMaterial;
import internal.lib.math.Rect2F;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class SpriteRegion2D extends TexturedNode2D implements Disposable {
    final DynamicMesh mesh = new DynamicMesh(6, false, true);

    private Rect2F region;

    public SpriteRegion2D() {
        material = new TexturedMaterial(mesh);
        color = Color.WHITE;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(18);
            vertices.put(-0.5f).put(-0.5f).put(0f);
            vertices.put(-0.5f).put(+0.5f).put(0f);
            vertices.put(+0.5f).put(+0.5f).put(0f);
            vertices.put(-0.5f).put(-0.5f).put(0f);
            vertices.put(+0.5f).put(-0.5f).put(0f);
            vertices.put(+0.5f).put(+0.5f).put(0f);
            vertices.rewind();

            FloatBuffer uvs = stack.mallocFloat(12);
            uvs.put(0f).put(0f);
            uvs.put(0f).put(1f);
            uvs.put(1f).put(1f);
            uvs.put(0f).put(0f);
            uvs.put(1f).put(0f);
            uvs.put(1f).put(1f);
            uvs.rewind();

            mesh.appendVertices(vertices);
            mesh.appendUVs(uvs);
        }
    }

    public void setRegion(Rect2F region) {
        this.region = region;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int sx = texture.getWidth();
            int sy = texture.getHeight();
            float x0 = region.x() / sx;
            float x1 = (region.x() + region.width()) / sx;
            float y0 = region.y() / sy;
            float y1 = (region.y() + region.height()) / sy;
            FloatBuffer uvs = stack.mallocFloat(12);
            uvs.put(x0).put(y0);
            uvs.put(x0).put(y1);
            uvs.put(x1).put(y1);
            uvs.put(x0).put(y0);
            uvs.put(x1).put(y0);
            uvs.put(x1).put(y1);

            mesh.setUVs(0, uvs);
        }
    }

    public void setRegion(float x, float y, float width, float height) {
        setRegion(new Rect2F(x, y, width, height));
    }

    public Rect2F getRegion() {
        return region;
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }

    @Override
    public boolean isDisposed() {
        return mesh.isDisposed();
    }
}
