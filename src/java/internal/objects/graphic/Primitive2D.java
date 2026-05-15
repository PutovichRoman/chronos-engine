package internal.objects.graphic;

import internal.data.Mesh;
import internal.graphic.SolidColorMaterial;

public class Primitive2D extends DrawableNode2D {
    public Primitive2D(Mesh mesh) {
        material = new SolidColorMaterial(mesh);
    }

    public void draw() {
        SolidColorMaterial g = (SolidColorMaterial) getMaterial();
        g.use(getWorldMatrix(), color, ignoreCameraZoom);
        g.drawMesh();
    }
}
