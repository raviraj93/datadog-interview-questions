package org.example.filesize;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * Utility to compute the total size of all files in a directory tree.
 */
public class DirectorySizeCalculator {

    /**
     * A platform‑agnostic File interface abstraction.
     */
    public interface FileIfc {
        boolean isDirectory();
        FileIfc[] listFiles() throws IOException;
        long size() throws IOException;
    }

    /**
     * Compute total size using the File interface, via iterative DFS.
     *
     * @param root root file or directory
     * @return total size in bytes
     * @throws IOException on I/O errors
     */
    public static long computeTotalSize(FileIfc root) throws IOException {
        long total = 0L;
        Stack<FileIfc> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            FileIfc file = stack.pop();
            if (file.isDirectory()) {
                for (FileIfc child : file.listFiles()) {
                    stack.push(child);
                }
            } else {
                total += file.size();
            }
        }
        return total;
    }
    /**
     * Recursively traverses the directory tree rooted at `startPath`, summing
     * the sizes of all regular files. Symlinks (if encountered) are followed.
     *
     * @param startPath the root directory to begin traversal
     * @return the total size, in bytes, of all files under startPath
     * @throws IOException if an I/O error occurs during traversal
     */
    public static long computeTotalSize(Path startPath) throws IOException {
        // Mutable holder for cumulative size
        final long[] totalSize = {0L};

        // Walk file tree with a visitor
        Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // Only count regular files
                if (attrs.isRegularFile()) {
                    totalSize[0] += attrs.size();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // If a file cannot be accessed, skip it
                return FileVisitResult.CONTINUE;
            }
        });

        return totalSize[0];
    }


    /**
     * Demo usage: prints the total size of files under the given directory.
     */
    public static void main(String[] args) {
        Path root = Path.of("/Users/ravirajsingh/Downloads/personalCode/datadog");
        try {
            long sizeVisitor = computeTotalSize(root);
            System.out.println("Total size using FileVisitor: " + sizeVisitor + " bytes");

            long sizeStream = computeTotalSize(root);
            System.out.println("Total size using Stream API:   " + sizeStream + " bytes");

            long sizeFiles = DirectorySizeCalculator.computeTotalSize(root);
            System.out.println("Total size using FileVisitor: " + sizeFiles + " bytes");
        } catch (IOException e) {
            System.err.println("Error computing directory size: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
