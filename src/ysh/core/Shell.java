
package ysh.core;

import ysh.filesystem.FileSystem;
import ysh.gui.YSHWindow;
import ysh.notes.NoteManager;
import ysh.calculator.ExpressionCalculator;
import network.NetworkManager;

import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.Desktop;
import java.io.File;

public class Shell {

    // win object for clearterminal() and other command related defined functions to access from YSHWindow to Shell
    YSHWindow win;

    private final CommandParser parser;
    private final CommandRegistry registry;
    private final FileSystem fileSystem;
    private final NoteManager noteManager;
    private final ExpressionCalculator calculator;
    private final NetworkManager networkManager;
    private boolean developerMode = false;
    private String currentTheme = "matrix";

    public Shell() {

        // CONSTRUCTOR 
        parser = new CommandParser();
        registry = new CommandRegistry();
        fileSystem = new FileSystem();
        calculator = new ExpressionCalculator();
        noteManager = new NoteManager();
        networkManager = new NetworkManager();

        registerBuiltInCommands();
    }

    private void registerBuiltInCommands() {

        // HELP
        registry.register("help", args -> {
        
            return
     """
        ===================== YSH v10 HELP =====================
        
        FILESYSTEM
          pwd                         show current directory
          ls                          list directory contents
          cd <directory>              change directory
          mkdir <name>                create directory
          touch <name>                create file
          rm <name>                   delete file or directory
          cat <file>                  read file contents
          write <file> <text>         write text to file
          rename <old> <new>          rename file or directory
          copy <name>                 copy item
          move <item> <directory>     move item
          search <name>               search files/folders
        
        SYSTEM
          echo <text>                 print text
          help                        show this help
          exit                        close YSH
          history                     Show command history
          calc <expression>           Calculate expression
          theme <name>                Change theme
          sysinfo                     Show system information
          whatsup                     YSH status
          check                       Run YSH system check
        
        ========================================================
    """;
        });

        // VERSION
        registry.register("version", args -> {
        
            return """
        YSH - Yousuf Shell
        Version: v10
        Engine: Java
        Mode: GUI
        """;
        });

        // SYSINFO
        registry.register("sysinfo", args -> {
        
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
                Runtime.getRuntime()
                       .availableProcessors()
            );
        });

        // DIR / LS (aliases)
        registry.register("dir", args -> {

            return fileSystem.ls();
        });

        // DEL / RM (aliases)
        registry.register("del", args -> {

            if (args.length == 0) {
                return "usage: del name\n";
            }
        
            return fileSystem.rm(args[0]) + "\n";
        });

        // MD / MKDIR (aliases)
        registry.register("md", args -> {

            if (args.length == 0) {
                return "usage: md name\n";
            }
        
            return fileSystem.mkdir(args[0]) + "\n";
        });

        // CP (aliases)
        registry.register("cp", args -> {

            if (args.length == 0) {
                return "usage: cp name\n";
            }
        
            return fileSystem.copy(args[0]) + "\n";
        });

        // REN (aliases)
        registry.register("ren", args -> {

            if (args.length < 2) {
                return "usage: ren oldname newname\n";
            }
        
            return fileSystem.rename(
                args[0],
                args[1]
            ) + "\n";
        });

        // ? / HELP (aliases)
        registry.register("?", args -> {

            return registry.execute(
                new Command("help", new String[0])
            );
        });

        // FIND (aliases)
        registry.register("find", args -> {

            if (args.length == 0) {
                return "usage: find name\n";
            }
        
            return fileSystem.search(
                args[0]
            ) + "\n";
        });

        // TYPE / CAT (aliases)
        registry.register("type", args -> {

            if (args.length == 0) {
                return "usage: type file\n";
            }
        
            return fileSystem.cat(
                args[0]
            ) + "\n";
        });

        // VER / VERSION (aliases)
        registry.register("ver", args -> {

            return registry.execute(
                new Command(
                    "version",
                    new String[0]
                )
            );
        });

        // CHECK 
        registry.register("check", args -> {

            return """
        YSH System Check
        ----------------
        Shell Engine: OK
        Command Registry: OK
        File System: OK
        GUI: Running

        """;
        });

        // NOTE
        registry.register("note", args -> {
        
            if (args.length == 0) {
            
                return """
        usage:
        note add <name> <text>
        note list
        note view <name>
            
        """;
            }
        
            String action =
                args[0].toLowerCase();
        
            switch (action) {
            
                case "add":
            
                    if (args.length < 3) {
                    
                        return
                            "usage: note add name text\n";
                    }
                
                    String text =
                        String.join(
                            " ",
                            Arrays.copyOfRange(
                                args,
                                2,
                                args.length
                            )
                        );
                    
                    return noteManager.add(
                        args[1],
                        text
                    ) + "\n";
                
                case "list":
                
                    return noteManager.list() + "\n";
                
                case "view":
                
                    if (args.length < 2) {
                    
                        return
                            "usage: note view name\n";
                    }
                
                    return noteManager.view(
                        args[1]
                    ) + "\n";
                
                case "delete":

                    if (args.length < 2) {
                    
                        return
                            "usage: note delete name\n";
                    }
                
                    return noteManager.delete(
                        args[1]
                    ) + "\n";

                case "clear":

                    return noteManager.clear() + "\n";

                default:
                
                    return "note: unknown operation\n";
            }
        });

        // WHATSAUP 
        registry.register("whatsup", args -> {

            return """
        YSH is running.

        Everything looks good.

        Try 'help' to see commands.

        """;
        });

        // ECHO
        registry.register("echo", args -> {

            return String.join(" ", args) + "\n";
        });

        // PWD
        registry.register("pwd", args -> {

            return fileSystem.pwd() + "\n";
        });

        // LS
        registry.register("ls", args -> {

            return fileSystem.ls();
        });

        // CD
        registry.register("cd", args -> {
        
            if (args.length == 0) {
                return fileSystem.cd("~") + "\n";
            }
        
            String result = fileSystem.cd(args[0]);
        
            if (result.isEmpty()) {
                return "";
            }
        
            return result + "\n";
        });

        // MKDIR
        registry.register("mkdir", args -> {

            if (args.length == 0) {
                return "usage: mkdir name\n";
            }

            return fileSystem.mkdir(args[0]) + "\n";
        });

        // TOUCH
        registry.register("touch", args -> {

            if (args.length == 0) {
                return "usage: touch name\n";
            }

            return fileSystem.touch(args[0]) + "\n";
        });

        // RM
        registry.register("rm", args -> {

            if (args.length == 0) {
                return "usage: rm name\n";
            }

            return fileSystem.rm(args[0]) + "\n";
        });

        // CAT
        registry.register("cat", args -> {

            if (args.length == 0) {
                return "usage: cat file\n";
            }

            return fileSystem.cat(args[0]) + "\n";
        });

        // WRITE
        registry.register("write", args -> {

            if (args.length < 2) {
                return "usage: write file text\n";
            }

            String text = String.join(
                " ",
                Arrays.copyOfRange(args, 1, args.length)
            );

            return fileSystem.write(
                args[0],
                text
            ) + "\n";
        });

        // RENAME
        registry.register("rename", args -> {

            if (args.length < 2) {
                return "usage: rename oldname newname\n";
            }

            return fileSystem.rename(
                args[0],
                args[1]
            ) + "\n";
        });

        // COPY
        registry.register("copy", args -> {

            if (args.length == 0) {
                return "usage: copy name\n";
            }

            return fileSystem.copy(
                args[0]
            ) + "\n";
        });

        // PASTE
        registry.register("paste", args -> {

            return fileSystem.paste() + "\n";
        });

        // MOVE
        registry.register("move", args -> {

            if (args.length < 2) {
                return "usage: move item destination\n";
            }

            return fileSystem.move(
                args[0],
                args[1]
            ) + "\n";
        });

        // DEVELOPER MODE
        registry.register("developer", args -> {
        
            developerMode = !developerMode;
        
            if (developerMode) {
            
                return """
        Developer Mode Enabled
        Welcome back, Developer.
            
        """;
            
            }
        
            return """
        Developer Mode Disabled
        
        """;
        });
        
        registry.register("devmode", args -> {

            developerMode = !developerMode;

            if (developerMode) {
            
                return "Developer Mode Enabled\n";
            }
        
            return "Developer Mode Disabled\n";
        });

        // SEARCH
        registry.register("search", args -> {

            if (args.length == 0) {
                return "usage: search name\n";
            }

            return fileSystem.search(
                args[0]
            ) + "\n";
        }); 

        // OPEN
        registry.register("open", args -> {
        
            if (args.length == 0) {
            
                return "usage: open file\n";
            }
        
            File file =
                new File(args[0]);
        
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
            
                return "open: "
                    + e.getMessage()
                    + "\n";
            }
        });

        // VERSION 
        registry.register("version", args -> {
        
            return """
        YSH - Yousuf Shell
        
        Version: v10 'BETA' || 'DEMO'
        Java: %s
        
        """.formatted(
                System.getProperty("java.version")
            );
        });

        // HOME 
        registry.register("home", args -> {
        
            return fileSystem.cd("~") + "\n";
        });

        // EXIT 
        registry.register("exit", args -> {                

            System.exit(0);
            return "";
        });

        // CLEAR
        registry.register("clear", args -> {  

            // win object for clearterminal()
            win.clearTerminal();
            return "__YSH_CLEAR__";
        });

        // TIME 
        registry.register("time", args -> {
        
            return LocalDateTime.now()
                .format(
                    DateTimeFormatter
                        .ofPattern("HH:mm:ss")
                ) + "\n";
        });
      
        // DATE 
        registry.register("date", args -> {
        
            return LocalDateTime.now()
                .format(
                    DateTimeFormatter
                        .ofPattern("dd-MM-yyyy")
                ) + "\n";
        });

        // CALC 
        registry.register("calc", args -> {

            if (args.length == 0) {
            
                return "usage: calc expression\n";
            }
        
            String expression =
                String.join(" ", args);
        
            try {
            
                double result =
                    calculator.calculate(expression);
            
                if (result == Math.floor(result)) {
                
                    return (long) result + "\n";
                }
            
                return result + "\n";
            
            } catch (Exception e) {
            
                return "calc: "
                    + e.getMessage()
                    + "\n";
            }
        });

        // TREE 
        registry.register("tree", args -> {

            return fileSystem.tree();
        });

        // HEAD 
        registry.register("head", args -> {

            if (args.length == 0) {
            
                return "usage: head file\n";
            }
        
            return fileSystem.head(args[0]);
        });

        // TAIL 
        registry.register("tail", args -> {

            if (args.length == 0) {
            
                return "usage: tail file\n";
            }
        
            return fileSystem.tail(args[0]);
        });

        // THEMES 
        registry.register("theme", args -> {

            if (args.length == 0) {
            
                return "themes: matrix | blue | purple | red | dark | light\n";
            }
        
            currentTheme = args[0].toLowerCase();
        
            return "theme changed to " + currentTheme + "\n";
        });

        // ***LAN NETWORKING***
        // HOST
        registry.register("host", args -> {
        
            int port = 6000;
        
            if (args.length > 0) {
            
                try {
                
                    port = Integer.parseInt(args[0]);
                
                } catch (NumberFormatException e) {
                
                    return "usage: host [port]\n";
                }
            }
        
            return networkManager.startServer(port) + "\n";
        });

        // STOPHOST
        registry.register("stophost", args -> {
        
            return networkManager.stopServer() + "\n";
        });

        // CONNECT
        registry.register("connect", args -> {
        
            if (args.length < 1) {
            
                return
                    "usage: connect host [port]\n";
            }
        
            int port = 6000;
        
            if (args.length >= 2) {
            
                try {
                
                    port =
                        Integer.parseInt(args[1]);
                
                } catch (NumberFormatException e) {
                
                    return
                        "connect: invalid port\n";
                }
            }
        
            return
                networkManager.connect(
                    args[0],
                    port
                ) + "\n";
        });


        // MSG
        registry.register("msg", args -> {
        
            if (args.length == 0) {
            
                return
                    "usage: msg message\n";
            }
        
            String message =
                String.join(
                    " ",
                    args
                );
            
            return
                networkManager.send(
                    message
                ) + "\n";
        });


        // DISCONNECT
        registry.register("disconnect", args -> {
        
            return
                networkManager.disconnect()
                + "\n";
        });

        // USERNAME
        registry.register("name", args -> {

            if (args.length == 0) {
            
                return
                    "usage: name username\n";
            }
        
            return networkManager.setName(
                String.join(" ", args)
            ) + "\n";
        });

        // ANNOUNCE 
        registry.register("announce", args -> {

            if (args.length == 0) {
            
                return
                    "usage: announce message\n";
            }
        
            return networkManager.announce(
                String.join(" ", args)
            ) + "\n";
        });

        // LIST 
        registry.register("listusers", args -> {

            return networkManager.listUsers()
                + "\n";
        });

        // KICK 
        registry.register("kick", args -> {

            if (args.length == 0) {
            
                return
                    "usage: kick username\n";
            }
        
            return networkManager.kick(
                String.join(" ", args)
            ) + "\n";
        });
        
    }

    public String getCurrentDirectoryName() {

        return fileSystem.getDirectoryName();
    }

    public boolean isDeveloperMode() {

        return developerMode;
    }

    public String execute(String input) {

        Command command = parser.parse(input);

        if (command == null) {
            return "";
        }

        String result = registry.execute(command);

        if (result == null) {

            return "YSH: command not found: "
                + command.getName()
                + "\n";
        }

        return result;
    }

    public void setMessageListener(
        java.util.function.Consumer<String> listener
    ) {
    
        networkManager.setMessageListener(listener);
    }
}
