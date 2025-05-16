package org.example.bufferedwriter;
import java.util.Arrays;

class Solution {

    static class FileOut {
        public int write(byte[] buf, int len) {
            int bytesToWrite = Math.min(len, 2);
            System.out.write(buf, 0, bytesToWrite);
            System.out.write('\n');
            return bytesToWrite;
        }
    }

    static class BufferedFile {
        private static final int DEFAULT_BUFFER_SIZE = 1024;
        private final byte[] buffer;
        private int position = 0;
        private final FileOut fileOut;

        BufferedFile(FileOut fw, int bufferSize) {
            if (bufferSize <= 0) {
                throw new IllegalArgumentException("Buffer size is invalid");
            }
            this.fileOut = fw;
            this.buffer = new byte[bufferSize];
        }

        BufferedFile(FileOut fw) {
            this.fileOut = fw;
            this.buffer = new byte[DEFAULT_BUFFER_SIZE];
        }

        void write(byte[] data) {
            int offset = 0;
            int length = data.length;
            while (offset < length) {
                int space = buffer.length - position;
                if (space == 0) {
                    flush();
                }
                int toWrite = Math.min(space, length - offset);
                System.arraycopy(data, offset, buffer, position, toWrite);
                position += toWrite;
                offset += toWrite;
            }
        }

//        void flush() {
//            while (position > 0) {
//                flushBuffer(position);
//            }
//        }

//        void flush() {
//           while (position > 0) {
//               flushBuffer(position);
//           }
//        }

        void flush(){
            int bytesToWrite = position;
            while (bytesToWrite > 0) {
                int written = fileOut.write(buffer, bytesToWrite);
                bytesToWrite -= written;
                if(written < position){
                    System.arraycopy(buffer, written, buffer, 0, position - written);
                    position -= written;
                } else {
                    position = 0;
                }
            }
        }

        private void flushBuffer(int length) {
            int written = fileOut.write(buffer, length);
            int remaining = length - written;
            int offset = 0;
            while (remaining > 0) {
               written += fileOut.write(buffer, offset + written);
               offset += written;
               remaining -= written;
           }
        }
    }

    public static void main(String[] args) {
        FileOut out = new FileOut();
        int bufSize = 1000;
        BufferedFile f = new BufferedFile(out, bufSize);
        f.write("hello world".getBytes());
        f.flush();

        f = new BufferedFile(out, 4);
        f.write("hello ".getBytes()); // "hell"
        f.flush();  // "o "

        f = new BufferedFile(out, 1);
        f.write("hello".getBytes()); // Each character flushed immediately
        f.flush();

        f.write("ravi".getBytes());
        f.flush();

        try {
            f = new BufferedFile(out, -1);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
