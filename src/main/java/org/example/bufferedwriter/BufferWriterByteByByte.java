package org.example.bufferedwriter;

import java.io.FileNotFoundException; // Needed for handling missing file errors
import java.io.FileOutputStream;      // Used to write bytes to a file
import java.io.IOException;           // Handles IO exceptions

public class BufferWriterByteByByte implements AutoCloseable { // Allows use in try-with-resources
    private static final int BUFFER_SIZE = 8192; // Default buffer size (8 KB)
    private final FileOutputStream fos;          // Output stream for writing to file
    private final byte[] buffer = new byte[BUFFER_SIZE]; // Byte buffer for temporary storage
    private int position;                        // Current position in the buffer

    // Constructor: opens file for writing
    public BufferWriterByteByByte(String fileName) throws FileNotFoundException {
        this.fos = new FileOutputStream(fileName); // Create output stream to file
    }

    public BufferWriterByteByByte(String fileName, boolean append) throws FileNotFoundException {
        this.fos = new FileOutputStream(fileName, append); // Create output stream to file
    }

    // Writes a string to the buffer (converts to bytes)
    public void write(String text) throws IOException {
        write(text.getBytes()); // Convert string to bytes, then write
    }

    // Writes a byte array to the buffer
    public void write(byte[] data) throws IOException {
        for (byte b : data) {                // Loop through each byte in the data
            if (position >= buffer.length) {  // If buffer is full,
                flushBuffer();                // write its contents to the file
            }
            buffer[position++] = b;           // Add byte to buffer and increment position
        }
    }

    // Flushes the buffer contents to the file
    private void flushBuffer() throws IOException {
        if (position > 0) {                        // Only flush if buffer has data
            fos.write(buffer, 0, position);        // Write buffer to file
            position = 0;                          // Reset buffer position
        }
    }

    // Public method to flush buffer and ensure data is written to disk
    public void flush() throws IOException {
        flushBuffer();                // Write buffer to file
        fos.getFD().sync();           // Ensure data is physically written to disk
    }

    // Closes the writer, flushing any remaining data
    @Override
    public void close() throws IOException {
        try {
            flush();                  // Flush remaining data
        } finally {
            fos.close();              // Always close the file output stream
        }
    }

    // Example usage: writes a line to "out.txt"
    public static void main(String[] args) {
        try (BufferWriterByteByByte w = new BufferWriterByteByByte("out.txt")) { // Auto-close with try-with-resources
            w.write("Simplified buffered writer!\n");                            // Write a line to the file
        } catch (IOException e) {
            e.printStackTrace();                                                 // Print any IO errors
        }

        try (BufferWriterByteByByte w = new BufferWriterByteByByte("out.txt",true)) { // Auto-close with try-with-resources
            w.write("Simplified buffered writer!\n");                            // Write a line to the file
        } catch (IOException e) {
            e.printStackTrace();                                                 // Print any IO errors
        }
    }
}

