package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.CommandEnum;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CommandEnumTest {
    private static final String LOGIN_COMMAND = "login";
    private static final String LOGIN_COMMAND_FULL_FORM = "login <username> <password>";
    private static final String LOGIN_COMMAND_DESCRIPTION
            = "Login to account with valid <username> and <password> combination";

    // getFullForm
    @Test
    public void testGetFullFormWithCorrectCommand() {
        assertEquals(LOGIN_COMMAND_FULL_FORM, CommandEnum.LOGIN_COMMAND.getFullForm());
    }

    @Test
    public void testGetFullFormWithDifferentCommand() {
        assertNotEquals(LOGIN_COMMAND_FULL_FORM, CommandEnum.ADD_FRIEND_COMMAND.getFullForm());
    }

    // getName
    @Test
    public void testGetNameWithCorrectCommand() {
        assertEquals(LOGIN_COMMAND, CommandEnum.LOGIN_COMMAND.getName());
    }

    @Test
    public void testGetNameWithDifferentCommand() {
        assertNotEquals(LOGIN_COMMAND, CommandEnum.ADD_FRIEND_COMMAND.getName());
    }

    // getDescription
    @Test
    public void testGetDescriptionWithCorrectCommand() {
        assertEquals(LOGIN_COMMAND_DESCRIPTION, CommandEnum.LOGIN_COMMAND.getDescription());
    }

    @Test
    public void testGetDescriptionWithDifferentCommand() {
        assertNotEquals(LOGIN_COMMAND_DESCRIPTION, CommandEnum.ADD_FRIEND_COMMAND.getDescription());
    }

}
