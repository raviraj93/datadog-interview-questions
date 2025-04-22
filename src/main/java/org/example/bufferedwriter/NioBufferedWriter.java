package org.example.bufferedwriter;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A buffered writer using NIO FileChannel and ByteBuffer.
 * Batches data in a direct ByteBuffer and writes to disk in blocks.
 * Not thread-safe; intended for single-threaded use.
 */
public class NioBufferedWriter implements Closeable {
    // Default buffer size (16 KB)
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024;

    // Underlying FileChannel for disk I/O
    private final FileChannel channel;

    // Internal ByteBuffer for batching writes
    private final ByteBuffer buffer;

    // Flag indicating whether the writer is closed
    private boolean closed = false;

    /**
     * Opens the file with default buffer size for overwrite writes.
     *
     * @param fileName path to the output file
     * @throws IOException if the file cannot be opened
     */
    public NioBufferedWriter(String fileName) throws IOException {
        this(fileName, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Opens the file with specified buffer size for overwrite writes.
     *
     * @param fileName   path to the output file
     * @param bufferSize capacity of the internal ByteBuffer; must be > 0
     * @throws IOException if the file cannot be opened or bufferSize invalid
     */
    public NioBufferedWriter(String fileName, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be positive");
        }
        this.channel = new FileOutputStream(fileName).getChannel();
        this.buffer = ByteBuffer.allocateDirect(bufferSize);
    }

    /**
     * Writes a byte array into the buffer. When the buffer fills,
     * flushes its contents to the FileChannel.
     *
     * @param data array of bytes to write
     * @throws IOException if the writer is closed or an I/O error occurs
     */
    public void write(byte[] data) throws IOException {
        if (closed) {
            throw new IOException("Writer is closed");
        }
        int offset = 0;
        while (offset < data.length) {
            if (!buffer.hasRemaining()) {
                flushBuffer();
            }
            int toPut = Math.min(buffer.remaining(), data.length - offset);
            buffer.put(data, offset, toPut);
            offset += toPut;
        }
    }

    /**
     * Writes a string using the platform default charset.
     *
     * @param text the string to write
     * @throws IOException if the writer is closed or an I/O error occurs
     */
    public void write(String text) throws IOException {
        write(text.getBytes());
    }

    /**
     * Flushes the internal ByteBuffer to the FileChannel.
     * Must be called before writing if buffer is full or on close/flush.
     *
     * @throws IOException if an I/O error occurs
     */
    private void flushBuffer() throws IOException {
        buffer.flip();                // switch to read mode
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        buffer.clear();               // ready for next write
    }

    /**
     * Forces any buffered data to disk.
     *
     * @throws IOException if the writer is closed or an I/O error occurs
     */
    public void flush() throws IOException {
        if (closed) {
            throw new IOException("Writer is closed");
        }
        if (buffer.position() > 0) {
            flushBuffer();
        }
        channel.force(false);         // request OS to flush
    }

    /**
     * Closes this writer: flushes remaining data and closes the channel.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            if (buffer.position() > 0) {
                flushBuffer();
            }
            channel.close();
            closed = true;
        }
    }

    /**
     * Example usage with NIO FileChannel.
     */
    public static void main(String[] args) {
        try (NioBufferedWriter writer = new NioBufferedWriter("out_nio.txt")) {
            writer.write("Hello via FileChannel!\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
