
package ysh.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandParser {

    public Command parse(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        List<String> parts = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        char quote = 0;
        boolean tokenStarted = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (quote != 0) {
                if (c == quote) {
                    quote = 0;
                } else if (c == '\\'
                        && i + 1 < input.length()
                        && (input.charAt(i + 1) == quote
                        || input.charAt(i + 1) == '\\')) {
                    token.append(input.charAt(++i));
                } else {
                    token.append(c);
                }
                continue;
            }

            if (c == '"' || c == '\'') {
                quote = c;
                tokenStarted = true;
            } else if (Character.isWhitespace(c)) {
                if (tokenStarted) {
                    parts.add(token.toString());
                    token.setLength(0);
                    tokenStarted = false;
                }
            } else {
                token.append(c);
                tokenStarted = true;
            }
        }

        if (quote != 0) {
            throw new IllegalArgumentException("Unclosed quotation mark");
        }

        if (tokenStarted) {
            parts.add(token.toString());
        }

        if (parts.isEmpty()) {
            return null;
        }

        String name = parts.remove(0).toLowerCase(Locale.ROOT);

        return new Command(name, parts.toArray(new String[0]));
    }
}
