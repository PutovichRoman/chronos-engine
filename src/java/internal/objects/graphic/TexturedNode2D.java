package internal.objects.graphic;

import internal.data.Texture;
import internal.exceptions.NodeException;
import internal.graphic.TexturedMaterial;

public abstract class TexturedNode2D extends DrawableNode2D {
    public Texture texture;

    @Override
    public void draw() {
        TexturedMaterial m = (TexturedMaterial) getMaterial();
        if (texture == null) {
            throw new NodeException("Texture not set!");
        }
        m.use(getWorldMatrix(), color, texture, ignoreCameraZoom);
        m.drawMesh();
    }
}
