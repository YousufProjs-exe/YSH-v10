
package ysh.core;

public class Command {

    private final String name;
    private final String[] args;

    public Command(String name, String[] args) {

        this.name = name;
        this.args = args;
    }

    public String getName() {

        return name;
    }

    public String[] getArgs() {

        return args;
    }
}
