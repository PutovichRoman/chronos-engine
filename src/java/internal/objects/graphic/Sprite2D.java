package internal.objects.graphic;

import internal.data.Mesh;
import internal.data.MeshBuilder;
import internal.graphic.Color;
import internal.graphic.TexturedMaterial;
import internal.lib.math.vectors.Vector2F;

public class Sprite2D extends TexturedNode2D {
    private static Mesh quadTexturedMesh;

    private static Mesh getQuadTexturedMesh() {
        if (quadTexturedMesh == null) {
            quadTexturedMesh = MeshBuilder.createBox(new Vector2F(0f, 0f), new Vector2F(1f, 1f), true, false);
        }
        return quadTexturedMesh;
    }

    public Sprite2D() {
        material = new TexturedMaterial(getQuadTexturedMesh());
        color = Color.WHITE;
    }
}
