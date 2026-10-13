
package ysh.filesystem;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FileSystem {

    private Path currentPath;
    private Path copiedPath;

    public FileSystem() {
        currentPath = Paths.get(
            System.getProperty("user.home")
        ).toAbsolutePath().normalize();
    }

    // SHARED PATH RESOLUTION

    private Path resolvePath(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        try {
            String value = input.trim();
            Path home = Paths.get(
                System.getProperty("user.home")
            ).toAbsolutePath().normalize();

            if (value.equals("~")) {
                return home;
            }

            if (value.startsWith("~/") || value.startsWith("~\\")) {
                String suffix = value.substring(2);

                while (suffix.startsWith("/") || suffix.startsWith("\\")) {
                    suffix = suffix.substring(1);
                }

                return home.resolve(suffix).normalize();
            }

            Path supplied = Paths.get(value);

            return (supplied.isAbsolute()
                    ? supplied
                    : currentPath.resolve(supplied))
                .toAbsolutePath()
                .normalize();

        } catch (InvalidPathException e) {
            return null;
        }
    }

    private String invalidPath(String command) {
        return command + ": invalid path";
    }

    private boolean isDirectory(Path path) {
        return Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
    }

    private String displayName(Path path) {
        Path name = path.getFileName();
        return name == null ? path.toString() : name.toString();
    }

    // PWD

    public String pwd() {
        return currentPath.toString();
    }

    // LS

    public String ls() {
        StringBuilder output = new StringBuilder();

        try (var stream = Files.list(currentPath)) {
            stream.sorted(
                Comparator
                    .comparing((Path p) -> !isDirectory(p))
                    .thenComparing(
                        p -> displayName(p).toLowerCase(Locale.ROOT)
                    )
            ).forEach(path -> {
                output.append(isDirectory(path) ? "[DIR]  " : "[FILE] ");
                output.append(displayName(path)).append("\n");
            });

        } catch (IOException | SecurityException e) {
            return "ls: " + e.getMessage();
        }

        return output.toString();
    }

    // CD

    public String cd(String directory) {
        Path target = resolvePath(directory);

        if (target == null) {
            return "cd: invalid path";
        }

        if (!Files.exists(target)) {
            return "cd: no such file or directory";
        }

        if (!Files.isDirectory(target)) {
            return "cd: not a directory";
        }

        currentPath = target;
        return "";
    }

    // MKDIR

    public String mkdir(String name) {
        Path directory = resolvePath(name);

        if (directory == null) {
            return "mkdir: invalid path";
        }

        try {
            if (Files.exists(directory)) {
                return "mkdir: already exists";
            }

            Files.createDirectory(directory);
            return "folder created";

        } catch (IOException | SecurityException e) {
            return "mkdir: " + e.getMessage();
        }
    }

    // TOUCH

    public String touch(String name) {
        Path file = resolvePath(name);

        if (file == null) {
            return "touch: invalid path";
        }

        try {
            if (Files.exists(file)) {
                return "touch: already exists";
            }

            Files.createFile(file);
            return "file created";

        } catch (IOException | SecurityException e) {
            return "touch: " + e.getMessage();
        }
    }

    // RM

    public String rm(String name) {
        Path target = resolvePath(name);

        if (target == null) {
            return "rm: invalid path";
        }

        if (target.equals(currentPath)) {
            return "rm: cannot delete the current directory";
        }

        try {
            if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                return "rm: not found";
            }

            boolean directory = isDirectory(target);
            Files.delete(target);

            return directory ? "folder deleted" : "file deleted";

        } catch (DirectoryNotEmptyException e) {
            return "rm: directory not empty";
        } catch (IOException | SecurityException e) {
            return "rm: " + e.getMessage();
        }
    }

    // CAT

    public String cat(String name) {
        Path file = resolvePath(name);

        if (file == null) {
            return "cat: invalid path";
        }

        try {
            if (!Files.exists(file)) {
                return "cat: file not found";
            }

            if (!Files.isRegularFile(file)) {
                return "cat: not a file";
            }

            return Files.readString(file);

        } catch (IOException | SecurityException e) {
            return "cat: " + e.getMessage();
        }
    }

    // WRITE

    public String write(String name, String text) {
        Path file = resolvePath(name);

        if (file == null) {
            return "write: invalid path";
        }

        if (text == null) {
            text = "";
        }

        try {
            if (!Files.exists(file)) {
                return "write: file not found";
            }

            if (!Files.isRegularFile(file)) {
                return "write: not a file";
            }

            Files.writeString(
                file,
                text,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            return "written";

        } catch (IOException | SecurityException e) {
            return "write: " + e.getMessage();
        }
    }

    // RENAME

    public String rename(String oldName, String newName) {
        Path source = resolvePath(oldName);
        Path target = resolvePath(newName);

        if (source == null || target == null) {
            return "rename: invalid path";
        }

        try {
            if (!Files.exists(source, LinkOption.NOFOLLOW_LINKS)) {
                return "rename: not found";
            }

            if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                return "rename: destination already exists";
            }

            Path parent = target.getParent();

            if (parent != null && !Files.isDirectory(parent)) {
                return "rename: destination directory not found";
            }

            Files.move(source, target);
            return "renamed";

        } catch (IOException | SecurityException e) {
            return "rename: " + e.getMessage();
        }
    }

    // COPY

    public String copy(String name) {
        Path source = resolvePath(name);

        if (source == null) {
            return "copy: invalid path";
        }

        if (!Files.exists(source, LinkOption.NOFOLLOW_LINKS)) {
            return "copy: not found";
        }

        copiedPath = source;
        return "copied";
    }

    // PASTE

    public String paste() {
        if (copiedPath == null) {
            return "paste: nothing copied";
        }

        if (!Files.exists(copiedPath, LinkOption.NOFOLLOW_LINKS)) {
            return "paste: source no longer exists";
        }

        Path destination = currentPath.resolve(
            copiedPath.getFileName()
        ).normalize();

        if (Files.exists(destination, LinkOption.NOFOLLOW_LINKS)) {
            return "paste: destination already exists";
        }

        if (isDirectory(copiedPath) && destination.startsWith(copiedPath)) {
            return "paste: cannot copy a directory into itself";
        }

        try {
            if (isDirectory(copiedPath)) {
                copyDirectory(copiedPath, destination);
            } else {
                Files.copy(
                    copiedPath,
                    destination,
                    LinkOption.NOFOLLOW_LINKS
                );
            }

            return "pasted";

        } catch (IOException | SecurityException e) {
            return "paste: " + e.getMessage();
        }
    }

    private void copyDirectory(Path source, Path destination)
            throws IOException {

        List<Path> paths;

        try (var stream = Files.walk(source)) {
            paths = stream.toList();
        }

        for (Path path : paths) {
            Path target = destination.resolve(source.relativize(path));

            if (isDirectory(path)) {
                Files.createDirectories(target);
            } else {
                Files.copy(
                    path,
                    target,
                    LinkOption.NOFOLLOW_LINKS
                );
            }
        }
    }

    // MOVE

    public String move(String name, String destinationName) {
        Path source = resolvePath(name);
        Path destination = resolvePath(destinationName);

        if (source == null || destination == null) {
            return "move: invalid path";
        }

        try {
            if (!Files.exists(source, LinkOption.NOFOLLOW_LINKS)) {
                return "move: source not found";
            }

            if (!Files.isDirectory(destination)) {
                return "move: destination is not a directory";
            }

            if (source.equals(currentPath)) {
                return "move: cannot move the current directory";
            }

            if (isDirectory(source) && destination.startsWith(source)) {
                return "move: cannot move a directory into itself";
            }

            Path target = destination.resolve(source.getFileName());

            if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                return "move: destination already exists";
            }

            Files.move(source, target);
            return "moved";

        } catch (IOException | SecurityException e) {
            return "move: " + e.getMessage();
        }
    }

    // SEARCH

    public String search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "usage: search name";
        }

        String target = keyword.toLowerCase(Locale.ROOT);
        StringBuilder output = new StringBuilder();

        try (var stream = Files.walk(currentPath)) {
            for (Path path : (Iterable<Path>) stream::iterator) {
                if (path.equals(currentPath)) {
                    continue;
                }

                if (displayName(path).toLowerCase(Locale.ROOT).contains(target)) {
                    output.append(isDirectory(path) ? "[DIR]  " : "[FILE] ");
                    output.append(path).append("\n");
                }
            }

        } catch (IOException | SecurityException e) {
            return "search: " + e.getMessage();
        }

        return output.length() == 0 ? "nothing found" : output.toString();
    }

    // HEAD

    public String head(String name) {
        Path file = resolvePath(name);

        if (file == null) {
            return "head: invalid path";
        }

        try {
            if (!Files.exists(file)) {
                return "head: file not found";
            }

            if (!Files.isRegularFile(file)) {
                return "head: not a file";
            }

            StringBuilder output = new StringBuilder();
            List<String> lines = Files.readAllLines(file);

            for (int i = 0; i < Math.min(10, lines.size()); i++) {
                output.append(lines.get(i)).append("\n");
            }

            return output.toString();

        } catch (IOException | SecurityException e) {
            return "head: " + e.getMessage();
        }
    }

    // TAIL

    public String tail(String name) {
        Path file = resolvePath(name);

        if (file == null) {
            return "tail: invalid path";
        }

        try {
            if (!Files.exists(file)) {
                return "tail: file not found";
            }

            if (!Files.isRegularFile(file)) {
                return "tail: not a file";
            }

            List<String> lines = Files.readAllLines(file);
            StringBuilder output = new StringBuilder();
            int start = Math.max(0, lines.size() - 10);

            for (int i = start; i < lines.size(); i++) {
                output.append(lines.get(i)).append("\n");
            }

            return output.toString();

        } catch (IOException | SecurityException e) {
            return "tail: " + e.getMessage();
        }
    }

    // PROMPT

    public String getDirectoryName() {
        return displayName(currentPath);
    }

    // TREE

    public String tree() {
        StringBuilder output = new StringBuilder();
        output.append(displayName(currentPath)).append("\n");

        try {
            buildTree(currentPath, "", output);
        } catch (IOException | SecurityException e) {
            return "tree: " + e.getMessage();
        }

        return output.toString();
    }

    private void buildTree(Path directory, String indent, StringBuilder output)
            throws IOException {

        List<Path> paths;

        try (var stream = Files.list(directory)) {
            paths = new ArrayList<>(stream.toList());
        }

        paths.sort(
            Comparator
                .comparing((Path p) -> !isDirectory(p))
                .thenComparing(
                    p -> displayName(p).toLowerCase(Locale.ROOT)
                )
        );

        for (Path path : paths) {
            boolean directoryEntry = isDirectory(path);

            output.append(indent)
                .append(directoryEntry ? "[DIR]  " : "[FILE] ")
                .append(displayName(path))
                .append("\n");

            if (directoryEntry) {
                buildTree(path, indent + "  ", output);
            }
        }
    }
}
