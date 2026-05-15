package internal.lib.math.vectors;

import org.lwjgl.BufferUtils;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Vector2I {
    public int x;
    public int y;

    public Vector2I(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2I() {
        this(0, 0);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Vector2I add(Vector2I v) {
        x += v.x();
        y += v.y();
        return this;
    }

    public Vector2I sub(Vector2I v) {
        x -= v.x();
        y -= v.y();
        return this;
    }

    public Vector2I mul(Vector2I v) {
        x *= v.x();
        y *= v.y();
        return this;
    }

    public Vector2I add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2I sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2I mul(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2F toFloat() {
        return new Vector2F(x, y);
    }

    public IntBuffer toBuffer() {
        IntBuffer buffer = BufferUtils.createIntBuffer(2);
        buffer.put(x);
        buffer.put(y);
        buffer.flip();
        return buffer;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Vector2I writeTo(MemorySegment segment, long offset) {
        // set() автоматически обрабатывает выравнивание
        segment.set(ValueLayout.JAVA_INT, offset, this.x);
        segment.set(ValueLayout.JAVA_INT, offset + Integer.BYTES, this.y);
        return this;
    }

    public Vector2I readFrom(MemorySegment segment, long offset) {
        x = segment.get(ValueLayout.JAVA_INT, offset);
        y = segment.get(ValueLayout.JAVA_INT, offset + Integer.BYTES);
        return this;
    }
}
