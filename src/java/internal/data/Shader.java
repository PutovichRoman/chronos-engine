package internal.data;

import internal.Engine;
import internal.exceptions.ResourceException;
import internal.graphic.Color;
import internal.lib.utils.FileUtils;
import org.lwjgl.opengl.GL46;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader extends Resource {
    int glProgram;
    private final Map<String, Integer> uniformCache = new HashMap<>();

    private void build(String vertexShaderPath, String fragmentShaderPath) {
        final int vertexShader = GL46.glCreateShader(GL_VERTEX_SHADER);
        GL46.glShaderSource(vertexShader, FileUtils.read(Path.of(vertexShaderPath), StandardCharsets.UTF_8));
        GL46.glCompileShader(vertexShader);

        final int fragmentShader = GL46.glCreateShader(GL_FRAGMENT_SHADER);
        GL46.glShaderSource(fragmentShader, FileUtils.read(Path.of(fragmentShaderPath), StandardCharsets.UTF_8));
        GL46.glCompileShader(fragmentShader);

        if (GL46.glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new ResourceException("Fragment shader compilation failed\n" + GL46.glGetShaderInfoLog(fragmentShader));
        }
        if (GL46.glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new ResourceException("Vertex shader compilation failed\n" + GL46.glGetShaderInfoLog(vertexShader));
        }

        glProgram = GL46.glCreateProgram();
        glAttachShader(glProgram, vertexShader);
        glAttachShader(glProgram, fragmentShader);
        glLinkProgram(glProgram);

        if (glGetProgrami(glProgram, GL_LINK_STATUS) == GL_FALSE) {
            throw new ResourceException("Shader program linking failed: " + glGetProgramInfoLog(glProgram));
        }

        GL46.glDeleteShader(vertexShader);
        GL46.glDeleteShader(fragmentShader);
    }

    public Shader(String path) {
        try {
            final Path vertexShaderPath = Engine.SHADERS_PATH.resolve(path).resolve("vertex.glsl").toRealPath();
            final Path fragmentShaderPath = Engine.SHADERS_PATH.resolve(path).resolve("fragment.glsl").toRealPath();
            if (!vertexShaderPath.toFile().exists()) {
                throw new ResourceException("Vertex shader not found: " + vertexShaderPath);
            }
            if (!fragmentShaderPath.toFile().exists()) {
                throw new ResourceException("Fragment shader not found: " + fragmentShaderPath);
            }
            build(vertexShaderPath.toString(), fragmentShaderPath.toString());
        } catch (IOException exception) {
            throw new ResourceException(exception.getMessage());
        }
    }

    public Shader(String vertexShaderPath, String fragmentShaderPath) {
        build(vertexShaderPath, fragmentShaderPath);
    }

    public void use() {
        GL46.glUseProgram(glProgram);
    }

    public int getUniformLocation(String name) {
        if (uniformCache.containsKey(name)) {
            return uniformCache.get(name);
        }
        int location = GL46.glGetUniformLocation(glProgram, name);
        if (location == -1) {
            throw new ResourceException("Could not get uniform location for " + name);
        }
        uniformCache.put(name, location);
        return location;
    }

    public void setUniform(String uniform, int value) {
        GL46.glUniform1i(getUniformLocation(uniform), value);
    }

    public void setUniform(String uniform, float value) {
        GL46.glUniform1f(getUniformLocation(uniform), value);
    }

    /*public void setUniformMat4f(String uniform, boolean transpose, Mat4 matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrixBuffer = stack.mallocFloat(16);
            matrix.get(matrixBuffer);
            GL46.glUniformMatrix4fv(getUniformLocation(uniform), transpose, matrixBuffer);
        }
    }*/

    public void setUniformMat4f(String uniform, boolean transpose, FloatBuffer matrixBuffer) {
        GL46.glUniformMatrix4fv(getUniformLocation(uniform), transpose, matrixBuffer);
    }

    /*public void setUniformVec4(String uniform, Vector4f vec) {
        int location = getUniformLocation(uniform);
        glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
    }*/

    public void setUniformColor(String uniform, Color color) {
        int location = getUniformLocation(uniform);
        glUniform4f(location, color.red(), color.green(), color.blue(), color.alpha());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shader[");
        sb.append("glProgram=").append(glProgram);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public void dispose() {
        super.dispose();
        glDeleteProgram(glProgram);
        glProgram = -1;
    }
}
