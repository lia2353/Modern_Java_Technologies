package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command;

import java.util.Arrays;

public class Command {
    private static final int ZERO_ARGUMENTS = 0;
    private static final int ONE_ARGUMENT = 1;
    private static final int TWO_ARGUMENTS = 2;
    private static final int THREE_ARGUMENTS = 3;

    private final String name;
    private final String[] arguments;

    public Command(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Provided command input should not be null.");
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

    public boolean hasNoArguments() {
        return getArgumentsCount() == ZERO_ARGUMENTS;
    }

    public boolean hasOneArgument() {
        return getArgumentsCount() == ONE_ARGUMENT;
    }

    public boolean hasTwoArguments() {
        return getArgumentsCount() == TWO_ARGUMENTS;
    }

    public boolean hasThreeArguments() {
        return getArgumentsCount() == THREE_ARGUMENTS;
    }
}
