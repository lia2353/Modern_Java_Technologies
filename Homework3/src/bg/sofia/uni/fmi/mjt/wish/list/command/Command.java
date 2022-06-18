package bg.sofia.uni.fmi.mjt.wish.list.command;

import java.util.Arrays;

public class Command {
    private final String name;
    private final String[] arguments;

    public Command(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Provided input should not be null.");
        }
        input = input.trim();
        String[] tokens = input.split("\\s+");
        name = tokens[0];
        arguments = Arrays.copyOfRange(tokens, 1, tokens.length);
    }

    public String getName() {
        return name;
    }

    public String[] getArguments() {
        return arguments;
    }

    public int getArgumentsCount() {
        return arguments.length;
    }

    public String getFirstArgument() {
        return (arguments.length >= 1) ? arguments[0] : null;
    }

    public String getSecondArgument() {
        return (arguments.length >= 2) ? arguments[1] : null;
    }
}
