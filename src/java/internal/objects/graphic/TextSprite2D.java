package internal.objects.graphic;

import internal.Log;
import internal.data.Disposable;
import internal.data.DynamicMesh;
import internal.data.Font;
import internal.graphic.TextMaterial;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class TextSprite2D extends DrawableNode2D implements Disposable {
    public Log log = new Log(TextSprite2D.class);
    public int whitespace = 35;
    public Font font;

    final DynamicMesh mesh = new DynamicMesh(false, true);
    float cursorX = 0, cursorY = 0;
    private @NotNull String text = "";

    public TextSprite2D(Font font) {
        material = new TextMaterial(mesh);
        this.font = font;
    }

    public void setText(String text) {
        if (text == null) {
            this.text = "";
            return;
        }
        if (text.equals(this.text)) {
            return;
        }
        bind(text);
    }

    public @NotNull String getText() {
        return text;
    }

    public void appendText(String text) {
        if (text == null || text.isEmpty()) return;
        appendChars(text.toCharArray());
    }

    private void appendChars(char @NotNull [] chars) {
        checkNotDisposed();
        StringBuilder newText = new StringBuilder(chars.length);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack.mallocFloat(18);
            FloatBuffer uvBuffer = stack.mallocFloat(12);
            for (char c : chars) {
                if (c == ' ') {
                    cursorX += whitespace;
                    newText.append(' ');
                    continue;
                }
                Font.GlyphInfo glyphInfo = font.getGlyphInfo(c);
                if (glyphInfo == null) {
                    log.warn("Could not get glyph info for {}", c);
                    continue;
                }

                float x0 = (cursorX + glyphInfo.bearingX());
                float y0 = (cursorY - glyphInfo.bearingY());
                float x1 = x0 + glyphInfo.width();
                float y1 = y0 - glyphInfo.height();

                float u0 = (float) glyphInfo.atlasX() / font.getAtlasWidth();
                float v0 = (float) glyphInfo.atlasY() / font.getAtlasHeight();
                float u1 = (float) (glyphInfo.atlasX() + glyphInfo.width()) / font.getAtlasWidth();
                float v1 = (float) (glyphInfo.atlasY() + glyphInfo.height()) / font.getAtlasHeight();

                vertexBuffer.rewind();
                vertexBuffer.put(x0).put(y0).put(0);
                vertexBuffer.put(x1).put(y0).put(0);
                vertexBuffer.put(x0).put(y1).put(0);
                vertexBuffer.put(x1).put(y0).put(0);
                vertexBuffer.put(x1).put(y1).put(0);
                vertexBuffer.put(x0).put(y1).put(0);
                vertexBuffer.flip();

                uvBuffer.rewind();
                uvBuffer.put(u0).put(v0);
                uvBuffer.put(u1).put(v0);
                uvBuffer.put(u0).put(v1);
                uvBuffer.put(u1).put(v0);
                uvBuffer.put(u1).put(v1);
                uvBuffer.put(u0).put(v1);
                uvBuffer.flip();

                mesh.appendVertices(vertexBuffer);
                mesh.appendUVs(uvBuffer);
                cursorX += glyphInfo.advance();
                newText.append(c);
            }
        }
        text += newText.toString();
    }

    public void removeLastChar() {
        checkNotDisposed();
        if (text.isEmpty()) return;
        char c = text.charAt(text.length() - 1);
        text = text.substring(0, text.length() - 1);
        if (c == ' ') {
            cursorX -= whitespace;
            return;
        }
        mesh.removeLastVertices(6);
        mesh.removeLastUVs(6);

        Font.GlyphInfo glyphInfo = font.getGlyphInfo(c);
        cursorX -= glyphInfo.advance();
    }

    public void bind(String text) {
        checkNotDisposed();
        mesh.reset();
        cursorX = 0;
        cursorY = 0;
        appendChars(text.toCharArray());
    }

    @Override
    public void draw() {
        checkNotDisposed();
        var material = (TextMaterial) getMaterial();
        material.use(getWorldMatrix(), color, font.getTexture(), ignoreCameraZoom);
        material.drawMesh();
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }

    @Override
    public boolean isDisposed() {
        return mesh.isDisposed();
    }
}
