package org.example.bufferedwriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A buffered writer that batches writes into an internal byte buffer
 * and writes to the underlying OutputStream in blocks to minimize syscalls.
 */
public class BufferedWriter implements AutoCloseable {
    // Default buffer size (16 KB)
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024;

    // Underlying output stream
    private final OutputStream out;

    // Internal byte buffer
    private final byte[] buffer;

    // Current write position in the buffer
    private int position = 0;

    // Flag indicating whether the writer is closed
    private boolean closed = false;

    /**
     * Wraps a given OutputStream using the default buffer size.
     *
     * @param out the OutputStream to wrap
     */
    public BufferedWriter(OutputStream out) {
        this(out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Wraps a given OutputStream using a custom buffer size.
     *
     * @param out        the OutputStream to wrap
     * @param bufferSize size of the internal buffer
     * @throws IllegalArgumentException if bufferSize <= 0
     */
    public BufferedWriter(OutputStream out, int bufferSize) {
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
    public BufferedWriter(String fileName) throws FileNotFoundException {
        this(new FileOutputStream(fileName), DEFAULT_BUFFER_SIZE);
    }

    /**
     * Opens (and overwrites) the given file for buffered writing with custom buffer size.
     *
     * @param fileName   path to the output file
     * @param bufferSize size of the internal buffer
     * @throws FileNotFoundException if the file cannot be opened
     */
    public BufferedWriter(String fileName, int bufferSize) throws FileNotFoundException {
        this(new FileOutputStream(fileName), bufferSize);
    }

    /**
     * Writes the given byte array into the internal buffer.
     * If the buffer becomes full, flushes its contents to the underlying stream.
     *
     * @param data array of bytes to write
     * @throws IOException if the writer is closed or an I/O error occurs
     */
    public void write(byte[] data) throws IOException {
        // Prevent writes on a closed stream
        if (closed) {
            throw new IOException("Stream closed");
        }
        int offset = 0;           // Current read offset in data
        int length = data.length; // Total bytes to write

        // Loop until all bytes are buffered
        while (offset < length) {
            int space = buffer.length - position;  // Remaining space in buffer
            if (space == 0) {
                flushBuffer();                    // Buffer full: flush to output
                space = buffer.length;           // Reset available space
            }
            // Calculate how many bytes to copy in this iteration
            int toCopy = Math.min(space, length - offset);
            // Bulk-copy from data to internal buffer
            System.arraycopy(data, offset, buffer, position, toCopy);
            position += toCopy; // Advance buffer write position
            offset += toCopy;   // Advance data read offset
        }
    }

    /**
     * Writes a String to the buffer using the platform default charset.
     * Throws IOException if the writer is closed.
     *
     * @param text the text to write
     * @throws IOException if an I/O error occurs or the writer is closed
     */
    public void write(String text) throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
        write(text.getBytes());
    }

    /**
     * Flushes any buffered data to the underlying OutputStream (but does not force disk sync).
     *
     * @throws IOException if an I/O error occurs
     */
    private void flushBuffer() throws IOException {
        if (position > 0) {
            out.write(buffer, 0, position);
            position = 0;
        }
    }

    /**
     * Public flush: flushes any remaining buffered data.
     *
     * @throws IOException if an I/O error occurs
     */
    public void flush() throws IOException {
        flushBuffer();
    }

    /**
     * Closes the writer: flushes remaining data, closes the stream, and
     * marks the writer as closed so further writes throw IOException.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            try {
                flush();
            } finally {
                out.close();
                closed = true;
            }
        }
    }

    /**
     * Example usage of BufferedWriter.
     */
    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter("out.txt")) {
            writer.write("Hello, buffered world!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
