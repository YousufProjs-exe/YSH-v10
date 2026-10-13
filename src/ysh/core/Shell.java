package ysh.core;

import ysh.filesystem.FileSystem;
import ysh.gui.YSHWindow;
import ysh.notes.NoteManager;
import ysh.calculator.ExpressionCalculator;
import network.NetworkManager;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Shell {

    private YSHWindow win;

    private final CommandParser parser;
    private final CommandRegistry registry;
    private final FileSystem fileSystem;
    private final NoteManager noteManager;
    private final ExpressionCalculator calculator;
    private final NetworkManager networkManager;
    private final List<String> commandHistory = new ArrayList<>();

    private boolean developerMode = false;

    public Shell() {
        parser = new CommandParser();
        registry = new CommandRegistry();
        fileSystem = new FileSystem();
        noteManager = new NoteManager();
        calculator = new ExpressionCalculator();
        networkManager = new NetworkManager();

        registerBuiltInCommands();
    }

    // WINDOW CONNECTION

    public void attachWindow(YSHWindow window) {
        this.win = window;
    }

    public void setMessageListener(
            java.util.function.Consumer<String> listener) {
        networkManager.setMessageListener(listener);
    }

    public String getCurrentDirectoryName() {
        return fileSystem.getDirectoryName();
    }

    public boolean isDeveloperMode() {
        return developerMode;
    }

    public List<String> getCommandHistory() {
        return List.copyOf(commandHistory);
    }

    // COMMAND REGISTRATION

    private void registerBuiltInCommands() {
        registerSystemCommands();
        registerFileSystemCommands();
        registerUtilityCommands();
        registerThemeCommands();
        registerNetworkCommands();
        registerLegacyAliases();
    }

    private void registerSystemCommands() {
        registry.register("help", this::commandHelp);
        registry.register("version", this::commandVersion);
        registry.register("sysinfo", this::commandSysInfo);
        registry.register("check", this::commandCheck);
        registry.register("whatsup", this::commandWhatsUp);
        registry.register("echo", this::commandEcho);
        registry.register("history", this::commandHistory);
        registry.register("exit", this::commandExit);
        registry.register("clear", this::commandClear);
        registry.register("time", this::commandTime);
        registry.register("date", this::commandDate);
    }

    private void registerFileSystemCommands() {
        registry.register("pwd", this::commandPwd);
        registry.register("ls", this::commandLs);
        registry.register("cd", this::commandCd);
        registry.register("home", this::commandHome);
        registry.register("mkdir", this::commandMkdir);
        registry.register("touch", this::commandTouch);
        registry.register("rm", this::commandRm);
        registry.register("cat", this::commandCat);
        registry.register("write", this::commandWrite);
        registry.register("rename", this::commandRename);
        registry.register("copy", this::commandCopy);
        registry.register("paste", this::commandPaste);
        registry.register("move", this::commandMove);
        registry.register("search", this::commandSearch);
        registry.register("tree", this::commandTree);
        registry.register("head", this::commandHead);
        registry.register("tail", this::commandTail);
        registry.register("open", this::commandOpen);
    }

    private void registerUtilityCommands() {
        registry.register("calc", this::commandCalc);
        registry.register("note", this::commandNote);
        registry.register("developer", this::commandDeveloper);
    }

    private void registerThemeCommands() {
        registry.register("theme", this::commandTheme);
    }

    private void registerNetworkCommands() {
        registry.register("host", this::commandHost);
        registry.register("stophost", this::commandStopHost);
        registry.register("connect", this::commandConnect);
        registry.register("msg", this::commandMessage);
        registry.register("disconnect", this::commandDisconnect);
        registry.register("name", this::commandName);
        registry.register("announce", this::commandAnnounce);
        registry.register("listusers", this::commandListUsers);
        registry.register("kick", this::commandKick);
    }

    private void registerLegacyAliases() {
        registry.register("dir", this::commandLs);
        registry.register("del", this::commandRm);
        registry.register("md", this::commandMkdir);
        registry.register("cp", this::commandCopy);
        registry.register("ren", this::commandRename);
        registry.register("?", this::commandHelp);
        registry.register("find", this::commandSearch);
        registry.register("type", this::commandCat);
        registry.register("ver", this::commandVersion);
        registry.register("devmode", this::commandDeveloper);
        registry.register("cls", this::commandClear);
    }

    // SYSTEM COMMANDS

    private String commandHelp(String[] args) {
        return """
            ===================== YSH v10 HELP =====================

            FILESYSTEM
              pwd                         show current directory
              ls, dir                     list directory contents
              cd <directory>              change directory
              home                        return to home directory
              mkdir, md <name>            create directory
              touch <name>                create file
              rm, del <name>              delete file or directory
              cat, type <file>            read file contents
              write <file> <text>         write text to file
              rename, ren <old> <new>     rename file or directory
              copy, cp <name>             select item to copy
              paste                       paste copied item
              move <item> <directory>     move item
              search, find <name>         search files and folders
              tree                        show directory tree
              head <file>                 show beginning of file
              tail <file>                 show end of file
              open <file>                 open file with system app

            SYSTEM
              help, ?                     show this help
              version, ver                show YSH version
              sysinfo                     show system information
              check                       check YSH components
              whatsup                     show YSH status
              echo <text>                 print text
              history                     show command history
              time                        show current time
              date                        show current date
              exit                        close YSH
              clear, cls                  clear terminal

            UTILITIES
              calc <expression>           calculate expression
              note add <name> <text>      save a note
              note list                   list notes
              note view <name>            read a note
              note delete <name>          delete a note
              note clear                  delete all notes
              developer, devmode          toggle Developer Mode

            THEMES
              theme black                 black and light-gray
              theme matrix                black and green
              theme blue
              theme cyan
              theme purple
              theme red
              theme dark
              theme light
              theme pink
              theme magenta
              theme orange

            NETWORKING
              name <username>             set username
              host [port]                 host a chat room
              connect <host> [port]        join a chat room
              msg <message>               send a message
              disconnect                  leave a chat room
              stophost                    stop hosting
              listusers                   list connected users
              announce <message>          announce to room
              kick <username>             remove a user

            ========================================================
            """;
    }

    private String commandVersion(String[] args) {
        return """
            YSH - Yousuf Shell
            Version: v10
            Engine: Java
            Mode: GUI
            Java: %s
            """.formatted(System.getProperty("java.version"));
    }

    private String commandSysInfo(String[] args) {
        return """
            YSH System Information
            ----------------------
            OS: %s
            Version: %s
            Architecture: %s
            Java: %s
            User: %s
            Processors: %d
            """.formatted(
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch"),
                System.getProperty("java.version"),
                System.getProperty("user.name"),
                Runtime.getRuntime().availableProcessors()
            );
    }

    private String commandCheck(String[] args) {
        String networking = networkManager.isServerRunning()
                ? "Hosting"
                : networkManager.isConnected()
                    ? "Connected"
                    : "Idle";

        return """
            YSH System Check
            ----------------
            Shell Engine: OK
            Command Registry: OK
            File System: OK
            Networking: %s
            GUI: Running
            """.formatted(networking);
    }

    private String commandWhatsUp(String[] args) {
        return """
            YSH is running.

            Everything looks good.

            Try 'help' to see available commands.
            """;
    }

    private String commandEcho(String[] args) {
        return String.join(" ", args) + "\n";
    }

    private String commandHistory(String[] args) {
        if (commandHistory.isEmpty()) {
            return "No command history\n";
        }

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < commandHistory.size(); i++) {
            output.append(
                String.format("%3d  %s%n", i + 1, commandHistory.get(i))
            );
        }

        return output.toString();
    }

    private String commandExit(String[] args) {
        System.exit(0);
        return "";
    }

    private String commandClear(String[] args) {
        if (win == null) {
            return "clear: window not ready\n";
        }

        win.clearTerminal();
        return "";
    }

    private String commandTime(String[] args) {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n";
    }

    private String commandDate(String[] args) {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\n";
    }

    // FILESYSTEM COMMANDS

    private String commandPwd(String[] args) {
        return fileSystem.pwd() + "\n";
    }

    private String commandLs(String[] args) {
        return fileSystem.ls();
    }

    private String commandCd(String[] args) {
        String target = args.length == 0 ? "~" : joinArgs(args, 0);
        String result = fileSystem.cd(target);
        return result.isEmpty() ? "" : result + "\n";
    }

    private String commandHome(String[] args) {
        return commandCd(new String[0]);
    }

    private String commandMkdir(String[] args) {
        if (args.length == 0) {
            return "usage: mkdir name\n";
        }

        return fileSystem.mkdir(joinArgs(args, 0)) + "\n";
    }

    private String commandTouch(String[] args) {
        if (args.length == 0) {
            return "usage: touch name\n";
        }

        return fileSystem.touch(joinArgs(args, 0)) + "\n";
    }

    private String commandRm(String[] args) {
        if (args.length == 0) {
            return "usage: rm name\n";
        }

        return fileSystem.rm(joinArgs(args, 0)) + "\n";
    }

    private String commandCat(String[] args) {
        if (args.length == 0) {
            return "usage: cat file\n";
        }

        return fileSystem.cat(joinArgs(args, 0)) + "\n";
    }

    private String commandWrite(String[] args) {
        if (args.length < 2) {
            return "usage: write file text\n";
        }

        return fileSystem.write(
            args[0],
            joinArgs(args, 1)
        ) + "\n";
    }

    private String commandRename(String[] args) {
        if (args.length < 2) {
            return "usage: rename oldname newname\n";
        }

        return fileSystem.rename(args[0], joinArgs(args, 1)) + "\n";
    }

    private String commandCopy(String[] args) {
        if (args.length == 0) {
            return "usage: copy name\n";
        }

        return fileSystem.copy(joinArgs(args, 0)) + "\n";
    }

    private String commandPaste(String[] args) {
        return fileSystem.paste() + "\n";
    }

    private String commandMove(String[] args) {
        if (args.length < 2) {
            return "usage: move item destination\n";
        }

        return fileSystem.move(args[0], joinArgs(args, 1)) + "\n";
    }

    private String commandSearch(String[] args) {
        if (args.length == 0) {
            return "usage: search name\n";
        }

        return fileSystem.search(joinArgs(args, 0)) + "\n";
    }

    private String commandTree(String[] args) {
        return fileSystem.tree();
    }

    private String commandHead(String[] args) {
        if (args.length == 0) {
            return "usage: head file\n";
        }

        return fileSystem.head(joinArgs(args, 0));
    }

    private String commandTail(String[] args) {
        if (args.length == 0) {
            return "usage: tail file\n";
        }

        return fileSystem.tail(joinArgs(args, 0));
    }

    private String commandOpen(String[] args) {
        if (args.length == 0) {
            return "usage: open file\n";
        }

        File file = new File(joinArgs(args, 0));

        try {
            if (!file.exists()) {
                return "open: file not found\n";
            }

            if (!Desktop.isDesktopSupported()) {
                return "open: not supported\n";
            }

            Desktop.getDesktop().open(file);
            return "opened\n";

        } catch (Exception e) {
            return "open: " + e.getMessage() + "\n";
        }
    }

    // UTILITY COMMANDS

    private String commandCalc(String[] args) {
        if (args.length == 0) {
            return "usage: calc expression\n";
        }

        try {
            double result = calculator.calculate(joinArgs(args, 0));

            if (Double.isFinite(result) && result == Math.floor(result)
                    && result >= Long.MIN_VALUE && result <= Long.MAX_VALUE) {
                return (long) result + "\n";
            }

            return result + "\n";

        } catch (Exception e) {
            return "calc: " + e.getMessage() + "\n";
        }
    }

    private String commandNote(String[] args) {
        if (args.length == 0) {
            return """
                usage:
                note add <name> <text>
                note list
                note view <name>
                note delete <name>
                note clear
                """;
        }

        String action = args[0].toLowerCase(Locale.ROOT);

        switch (action) {
            case "add":
                if (args.length < 3) {
                    return "usage: note add name text\n";
                }

                return noteManager.add(
                    args[1],
                    joinArgs(args, 2)
                ) + "\n";

            case "list":
                return noteManager.list() + "\n";

            case "view":
                if (args.length < 2) {
                    return "usage: note view name\n";
                }

                return noteManager.view(joinArgs(args, 1)) + "\n";

            case "delete":
                if (args.length < 2) {
                    return "usage: note delete name\n";
                }

                return noteManager.delete(joinArgs(args, 1)) + "\n";

            case "clear":
                return noteManager.clear() + "\n";

            default:
                return "note: unknown operation\n";
        }
    }

    private String commandDeveloper(String[] args) {
        developerMode = !developerMode;

        if (developerMode) {
            return """
                Developer Mode Enabled
                Welcome back, Developer.
                """;
        }

        return "Developer Mode Disabled\n";
    }

    // THEME COMMAND

    private String commandTheme(String[] args) {
        if (args.length == 0) {
            return "themes: black | matrix | blue | cyan | purple | red | dark | light | pink | magenta | orange\n";
        }

        if (win == null) {
            return "theme: window not ready\n";
        }

        String selected = args[0].toLowerCase(Locale.ROOT);

        if (!win.setTheme(selected)) {
            return "theme: unknown theme '" + selected + "'\n";
        }

        return "theme changed to " + selected + "\n";
    }

    // NETWORK COMMANDS

    private String commandHost(String[] args) {
        int port = 6000;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    return "host: port must be between 1 and 65535\n";
                }
            } catch (NumberFormatException e) {
                return "usage: host [port]\n";
            }
        }

        return networkManager.startServer(port) + "\n";
    }

    private String commandStopHost(String[] args) {
        return networkManager.stopServer() + "\n";
    }

    private String commandConnect(String[] args) {
        if (args.length == 0) {
            return "usage: connect host [port]\n";
        }

        int port = 6000;

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
                if (port < 1 || port > 65535) {
                    return "connect: port must be between 1 and 65535\n";
                }
            } catch (NumberFormatException e) {
                return "connect: invalid port\n";
            }
        }

        return networkManager.connect(args[0], port) + "\n";
    }

    private String commandMessage(String[] args) {
        if (args.length == 0) {
            return "usage: msg message\n";
        }

        return networkManager.send(joinArgs(args, 0)) + "\n";
    }

    private String commandDisconnect(String[] args) {
        return networkManager.disconnect() + "\n";
    }

    private String commandName(String[] args) {
        if (args.length == 0) {
            return "usage: name username\n";
        }

        return networkManager.setName(joinArgs(args, 0)) + "\n";
    }

    private String commandAnnounce(String[] args) {
        if (args.length == 0) {
            return "usage: announce message\n";
        }

        return networkManager.announce(joinArgs(args, 0)) + "\n";
    }

    private String commandListUsers(String[] args) {
        return networkManager.listUsers() + "\n";
    }

    private String commandKick(String[] args) {
        if (args.length == 0) {
            return "usage: kick username\n";
        }

        return networkManager.kick(joinArgs(args, 0)) + "\n";
    }

    // SHELL ENTRY POINT

    public String execute(String input) {
        Command command;
    
        try {
            command = parser.parse(input);
        } catch (IllegalArgumentException e) {
            return "YSH: " + e.getMessage() + "\n";
        }
    
        if (command == null) {
            return "";
        }
    
        String trimmed = input.trim();
    
        if (commandHistory.isEmpty()
                || !commandHistory.get(commandHistory.size() - 1).equals(trimmed)) {
            commandHistory.add(trimmed);
        }
    
        String result = registry.execute(command);
    
        if (result == null) {
            return "YSH: command not found: " + command.getName() + "\n";
        }
    
        return result;
    }
    
    private String joinArgs(String[] args, int start) {
        if (start >= args.length) {
            return "";
        }

        return String.join(
            " ",
            Arrays.copyOfRange(args, start, args.length)
        );
    }

    public List<String> getCommandCompletions(String input) {
        if (input == null || input.isBlank()) {
            return List.of();
        }

        boolean trailingSpace =
                Character.isWhitespace(input.charAt(input.length() - 1));

        String text = input.trim();
        int separator = text.indexOf(' ');

        if (separator < 0) {
            String prefix = text.toLowerCase(Locale.ROOT);

            if (prefix.equals("theme")) {
                return themeCompletions("");
            }

            if (prefix.equals("note")) {
                return noteCompletions("");
            }

            List<String> matches = new ArrayList<>();

            for (String name : registry.getCommandNames()) {
                if (name.startsWith(prefix)) {
                    matches.add(name);
                }
            }

            matches.sort(String.CASE_INSENSITIVE_ORDER);
            return matches;
        }

        if (trailingSpace) {
            return List.of();
        }

        String command =
                text.substring(0, separator).toLowerCase(Locale.ROOT);
        String prefix = text.substring(separator + 1).toLowerCase(Locale.ROOT);

        if (command.equals("theme") && !prefix.contains(" ")) {
            return themeCompletions(prefix);
        }

        if (command.equals("note") && !prefix.contains(" ")) {
            return noteCompletions(prefix);
        }

        return List.of();
    }

    private List<String> themeCompletions(String prefix) {
        List<String> matches = new ArrayList<>();

        for (String theme : new String[]{
                "black", "matrix", "blue", "cyan", "purple",
                "red", "dark", "light", "pink", "magenta", "orange"
        }) {
            if (theme.startsWith(prefix)) {
                matches.add("theme " + theme);
            }
        }

        return matches;
    }

    private List<String> noteCompletions(String prefix) {
        List<String> matches = new ArrayList<>();

        for (String action : new String[]{
                "add", "list", "view", "delete", "clear"
        }) {
            if (action.startsWith(prefix)) {
                matches.add("note " + action);
            }
        }

        return matches;
    }
}
