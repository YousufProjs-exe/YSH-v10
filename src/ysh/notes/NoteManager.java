
package ysh.notes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NoteManager {

    private final Path notesDirectory;

    public NoteManager() {

        notesDirectory =
            Paths.get(
                System.getProperty("user.home"),
                ".ysh",
                "notes"
            );

        try {

            Files.createDirectories(
                notesDirectory
            );

        } catch (IOException ignored) {

        }
    }

    public String add(
        String name,
        String content
    ) {

        if (name.isEmpty()) {

            return "usage: note add name text";
        }

        Path note =
            notesDirectory.resolve(
                name + ".txt"
            );

        try {

            Files.writeString(
                note,
                content
            );

            return "note saved";

        } catch (IOException e) {

            return "note: "
                + e.getMessage();
        }
    }

    public String list() {

        try {

            List<String> notes =
                new ArrayList<>();

            try (var stream =
                     Files.list(notesDirectory)) {

                stream.forEach(path -> {

                    String name =
                        path.getFileName()
                            .toString();

                    if (name.endsWith(".txt")) {

                        notes.add(
                            name.substring(
                                0,
                                name.length() - 4
                            )
                        );
                    }
                });
            }

            if (notes.isEmpty()) {

                return "no notes";
            }

            return String.join(
                "\n",
                notes
            );

        } catch (IOException e) {

            return "note: "
                + e.getMessage();
        }
    }

    public String view(String name) {

        if (name.isEmpty()) {

            return "usage: note view name";
        }

        Path note =
            notesDirectory.resolve(
                name + ".txt"
            );

        try {

            if (!Files.exists(note)) {

                return "note not found";
            }

            return Files.readString(note);

        } catch (IOException e) {

            return "note: "
                + e.getMessage();
        }
    }

    public String delete(String name) {

        if (name.isEmpty()) {
            return "usage: note delete name";
        }

        Path note =
            notesDirectory.resolve(
                name + ".txt"
            );

        try {

            if (!Files.exists(note)) {
                return "note not found";
            }

            Files.delete(note);

            return "note deleted";

        } catch (IOException e) {

            return "note: "
                + e.getMessage();
        }
    }

    public String clear() {

        try (var stream =
                 Files.list(notesDirectory)) {
                
            for (Path note :
                    (Iterable<Path>) stream::iterator) {
                    
                Files.delete(note);
            }
        
            return "all notes deleted";
        
        } catch (IOException e) {
        
            return "note: "
                + e.getMessage();
        }
    }

}
