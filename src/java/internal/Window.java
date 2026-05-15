package internal;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.GLFW.*;

public class Window {
    final long id;
    int width;
    int height;
    String title;

    final Viewport viewport = new Viewport();

    public Viewport getViewport() {
        return viewport;
    }

    // Скрыто ли окно
    boolean isHidden;

    final GLFWFramebufferSizeCallback framebufferSizeCallback;

    Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 8);

        id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (id == NULL) {
            throw new RuntimeException("Failed to create window");
        }

        framebufferSizeCallback = glfwSetFramebufferSizeCallback(id, (window, w, h) -> {
            this.width = w;
            this.height = h;
            viewport.update();
            //System.out.println("Resized window at " + w + "x" + h);
        });

        glfwMakeContextCurrent(id);
        glfwSwapInterval(0);
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(id, title);
    }

    public String getTitle() {
        return title;
    }

    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        glfwSetWindowSize(id, width, height);
        viewport.update();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void show() {
        isHidden = false;
        glfwShowWindow(id);
    }

    public void hide() {
        isHidden = true;
        glfwHideWindow(id);
    }

    public boolean isHidden() {
        return isHidden;
    }

    void poolEvents() {
        glfwPollEvents();
    }

    void swapBuffers() {
        glfwSwapBuffers(id);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(id);
    }

    public void close() {
        glfwSetWindowShouldClose(id, true);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Window[");
        sb.append("title='").append(title);
        sb.append("', width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", isHidden=").append(isHidden);
        sb.append(']');
        return sb.toString();
    }
}
