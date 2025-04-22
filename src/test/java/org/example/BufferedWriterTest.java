package org.example;

import org.example.bufferedwriter.BufferedWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class BufferedWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void testWriteSingleByte() throws IOException {
        Path testFile = tempDir.resolve("single-byte.txt");
        try (BufferedWriter writer = new BufferedWriter(testFile.toString())) {
            // Write a single byte (value 65)
            writer.write(new byte[]{65});
        }
        byte[] content = Files.readAllBytes(testFile);
        assertArrayEquals(new byte[]{65}, content);
    }

    @Test
    void testWriteString() throws IOException {
        Path testFile = tempDir.resolve("string.txt");
        String testString = "Hello, World!";
        try (BufferedWriter writer = new BufferedWriter(testFile.toString())) {
            writer.write(testString);
        }
        String fileContent = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals(testString, fileContent);
    }

    @Test
    void testAutoFlushOnBufferFull() throws IOException {
        Path testFile = tempDir.resolve("auto-flush.txt");
        int bufferSize = 4;
        try (BufferedWriter writer = new BufferedWriter(testFile.toString(), bufferSize)) {
            // Write exactly bufferSize bytes
            writer.write("1234".getBytes(StandardCharsets.UTF_8));
            // Should not have flushed yet
            assertEquals(0, Files.size(testFile));
            // Write one more byte to trigger flush of the first 4
            writer.write("5".getBytes(StandardCharsets.UTF_8));
        }
        byte[] content = Files.readAllBytes(testFile);
        assertArrayEquals("12345".getBytes(StandardCharsets.UTF_8), content);
    }

    @Test
    void testMultipleWrites() throws IOException {
        Path testFile = tempDir.resolve("multiple-writes.txt");
        try (BufferedWriter writer = new BufferedWriter(testFile.toString())) {
            writer.write("Hello".getBytes(StandardCharsets.UTF_8));
            writer.write(", ".getBytes(StandardCharsets.UTF_8));
            writer.write("World!".getBytes(StandardCharsets.UTF_8));
        }
        String content = Files.readString(testFile, StandardCharsets.UTF_8);
        assertEquals("Hello, World!", content);
    }

    @Test
    void testFlushManually() throws IOException {
        Path testFile = tempDir.resolve("manual-flush.txt");
        try (BufferedWriter writer = new BufferedWriter(testFile.toString(), 10)) {
            writer.write("Partial".getBytes(StandardCharsets.UTF_8));
            writer.flush();
            byte[] contentAfterFlush = Files.readAllBytes(testFile);
            assertArrayEquals("Partial".getBytes(StandardCharsets.UTF_8), contentAfterFlush);

            writer.write("Final".getBytes(StandardCharsets.UTF_8));
        }
        byte[] finalContent = Files.readAllBytes(testFile);
        assertArrayEquals("PartialFinal".getBytes(StandardCharsets.UTF_8), finalContent);
    }

    @Test
    void testCloseFlushesBuffer() throws IOException {
        Path testFile = tempDir.resolve("close-flush.txt");
        try (BufferedWriter writer = new BufferedWriter(testFile.toString(), 10)) {
            writer.write("Unflushed".getBytes(StandardCharsets.UTF_8));
        }
        byte[] content = Files.readAllBytes(testFile);
        assertArrayEquals("Unflushed".getBytes(StandardCharsets.UTF_8), content);
    }

    @Test
    void testWriteAfterCloseThrowsException() throws IOException {
        Path testFile = tempDir.resolve("write-after-close.txt");
        BufferedWriter writer = new BufferedWriter(testFile.toString());
        writer.close();
        assertThrows(IOException.class, () -> writer.write("test"));
    }

    @Test
    void testInvalidBufferSizeThrowsException() {
        Path testFile = tempDir.resolve("invalid-buffer.txt");
        assertThrows(IllegalArgumentException.class,
                () -> new BufferedWriter(testFile.toString(), 0));
    }

    @Test
    void testWriteEmptyString() throws IOException {
        Path testFile = tempDir.resolve("empty-string.txt");
        try (BufferedWriter writer = new BufferedWriter(testFile.toString())) {
            writer.write("");
        }
        assertEquals(0, Files.size(testFile));
    }

    @Test
    void testWriteExactlyBufferSize() throws IOException {
        Path testFile = tempDir.resolve("exact-buffer.txt");
        int bufferSize = 5;
        String testData = "12345";

        try (BufferedWriter writer = new BufferedWriter(testFile.toString(), bufferSize)) {
            writer.write(testData.getBytes(StandardCharsets.UTF_8));
            // Buffer should be full but not flushed yet
            assertEquals(0, Files.size(testFile));
        }
        // After close, data should be flushed
        byte[] content = Files.readAllBytes(testFile);
        assertArrayEquals(testData.getBytes(StandardCharsets.UTF_8), content);
    }
}
