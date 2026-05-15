package internal.data;

import internal.Engine;
import internal.Log;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static internal.Engine.FONTS_PATH;
import static org.lwjgl.opengl.GL11C.GL_RED;

public class Font extends Resource {
    public static String DEFAULT_CHARACTERS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ_-=$#*&!?@%^'\"/\\()[]{}:;<>.,+";

    static final int FIRST_CHAR = 32;
    static final int CHAR_COUNT = 512;
    static final Log logger = new Log(Font.class);

    final ByteBuffer file;
    final STBTTFontinfo info;
    final STBTTBakedChar.Buffer bakedCharBuffer;
    final ByteBuffer bitmap;
    final Texture texture;

    final float pixelHeight;
    final int atlasWidth;
    final int atlasHeight;

    final float scaleForPixelHeight;

    final String characters;
    final HashMap<Character, GlyphInfo> charInfoMap;

    public Font(String path, float pixelHeight, int atlasWidth, int atlasHeight, String characters) {
        try {
            Path realPath = FONTS_PATH.resolve(path).toRealPath();
            file = fileToBuffer(realPath.toString());

            info = STBTTFontinfo.create();
            if (!STBTruetype.stbtt_InitFont(info, file)) {
                throw new RuntimeException("Failed to initialize font: " + path);
            }

            bakedCharBuffer = STBTTBakedChar.create(CHAR_COUNT);

            this.pixelHeight = pixelHeight;
            this.atlasWidth = atlasWidth;
            this.atlasHeight = atlasHeight;
            scaleForPixelHeight = STBTruetype.stbtt_ScaleForPixelHeight(info, pixelHeight);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer ascent = stack.mallocInt(1);
                IntBuffer descent = stack.mallocInt(1);
                IntBuffer lineGap = stack.mallocInt(1);
                STBTruetype.stbtt_GetFontVMetrics(info, ascent, descent, lineGap);
            }

            this.characters = characters;
            charInfoMap = new HashMap<>(characters.length());
            bitmap = createBitmap(toCodePoints(characters));
            texture = createAtlas(bitmap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[] toCodePoints(String characters) {
        return characters.codePoints().toArray();
    }

    private Texture createAtlas(ByteBuffer bitmap) {
        return new Texture(bitmap, atlasWidth, atlasHeight, GL_RED, false);
    }

    private ByteBuffer createBitmap(int[] codePoints) {
        final float padding = 1f;
        ByteBuffer bitmap = MemoryUtil.memAlloc(atlasWidth * atlasHeight);

        float atlasX = 0f, atlasY = 0f, maxHeight = 0f;

        int charIndex = 0;
        for (int codePoint : codePoints) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer advance = stack.mallocInt(1);
                IntBuffer bearingLeft = stack.mallocInt(1);
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer bearingX = stack.mallocInt(1);
                IntBuffer bearingY = stack.mallocInt(1);

                // Ищем индекс глифа
                int glyphIndex = STBTruetype.stbtt_FindGlyphIndex(info, codePoint);
                if (glyphIndex == 0) {
                    logger.warn("Glyph not found for codepoint {}", codePoint);
                    continue;
                }
                // Получаем размеры глифа
                STBTruetype.stbtt_GetGlyphHMetrics(info, glyphIndex, advance, bearingLeft);
                ByteBuffer glyphBitmap = STBTruetype.stbtt_GetGlyphBitmap(
                        info,
                        scaleForPixelHeight,
                        scaleForPixelHeight,
                        glyphIndex,
                        width,   // сюда запишется ширина
                        height,  // сюда запишется высота
                        bearingX,    // сюда запишется смещение по X
                        bearingY    // сюда запишется смещение по Y
                );

                if (atlasX + width.get(0) > atlasWidth) {
                    // Переход на новую строку
                    atlasX = 0;
                    atlasY += maxHeight;
                    maxHeight = 0;
                }
                if (atlasY + height.get(0) + padding > atlasHeight) {
                    System.out.println(atlasY + ", " + height.get(0) + ", " + atlasHeight);
                    logger.warn("Atlas too small for all characters!");
                    break;
                }

                if (glyphBitmap == null) {
                    logger.warn("Could not get glyph bitmap for codepoint {}", codePoint);
                } else {
                    for (int gy = 0; gy < height.get(0); gy++) {
                        for (int gx = 0; gx < width.get(0); gx++) {
                            byte pixel = glyphBitmap.get(gy * width.get(0) + gx);
                            int x = (int) (atlasX + gx);
                            int y = (int) (atlasY + gy);
                            int index = y * atlasWidth + x;
                            if (index < bitmap.capacity()) {
                                bitmap.put(index, pixel);
                            }
                        }
                    }
                    STBTruetype.stbtt_FreeBitmap(glyphBitmap);
                }

                charInfoMap.put(characters.charAt(charIndex), new GlyphInfo(
                        advance.get(0) * scaleForPixelHeight, width.get(0), height.get(0),
                        bearingX.get(0), bearingY.get(0), (int) atlasX, (int) atlasY));
                atlasX += width.get(0) + padding;
                maxHeight = Math.max(maxHeight, height.get(0) + padding);
                charIndex++;
            }
        }

        return bitmap;
    }

    public float getScale() {
        return scaleForPixelHeight;
    }

    public GlyphInfo getGlyphInfo(char character) {
        return charInfoMap.get(character);
    }

    public int length() {
        return charInfoMap.size();
    }

    public int getAtlasWidth() {
        return atlasWidth;
    }

    public int getAtlasHeight() {
        return atlasHeight;
    }

    public void saveAtlasImage(String path) {
        checkNotDisposed();
        Path filepath = Engine.IMAGES_PATH.resolve(path);
        try {
            if (!Files.exists(filepath)) {
                Files.createFile(filepath);
            }
            boolean success = STBImageWrite.stbi_write_png(
                    filepath.toRealPath().toString(),
                    atlasWidth,
                    atlasHeight,
                    1,
                    bitmap,
                    0
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ByteBuffer fileToBuffer(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes).flip();
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load font file: " + path, e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        bakedCharBuffer.free();
        info.free();
        MemoryUtil.memFree(file);
        MemoryUtil.memFree(bitmap);
        charInfoMap.clear();
    }

    public Texture getTexture() {
        return texture;
    }

    public record GlyphInfo(
            float advance,
            int width,
            int height,
            int bearingX,
            int bearingY,
            int atlasX,
            int atlasY
    ) {
    }
}