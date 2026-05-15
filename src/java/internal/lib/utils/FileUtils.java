package internal.lib.utils;

import internal.exceptions.ResourceException;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public abstract class FileUtils {
    public static String read(Path path, Charset charset) {
        if (path == null) {
            throw new NullPointerException("Path is null!");
        } else if (!Files.exists(path)) {
            throw new ResourceException("File does not exist!");
        }
        try (FileChannel channel = FileChannel.open(path)) {
            ByteBuffer buffer = MemoryUtil.memAlloc((int) channel.size());
            channel.read(buffer);
            buffer.flip();

            String fileContent = charset.decode(buffer).toString();
            MemoryUtil.memFree(buffer);
            return fileContent;
        } catch (Exception e) {
            throw new ResourceException("Error reading file '" + path + '\'');
        }
    }

    public static void write(Path path, Charset charset, String content) {
        if (path == null) {
            throw new NullPointerException("Path is null!");
        }
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException exception) {
            throw new ResourceException("Failed to create directories for '" + path + '\'');
        }

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            CharsetEncoder encoder = charset.newEncoder();
            ByteBuffer buffer = encoder.encode(CharBuffer.wrap(content));
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        } catch (Exception e) {
            throw new ResourceException("Error writing file '" + path + '\'');
        }
    }

    public static void write(Path path, byte[] content) {
        if (path == null) {
            throw new NullPointerException("Path is null!");
        }
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException exception) {
            throw new ResourceException("Failed to create directories for '" + path + '\'');
        }

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buffer = MemoryUtil.memAlloc(content.length);
            buffer.put(content);
            buffer.flip();
            channel.write(buffer);
            MemoryUtil.memFree(buffer);
        } catch (Exception e) {
            throw new ResourceException("Error writing file '" + path + '\'');
        }
    }

    public static String read(Path path) {
        return read(path, StandardCharsets.UTF_8);
    }

    public static void write(Path path, String content) {
        write(path, StandardCharsets.UTF_8, content);
    }

    public static Path searchPath(Path root, String name) {
        if (!Files.isDirectory(root)) return null;

        try (Stream<Path> stream = Files.walk(root, Integer.MAX_VALUE)) {
            return stream
                    .filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().equals(name))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    public static Path searchFile(Path root, String name) {
        if (!Files.isDirectory(root)) return null;

        try (Stream<Path> stream = Files.walk(root, Integer.MAX_VALUE)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equals(name))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }
}
