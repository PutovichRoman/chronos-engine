package internal.data;

import internal.lib.EmptyArrays;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh extends Resource {
    public static final int VERTEX_INDEX = 0;
    public static final int NORMALS_INDEX = 1;
    public static final int UV_INDEX = 2;

    int vertexCount; // Количество вершин
    int normalCount; // Количество нормалей
    int uvCount; // Количество UV вершин

    boolean hasNormals;
    boolean hasUVs;

    int glVerticesArray;
    int glVertices; // index '0'
    int glNormals; // index '1'
    int glUVs; // index '2'

    public int getVertexCount() {
        return vertexCount;
    }

    public int getNormalCount() {
        return normalCount;
    }

    public int getUVCount() {
        return uvCount;
    }

    public float[] getNormals() {
        if (!hasNormals) {
            return EmptyArrays.FLOAT;
        }
        float[] normals = new float[normalCount * 3];
        readBuffer(glNormals, normals);
        return normals;
    }

    public float[] getUVs() {
        if (!hasUVs) {
            return EmptyArrays.FLOAT;
        }
        float[] uvs = new float[uvCount * 2];
        readBuffer(glUVs, uvs);
        return uvs;
    }

    public float[] getVertices() {
        float[] vertices = new float[vertexCount * 3];
        readBuffer(glVertices, vertices);
        return vertices;
    }

    void readBuffer(int bufferID, FloatBuffer buffer) {
        buffer.rewind();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, bufferID);
        ByteBuffer mappedBuffer = GL46.glMapBuffer(GL46.GL_ARRAY_BUFFER, GL46.GL_READ_ONLY);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            if (mappedBuffer != null) {
                // Получаем размер буфера в байтах
                IntBuffer size = stack.mallocInt(1);
                GL46.glGetBufferParameteriv(GL46.GL_ARRAY_BUFFER, GL46.GL_BUFFER_SIZE, size);
                int floatCount = size.get(0) / Float.BYTES;

                for (int i = 0; i < floatCount; i++) {
                    buffer.put(mappedBuffer.getFloat(i * Float.BYTES));
                }
                GL46.glUnmapBuffer(GL46.GL_ARRAY_BUFFER);
            }
        }
    }

    void readBuffer(int bufferID, float[] array) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertexCount * 3);
        readBuffer(bufferID, buffer);
        buffer.rewind();
        buffer.get(array);
        MemoryUtil.memFree(buffer);
    }

    public Mesh(FloatBuffer vertices, FloatBuffer normals, FloatBuffer uvs) {
        glVerticesArray = GL46.glGenVertexArrays(); // Создаём массив буферов
        GL46.glBindVertexArray(glVerticesArray); // Активируем массив

        // Координаты вершин
        if (vertices == null) {
            throw new IllegalArgumentException("Vertices can't be null");
        }
        this.glVertices = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.glVertices); // Активируем буфер для его изменения
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, vertices, GL46.GL_DYNAMIC_DRAW); // Записываем данные
        // Настройка атрибутов вершин
        GL46.glVertexAttribPointer(VERTEX_INDEX /*индекс для работы в шейдерах*/,
                3 /*сколько чисел занимает одна вершина*/,
                GL46.GL_FLOAT /*тип данных чисел*/,
                false, 0, 0);
        vertexCount = vertices.capacity() / 3;

        GL46.glEnableVertexAttribArray(VERTEX_INDEX);

        if (normals != null) {
            // Координаты нормалей
            this.glNormals = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.glNormals);
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, normals, GL46.GL_STATIC_DRAW);
            GL46.glVertexAttribPointer(NORMALS_INDEX /*индекс для работы в шейдерах*/,
                    3 /*сколько чисел занимает одна вершина*/,
                    GL46.GL_FLOAT /*тип данных чисел*/,
                    false, 0, 0);
            normalCount = normals.capacity() / 3;

            hasNormals = true;
            GL46.glEnableVertexAttribArray(NORMALS_INDEX);
        }

        if (uvs != null) {
            // Координаты вершин текстуры
            this.glUVs = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.glUVs);
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, uvs, GL46.GL_STATIC_DRAW);
            GL46.glVertexAttribPointer(UV_INDEX /*индекс для работы в шейдерах*/,
                    2 /*сколько чисел занимает одна вершина*/,
                    GL46.GL_FLOAT /*тип данных чисел*/,
                    false, 0, 0);
            uvCount = uvs.capacity() / 2;

            hasUVs = true;
            GL46.glEnableVertexAttribArray(UV_INDEX);
        }

        GL46.glBindVertexArray(0); // Отвязка массива вершин
    }

    public Mesh(boolean hasNormals, boolean hasUVs) {
        glVerticesArray = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(glVerticesArray);

        // Создаём пустые буферы
        this.glVertices = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.glVertices);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, 0, GL46.GL_DYNAMIC_DRAW);
        GL46.glVertexAttribPointer(VERTEX_INDEX, 3, GL46.GL_FLOAT, false, 0, 0);
        GL46.glEnableVertexAttribArray(VERTEX_INDEX);

        if (hasNormals) {
            this.glNormals = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.glNormals);
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, 0, GL46.GL_DYNAMIC_DRAW);
            GL46.glVertexAttribPointer(NORMALS_INDEX, 3, GL46.GL_FLOAT, false, 0, 0);
            GL46.glEnableVertexAttribArray(NORMALS_INDEX);
            this.hasNormals = true;
        }

        if (hasUVs) {
            this.glUVs = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.glUVs);
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, 0, GL46.GL_DYNAMIC_DRAW);
            GL46.glVertexAttribPointer(UV_INDEX, 2, GL46.GL_FLOAT, false, 0, 0);
            GL46.glEnableVertexAttribArray(UV_INDEX);
            this.hasUVs = true;
        }

        GL46.glBindVertexArray(0);
        vertexCount = 0;
        normalCount = 0;
        uvCount = 0;
    }

    public int getVAO() {
        return glVerticesArray;
    }

    public int getVBO(int index) {
        switch (index) {
            case 0 -> {
                return glVertices;
            }

            case 1 -> {
                return glNormals;
            }

            case 2 -> {
                return glUVs;
            }
        }
        throw new IllegalArgumentException("Invalid index");
    }

    public boolean hasNormals() {
        return hasNormals && normalCount > 0;
    }

    public boolean hasUVs() {
        return hasUVs && uvCount > 0;
    }

    @Override
    public void dispose() {
        super.dispose();
        GL46.glDeleteVertexArrays(glVerticesArray);
        GL46.glDeleteBuffers(glNormals);
        GL46.glDeleteBuffers(glUVs);
        GL46.glDeleteBuffers(glVertices);

        uvCount = 0;
        vertexCount = 0;
        normalCount = 0;
        hasNormals = false;
        hasUVs = false;
    }

    public void glDraw() {
        GL46.glBindVertexArray(glVerticesArray);
        GL46.glDrawArrays(GL46.GL_TRIANGLES, 0, vertexCount);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mesh[");
        sb.append("vertexCount=").append(vertexCount);
        sb.append(", normalCount=").append(normalCount);
        sb.append(", uvCount=").append(uvCount);
        sb.append(", isDisposed=").append(isDisposed);
        sb.append(']');
        return sb.toString();
    }
}
