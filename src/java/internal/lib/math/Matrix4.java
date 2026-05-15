package internal.lib.math;

import internal.lib.math.vectors.Vector2F;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Locale;

/*
m00, m01, m02, m03,
m10, m11, m12, m13,
m20, m21, m22, m23,
m30, m31, m32, m33
 */
public value record Matrix4(float m00, float m01, float m02, float m03,
                            float m10, float m11, float m12, float m13,
                            float m20, float m21, float m22, float m23,
                            float m30, float m31, float m32, float m33) {

    private static final Matrix4 IDENTITY = new Matrix4(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    );

    private static final Matrix4 ZERO = new Matrix4(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0
    );

    public static Matrix4 identity() {
        return IDENTITY;
    }

    public static Matrix4 zero() {
        return ZERO;
    }

    public static Matrix4 of(Vector2F position, Vector2F scale, float rotation) {
        return Matrix4.identity()
                .translate(position)
                .rotateZ(rotation)
                .scale(scale);
    }

    public Matrix4 mul(Matrix4 b) {
        return new Matrix4(
                m00 * b.m00 + m01 * b.m10 + m02 * b.m20 + m03 * b.m30,
                m00 * b.m01 + m01 * b.m11 + m02 * b.m21 + m03 * b.m31,
                m00 * b.m02 + m01 * b.m12 + m02 * b.m22 + m03 * b.m32,
                m00 * b.m03 + m01 * b.m13 + m02 * b.m23 + m03 * b.m33,

                m10 * b.m00 + m11 * b.m10 + m12 * b.m20 + m13 * b.m30,
                m10 * b.m01 + m11 * b.m11 + m12 * b.m21 + m13 * b.m31,
                m10 * b.m02 + m11 * b.m12 + m12 * b.m22 + m13 * b.m32,
                m10 * b.m03 + m11 * b.m13 + m12 * b.m23 + m13 * b.m33,

                m20 * b.m00 + m21 * b.m10 + m22 * b.m20 + m23 * b.m30,
                m20 * b.m01 + m21 * b.m11 + m22 * b.m21 + m23 * b.m31,
                m20 * b.m02 + m21 * b.m12 + m22 * b.m22 + m23 * b.m32,
                m20 * b.m03 + m21 * b.m13 + m22 * b.m23 + m23 * b.m33,

                m30 * b.m00 + m31 * b.m10 + m32 * b.m20 + m33 * b.m30,
                m30 * b.m01 + m31 * b.m11 + m32 * b.m21 + m33 * b.m31,
                m30 * b.m02 + m31 * b.m12 + m32 * b.m22 + m33 * b.m32,
                m30 * b.m03 + m31 * b.m13 + m32 * b.m23 + m33 * b.m33
        );
    }

    public Matrix4 translate(float x, float y, float z) {
        return new Matrix4(
                m00, m01, m02, m03 + x,
                m10, m11, m12, m13 + y,
                m20, m21, m22, m23 + z,
                m30, m31, m32, m33
        );
    }

    public Matrix4 translate(Vector2F positon) {
        return new Matrix4(
                m00, m01, m02, m03 + positon.x,
                m10, m11, m12, m13 + positon.y,
                m20, m21, m22, m23,
                m30, m31, m32, m33
        );
    }

    public Matrix4 translateX(float x) {
        return new Matrix4(
                m00, m01, m02, m03 + x,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33
        );
    }

    public Matrix4 translateY(float y) {
        return new Matrix4(
                m00, m01, m02, m03,
                m10, m11, m12, m13 + y,
                m20, m21, m22, m23,
                m30, m31, m32, m33
        );
    }

    public Matrix4 translateZ(float z) {
        return new Matrix4(
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23 + z,
                m30, m31, m32, m33
        );
    }

    public Matrix4 rotateZ(float radians) {
        float c = (float) Math.cos(radians);
        float s = (float) Math.sin(radians);

        return this.mul(new Matrix4(
                c, -s, 0, 0,
                s, c, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        ));
    }

    public Matrix4 scale(float sx, float sy, float sz) {
        return this.mul(new Matrix4(
                sx, 0, 0, 0,
                0, sy, 0, 0,
                0, 0, sz, 0,
                0, 0, 0, 1
        ));
    }

    public Matrix4 scale(Vector2F scale) {
        return this.mul(new Matrix4(
                scale.x, 0, 0, 0,
                0, scale.y, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        ));
    }

    public Matrix4 scale(float s) {
        return scale(s, s, s);
    }

    public static Matrix4 ortho(float left, float right, float bottom, float top, float near, float far) {
        float rmw = 1.0f / (right - left);
        float tmh = 1.0f / (top - bottom);
        float fmn = 1.0f / (far - near);

        return new Matrix4(
                2.0f * rmw, 0, 0, -(right + left) * rmw,
                0, 2.0f * tmh, 0, -(top + bottom) * tmh,
                0, 0, -2.0f * fmn, -(far + near) * fmn,
                0, 0, 0, 1
        );
    }

    public FloatBuffer toFloatBuffer() {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        fb.put(m00).put(m10).put(m20).put(m30);
        fb.put(m01).put(m11).put(m21).put(m31);
        fb.put(m02).put(m12).put(m22).put(m32);
        fb.put(m03).put(m13).put(m23).put(m33);
        fb.flip();
        return fb;
    }

    public float[] toFloatArray() {
        return new float[]{
                m00, m10, m20, m30,
                m01, m11, m21, m31,
                m02, m12, m22, m32,
                m03, m13, m23, m33
        };
    }

    public void get(FloatBuffer fb) {
        fb.rewind();
        fb.put(m00).put(m10).put(m20).put(m30);
        fb.put(m01).put(m11).put(m21).put(m31);
        fb.put(m02).put(m12).put(m22).put(m32);
        fb.put(m03).put(m13).put(m23).put(m33);
        fb.flip();
    }

    public void get(float[] array) {
        array[0] = m00;
        array[1] = m10;
        array[2] = m20;
        array[3] = m30;

        array[4] = m01;
        array[5] = m11;
        array[6] = m21;
        array[7] = m31;

        array[8] = m02;
        array[9] = m12;
        array[10] = m22;
        array[11] = m32;

        array[12] = m03;
        array[13] = m13;
        array[14] = m23;
        array[15] = m33;
    }

    public boolean isFinite() {
        return !Float.isInfinite(m00) && !Float.isNaN(m00) &&
                !Float.isInfinite(m01) && !Float.isNaN(m01) &&
                !Float.isInfinite(m02) && !Float.isNaN(m02) &&
                !Float.isInfinite(m03) && !Float.isNaN(m03) &&
                !Float.isInfinite(m10) && !Float.isNaN(m10) &&
                !Float.isInfinite(m11) && !Float.isNaN(m11) &&
                !Float.isInfinite(m12) && !Float.isNaN(m12) &&
                !Float.isInfinite(m13) && !Float.isNaN(m13) &&
                !Float.isInfinite(m20) && !Float.isNaN(m20) &&
                !Float.isInfinite(m21) && !Float.isNaN(m21) &&
                !Float.isInfinite(m22) && !Float.isNaN(m22) &&
                !Float.isInfinite(m23) && !Float.isNaN(m23) &&
                !Float.isInfinite(m30) && !Float.isNaN(m30) &&
                !Float.isInfinite(m31) && !Float.isNaN(m31) &&
                !Float.isInfinite(m32) && !Float.isNaN(m32) &&
                !Float.isInfinite(m33) && !Float.isNaN(m33);
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
                "[%8.2f, %8.2f, %8.2f, %8.2f\n" +
                        " %8.2f, %8.2f, %8.2f, %8.2f\n" +
                        " %8.2f, %8.2f, %8.2f, %8.2f\n" +
                        " %8.2f, %8.2f, %8.2f, %8.2f]",
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33
        );
    }
}
