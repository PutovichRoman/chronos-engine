package internal.objects.graphic;

import internal.Engine;
import internal.graphic.Color;
import internal.graphic.Material;
import internal.objects.Node2D;

public abstract class DrawableNode2D extends Node2D implements Drawable {
    int layer;

    public int getLayer() {
        return layer;
    }

    boolean visible;
    Material material;

    public Color color = Color.BLACK;

    @Override
    public void startRendering() {
        Engine.getRenderer().add(this);
        visible = true;
    }

    @Override
    public void stopRendering() {
        Engine.getRenderer().remove(this);
        visible = false;
    }

    @Override
    public boolean isRendering() {
        return visible;
    }

    @Override
    public Material getMaterial() {
        return material;
    }
}
