package internal.objects;

import internal.lib.math.Matrix4;
import internal.lib.math.colliders.PolygonCollider2D;
import internal.lib.math.points.Point2F;
import internal.lib.math.vectors.Vector2F;

public class Node2D extends Node implements Transformable {
    public Vector2F position = new Vector2F();
    public Vector2F scale = new Vector2F(1f, 1f);
    public float rotation;

    public boolean ignoreParentTransform;
    public boolean ignoreCameraZoom;
    Matrix4 localMatrix = Matrix4.identity();
    Matrix4 worldMatrix = Matrix4.identity();

    public static void updateTransforms(Node node, Matrix4 parentGlobalMatrix) {
        if (node instanceof Node2D graphicNode) {
            graphicNode.localMatrix = graphicNode.toMatrix();

            if (parentGlobalMatrix != null && !graphicNode.ignoreParentTransform) {
                graphicNode.worldMatrix = parentGlobalMatrix.mul(graphicNode.localMatrix);

                if (node instanceof PolygonCollider2D polygonCollider2D) {
                    polygonCollider2D.updateWorldVertices();
                }
            } else {
                graphicNode.worldMatrix = graphicNode.localMatrix;
            }

            for (Node child : graphicNode.getChildren()) {
                updateTransforms(child, graphicNode.worldMatrix);
            }

        } else {
            for (Node child : node.getChildren()) {
                updateTransforms(child, parentGlobalMatrix);
            }
        }
    }

    @Override
    public Matrix4 toMatrix() {
        return Matrix4.of(position, scale, rotation);
    }

    public Point2F getPosition() {
        return new Point2F(position.x, position.y);
    }

    public Vector2F getScale() {
        return new Vector2F(scale.x, scale.y);
    }

    public float getRotation() {
        return rotation;
    }

    public Matrix4 getWorldMatrix() {
        return worldMatrix;
    }

    public Matrix4 getLocalMatrix() {
        return localMatrix;
    }
}