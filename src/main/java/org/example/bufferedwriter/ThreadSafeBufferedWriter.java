package org.example.bufferedwriter;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread-safe buffered writer that batches writes into an internal byte buffer
 * and writes to the underlying OutputStream in blocks to minimize syscalls.
 * Uses a ReentrantLock for explicit lock control and potential advanced features.
 */
public class ThreadSafeBufferedWriter implements Closeable {
    // Default buffer size (16 KB)
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024;

    // Underlying output stream for actual I/O
    private final OutputStream out;

    // Internal byte buffer
    private final byte[] buffer;

    // Current write position in the buffer
    private int position = 0;

    // Flag indicating whether the writer is closed
    private boolean closed = false;

    // ReentrantLock for thread-safe operations
    private final ReentrantLock lock = new ReentrantLock(true); // fair lock

    /**
     * Wraps a given OutputStream using the default buffer size.
     *
     * @param out the OutputStream to wrap
     */
    public ThreadSafeBufferedWriter(OutputStream out) {
        this(out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Wraps a given OutputStream using a custom buffer size.
     *
     * @param out        the OutputStream to wrap
     * @param bufferSize size of the internal buffer; must be > 0
     * @throws IllegalArgumentException if bufferSize <= 0
     */
    public ThreadSafeBufferedWriter(OutputStream out, int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be positive");
        }
        this.out = out;
        this.buffer = new byte[bufferSize];
    }

    /**
     * Opens (and overwrites) the given file for buffered writing with default buffer size.
     *
     * @param fileName path to the output file
     * @throws FileNotFoundException if the file cannot be opened
     */
    public ThreadSafeBufferedWriter(String fileName) throws FileNotFoundException {
        this(new FileOutputStream(fileName), DEFAULT_BUFFER_SIZE);
    }

    /**
     * Opens (and overwrites) the given file for buffered writing with custom buffer size.
     *
     * @param fileName   path to the output file
     * @param bufferSize size of the internal buffer; must be > 0
     * @throws FileNotFoundException if the file cannot be opened
     */
    public ThreadSafeBufferedWriter(String fileName, int bufferSize) throws FileNotFoundException {
        this(new FileOutputStream(fileName), bufferSize);
    }

    /**
     * Writes the given byte array into the buffer in a thread-safe manner.
     * Automatically flushes the buffer when full.
     * Uses ReentrantLock for mutual exclusion.
     *
     * @param data array of bytes to write
     * @throws IOException if the writer is closed or an I/O error occurs
     */
    public void write(byte[] data) throws IOException {
        lock.lock();
        try {
            if (closed) {
                throw new IOException("Stream closed");
            }
            int offset = 0;
            int length = data.length;
            while (offset < length) {
                int space = buffer.length - position;
                if (space == 0) {
                    flushBuffer();  // still under lock
                    space = buffer.length;
                }
                int toCopy = Math.min(space, length - offset);
                System.arraycopy(data, offset, buffer, position, toCopy);
                position += toCopy;
                offset += toCopy;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Writes a string to the buffer using the platform default charset.
     * Thread-safe via underlying byte-array write.
     *
     * @param text the string to write
     * @throws IOException if the writer is closed or an I/O error occurs
     */
    public void write(String text) throws IOException {
        write(text.getBytes());
    }

    /**
     * Flushes only the buffered bytes to the underlying stream.
     * Must be called within the lock protecting buffer state.
     *
     * @throws IOException if an I/O error occurs
     */
    private void flushBuffer() throws IOException {
        out.write(buffer, 0, position);
        position = 0;
    }

    /**
     * Flushes any remaining buffered data to the stream in a thread-safe manner.
     *
     * @throws IOException if the writer is closed or an I/O error occurs
     */
    public void flush() throws IOException {
        lock.lock();
        try {
            if (closed) {
                throw new IOException("Stream closed");
            }
            if (position > 0) {
                flushBuffer();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes this writer: flushes buffered data, closes the stream, and marks it closed.
     * Thread-safe.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        lock.lock();
        try {
            if (!closed) {
                if (position > 0) {
                    flushBuffer();
                }
                out.close();
                closed = true;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Example usage of ThreadSafeBufferedWriter.
     */
    public static void main(String[] args) {
        try (ThreadSafeBufferedWriter writer = new ThreadSafeBufferedWriter("out.txt")) {
            writer.write("Hello, buffered world!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
