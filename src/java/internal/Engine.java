package internal;

import internal.data.Resources;
import internal.lib.utils.FileUtils;
import internal.objects.Node;
import internal.objects.Scene;
import internal.objects.graphic.DrawableNode2D;
import internal.objects.graphic.Renderer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11C.*;

public abstract class Engine {
    public static final Path PROJECT_ROOT = findProjectRoot();
    public static final Path IMAGES_PATH = findImagesPath();
    public static final Path SHADERS_PATH = findShadersPath();
    public static final Path FONTS_PATH = findFontsPath();

    private static final Log log = new Log(Engine.class);

    static Scene currentScene;
    static Renderer renderer;
    static Window window;

    private static boolean sceneChanged;
    private static Scene nextScene;

    public static Scene getActivatedScene() {
        return currentScene;
    }

    public static void activateScene(Class<? extends Scene> sceneClass) {
        try {
            nextScene = sceneClass.getConstructor().newInstance();
            sceneChanged = true;
        } catch (Exception e) {
            log.error("Scene creation failed: {}", e);
        }
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public static Window getWindow() {
        return window;
    }

    static boolean running = false;

    static void init() {
        log.debug("Initializing...");
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = new Window("chronos engine", 800, 600);
        renderer = new Renderer();
        GL.createCapabilities();
        Input.setupCallbacks();
        Mouse.setupCallback();

        GL46.glEnable(GL_BLEND);
        GL46.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Resources.initThreadPool();
        window.viewport.update();
        //Resources.save(Resources.loadShader(Path.of("colored"), "colored"));
    }

    private static double delta;

    public static double getDelta() {
        return delta;
    }

    private static void exitTree(Node node) {
        node.onDetach();
        for (Node child : node.getChildren()) {
            exitTree(child);
        }
    }

    static void sceneChange() {
        if (currentScene != null) {
            currentScene.onDetach();
            exitTree(currentScene);
            renderer.clearAll();
        }
        if (nextScene == null) {
            throw new RuntimeException("Next Scene is null");
        }
        currentScene = nextScene;
        log.debug("Scene '{}' activated", currentScene.getClass().getName());
        currentScene.onAttach();

        nextScene = null;
        sceneChanged = false;
    }

    public static final float MAX_DELTA = 0.075f;

    static void loop() {
        long lastTime = System.nanoTime();

        while (running && !window.shouldClose()) {
            if (sceneChanged) {
                sceneChange();
            }
            window.poolEvents();

            long currentTime = System.nanoTime();
            delta = Math.min((currentTime - lastTime) / 1_000_000_000.0f, MAX_DELTA);
            lastTime = currentTime;

            DrawableNode2D.updateTransforms(currentScene, null);
            currentScene.updateTree((float) delta);

            Input.update();
            Mouse.update();

            renderer.clearBackground();
            renderer.render();
            window.swapBuffers();
        }
    }

    public static void stop() {
        running = false;
    }

    static void cleanup() {
        Resources.shutdownThreadPool();
        glfwTerminate();
    }

    public static boolean isRunning() {
        return running;
    }

    public static void run(Class<? extends Scene> sceneClass) {
        init();
        running = true;
        activateScene(sceneClass);
        loop();
        cleanup();
    }

    private static Path findProjectRoot() {
        try {
            CodeSource cs = Engine.class.getProtectionDomain().getCodeSource();
            Path jarPath = Paths.get(cs.getLocation().toURI());
            // Если это JAR - берём его папку
            if (jarPath.toString().endsWith(".jar")) {
                return jarPath.getParent();
            }

            final Path start = Paths.get("").toAbsolutePath();
            Path current = start;
            while (current != null) {
                if (isProjectRoot(current)) {
                    return current;
                }
                current = current.getParent();
            }
            return start;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isProjectRoot(Path path) {
        if (Files.exists(path.resolve("src"))) {
            return true;
        }
        if (Files.exists(path.resolve("pom.xml")) ||    // Maven
                Files.exists(path.resolve("build.gradle")) || // Gradle
                Files.exists(path.resolve("build.xml"))) {    // Ant
            return true;
        }
        return Files.exists(path.resolve(".idea")) ||
                Files.exists(path.resolve(".vscode"));
    }

    private static Path findImagesPath() {
        return FileUtils.searchPath(PROJECT_ROOT, "images");
    }

    private static Path findFontsPath() {
        return FileUtils.searchPath(PROJECT_ROOT, "fonts");
    }

    private static Path findShadersPath() {
        return FileUtils.searchPath(PROJECT_ROOT, "shaders");
    }
}
