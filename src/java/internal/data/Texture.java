package internal.data;

import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;

public class Texture extends Resource {
    final int width, height;
    boolean hasMipmap = false;
    int glTexture; // ID текстуры в OpenGL

    public Texture(Image image, boolean generateMipmap) {
        glTexture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, glTexture);

        // Настройка текстуры
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Загружаем данные в OpenGL
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, GL46.GL_RGBA, image.width, image.height, 0,
                GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, image.pixelsBuffer);

        this.width = image.getWidth();
        this.height = image.getHeight();

        // Создание мип-мапы
        if (generateMipmap) {
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAG_FILTER, GL46.GL_LINEAR);
            GL46.glTexParameteri(GL46.GL_TEXTURE_2D, GL46.GL_TEXTURE_MIN_FILTER, GL46.GL_LINEAR_MIPMAP_LINEAR);
            GL46.glGenerateMipmap(GL_TEXTURE_2D);
            hasMipmap = true;
        }
    }

    public Texture(ByteBuffer buffer, int width, int height, int format, boolean generateMipmap) {
        glTexture = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, glTexture);

        // Настройка текстуры
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GL46.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Загружаем данные в OpenGL
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, format, width, height, 0,
                format, GL46.GL_UNSIGNED_BYTE, buffer);

        this.width = width;
        this.height = height;

        // Создание мип-мапы
        if (generateMipmap) {
            GL46.glGenerateMipmap(GL_TEXTURE_2D);
            hasMipmap = true;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Texture(Image image) {
        this(image, false);
    }

    public boolean hasMipmap() {
        return hasMipmap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Texture[");
        sb.append("glTexture=").append(glTexture);
        sb.append(']');
        return sb.toString();
    }

    public void bind(int slot) {
        checkNotDisposed();
        GL46.glActiveTexture(GL_TEXTURE0 + slot);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, glTexture);
    }

    @Override
    public void dispose() {
        super.dispose();
        GL46.glDeleteTextures(glTexture);
        glTexture = -1;
        hasMipmap = false;
    }
}
