package internal.graphic;

public value record Color(float red, float green, float blue, float alpha) {
    public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);
    public static final Color RED = new Color(1f, 0f, 0f, 1f);
    public static final Color GREEN = new Color(0f, 1f, 0f, 1f);
    public static final Color BLUE = new Color(0f, 0f, 1f, 1f);
    public static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    public static final Color BLACK = new Color(0f, 0f, 0f, 1f);
    public static final Color GREY = new Color(127, 127, 127, 255);

    public static final Color ANTHRACITE_GREY = new Color(41, 49, 51, 255);
    public static final Color DEEP_RED = new Color(123, 0, 28, 255);
    public static final Color AMARANTH_PINK = Color.fromHex("#f19cbb"); // Амарантово-розовый
    public static final Color CARAMEL_PINK = Color.fromHex("#e4717a"); // Карамельно-розовый

    public static Color fromHex(String hex) {
        String cleanHex = hex.startsWith("#") ? hex.substring(1) : hex;
        int rgb8 = Integer.parseInt(cleanHex, 16);
        return new Color((rgb8 >> 16) & 0xFF, (rgb8 >> 8) & 0xFF, rgb8 & 0xFF, 255);
    }

    public Color(int red8, int green8, int blue8, int alpha8) {
        this(red8 / 255f, green8 / 255f, blue8 / 255f, alpha8 / 255f);
    }

    public Color(int red8, int green8, int blue8) {
        this(red8 / 255f, green8 / 255f, blue8 / 255f, 1f);
    }

    public Color {
        red = Math.clamp(red, 0f, 1f);
        green = Math.clamp(green, 0f, 1f);
        blue = Math.clamp(blue, 0f, 1f);
        alpha = Math.clamp(alpha, 0f, 1f);
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    public int toRed8() {
        return Math.round(red * 255f);
    }

    public int toGreen8() {
        return Math.round(green * 255f);
    }

    public int toBlue8() {
        return Math.round(blue * 255f);
    }

    public int toAlpha8() {
        return Math.round(alpha * 255f);
    }

    public Color withRed(float newRed) {
        return new Color(newRed, green, blue, alpha);
    }

    public Color withGreen(float newGreen) {
        return new Color(red, newGreen, blue, alpha);
    }

    public Color withBlue(float newBlue) {
        return new Color(red, green, newBlue, alpha);
    }

    public Color withAlpha(float newAlpha) {
        return new Color(red, green, blue, newAlpha);
    }


    public Color withRed(int newRed8) {
        return new Color(newRed8 / 255f, green, blue, alpha);
    }

    public Color withGreen(int newGreen8) {
        return new Color(red, newGreen8 / 255f, blue, alpha);
    }

    public Color withBlue(int newBlue8) {
        return new Color(red, green, newBlue8 / 255f, alpha);
    }

    public Color withAlpha(int newAlpha8) {
        return new Color(red, green, blue, newAlpha8 / 255f);
    }

    public Color inverted() {
        return new Color(1f - red, 1f - green, 1f - blue, alpha);
    }

    public Color plus(Color other) {
        return plus(other.red, other.green, other.blue, other.alpha);
    }

    public Color plus(float red, float green, float blue) {
        return new Color(this.red + red, this.green + green, this.blue + blue, alpha);
    }

    public Color plus(float red, float green, float blue, float alpha) {
        return new Color(this.red + red, this.green + green, this.blue + blue, this.alpha + alpha);
    }

    public Color plus(int red8, int green8, int blue8) {
        return new Color(this.red + red8 / 255f, this.green + green8 / 255f, this.blue + blue8 / 255f, alpha);
    }

    public Color plus(int red8, int green8, int blue8, int alpha8) {
        return new Color(this.red + red8 / 255f, this.green + green8 / 255f, this.blue + blue8 / 255f, this.alpha + alpha8 / 255f);
    }

    public Color mul(float factor) {
        return new Color(red * factor, green * factor, blue * factor, alpha);
    }

    public Color mul(float red, float green, float blue, float alpha) {
        return new Color(this.red * red, this.green * green, this.blue * blue, this.alpha * alpha);
    }

    public Color mul(float red, float green, float blue) {
        return new Color(this.red * red, this.green * green, this.blue * blue, alpha);
    }

    public Color mul(Color other) {
        return mul(other.red, other.green, other.blue, other.alpha);
    }

    public Color brighter(float factor) {
        return mul(1f + factor);
    }

    public Color darker(float factor) {
        return mul(1f - factor);
    }

    public Color lerp(Color other, float t) {
        return lerp(other.red, other.green, other.blue, other.alpha, t);
    }

    public Color lerp(float red, float green, float blue, float alpha, float t) {
        return new Color(
                red + (this.red - red) * t,
                green + (this.green - green) * t,
                blue + (this.blue - blue) * t,
                alpha + (this.alpha - alpha) * t
        );
    }

    public Color lerp(float red, float green, float blue, float t) {
        return new Color(
                red + (this.red - red) * t,
                green + (this.green - green) * t,
                blue + (this.blue - blue) * t,
                alpha
        );
    }

    public String toHex() {
        int rgb8 = (toRed8() << 16) | (toGreen8() << 8) | toBlue8();
        return '#' + Integer.toHexString(rgb8).toUpperCase();
    }
}
