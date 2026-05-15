package internal.data;

import internal.exceptions.MappedBufferException;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class DynamicMesh extends Mesh {
    private static final int DEFAULT_CAPACITY = 16;

    int verticesCapacity = 0; // Размер вершин в байтах
    int uvsCapacity = 0;

    public DynamicMesh(FloatBuffer vertices, FloatBuffer normals, FloatBuffer uvs) {
        super(vertices, normals, uvs);
        verticesCapacity = vertices.capacity();
        uvsCapacity = uvs.capacity();
    }

    public DynamicMesh(int capacity, boolean hasNormals, boolean hasUVs) {
        super(hasNormals, hasUVs);
        resizeVertices(capacity);
        if (hasUVs) {
            resizeUVs(capacity);
        }
    }

    public DynamicMesh(boolean hasNormals, boolean hasUVs) {
        this(DEFAULT_CAPACITY, hasNormals, hasUVs);
    }

    private void bindBuffer(int vbo, int bufferIndex, int size) {
        glBindVertexArray(glVerticesArray);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(bufferIndex, size, GL_FLOAT, false, 0, 0);
        glBindVertexArray(0);
    }

    public void trimToSize() {
        resizeVertices(vertexCount);
        if (hasUVs) {
            resizeUVs(uvsCapacity);
        }
    }

    public void resizeUVs(int capacity) {
        checkNotDisposed();
        if (!hasUVs) {
            throw new IllegalStateException("UVs has not been set");
        }
        if (capacity == uvsCapacity) {
            return;
        }

        ByteBuffer oldData;
        if (capacity > uvCount) {
            // Выделяем ровно под существующие данные
            oldData = MemoryUtil.memAlloc(uvCount * 2 * Float.BYTES);
            glBindBuffer(GL_ARRAY_BUFFER, glUVs);
            glGetBufferSubData(GL_ARRAY_BUFFER, 0, oldData);
            oldData.rewind();
        } else {
            oldData = MemoryUtil.memAlloc(capacity * 2 * Float.BYTES);
            glBindBuffer(GL_ARRAY_BUFFER, glUVs);
            glGetBufferSubData(GL_ARRAY_BUFFER, 0, oldData);
            oldData.rewind();
        }

        int newVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, newVBO);
        glBufferData(GL_ARRAY_BUFFER, capacity * 2L * Float.BYTES, GL_DYNAMIC_DRAW);

        glBufferSubData(GL_ARRAY_BUFFER, 0, oldData);
        MemoryUtil.memFree(oldData);
        if (capacity < uvCount) {
            uvCount = capacity;
        }

        glDeleteBuffers(glUVs);
        this.glUVs = newVBO;
        this.uvsCapacity = capacity;

        bindBuffer(glUVs, UV_INDEX, 2);
    }

    public void resizeVertices(int capacity) {
        checkNotDisposed();
        if (capacity == verticesCapacity) {
            return;
        }

        ByteBuffer oldData;
        if (capacity > vertexCount) {
            // Выделяем ровно под существующие данные
            oldData = MemoryUtil.memAlloc(vertexCount * 3 * Float.BYTES);
            glBindBuffer(GL_ARRAY_BUFFER, glVertices);
            glGetBufferSubData(GL_ARRAY_BUFFER, 0, oldData);
            oldData.rewind();
        } else {
            oldData = MemoryUtil.memAlloc(capacity * 3 * Float.BYTES);
            glBindBuffer(GL_ARRAY_BUFFER, glVertices);
            glGetBufferSubData(GL_ARRAY_BUFFER, 0, oldData);
            oldData.rewind();
        }

        int newVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, newVBO);
        glBufferData(GL_ARRAY_BUFFER, capacity * 3L * Float.BYTES, GL_DYNAMIC_DRAW);

        glBufferSubData(GL_ARRAY_BUFFER, 0, oldData);
        MemoryUtil.memFree(oldData);
        if (capacity < vertexCount) {
            vertexCount = capacity;
        }

        glDeleteBuffers(glVertices);
        this.glVertices = newVBO;
        this.verticesCapacity = capacity;

        bindBuffer(glVertices, VERTEX_INDEX, 3);
    }

    public void appendVertices(FloatBuffer newVertices) {
        checkNotDisposed();
        int newVertexCount = newVertices.capacity() / 3;
        if (newVertexCount == 0) {
            return;
        }

        if (vertexCount + newVertexCount > verticesCapacity) {
            int minCapacity = vertexCount + newVertexCount;
            int newCapacity = verticesCapacity + (verticesCapacity >> 1);  // 1.5× от текущей
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            resizeVertices(newCapacity);
        }

        // Записываем новые данные в конец
        int offsetBytes = vertexCount * 3 * Float.BYTES;
        glBindBuffer(GL_ARRAY_BUFFER, glVertices);
        glBufferSubData(GL_ARRAY_BUFFER, offsetBytes, newVertices);
        vertexCount += newVertexCount;
    }

    public void appendUVs(FloatBuffer newUvs) {
        if (!hasUVs) {
            throw new IllegalStateException("UVs has not been set");
        }
        checkNotDisposed();
        int newUVsCount = newUvs.capacity() / 2;
        if (newUVsCount == 0) {
            return;
        }

        if (uvCount + newUVsCount > uvsCapacity) {
            int minCapacity = uvCount + newUVsCount;
            int newCapacity = uvsCapacity + (uvsCapacity >> 1);  // 1.5× от текущей
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            resizeUVs(newCapacity);
        }

        // Записываем новые данные в конец
        int offsetBytes = uvCount * 2 * Float.BYTES;
        glBindBuffer(GL_ARRAY_BUFFER, glUVs);
        glBufferSubData(GL_ARRAY_BUFFER, offsetBytes, newUvs);
        uvCount += newUVsCount;
    }

    public void appendUVs(float[] newUVs) {
        FloatBuffer newUVsBuffer = MemoryUtil.memAllocFloat(newUVs.length);
        newUVsBuffer.put(newUVs);
        newUVsBuffer.flip();
        appendUVs(newUVsBuffer);
        MemoryUtil.memFree(newUVsBuffer);
    }

    public void appendVertices(float[] newVertices) {
        FloatBuffer newVerticesBuffer = MemoryUtil.memAllocFloat(newVertices.length);
        newVerticesBuffer.put(newVertices);
        newVerticesBuffer.flip();
        appendVertices(newVerticesBuffer);
        MemoryUtil.memFree(newVerticesBuffer);
    }

    public void removeLastVertices(int count) {
        if (count <= 0) return;
        int newVertexCount = vertexCount - count;
        if (newVertexCount < 0) newVertexCount = 0;
        vertexCount = newVertexCount;
    }

    public void removeLastUVs(int count) {
        if (count <= 0) return;
        int newUVsCount = uvCount - count;
        if (newUVsCount < 0) newUVsCount = 0;
        uvCount = newUVsCount;
    }

    public void setVertices(int index, FloatBuffer verticesBuffer) {
        checkNotDisposed();
        if (index < 0 || index + verticesBuffer.capacity() / 3 > vertexCount) {
            throw new IndexOutOfBoundsException();
        }

        glBindBuffer(GL_ARRAY_BUFFER, glVertices);
        int offset = index * 3 * Float.BYTES; // Смещение в байтах от начала буфера
        int length = verticesBuffer.capacity() * Float.BYTES; // Количество байт для отображения
        int flags = GL46.GL_MAP_WRITE_BIT;

        ByteBuffer mappedBuffer = GL46.glMapBufferRange(GL46.GL_ARRAY_BUFFER, offset, length, flags, null);
        if (mappedBuffer == null) {
            throw new MappedBufferException("Failed to map buffer range for vertex");
        }

        mappedBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = mappedBuffer.asFloatBuffer();
        for (int i = 0; i < verticesBuffer.capacity(); i++) {
            floatBuffer.put(i, verticesBuffer.get(i));
        }

        if (!glUnmapBuffer(GL_ARRAY_BUFFER)) {
            throw new MappedBufferException("Buffer corruption during unmap");
        }
    }

    public void setUVs(int index, FloatBuffer uvsBuffer) {
        checkNotDisposed();
        if (index < 0 || index + uvsBuffer.capacity() / 2 > uvCount) {
            throw new IndexOutOfBoundsException();
        }

        glBindBuffer(GL_ARRAY_BUFFER, glUVs);
        int offset = index * 2 * Float.BYTES; // Смещение в байтах от начала буфера
        int length = uvsBuffer.capacity() * Float.BYTES; // Количество байт для отображения
        int flags = GL46.GL_MAP_WRITE_BIT;

        ByteBuffer mappedBuffer = GL46.glMapBufferRange(GL46.GL_ARRAY_BUFFER, offset, length, flags, null);
        if (mappedBuffer == null) {
            throw new MappedBufferException("Failed to map buffer range for vertex");
        }

        mappedBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = mappedBuffer.asFloatBuffer();
        for (int i = 0; i < uvsBuffer.capacity(); i++) {
            floatBuffer.put(i, uvsBuffer.get(i));
        }

        if (!glUnmapBuffer(GL_ARRAY_BUFFER)) {
            throw new MappedBufferException("Buffer corruption during unmap");
        }
    }

    public void reset() {
        vertexCount = 0;
    }

    public int getVerticesCapacity() {
        return verticesCapacity;
    }

    public int getUVsCapacity() {
        return uvsCapacity;
    }
}