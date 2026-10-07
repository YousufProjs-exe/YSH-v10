
package ysh.core;

public class CommandParser {

    public Command parse(String input) {

        input = input.trim();

        if (input.isEmpty()) {
            
            return null;
        }

        String[] parts = input.split("\\s+");
        String name = parts[0].toLowerCase();            
        String[] args = new String[parts.length - 1];
        System.arraycopy(
            parts,
            1,
            args,
            0,
            args.length
        );

        return new Command(name, args);
    }
}
