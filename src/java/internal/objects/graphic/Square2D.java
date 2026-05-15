package internal.objects.graphic;

import internal.data.Mesh;
import internal.data.MeshBuilder;
import internal.lib.math.vectors.Vector2F;

public class Square2D extends Primitive2D {
    private static Mesh quadMesh;

    private static Mesh getQuadMesh() {
        if (quadMesh == null) {
            quadMesh = MeshBuilder.createBox(new Vector2F(0f, 0f), new Vector2F(1f, 1f), false, false);
        }
        return quadMesh;
    }

    public Square2D() {
        super(getQuadMesh());
    }
}
