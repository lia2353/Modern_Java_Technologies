package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.RegistrationsData;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendsAndGroupsHandler {

    // Commands
    private static final String ADD_FRIEND_COMMAND = "add-friend";
    private static final String CREATE_GROUP_COMMAND = "create-group";
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
    private static final int FIRST_ARGUMENT = 0;
    private static final String FRIENDS = "Friends: ";
    private static final String GROUPS = "Groups: ";
    private static final String DASH = "-";

    private final RegistrationsData registrations;
    private final LoginData loginData;
    private final ExpensesData expenses;

    public FriendsAndGroupsHandler(RegistrationsData registrations, LoginData loginData, ExpensesData expenses) {
        this.registrations = registrations;
        this.loginData = loginData;
        this.expenses = expenses;
    }

    public String addFriend(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasOneArgument()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, ADD_FRIEND_COMMAND, ONE_ARGUMENT);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        String friendName = command.getArguments()[FIRST_ARGUMENT];
        if (!registrations.isRegistered(friendName)) {
            return MessageFormat.format(NO_SUCH_REGISTERED_USER_MESSAGE, friendName);
        }

        String username = loginData.getLoggedUsername(socketOfExecutor);
        if (friendName.equals(username)) {
            return CANT_ADD_SELF_TO_FRIENDS_MESSAGE;
        }
        if (expenses.areFriends(username, friendName)) {
            return MessageFormat.format(ALREADY_FRIENDS_MESSAGE, friendName);
        }

        expenses.addFriends(username, friendName);
        return MessageFormat.format(SUCCESSFUL_ADD_FRIEND_MESSAGE, friendName);
    }

    public String createGroup(SocketChannel socketOfExecutor, Command command) {
        if (command.getArgumentsCount() < THREE_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, CREATE_GROUP_COMMAND, "at least " + THREE_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        String groupName = command.getArguments()[FIRST_ARGUMENT];
        if (groupName.contains(DASH)) {
            return MessageFormat.format(GROUP_NAME_SHOULD_NOT_CONTAIN_MESSAGE, DASH);
        }
        String admin = loginData.getLoggedUsername(socketOfExecutor);
        if (expenses.hasGroup(admin, groupName)) {
            return MessageFormat.format(ALREADY_TAKEN_MESSAGE, groupName);
        }

        Set<String> members = Stream.of(command.getArguments())
                .skip(ONE_ARGUMENT)
                .collect(Collectors.toSet());
        for (String m : members) {
            if (!registrations.isRegistered(m)) {
                return MessageFormat.format(NO_SUCH_REGISTERED_USER_MESSAGE, m);
            }
            if (!expenses.areFriends(admin, m)) {
                return MessageFormat.format(NO_SUCH_FRIEND_MESSAGE, m);
            }
        }

        members.add(admin);
        for (String m : members) {
            expenses.addGroup(m, admin, groupName, members);
        }
        return MessageFormat.format(SUCCESSFUL_ADD_GROUP_MESSAGE, groupName);
    }

    public String showFriendList(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasNoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SHOW_FRIEND_LIST_COMMAND, ZERO_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        String username = loginData.getLoggedUsername(socketOfExecutor);
        return FRIENDS + expenses.getFriends(username);
    }

    public String showGroupList(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasNoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SHOW_GROUP_LIST_COMMAND, ZERO_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        String username = loginData.getLoggedUsername(socketOfExecutor);
        return GROUPS + expenses.getGroups(username);
    }
}
