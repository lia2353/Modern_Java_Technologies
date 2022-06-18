package bg.sofia.uni.fmi.mjt.wish.list.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CommandTest {
    private static final String commandNoArguments = "cmd";
    private static final String commandOneArgument = "cmd arg1";
    private static final String commandTwoArguments = "cmd arg1 arg2";
    private static final String commandToBeTrimmed = "  cmd arg    ";

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCommandWIthNullArgument() {
        new Command(null);
    }

    // getCommand
    @Test
    public void testGetCommandWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertEquals("Unexpected command returned for the command", commandNoArguments, cmd.getName());
    }

    @Test
    public void testGetCommandWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertEquals("Unexpected command returned for the command",
                commandOneArgument.split(" ")[0], cmd.getName());
    }

    @Test
    public void testGetCommandWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertEquals("Unexpected command returned for the command",
                commandTwoArguments.split(" ")[0], cmd.getName());
    }

    @Test
    public void testGetCommandStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertEquals("Unexpected command returned for the command",
                commandToBeTrimmed.trim().split(" ")[0], cmd.getName());
    }

    // getArguments
    @Test
    public void testGetArgumentsWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertNotNull("Command arguments list should not be null", cmd.getArguments());
        assertEquals("Unexpected command arguments count", 0, cmd.getArguments().length);
    }

    @Test
    public void testGetArgumentsWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertNotNull("Command arguments should not be null", cmd.getArguments());
        assertEquals("Unexpected command arguments count", 1, cmd.getArguments().length);
        assertEquals("Unexpected argument returned for the command",
                commandOneArgument.split(" ")[1], cmd.getArguments()[0]);
    }

    @Test
    public void testGetArgumentsWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertNotNull("Command arguments should not be null", cmd.getArguments());
        assertEquals("Unexpected command arguments count", 2, cmd.getArguments().length);
        assertEquals("Unexpected first argument returned for the command",
                commandTwoArguments.split(" ")[1], cmd.getArguments()[0]);
        assertEquals("Unexpected second argument returned for the command",
                commandTwoArguments.split(" ")[2], cmd.getArguments()[1]);
    }

    @Test
    public void testGetArgumentsStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertNotNull("Command arguments should not be null", cmd.getArguments());
        assertEquals("Unexpected command arguments count", 1, cmd.getArguments().length);
        assertEquals("Unexpected first argument returned for the command",
                commandToBeTrimmed.trim().split(" ")[1], cmd.getArguments()[0]);
    }

    // getArgumentsCount
    @Test
    public void testGetArgumentsCountWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertEquals("Unexpected command arguments count", 0, cmd.getArgumentsCount());
    }

    @Test
    public void testGetArgumentsCountWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertEquals("Unexpected command arguments count", 1, cmd.getArgumentsCount());
    }

    @Test
    public void testGetArgumentsCountWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertEquals("Unexpected command arguments count", 2, cmd.getArgumentsCount());
    }

    @Test
    public void testGetArgumentsCountStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertEquals("Unexpected command arguments count", 1, cmd.getArgumentsCount());
    }

    // getFirstArgument
    @Test
    public void testGetFirstArgumentWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertNull("First argument should be null", cmd.getFirstArgument());
    }

    @Test
    public void testGetFirstArgumentWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertEquals("Unexpected first argument returned for the command",
                commandOneArgument.split(" ")[1], cmd.getFirstArgument());
    }

    @Test
    public void testGetFirstArgumentWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertEquals("Unexpected first argument returned for the command",
                commandTwoArguments.split(" ")[1], cmd.getFirstArgument());
    }

    @Test
    public void testGetFirstArgumentStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertEquals("Unexpected first argument returned for the command",
                commandToBeTrimmed.trim().split(" ")[1], cmd.getFirstArgument());
    }

    // getSecondArgument
    @Test
    public void testGetSecondArgumentWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertNull("First argument should be null", cmd.getSecondArgument());
    }

    @Test
    public void testGetSecondArgumentWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertNull("First argument should be null", cmd.getSecondArgument());
    }

    @Test
    public void testGetSecondArgumentWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertEquals("Unexpected first argument returned for the command",
                commandTwoArguments.split(" ")[2], cmd.getSecondArgument());
    }

    @Test
    public void testGetSecondArgumentStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertNull("First argument should be null", cmd.getSecondArgument());
    }

}
