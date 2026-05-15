package internal.objects.graphic;

import internal.Engine;
import internal.graphic.Material;
import internal.objects.Node;

public abstract class Control extends Node implements Drawable {
    boolean isRendering = true;

    @Override
    public Material getMaterial() {
        return null;
    }

    @Override
    public void startRendering() {
        Engine.getRenderer().add(this);
        isRendering = true;
    }

    @Override
    public void stopRendering() {
        Engine.getRenderer().remove(this);
        isRendering = false;
    }

    @Override
    public boolean isRendering() {
        return isRendering;
    }
}
