package internal.objects.graphic;

import internal.graphic.Color;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

public class Renderer {
    Color backgroundColor = new Color(25, 40, 48, 255);
    final List<DrawableNode2D> drawableNodes2D = new ArrayList<>(50);
    final List<Control> controlNodes = new ArrayList<>(25);

    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void clearBackground() {
        GL46.glClearColor(backgroundColor.red(), backgroundColor.green(), backgroundColor.blue(), backgroundColor.alpha());
        GL46.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render() {
        for (Drawable node : drawableNodes2D) {
            node.draw();
        }
        for (Drawable control : controlNodes) {
            control.draw();
        }
    }

    void add(DrawableNode2D graphicNode) {
        if (graphicNode.isRendering()) {
            return;
        }
        drawableNodes2D.add(graphicNode);
        graphicNode.layer = drawableNodes2D.size();
    }

    void add(Control control) {
        if (control.isRendering()) {
            return;
        }
        controlNodes.add(control);
    }

    void remove(Control control) {
        if (!control.isRendering()) {
            return;
        }
        controlNodes.remove(control);
    }

    void remove(DrawableNode2D graphicNode) {
        if (!graphicNode.isRendering()) {
            return;
        }
        drawableNodes2D.remove(graphicNode);
        graphicNode.layer = -1;
    }

    public void clearAll() {
        drawableNodes2D.clear();
        controlNodes.clear();
    }
}
