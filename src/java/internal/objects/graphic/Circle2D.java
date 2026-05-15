package internal.objects.graphic;

import internal.data.Disposable;
import internal.data.Mesh;
import internal.data.MeshBuilder;
import internal.exceptions.ResourceException;
import internal.lib.math.vectors.Vector2F;

public class Circle2D extends Primitive2D {
    private static Mesh circleMesh;

    private static Mesh getCircleMesh() {
        if (circleMesh == null) {
            circleMesh = MeshBuilder.createCircle(new Vector2F(0, 0), 1f, 64);
        }
        return circleMesh;
    }

    public Circle2D() {
        super(getCircleMesh());
    }
}
