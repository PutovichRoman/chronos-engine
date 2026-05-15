package internal.objects.graphic;

import internal.graphic.Material;

public interface Drawable {
    Material getMaterial();

    void draw();

    void startRendering();

    void stopRendering();

    boolean isRendering();
}
