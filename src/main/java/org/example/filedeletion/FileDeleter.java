package org.example.filedeletion;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class FileDeleter {

    public void DeleteAllFilesAndDir(String path) {
        Deque<String> stack = new ArrayDeque<>();       // Stack for DFS traversal
        Deque<String> deleteStack = new ArrayDeque<>(); // Stores directories for post-order deletion

        stack.push(path);

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (!IsDirectory(current)) {
                Delete(current); // Delete files immediately
            } else {
                deleteStack.push(current); // Postpone directory deletion

                for (String child : GetAllFiles(current)) {
                    stack.push(child); // Add all contents (files & subdirs) to stack
                }
            }
        }

        // Delete directories after their contents are deleted
        while (!deleteStack.isEmpty()) {
            Delete(deleteStack.pop());
        }
    }

    // === Mocked API (for demonstration) ===

    private boolean IsDirectory(String path) {
        return path.endsWith("/"); // Example logic
    }

    private boolean Delete(String path) {
        System.out.println("Deleted: " + path);
        return true;
    }

    private List<String> GetAllFiles(String path) {
        // Simulated static file structure
        Map<String, List<String>> mock = Map.of(
                "/a/", List.of("/a/file1", "/a/b/", "/a/c/"),
                "/a/b/", List.of("/a/b/file2"),
                "/a/c/", List.of()
        );
        return mock.getOrDefault(path, List.of());
    }

    public static void main(String[] args) {
        new FileDeleter().DeleteAllFilesAndDir("/a/");
    }
}


