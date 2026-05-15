package internal.lib.math.vectors;

import internal.lib.math.points.Point2F;
import org.lwjgl.BufferUtils;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.FloatBuffer;

public class Vector2F {
    public float x;
    public float y;

    public Vector2F() {
        x = 0f;
        y = 0f;
    }

    public Vector2F(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2F(Vector2F v) {
        this.x = v.x();
        this.y = v.y();
    }

    public Vector2F(Vector2I v) {
        this.x = v.x();
        this.y = v.y();
    }

    public Vector2F set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2F set(float d) {
        x = d;
        y = d;
        return this;
    }

    public Vector2F set(Vector2F v) {
        this.x = v.x();
        this.y = v.y();
        return this;
    }

    public Vector2F set(Point2F p) {
        set(p.x(), p.y());
        return this;
    }

    public Vector2F add(Point2F p) {
        add(p.x(), p.y());
        return this;
    }

    public Vector2F sub(Point2F p) {
        sub(p.x(), p.y());
        return this;
    }

    public Vector2F div(Point2F p) {
        div(p.x(), p.y());
        return this;
    }

    public Vector2F mul(Point2F p) {
        mul(p.x(), p.y());
        return this;
    }

    public Vector2F add(Vector2F v) {
        x += v.x();
        y += v.y();
        return this;
    }

    public Vector2F set(Vector2I v) {
        this.x = v.x();
        this.y = v.y();
        return this;
    }

    public Vector2F add(Vector2I v) {
        x += v.x();
        y += v.y();
        return this;
    }

    public Vector2F add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2F sub(Vector2F v) {
        x -= v.x();
        y -= v.y();
        return this;
    }

    public Vector2F sub(Vector2I v) {
        x -= v.x();
        y -= v.y();
        return this;
    }

    public Vector2F sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2F mul(Vector2F v) {
        x *= v.x();
        y *= v.y();
        return this;
    }

    public Vector2F mul(Vector2I v) {
        x *= v.x();
        y *= v.y();
        return this;
    }

    public Vector2F mul(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2F mul(float d) {
        this.x *= d;
        this.y *= d;
        return this;
    }

    public Vector2F div(Vector2F v) {
        x /= v.x();
        y /= v.y();
        return this;
    }

    public Vector2F div(Vector2I v) {
        x /= v.x();
        y /= v.y();
        return this;
    }

    public Vector2F div(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vector2F negate() {
        x = -x;
        y = -y;
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float dot(Vector2F v) {
        return x * v.x() + y * v.y();
    }

    public float dot(Vector2I v) {
        return x * v.x() + y * v.y();
    }

    public float angle() {
        return (float) Math.atan2(x, y);
    }

    public Vector2F normalize() {
        float len = length();
        x /= len;
        y /= len;
        return this;
    }

    public Vector2F rotate(float radians) {
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float newX = x * cos - y * sin;
        float newY = x * sin + y * cos;
        x = newX;
        y = newY;
        return this;
    }

    public Vector2F abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public Vector2F floor() {
        x = (float) Math.floor(x);
        y = (float) Math.floor(y);
        return this;
    }

    public Vector2F ceil() {
        x = (float) Math.ceil(x);
        y = (float) Math.ceil(y);
        return this;
    }

    public Vector2F round() {
        x = Math.round(x);
        y = Math.round(y);
        return this;
    }

    public Vector2I toInt() {
        return new Vector2I((int) x, (int) y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public FloatBuffer toBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(2);
        buffer.put(x);
        buffer.put(y);
        buffer.flip();
        return buffer;
    }

    public Vector2F writeTo(MemorySegment segment, long offset) {
        // set() автоматически обрабатывает выравнивание
        segment.set(ValueLayout.JAVA_FLOAT, offset, this.x);
        segment.set(ValueLayout.JAVA_FLOAT, offset + Float.BYTES, this.y);
        return this;
    }

    public Vector2F readFrom(MemorySegment segment, long offset) {
        x = segment.get(ValueLayout.JAVA_FLOAT, offset);
        y = segment.get(ValueLayout.JAVA_FLOAT, offset + Float.BYTES);
        return this;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}
