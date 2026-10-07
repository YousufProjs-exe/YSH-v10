
// YSH Networking - yes alot of AI help sadly 
package ysh.filesystem;

import java.io.IOException;
import java.nio.file.*;

public class FileSystem {

    private Path currentPath;
    private Path copiedPath;

    public FileSystem() {

        currentPath = Paths.get(System.getProperty("user.home")).toAbsolutePath().normalize();

    }

    // PWD 
    public String pwd() {

        return currentPath.toString();
    }

    // LS
    public String ls() {

        StringBuilder output = new StringBuilder();

        try (var stream = Files.list(currentPath)) {

            stream
                .sorted((first, second) -> {

                    boolean firstDirectory =
                        Files.isDirectory(first);

                    boolean secondDirectory =
                        Files.isDirectory(second);

                    if (firstDirectory
                            && !secondDirectory) {

                        return -1;
                    }

                    if (!firstDirectory
                            && secondDirectory) {

                        return 1;
                    }

                    return first
                        .getFileName()
                        .toString()
                        .compareToIgnoreCase(
                            second.getFileName()
                                .toString()
                        );
                })
                .forEach(path -> {

                    if (Files.isDirectory(path)) {

                        output.append("[DIR]  ");

                    } else {

                        output.append("[FILE] ");
                    }

                    output.append(
                        path.getFileName()
                    );

                    output.append("\n");
                });

        } catch (IOException e) {

            return "ls: "
                + e.getMessage();
        }

        return output.toString();
    }

    // CD SYSTEM / FUNCTION 
    public String cd(String directory) {

        if (directory.isEmpty()) {
        
            return "usage: cd directory";
        }
    
        Path target;
    
        if (directory.equals("~")) {
        
            target =
                Paths.get(
                    System.getProperty("user.home")
                );
            
        } else {
        
            Path input =
                Paths.get(directory);
        
            if (input.isAbsolute()) {
            
                target = input.normalize();
            
            } 
            
            else {
            
                target =
                    currentPath
                        .resolve(directory)
                        .normalize();
            }
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

        if (name.isEmpty()) {

            return "usage: mkdir name";
        }

        Path directory =
            currentPath.resolve(name).normalize();

        try {

            if (Files.exists(directory)) {

                return "mkdir: already exists";
            }

            Files.createDirectory(directory);

            return "folder created";

        } catch (IOException e) {

            return "mkdir: " + e.getMessage();
        }
    }

    // TOUCH
    public String touch(String name) {

        if (name.isEmpty()) {

            return "usage: touch name";
        }

        Path file =
            currentPath.resolve(name).normalize();

        try {

            if (Files.exists(file)) {

                return "touch: already exists";
            }

            Files.createFile(file);

            return "file created";

        } catch (IOException e) {

            return "touch: " + e.getMessage();
        }
    }

    // RM
    public String rm(String name) {

        if (name.isEmpty()) {

            return "usage: rm name";
        }

        Path target =
            currentPath.resolve(name).normalize();

        try {

            if (!Files.exists(target)) {

                return "rm: not found";
            }

            boolean isDirectory =
                Files.isDirectory(target);

            Files.delete(target);

            if (isDirectory) {

                return "folder deleted";

            } else {

                return "file deleted";
            }

        } catch (DirectoryNotEmptyException e) {

            return "rm: directory not empty";

        } catch (IOException e) {

            return "rm: " + e.getMessage();
        }
    }


    // CAT
    public String cat(String name) {

        if (name.isEmpty()) {

            return "usage: cat file";
        }

        Path file =
            currentPath.resolve(name).normalize();

        try {

            if (!Files.exists(file)) {

                return "cat: file not found";
            }

            if (!Files.isRegularFile(file)) {

                return "cat: not a file";
            }

            return Files.readString(file);

        } catch (IOException e) {

            return "cat: " + e.getMessage();
        }
    }


    // WRITE
    public String write(String name, String text) {

        if (name.isEmpty()) {

            return "usage: write file text";
        }

        Path file =
            currentPath.resolve(name).normalize();

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
                StandardOpenOption.TRUNCATE_EXISTING
            );

            return "written";

        } catch (IOException e) {

            return "write: " + e.getMessage();
        }
    }


    // RENAME
    public String rename(String oldName, String newName) {

        if (oldName.isEmpty() || newName.isEmpty()) {

            return "usage: rename oldname newname";
        }

        Path oldPath =
            currentPath.resolve(oldName).normalize();

        Path newPath =
            currentPath.resolve(newName).normalize();

        try {

            if (!Files.exists(oldPath)) {

                return "rename: not found";
            }

            if (Files.exists(newPath)) {

                return "rename: destination already exists";
            }

            Files.move(oldPath, newPath);

            return "renamed";

        } catch (IOException e) {

            return "rename: " + e.getMessage();
        }
    }


    // COPY
    public String copy(String name) {

        if (name.isEmpty()) {
            return "usage: copy name";
        }

        Path source =
            currentPath.resolve(name).normalize();

        if (!Files.exists(source)) {
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

        if (!Files.exists(copiedPath)) {
            return "paste: source no longer exists";
        }

        Path destination =
            currentPath.resolve(
                copiedPath.getFileName()
            );

        if (Files.exists(destination)) {
            return "paste: destination already exists";
        }

        try {

            if (Files.isDirectory(copiedPath)) {

                copyDirectory(
                    copiedPath,
                    destination
                );

            } else {

                Files.copy(
                    copiedPath,
                    destination
                );
            }

            return "pasted";

        } catch (IOException e) {

            return "paste: "
                + e.getMessage();
        }
    }
    // helper for PASTE 
    private void copyDirectory(
        Path source,
        Path destination
    ) throws IOException {
    
        try (var stream = Files.walk(source)) {
        
            for (Path path :
                    (Iterable<Path>) stream::iterator) {
                    
                Path target =
                    destination.resolve(
                        source.relativize(path)
                    );
                
                if (Files.isDirectory(path)) {
                
                    Files.createDirectories(target);
                
                } else {
                
                    Files.copy(path, target);
                }
            }
        }
    }

    // MOVE
    public String move(
        String name,
        String destinationName
    ) {

        if (name.isEmpty()
            || destinationName.isEmpty()) {

            return "usage: move item destination";
        }

        Path source =
            currentPath.resolve(name).normalize();

        Path destination =
            currentPath.resolve(destinationName)
                       .normalize();

        try {

            if (!Files.exists(source)) {

                return "move: source not found";
            }

            if (!Files.exists(destination)) {

                return "move: destination not found";
            }

            if (!Files.isDirectory(destination)) {

                return
                    "move: destination is not a directory";
            }

            Path target =
                destination.resolve(
                    source.getFileName()
                );

            if (Files.exists(target)) {

                return
                    "move: destination already exists";
            }

            Files.move(source, target);

            return "moved";

        } catch (IOException e) {

            return "move: " + e.getMessage();
        }
    }


    // SEARCH
    public String search(String keyword) {

        if (keyword.isEmpty()) {

            return "usage: search name";
        }

        StringBuilder output =
            new StringBuilder();

        String target =
            keyword.toLowerCase();

        try {

            try (var stream =
                     Files.walk(currentPath)) {

                for (Path path :
                     (Iterable<Path>)
                         stream::iterator) {

                    if (path.equals(currentPath)) {

                        continue;
                    }

                    String name =
                        path.getFileName()
                            .toString();

                    if (name.toLowerCase()
                            .contains(target)) {

                        if (Files.isDirectory(path)) {

                            output.append("[DIR]  ");

                        } else {

                            output.append("[FILE] ");
                        }

                        output.append(path)
                              .append("\n");
                    }
                }
            }

        } catch (IOException e) {

            return "search: "
                + e.getMessage();
        }

        if (output.length() == 0) {

            return "nothing found";
        }

        return output.toString();
    }

    // HEAD
    public String head(String name) {

        Path file =
            currentPath.resolve(name)
                       .normalize();

        try {

            if (!Files.exists(file)) {

                return "head: file not found";
            }

            if (!Files.isRegularFile(file)) {

                return "head: not a file";
            }

            return Files.readAllLines(file)
                .stream()
                .limit(10)
                .reduce(
                    "",
                    (result, line) ->
                        result + line + "\n"
                );

        } catch (IOException e) {

            return "head: "
                + e.getMessage();
        }
    }

    // TAIL
    public String tail(String name) {

        Path file =
            currentPath.resolve(name)
                       .normalize();

        try {

            if (!Files.exists(file)) {

                return "tail: file not found";
            }

            if (!Files.isRegularFile(file)) {

                return "tail: not a file";
            }

            var lines =
                Files.readAllLines(file);

            int start =
                Math.max(
                    0,
                    lines.size() - 10
                );

            StringBuilder output =
                new StringBuilder();

            for (int i = start;
                 i < lines.size();
                 i++) {

                output.append(
                    lines.get(i)
                ).append("\n");
            }

            return output.toString();

        } catch (IOException e) {

            return "tail: "
                + e.getMessage();
        }
    }

    // REAL PROMPT 
    public String getDirectoryName() {

        Path name = currentPath.getFileName();
        
        if (name == null) {
            return currentPath.toString();
        }
    
        return name.toString();
    }

    // TREE 
    public String tree() {

        StringBuilder output =
            new StringBuilder();

        output.append(
            currentPath.getFileName()
        ).append("\n");

        try {

            buildTree(
                currentPath,
                "",
                output
            );

        } catch (IOException e) {

            return "tree: "
                + e.getMessage();
        }

        return output.toString();
    }
    private void buildTree(
        Path directory,
        String indent,
        StringBuilder output
    ) throws IOException {

        try (var stream =
                 Files.list(directory)) {

            var paths =
                stream
                    .sorted()
                    .toList();

            for (Path path : paths) {

                output.append(indent);

                if (Files.isDirectory(path)) {

                    output.append(
                        "[DIR] "
                    );

                } else {

                    output.append(
                        "[FILE] "
                    );
                }

                output.append(
                    path.getFileName()
                ).append("\n");

                if (Files.isDirectory(path)) {

                    buildTree(
                        path,
                        indent + "  ",
                        output
                    );
                }
            }
        }
    }
}

// implementation - back to legacy commands are pending 
