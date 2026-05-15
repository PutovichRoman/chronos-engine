package internal;

import internal.objects.Camera2D;
import org.lwjgl.opengl.GL30;

public class Viewport {
    private int x;      // смещение по X
    private int y;      // смещение по Y
    private int width;
    private int height;

    public void update() {
        Camera2D camera = Camera2D.getCurrent();
        float scaleX = (float) Engine.window.width / camera.scale.x;
        float scaleY = (float) Engine.window.height / camera.scale.y;
        float scale = Math.min(scaleX, scaleY);

        width = (int) (camera.scale.x * scale);
        height = (int) (camera.scale.y * scale);

        x = (Engine.window.width - width) / 2;
        y = (Engine.window.height - height) / 2;

        GL30.glViewport(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}