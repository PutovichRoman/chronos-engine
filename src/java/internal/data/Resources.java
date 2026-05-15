package internal.data;

import internal.Engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public abstract class Resources {
    static final Map<String, Image> imageCache = new HashMap<>();
    static final Map<String, Texture> textureCache = new HashMap<>();
    static final Map<String, Shader> shaderCache = new HashMap<>();

    // Потокобезопасный список для частого чтения и редкого изменения
    static final List<CompletableFuture<Void>> imageLoadingTasks = new CopyOnWriteArrayList<>();
    static final List<CompletableFuture<Void>> textureLoadingTasks = new CopyOnWriteArrayList<>();
    static final List<CompletableFuture<Void>> shaderLoadingTasks = new CopyOnWriteArrayList<>();

    static ExecutorService threadPool;

    public static void initThreadPool() {
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public static void shutdownThreadPool() {
        threadPool.shutdown();
    }

    public static int getImageCount() {
        return imageCache.size();
    }

    public static int getTextureCount() {
        return textureCache.size();
    }

    public static int getShaderCount() {
        return shaderCache.size();
    }

    public static boolean isAnyLoading() {
        return !imageLoadingTasks.isEmpty() || !textureLoadingTasks.isEmpty() || !shaderLoadingTasks.isEmpty();
    }

    public static boolean isImagesLoading() {
        return !imageLoadingTasks.isEmpty();
    }

    public static boolean isShadersLoading() {
        return !shaderLoadingTasks.isEmpty();
    }

    public static boolean isTexturesLoading() {
        return !textureLoadingTasks.isEmpty();
    }

    public static void loadImages() {
        try (Stream<Path> stream = Files.find(
                Engine.IMAGES_PATH,
                Integer.MAX_VALUE,
                (path, attrs) ->
                        attrs.isRegularFile() &&
                                (path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png")
                                        || path.toString().toLowerCase().endsWith(".jpeg"))
        )) {
            List<Path> paths = stream.toList();
            for (Path path : paths) {
                String relativePath = Engine.IMAGES_PATH.relativize(path).toString();
                loadImage(relativePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadTextures() {
        try (Stream<Path> stream = Files.find(
                Engine.IMAGES_PATH,
                Integer.MAX_VALUE,
                (path, attrs) ->
                        attrs.isRegularFile() &&
                                (path.toString().toLowerCase().endsWith(".jpg") || path.toString().toLowerCase().endsWith(".png")
                                        || path.toString().toLowerCase().endsWith(".jpeg"))
        )) {
            List<Path> paths = stream.toList();
            for (Path path : paths) {
                String relativePath = Engine.IMAGES_PATH.relativize(path).toString();
                loadTexture(relativePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void waitForImages() {
        if (imageLoadingTasks.isEmpty()) {
            return;
        }
        CompletableFuture.allOf(imageLoadingTasks.toArray(CompletableFuture[]::new)).join();
        imageLoadingTasks.clear();
    }

    public static void waitForTexture() {
        if (textureLoadingTasks.isEmpty()) {
            return;
        }
        CompletableFuture.allOf(textureLoadingTasks.toArray(CompletableFuture[]::new)).join();
        textureLoadingTasks.clear();
    }

    public static void waitForShader() {
        if (shaderLoadingTasks.isEmpty()) {
            return;
        }
        CompletableFuture.allOf(shaderLoadingTasks.toArray(CompletableFuture[]::new)).join();
        shaderLoadingTasks.clear();
    }

    public static void waitForResources() {
        if (textureLoadingTasks.isEmpty() && shaderLoadingTasks.isEmpty() && imageLoadingTasks.isEmpty()) {
            return;
        }
        List<CompletableFuture> allTask = new ArrayList<>(textureLoadingTasks.size() + shaderLoadingTasks.size() + imageLoadingTasks.size());
        allTask.addAll(textureLoadingTasks);
        allTask.addAll(shaderLoadingTasks);
        allTask.addAll(imageLoadingTasks);
        CompletableFuture.allOf(allTask.toArray(CompletableFuture[]::new)).join();
        shaderLoadingTasks.clear();
        textureLoadingTasks.clear();
        imageLoadingTasks.clear();
    }

    public static void loadImage(String path) {
        if (imageCache.containsKey(path) && !imageCache.get(path).isDisposed) {
            return;
        }
        var task = CompletableFuture.supplyAsync(() -> new Image(path), threadPool)
                .thenAccept(image -> imageCache.put(path, image));

        imageLoadingTasks.add(task);
    }

    public static void loadShader(String path) {
        if (shaderCache.containsKey(path) && !shaderCache.get(path).isDisposed) {
            return;
        }
        var task = CompletableFuture.supplyAsync(() -> new Shader(path), threadPool)
                .thenAccept(shader -> shaderCache.put(path, shader));

        shaderLoadingTasks.add(task);
    }

    public static void loadTexture(String path) {
        if (textureCache.containsKey(path) && !textureCache.get(path).isDisposed) {
            return;
        }
        var task = CompletableFuture.supplyAsync(() -> {
                    Image image = new Image(path);
                    Texture texture = new Texture(image, true);
                    image.dispose();
                    return texture;
                }, threadPool)
                .thenAccept(texture -> textureCache.put(path, texture));
        textureLoadingTasks.add(task);
    }

    public static Shader getShader(String path) {
        if (shaderCache.containsKey(path) && !shaderCache.get(path).isDisposed) {
            return shaderCache.get(path);
        }
        Shader shader = new Shader(path);
        shaderCache.put(path, shader);
        return shader;
    }

    public static Image getImage(String path) {
        if (imageCache.containsKey(path) && !imageCache.get(path).isDisposed) {
            return imageCache.get(path);
        }
        Image image = new Image(path);
        imageCache.put(path, image);
        return image;
    }

    public static Texture getTexture(String path) {
        if (textureCache.containsKey(path) && !textureCache.get(path).isDisposed) {
            return textureCache.get(path);
        }
        Image image = new Image(path);
        Texture texture = new Texture(image, true);
        image.dispose();
        textureCache.put(path, texture);
        return texture;
    }

    public static void disposeAll() {
        for (Image image : imageCache.values()) {
            image.dispose();
        }
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        for (Shader shader : shaderCache.values()) {
            shader.dispose();
        }
        imageCache.clear();
        textureCache.clear();
        shaderCache.clear();
    }
}
