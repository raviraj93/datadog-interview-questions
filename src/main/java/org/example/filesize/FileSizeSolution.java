package org.example.filesize;
import java.util.*;

public class FileSizeSolution {

    abstract static class FSEntry {
        String name;
        FSEntry(String name) {
            this.name = name;
        }
    }

    static class File extends FSEntry {
        int size;
        File(String name, int size) {
            super(name);
            this.size = size;
        }
    }

    static class Directory extends FSEntry {
        List<FSEntry> content;
        Directory(String name, FSEntry... entries) {
            super(name);
            this.content = List.of(entries);
        }
    }

    public static int getTotalSize(FSEntry root, String path) {
        if (path.equals("/") || path.isEmpty()) {
            return getSizeRecursive(root);
        }

        String[] parts = path.replaceAll("^/+", "").split("/");
        FSEntry target = findEntry(root, parts, 0);
        return computeSizeIterative(target);
    }

    private static FSEntry getEntry(FSEntry current, String[] parts, int index) {
        if (index >= parts.length) return current;

        if (current instanceof Directory dir) {
            for (FSEntry entry : dir.content) {
                if (entry.name.equals(parts[index])) {
                    return getEntry(entry, parts, index + 1);
                }
            }
        }
        return null;
    }

    private static int getSizeRecursive(FSEntry entry) {
        if (entry == null) return 0;

        if (entry instanceof File file) {
            return file.size;
        } else if (entry instanceof Directory dir) {
            int total = 0;
            for (FSEntry child : dir.content) {
                total += getSizeRecursive(child);
            }
            return total;
        }
        return 0;
    }


    private static int computeSizeIterative(FSEntry entry) {
        if (entry == null) return 0;

        int total = 0;
        Deque<FSEntry> stack = new ArrayDeque<>();
        stack.push(entry);

        while (!stack.isEmpty()) {
            FSEntry current = stack.pop();
            if (current instanceof File file) {
                total += file.size;
            } else if (current instanceof Directory dir) {
                if (dir.content.size() > 100_000) {
                    throw new IllegalStateException("Directory too large to process safely");
                }
                for (FSEntry child : dir.content) {
                    stack.push(child);
                }
            }
        }

        return total;
    }

    // Find entry at path
    private static FSEntry findEntry(FSEntry current, String[] parts, int index) {
        if (index >= parts.length) return current;

        if (current instanceof Directory dir) {
            for (FSEntry entry : dir.content) {
                if (entry.name.equals(parts[index])) {
                    return findEntry(entry, parts, index + 1);
                }
            }
        }

        return null;
    }

        public static void main(String[] args) {
        Directory root = new Directory("",
                new Directory("home",
                        new Directory("me",
                                new File("foo.txt", 416),
                                new File("metrics.txt", 5892),
                                new Directory("src",
                                        new File("site.html", 6051),
                                        new File("site.css", 5892),
                                        new File("data.csv", 332789))),
                        new Directory("you",
                                new File("dict.json", 4913364))),
                new Directory("bin",
                        new File("bash", 618416),
                        new File("cat", 23648),
                        new File("ls", 38704)),
                new Directory("var",
                        new Directory("log",
                                new File("dmesg", 1783894),
                                new File("wifi.log", 924818),
                                new Directory("httpd",
                                        new File("access.log", 17881),
                                        new File("access.log.0.gz", 4012)))));

        System.out.println("Total /: " + getTotalSize(root, "/")); // 8675777
        System.out.println("Total /home: " + getTotalSize(root, "/home")); // 5264404
        System.out.println("Total /bin: " + getTotalSize(root, "/bin")); // 680768
        System.out.println("Total /var/: " + getTotalSize(root, "var/")); // 2730605
        System.out.println("Total /home/me/: " + getTotalSize(root, "/home/me/")); // 351040
        System.out.println("Total /var/log/wifi.log: " + getTotalSize(root, "/var/log/wifi.log")); // 924818
    }
}
