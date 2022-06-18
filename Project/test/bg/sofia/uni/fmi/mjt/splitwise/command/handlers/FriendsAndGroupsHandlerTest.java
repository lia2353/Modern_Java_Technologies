package bg.sofia.uni.fmi.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.FriendsAndGroupsHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.RegistrationsData;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FriendsAndGroupsHandlerTest {

    private static final String TEST_FRIEND_NAME = "testFriendName";
    private static final String TEST_FRIEND_NAME2 = "testFriendName2";
    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_GROUP_NAME = "testGroupName";
    private static final String TEST_INVALID_GROUP_NAME = "test-groupName";
    private static final String TEST_MEMBER1 = "testMember1";
    private static final String TEST_MEMBER2 = "testMember2";

    // Commands
    private static final String ADD_FRIEND_COMMAND = "add-friend";
    private static final String ADD_FRIEND_COMMAND_INVALID_ARGUMENTS_COUNT = ADD_FRIEND_COMMAND;
    private static final String ADD_FRIEND_COMMAND_VALID = ADD_FRIEND_COMMAND + " " + TEST_FRIEND_NAME;
    private static final String CREATE_GROUP_COMMAND = "create-group";
    private static final String CREATE_GROUP_COMMAND_INVALID_ARGUMENTS_COUNT = CREATE_GROUP_COMMAND;
    private static final String CREATE_GROUP_COMMAND_VALID = CREATE_GROUP_COMMAND + " "
            + TEST_GROUP_NAME + " " + TEST_MEMBER1 + " " + TEST_MEMBER2;
    private static final String CREATE_GROUP_COMMAND_INVALID_GROUP_NAME = CREATE_GROUP_COMMAND + " "
            + TEST_INVALID_GROUP_NAME + " " + TEST_MEMBER1 + " " + TEST_MEMBER2;
    private static final String SHOW_FRIEND_LIST_COMMAND = "show-friends";
    private static final String SHOW_GROUP_LIST_COMMAND = "show-groups";

    // Messages
    private static final String SUCCESSFUL_ADD_FRIEND_MESSAGE = "You are now friends with {0}.";
    private static final String SUCCESSFUL_ADD_GROUP_MESSAGE = "Successfully created new group {0}.";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String NO_SUCH_REGISTERED_USER_MESSAGE = "Username {0} is not registered.";
    private static final String ALREADY_TAKEN_MESSAGE = "{0} is already taken, select another one.";
    private static final String ALREADY_FRIENDS_MESSAGE = "You are already friends with {0}.";
    private static final String CANT_ADD_SELF_TO_FRIENDS_MESSAGE = "You can't add yourself to friends.";
    private static final String NO_SUCH_FRIEND_MESSAGE = "You are not friends with {0}.";
    private static final String GROUP_NAME_SHOULD_NOT_CONTAIN_MESSAGE = "Group name should not contain symbol ({0}).";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ZERO_ARGUMENTS = 0;
    private static final int ONE_ARGUMENT = 1;
    private static final int THREE_ARGUMENTS = 3;
    private static final String FRIENDS = "Friends: ";
    private static final String GROUPS = "Groups: ";
    private static final String DASH = "-";

    private static RegistrationsData registrations;
    private static LoginData loginData;
    private static ExpensesData expenses;

    private static SocketChannel testSocket;
    private Command cmd;

    private static FriendsAndGroupsHandler friendsAndGroupsHandler;

    @Before
    public void setUp() {
        registrations = mock(RegistrationsData.class);
        loginData = mock(LoginData.class);
        expenses = mock(ExpensesData.class);
        testSocket = mock(SocketChannel.class);
        friendsAndGroupsHandler = new FriendsAndGroupsHandler(registrations, loginData, expenses);
    }

    // ADD_FRIEND_COMMAND
    @Test
    public void testAddFriendCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, ADD_FRIEND_COMMAND, ONE_ARGUMENT);
        cmd = new Command(ADD_FRIEND_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = friendsAndGroupsHandler.addFriend(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddFriendCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(ADD_FRIEND_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.addFriend(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddFriendCommandNoSuchRegisteredUser() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_REGISTERED_USER_MESSAGE, TEST_FRIEND_NAME);
        cmd = new Command(ADD_FRIEND_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.addFriend(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddFriendCommandAddSelf() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_FRIEND_NAME);

        String expected = CANT_ADD_SELF_TO_FRIENDS_MESSAGE;
        cmd = new Command(ADD_FRIEND_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.addFriend(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddFriendCommandAlreadyFriends() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(true);

        String expected = MessageFormat.format(ALREADY_FRIENDS_MESSAGE, TEST_FRIEND_NAME);
        cmd = new Command(ADD_FRIEND_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.addFriend(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddFriendCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(false);
        doNothing().when(expenses).addFriends(TEST_USERNAME, TEST_FRIEND_NAME);

        String expected = MessageFormat.format(SUCCESSFUL_ADD_FRIEND_MESSAGE, TEST_FRIEND_NAME);
        cmd = new Command(ADD_FRIEND_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.addFriend(testSocket, cmd);

        assertEquals(expected, actual);
        verify(expenses, times(1)).addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
    }

    // CREATE_GROUP_COMMAND
    @Test
    public void testCreateGroupCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, CREATE_GROUP_COMMAND, "at least " + THREE_ARGUMENTS);
        cmd = new Command(CREATE_GROUP_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = friendsAndGroupsHandler.createGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroupCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(CREATE_GROUP_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.createGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroupCommandInvalidGroupName() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(false);

        String expected = MessageFormat.format(GROUP_NAME_SHOULD_NOT_CONTAIN_MESSAGE, DASH);
        cmd = new Command(CREATE_GROUP_COMMAND_INVALID_GROUP_NAME);
        String actual = friendsAndGroupsHandler.createGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroupAlreadyHaveGroup() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(true);

        String expected = MessageFormat.format(ALREADY_TAKEN_MESSAGE, TEST_GROUP_NAME);
        cmd = new Command(CREATE_GROUP_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.createGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroupCommandMemberNotRegistered() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(false);
        when(registrations.isRegistered(TEST_MEMBER1)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_REGISTERED_USER_MESSAGE, TEST_MEMBER1);
        cmd = new Command(CREATE_GROUP_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.createGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroupCommandMemberNotFriend() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(false);
        when(registrations.isRegistered(TEST_MEMBER1)).thenReturn(true);
        when(registrations.isRegistered(TEST_MEMBER2)).thenReturn(true);
        when(expenses.areFriends(TEST_USERNAME, TEST_MEMBER1)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_FRIEND_MESSAGE, TEST_MEMBER1);
        cmd = new Command(CREATE_GROUP_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.createGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testCreateGroupCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrations.isRegistered(TEST_FRIEND_NAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(false);
        when(registrations.isRegistered(TEST_MEMBER1)).thenReturn(true);
        when(registrations.isRegistered(TEST_MEMBER2)).thenReturn(true);
        when(expenses.areFriends(TEST_USERNAME, TEST_MEMBER1)).thenReturn(true);
        when(expenses.areFriends(TEST_USERNAME, TEST_MEMBER2)).thenReturn(true);

        Set<String> members = Set.of(TEST_USERNAME, TEST_MEMBER1, TEST_MEMBER2);
        doNothing().when(expenses).addGroup(TEST_USERNAME, TEST_USERNAME, TEST_GROUP_NAME, members);
        doNothing().when(expenses).addGroup(TEST_MEMBER1, TEST_USERNAME, TEST_GROUP_NAME, members);
        doNothing().when(expenses).addGroup(TEST_MEMBER2, TEST_USERNAME, TEST_GROUP_NAME, members);

        String expected = MessageFormat.format(SUCCESSFUL_ADD_GROUP_MESSAGE, TEST_GROUP_NAME);
        cmd = new Command(CREATE_GROUP_COMMAND_VALID);
        String actual = friendsAndGroupsHandler.createGroup(testSocket, cmd);

        assertEquals(expected, actual);
        verify(expenses, times(1)).addGroup(TEST_USERNAME, TEST_USERNAME, TEST_GROUP_NAME, members);
        verify(expenses, times(1)).addGroup(TEST_MEMBER1, TEST_USERNAME, TEST_GROUP_NAME, members);
        verify(expenses, times(1)).addGroup(TEST_MEMBER2, TEST_USERNAME, TEST_GROUP_NAME, members);
    }

    // SHOW_FRIEND_LIST_COMMAND
    @Test
    public void testShowFriendListCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SHOW_FRIEND_LIST_COMMAND, ZERO_ARGUMENTS);
        cmd = new Command(SHOW_FRIEND_LIST_COMMAND + " " + SHOW_FRIEND_LIST_COMMAND);
        String actual = friendsAndGroupsHandler.showFriendList(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testShowFriendListCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(SHOW_FRIEND_LIST_COMMAND);
        String actual = friendsAndGroupsHandler.showFriendList(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testShowFriendListCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.getFriends(TEST_USERNAME)).thenReturn(TEST_FRIEND_NAME + ", " + TEST_FRIEND_NAME2);

        String expected = FRIENDS + TEST_FRIEND_NAME + ", " + TEST_FRIEND_NAME2;
        cmd = new Command(SHOW_FRIEND_LIST_COMMAND);
        String actual = friendsAndGroupsHandler.showFriendList(testSocket, cmd);

        assertEquals(expected, actual);
    }

    // SHOW_GROUP_LIST_COMMAND
    @Test
    public void testShowGroupListCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SHOW_GROUP_LIST_COMMAND, ZERO_ARGUMENTS);
        cmd = new Command(SHOW_GROUP_LIST_COMMAND + " " + SHOW_GROUP_LIST_COMMAND);
        String actual = friendsAndGroupsHandler.showGroupList(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testShowGroupListCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(SHOW_GROUP_LIST_COMMAND);
        String actual = friendsAndGroupsHandler.showGroupList(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testShowGroupListCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.getGroups(TEST_USERNAME)).thenReturn(TEST_GROUP_NAME);

        String expected = GROUPS + TEST_GROUP_NAME;
        cmd = new Command(SHOW_GROUP_LIST_COMMAND);
        String actual = friendsAndGroupsHandler.showGroupList(testSocket, cmd);

        assertEquals(expected, actual);
    }
}
