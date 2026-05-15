package internal.lib.math.colliders;

import internal.objects.Node2D;

public abstract class Collider2D extends Node2D {
    public abstract boolean collidesWith(Collider2D c);
}
