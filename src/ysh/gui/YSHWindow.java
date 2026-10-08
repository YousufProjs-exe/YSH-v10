
package ysh.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

import ysh.core.Shell;


public class YSHWindow {

    private final Shell shell;
    private JTextArea terminal;
    private int promptPosition;
    private final List<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;
    
    // terminal.setBackground(bgColor);
    // terminal.setForeground(textColor);
    // terminal.setCaretColor(textColor);

    private Color bgColor = Color.BLACK;
    private Color textColor = new Color(0, 255, 70);

    // KONAMI FOR DEVELOPER 
    private final int[] konamiCode = {

        KeyEvent.VK_UP,
        KeyEvent.VK_UP,
        KeyEvent.VK_DOWN,
        KeyEvent.VK_DOWN,
        KeyEvent.VK_LEFT,
        KeyEvent.VK_RIGHT,
        KeyEvent.VK_LEFT,
        KeyEvent.VK_RIGHT,
        KeyEvent.VK_B,
        KeyEvent.VK_A,
        KeyEvent.VK_ENTER
    };

    private int konamiIndex = 0;

    // ARCH MODE ( no AI just ME )
    private final int[] archCode = {

        KeyEvent.VK_A,
        KeyEvent.VK_R,
        KeyEvent.VK_C,
        KeyEvent.VK_H,
        KeyEvent.VK_ENTER
    };

    private int archIndex = 0;

    public YSHWindow() {

        shell = new Shell();
        shell.setMessageListener(message -> {

            SwingUtilities.invokeLater(() -> {
            
                showNetworkMessage(message);
            
            });
        });

        // AWT SYS AND FRAMEWORK 
        JFrame frame = new JFrame("YSH v10 DEMO");
        frame.setSize(760, 470);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        terminal = new JTextArea();
        terminal.setFont(new Font("Consolas", Font.PLAIN, 15));
        
        terminal.setBackground(Color.BLACK);
        terminal.setForeground(new Color(0, 255, 70));
        terminal.setCaretColor(Color.WHITE);
        terminal.setLineWrap(false);
        terminal.setWrapStyleWord(false);
        terminal.setEditable(true);
        frame.add(
            new JScrollPane(terminal),
            BorderLayout.CENTER
        );

        applyTheme();
        printBanner();

        // KEY LISTENERS 
        terminal.addKeyListener(
            new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {

                    if (checkKonami(e.getKeyCode())) {

                        terminal.append("\nKonami Code Activated!\n");
                        terminal.append(shell.execute("developer"));
                    
                        if (shell.isDeveloperMode()) {                        
                            printDeveloperBanner();
                        }
                    
                        printPrompt();                    
                        e.consume();                    
                        return;
                    }
                    protectPrompt();

                    if (checkArch(e.getKeyCode())) {

                        terminal.append("\n Arch Mode Activated!\n");
                        terminal.append(shell.execute("developer")); // developer to arch pending 
                    
                        if (shell.isDeveloperMode()) {                        
                            printDeveloperBanner();
                        }
                    
                        printPrompt();                    
                        e.consume();                    
                        return;
                    }
                    protectPrompt();

                    // ENTER
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                        e.consume();
                        executeCurrentCommand();
                        return;
                    }

                    // TAB 
                    if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    
                        e.consume();                    
                        completeCommand();                    
                        return;
                    }

                    // UP - COMMAND HISTORY
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                    
                        e.consume();                    
                        if (!commandHistory.isEmpty() && historyIndex > 0) {                            

                            historyIndex--;                            
                            replaceCurrentInput(commandHistory.get(historyIndex));
                        }                    
                        return;
                    }

                    // DOWN - COMMAND HISTORY
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    
                        e.consume();                    
                        if (!commandHistory.isEmpty() && historyIndex < commandHistory.size() - 1) {
                                
                            historyIndex++;                                
                            replaceCurrentInput(commandHistory.get(historyIndex));
                        
                        } 

                        else {
                        
                            historyIndex = commandHistory.size();
                            replaceCurrentInput("");
                        }
                        return;
                    }

                    // CTRL + L TO CLEAR SCREEN 
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_L) {
                        
                        e.consume();
                        clearTerminal();
                        return;
                    } 

                    // CTRL + UP TO SCROLL UP 
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_UP) {
                            
                        e.consume();                            
                        JScrollPane scrollPane = (JScrollPane)SwingUtilities.getAncestorOfClass(JScrollPane.class,terminal);
                        
                        if (scrollPane != null) {
                        
                            JScrollBar bar = scrollPane.getVerticalScrollBar();
                            bar.setValue(Math.max(bar.getMinimum(),bar.getValue() - 50));
                        }
                        return;
                    } 

                    // CTRL + DOWN TO SCROLL DOWN 
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_DOWN) {
                            
                        e.consume();                            
                        JScrollPane scrollPane = (JScrollPane)SwingUtilities.getAncestorOfClass(JScrollPane.class,terminal);
                        
                        if (scrollPane != null) {
                        
                            JScrollBar bar = scrollPane.getVerticalScrollBar();                        
                            bar.setValue(Math.min(bar.getMaximum(),bar.getValue() + 50));
                        }
                        return;
                    } 

                    // CTRL + C TO CANCEL 
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
                            
                        e.consume();
                        replaceCurrentInput("");
                        return;
                    }

                    // CTRL + A (to protect the shell) 
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
                            
                        e.consume();
                        terminal.select(promptPosition,terminal.getDocument().getLength());
                        return;
                    }

                    // CTRL + HOME
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_HOME) {
                            
                        e.consume(); 
                        terminal.setCaretPosition(0);
                        return;
                    }                    

                    // CTRL + END
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_END) {
                            
                        e.consume();   
                        terminal.setCaretPosition(terminal.getDocument().getLength());
                        return;
                    }

                    // CTRL + E to close / exit 
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_E) {
                            
                        e.consume();
                        replaceCurrentInput("exit");
                        executeCurrentCommand();
                        return;
                    }

                    // F1 - HELP
                    if (e.getKeyCode() == KeyEvent.VK_F1) {
                    
                        e.consume();
                        replaceCurrentInput("help");
                        executeCurrentCommand();
                        return;
                    }

                    // ESC - CLEAR CURRENT INPUT
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    
                        e.consume();
                        replaceCurrentInput("");
                        return;
                    }

                    // BACKSPACE
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

                        if (terminal.getCaretPosition() <= promptPosition) {

                            e.consume();
                        }
                        return;
                    }

                    // LEFT
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                        if (terminal.getCaretPosition() <= promptPosition) {

                            e.consume();
                        }
                        return;
                    }

                    // HOME
                    if (e.getKeyCode() == KeyEvent.VK_HOME) {

                        e.consume();
                        terminal.setCaretPosition(promptPosition);
                        return;
                    }
                }
            }
        );

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        terminal.requestFocusInWindow();
    }

    // TIME GREETING FUNCTION 
    private void timeGreeting() {

        int hour = LocalTime.now().getHour();
        
        if (hour >= 0 && hour < 5) {
        
            terminal.append(
                "\n"
                + "_______________________________\n\n"
                + "  Late Night Session on YSH\n"
                + "  try 'theme dark'\n"
                + "  Respect.\n"
                + "_______________________________\n\n"
            );

            printPrompt();
            // printBanner();
        }
    }

    // **BANNER FUNCTION** 
    private void printBanner() {

        timeGreeting();

        terminal.append("YSH - Yousuf Shell [v10 BETA & DEMO]\n");
        terminal.append("(c) Khaja Yousuf Uddin & Yousuf Shell \n");
        terminal.append("(!) this version is a used to take a grow into the better file tree - BETA version \n");
        terminal.append("Type 'help' to see available commands\n\n");

        printPrompt();
    }

    // **PROMPT FUNCTION** 
    private void printPrompt() {

        terminal.append("YSH " + shell.getCurrentDirectoryName() +" > ");
    
        promptPosition = terminal.getDocument().getLength();
        terminal.setCaretPosition(promptPosition);
    }

    private void executeCurrentCommand() {

        try {

            int end = terminal.getDocument().getLength();

            if (end < promptPosition) {
                return;
            }

            String command =
                terminal.getText(
                    promptPosition,
                    end - promptPosition
                ).trim();

            terminal.append("\n");

            if (command.equalsIgnoreCase("clear")
                    || command.equalsIgnoreCase("cls")) {
                    
                terminal.setText("");
                    
                printBanner();
                scrollToBottom();
                    
                return;
            }

            if (!command.isEmpty()) {

            if (commandHistory.isEmpty() || !commandHistory.get(commandHistory.size() - 1).equals(command)) {
                    
                commandHistory.add(command);
            }

            historyIndex = commandHistory.size();

            if (command.equalsIgnoreCase("clear")) {
            
                terminal.setText("");
                printPrompt();
                scrollToBottom();
                return;
            }

            // HISTORY 
            if (command.equalsIgnoreCase("history")) {

                if (commandHistory.isEmpty()) {
                
                    terminal.append("No command history\n");
                
                } 
                
                else {
                
                    for (int i = 0; i < commandHistory.size(); i++) {
                        
                        terminal.append(
                            String.format(
                                "%3d  %s%n",
                                i + 1,
                                commandHistory.get(i)
                            )
                        );
                    }
                }
            
                printPrompt();
                scrollToBottom();
            
                return;
            }

            // THEME 
            if (command.equalsIgnoreCase("theme")) {

                setTheme(command);
                printPrompt();
                scrollToBottom();
                return;
            }
        
            String output = shell.execute(command);

            terminal.append(output);

            if (command.equalsIgnoreCase("developer") && shell.isDeveloperMode()) {
                    
                printDeveloperBanner();
            }
        }

            printPrompt();
            scrollToBottom();

        } 
        
        catch (Exception e) {

            terminal.append("YSH: input error\n");

            printPrompt();
            scrollToBottom();
        }
    }

    private void replaceCurrentInput(String text) {

        try {

            int end = terminal.getDocument().getLength();

            terminal.getDocument().remove(
                promptPosition,
                end - promptPosition
            );

            terminal.append(text);

            terminal.setCaretPosition(terminal.getDocument().getLength());

        } 
        
        catch (Exception ignored) {

        }
    } 

    
    // COMMAND COMPLETION
    private void completeCommand() {
    
        String input;
    
        try {
        
            int length = terminal.getDocument().getLength() - promptPosition;
        
            input = terminal.getText(promptPosition, length).trim();
        
        } catch (Exception e) {
        
            return;
        }
    
        if (input.isEmpty()) {
            return;
        }
    
        String[] commands = {
        
            "help",
            "chat host",
            "chat join",
            "mkdir",
            "touch",
            "rm",
            "rename",
            "copy",
            "paste",
            "move",
            "ls",
            "search",
            "cat",
            "write",
            "cd",
            "pwd",
            "home",
            "calc",
            "echo",
            "clear",
            "cls",
            "whatsup",
            "sysinfo",
            "theme dark",
            "theme cyan",
            "theme red",
            "theme light",
            "theme matrix",
            "theme purple",
            "theme pink",
            "theme magenta",
            "open",
            "run",
            "check",
            "developer",
            "note",
            "scan",
            "msg",
            "username",
            "listusers",
            "kick",
            "announce",
            "fileshare host",
            "fileshare get",
            "sudo",
            "whoami",
            "whocreatedyou",
            "easteregg",
            "exit"
        };
    
        ArrayList<String> matches = new ArrayList<>();
    
        for (String command : commands) {
        
            if (command.startsWith(input)) {
                matches.add(command);
            }
        }
    
        if (matches.size() == 1) {
        
            replaceCurrentInput(matches.get(0));
        
        } else if (matches.size() > 1) {
        
            terminal.append("\n");
        
            for (String match : matches) {
                terminal.append(match + "    ");
            }
        
            terminal.append("\n");
        
            printPrompt();
            replaceCurrentInput(input);
        }
    }

    // CLEAR 
    public void clearTerminal() {

        terminal.setText("");
        printBanner();
    }

    // SCROOL BOTTOM 
    private void scrollToBottom() {

        terminal.setCaretPosition(terminal.getDocument().getLength());
    }

    // PROMPT PROTECTION 
    private void protectPrompt() {

        if (terminal.getCaretPosition() < promptPosition) {

            terminal.setCaretPosition(terminal.getDocument().getLength());
        }
    }

    // KOMANI 
    private boolean checkKonami(int keyCode) {

        if (keyCode == konamiCode[konamiIndex]) {

            konamiIndex++;

            if (konamiIndex == konamiCode.length) {

                konamiIndex = 0;
                return  true;
            }

        } 
        
        else {

            konamiIndex = 0;
        }

        return false;
    }

    // ARCH 
    private boolean checkArch(int archkey) {

        if (archkey == archCode[archIndex]) {

            archIndex++;

            if (archIndex == archCode.length) {

                archIndex = 0;

                return true;
            }

        } 
        
        else {

            archIndex = 0;
        }

        return false;
    }

    // THEMES SYSTEM / FUNCTION 
    public  void setTheme(String theme) {

        switch (theme.toLowerCase()) {

            case "matrix":
                bgColor = Color.BLACK;
                textColor = Color.GREEN;
                break;

            case "blue":
                bgColor = Color.DARK_GRAY;
                textColor = Color.CYAN;
                break;

            case "purple":
                bgColor = new Color(30, 0, 50);
                textColor = new Color(200, 120, 255);
                break;

            case "red":
                bgColor = new Color(50, 0, 0);
                textColor = Color.RED;
                break;

            case "dark":
            case "black":
                bgColor = Color.BLACK;
                textColor = Color.LIGHT_GRAY;
                break;

            case "light":
            case "default":
                bgColor = Color.WHITE;
                textColor = Color.BLACK;
                break;

            case "pink":
                bgColor = Color.pink; 
                textColor = Color.CYAN;

            case "magenta":
                bgColor = Color.MAGENTA;
                textColor = Color.DARK_GRAY;
            

            default:
                terminal.append("themes: matrix | blue | purple | red | dark | light\n");
                terminal.append("upcoming: Translucent | Obuqe | Magenta | Pink - White | Orange - Yellow \n");
                return;
        }

        applyTheme();
    }

    // APPLY THEME - needed v8 refff 
    public void applyTheme(){
        
        terminal.setBackground(bgColor);
        terminal.setForeground(textColor);
        terminal.setCaretColor(textColor);
    }

    private void printDeveloperBanner() {

        terminal.append(
            """
            
    ========================================
              YSH DEVELOPER MODE
            BETA VERSION ACTIVATED -
        SWITCH YOUR VERSION FOR FULL COPY
    ========================================
        
    Developer access enabled.
    Build. Break. Learn. Repeat.
        
    """
        );
    }

    private void showNetworkMessage(String message) {

        try {
    
            int end = terminal.getDocument().getLength();
    
            String currentInput = terminal.getText(
                    promptPosition,
                    end - promptPosition
                );
    
            terminal.getDocument().remove(
                promptPosition,
                end - promptPosition
            );
    
            terminal.append("\n" + message + "\n");
            printPrompt();
            terminal.append(currentInput);
            scrollToBottom();
    
        } 
        
        catch (Exception ignored) {
    
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
            YSHWindow::new
        );
    }
}
