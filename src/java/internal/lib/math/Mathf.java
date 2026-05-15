package internal.lib.math;

public abstract class Mathf {
    public static final float PI = 3.14159265358979f;
    private static final float[] COSINES;
    private static final float[] SINUSES;

    static {
        COSINES = new float[360];
        for (int i = 0; i < 360; i++) {
            COSINES[i] = (float) Math.cos(Math.toRadians(i));
        }

        SINUSES = new float[360];
        for (int i = 0; i < 360; i++) {
            SINUSES[i] = (float) Math.sin(Math.toRadians(i));
        }
    }

    public static float toRadians(float angle) {
        return (float) Math.toRadians(angle);
    }

    public static float cos(double a) {
        return (float) Math.cos(a);
    }

    public static float cosDegrees(int angle) {
        angle %= 360;
        if (angle < 0) angle += 360;
        return COSINES[angle];
    }

    public static float sin(double a) {
        return (float) Math.sin(a);
    }

    public static float sinDegrees(int angle) {
        angle %= 360;
        if (angle < 0) angle += 360;
        return SINUSES[angle];
    }
}
