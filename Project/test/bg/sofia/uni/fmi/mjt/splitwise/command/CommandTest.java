package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandTest {
    private static final String commandNoArguments = "cmd";
    private static final String commandOneArgument = "cmd arg1";
    private static final String commandTwoArguments = "cmd arg1 arg2";
    private static final String commandThreeArguments = "cmd arg1 arg2 arg3";
    private static final String commandToBeTrimmed = "  cmd arg    ";

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCommandWIthNullArgument() {
        new Command(null);
    }

    // getName
    @Test
    public void testGetNameWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertEquals("Unexpected command name returned", commandNoArguments, cmd.getName());
    }

    @Test
    public void testGetNameWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertEquals("Unexpected command name returned", commandOneArgument.split(" ")[0], cmd.getName());
    }

    @Test
    public void testGetNameWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertEquals("Unexpected command name returned", commandTwoArguments.split(" ")[0], cmd.getName());
    }

    @Test
    public void testGetNameStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertEquals("Unexpected command name returned",
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

    // hasNoArguments
    @Test
    public void testHasNoArgumentsWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertTrue("Unexpected command arguments count", cmd.hasNoArguments());
    }

    @Test
    public void testHasNoArgumentsWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertFalse("Unexpected command arguments count", cmd.hasNoArguments());
    }

    @Test
    public void testHasNoArgumentsWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertFalse("Unexpected command arguments count", cmd.hasNoArguments());
    }

    @Test
    public void testHasNoArgumentsStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertFalse(cmd.hasNoArguments());
    }

    // hasOneArgument
    @Test
    public void testHasOneArgumentWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertFalse("Unexpected command arguments count", cmd.hasOneArgument());
    }

    @Test
    public void testHasOneArgumentWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertTrue("Unexpected command arguments count", cmd.hasOneArgument());
    }

    @Test
    public void testHasOneArgumentWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertFalse("Unexpected command arguments count", cmd.hasOneArgument());
    }

    @Test
    public void testHasOneArgumentStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertTrue("Unexpected command arguments count", cmd.hasOneArgument());

    }

    // hasTwoArguments
    @Test
    public void testHasTwoArgumentsWithNoArguments() {
        Command cmd = new Command(commandNoArguments);
        assertFalse("Unexpected command arguments count", cmd.hasTwoArguments());
    }

    @Test
    public void testHasTwoArgumentsWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertFalse("Unexpected command arguments count", cmd.hasTwoArguments());
    }

    @Test
    public void testHasTwoArgumentsWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertTrue("Unexpected command arguments count", cmd.hasTwoArguments());
    }

    @Test
    public void testHasTwoArgumentsStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertFalse("Unexpected command arguments count", cmd.hasTwoArguments());
    }

    // hasThreeArguments
    @Test
    public void testHasThreeArguments() {
        Command cmd = new Command(commandNoArguments);
        assertFalse("Unexpected command arguments count", cmd.hasThreeArguments());
    }

    @Test
    public void testHasThreeArgumentsWithOneArgument() {
        Command cmd = new Command(commandOneArgument);
        assertFalse("Unexpected command arguments count", cmd.hasThreeArguments());
    }

    @Test
    public void testHasThreeArgumentsWithTwoArguments() {
        Command cmd = new Command(commandTwoArguments);
        assertFalse("Unexpected command arguments count", cmd.hasThreeArguments());
    }

    @Test
    public void testHasThreeArgumentsWithThreeArguments() {
        Command cmd = new Command(commandThreeArguments);
        assertTrue("Unexpected command arguments count", cmd.hasThreeArguments());
    }

    @Test
    public void testHasThreeArgumentsStringToBeTrimmed() {
        Command cmd = new Command(commandToBeTrimmed);
        assertFalse("Unexpected command arguments count", cmd.hasThreeArguments());
    }
}
