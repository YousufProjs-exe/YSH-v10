
package ysh.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandRegistry {

    private final Map<String, Function<String[], String>> commands =
        new HashMap<>();

    public void register(
        String name,
        Function<String[], String> command
    ) {
        commands.put(name, command);
    }

    public String execute(Command command) {

        Function<String[], String> action =
            commands.get(command.getName());

        if (action == null) {
            return null;
        }

        return action.apply(command.getArgs());
    }
}
