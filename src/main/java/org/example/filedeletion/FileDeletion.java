package org.example.filedeletion;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Deque;

public class FileDeletion {
    public static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;

        File file = path.toFile();
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteRecursively(entry.toPath());
                }
            }
        }
        Files.delete(path);
    }

    public void DeleteAllFilesAndDir(Path path) throws IOException {
        Deque<Path> stack = new ArrayDeque<>();
        stack.push(path);
        Deque<Path> deleteStack = new ArrayDeque<>();
        while(!stack.isEmpty()){
            Path p = stack.pop();
            if(!Files.isDirectory(p)){
                Files.delete(p);
            }
            else{
                deleteStack.push(p);
                for(Path next : p){ // Get All files
                    stack.push(next);
                }
            }
        }
        while(!deleteStack.isEmpty()){
            Files.delete(deleteStack.pop());
        }
    }

    public static void deleteRecursively1(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public static void main(String[] args) {
        Path targetPath = Path.of("path/to/delete"); // replace with actual path

        try {
            deleteRecursively(targetPath);
            System.out.println("Deleted: " + targetPath);
        } catch (IOException e) {
            System.err.println("Failed to delete: " + e.getMessage());
        }
    }
}
