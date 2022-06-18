package bg.sofia.uni.fmi.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.CommandEnum;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.HelpHandler;

import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;

public class HelpHandlerTest {

    private static final String TEST_UNKNOWN_COMMAND = "unknown-command";

    // Commands
    private static final String HELP_COMMAND = "help";
    private static final String HELP_COMMAND_INVALID_ARGUMENTS_COUNT
            = HELP_COMMAND + " " + HELP_COMMAND + " " + HELP_COMMAND;
    private static final String HELP_COMMAND_WITH_UNKNOWN_COMMAND = HELP_COMMAND + " " + TEST_UNKNOWN_COMMAND;

    // Messages
    private static final String COMMANDS = "Commands: ";
    private static final String COMMAND = "Command: ";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ONE_ARGUMENT = 1;

    private Command cmd;

    private static HelpHandler helpHandler;

    @Before
    public void setUp() {
        helpHandler = new HelpHandler();
    }

    // HELP_COMMAND
    @Test
    public void testHelpCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, HELP_COMMAND, "no more than " + ONE_ARGUMENT);
        cmd = new Command(HELP_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = helpHandler.help(cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void tesHelpCommandWithNoArguments() {
        String expected = COMMANDS + System.lineSeparator() + CommandEnum.getAllCommandsFullNames();
        cmd = new Command(HELP_COMMAND);
        String actual = helpHandler.help(cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void tesHelpCommandWithOneArgumentUnknownCommand() {
        String expected = COMMAND + System.lineSeparator() + UNKNOWN_COMMAND_MESSAGE;
        cmd = new Command(HELP_COMMAND_WITH_UNKNOWN_COMMAND);
        String actual = helpHandler.help(cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void tesHelpCommandWithOneArgumentValidCommand() {
        String expected = COMMAND + System.lineSeparator()
                + CommandEnum.getFullNameAndDescriptionForCommand(HELP_COMMAND);
        cmd = new Command(HELP_COMMAND + " " + HELP_COMMAND);
        String actual = helpHandler.help(cmd);

        assertEquals(expected, actual);
    }

}
