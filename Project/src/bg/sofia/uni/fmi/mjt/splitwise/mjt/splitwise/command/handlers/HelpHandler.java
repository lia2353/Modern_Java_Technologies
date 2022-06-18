package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.CommandEnum;

import java.text.MessageFormat;

public class HelpHandler {

    // Commands
    private static final String HELP_COMMAND = "help";

    // Messages
    private static final String COMMANDS = "Commands: ";
    private static final String COMMAND = "Command: ";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ONE_ARGUMENT = 1;

    public String help(Command command) {
        if (command.hasNoArguments()) {
            return COMMANDS + System.lineSeparator() + CommandEnum.getAllCommandsFullNames();
        }
        if (command.hasOneArgument()) {
            String cmd = command.getArguments()[0];

            String message = CommandEnum.getFullNameAndDescriptionForCommand(cmd);
            if (message == null) {
                return COMMAND + System.lineSeparator() + UNKNOWN_COMMAND_MESSAGE;
            }
            return COMMAND + System.lineSeparator() + message;
        }
        return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, HELP_COMMAND, "no more than " + ONE_ARGUMENT);
    }
}
