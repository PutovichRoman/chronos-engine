package internal.data;

import internal.Engine;
import internal.exceptions.ResourceException;
import internal.graphic.Color;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

public class Image extends Resource {
    int width;
    int height;
    final ByteBuffer pixelsBuffer;

    public Image(String path) {
        STBImage.stbi_set_flip_vertically_on_load(true);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            Path realPath = Engine.IMAGES_PATH.resolve(path).toRealPath();
            pixelsBuffer = STBImage.stbi_load(realPath.toString(), w, h, channels, 4);
            if (pixelsBuffer == null) {
                throw new ResourceException("Failed to load image: " + STBImage.stbi_failure_reason());
            }
            width = w.get(0);
            height = h.get(0);
        } catch (IOException e) {
            throw new ResourceException("Failed to load image\n" + STBImage.stbi_failure_reason());
        }
    }

    Image(ByteBuffer pixelsBuffer, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        this.pixelsBuffer = pixelsBuffer;
    }

    public Image(byte[] pixels, int width, int height) {
        if (width <= 0 || height <= 0 || pixels.length != width * height * 4) {
            throw new IllegalArgumentException();
        }
        this.pixelsBuffer = MemoryUtil.memAlloc(width * height * 4);
        this.pixelsBuffer.put(pixels);
        this.width = width;
        this.height = height;
    }

    public Image(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
        this.pixelsBuffer = MemoryUtil.memAlloc(width * height * 4);
    }

    public int getWidth() {
        checkNotDisposed();
        return width;
    }

    public int getHeight() {
        checkNotDisposed();
        return height;
    }

    public Color getPixel(int x, int y) {
        checkNotDisposed();
        int index = (x + y * width) * 4;  // *4 потому что 4 байта на пиксель

        int r = pixelsBuffer.get(index) & 0xFF;      // красный
        int g = pixelsBuffer.get(index + 1) & 0xFF;  // зелёный
        int b = pixelsBuffer.get(index + 2) & 0xFF;  // синий
        int a = pixelsBuffer.get(index + 3) & 0xFF;  // альфа

        // Упаковываем в один int: 0xRRGGBBAA
        return new Color(r, g, b, a);
    }

    public void setPixel(int x, int y, Color color) {
        checkNotDisposed();
        int index = (x + y * width) * 4;
        pixelsBuffer.put(index, (byte) color.toRed8());
        pixelsBuffer.put(index + 1, (byte) color.toGreen8());
        pixelsBuffer.put(index + 2, (byte) color.toBlue8());
        pixelsBuffer.put(index + 3, (byte) color.toAlpha8());
    }

    public void fill(Color color) {
        checkNotDisposed();
        byte[] pixel = new byte[4];
        pixel[0] = (byte) color.toRed8();
        pixel[1] = (byte) color.toGreen8();
        pixel[2] = (byte) color.toBlue8();
        pixel[3] = (byte) color.toAlpha8();

        // Заполняем буфер копированием
        for (int i = 0; i < width * height; i++) {
            pixelsBuffer.put(pixel);  // копирует все 4 байта сразу
        }
    }

    public byte[] getPixels() {
        checkNotDisposed();
        byte[] pixels = new byte[width * height];
        pixelsBuffer.get(pixels);
        return pixels;
    }

    @Override
    public void dispose() {
        super.dispose();
        MemoryUtil.memFree(pixelsBuffer);
        width = -1;
        height = -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Image[");
        sb.append("width=");
        sb.append(width);
        sb.append(", height=");
        sb.append(height);
        sb.append(']');
        return sb.toString();
    }
}
