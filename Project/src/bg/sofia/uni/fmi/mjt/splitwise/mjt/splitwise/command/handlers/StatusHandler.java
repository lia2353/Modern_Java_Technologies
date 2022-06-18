package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

public class StatusHandler {

    // Commands
    private static final String GET_STATUS_COMMAND = "get-status";

    // Messages
    private static final String STATUS = "*** Status ***";
    private static final String NOTHING_TO_SHOW = "All settled up!";
    private static final String FRIENDS = "Friends: ";
    private static final String GROUPS = "Groups: ";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ZERO_ARGUMENTS = 0;

    private final LoginData loginData;
    private final ExpensesData expenses;

    public StatusHandler(LoginData loginData, ExpensesData expenses) {
        this.loginData = loginData;
        this.expenses = expenses;
    }

    public String getStatus(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasNoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, GET_STATUS_COMMAND, ZERO_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        String username = loginData.getLoggedUsername(socketOfExecutor);
        String friendsStatus = expenses.getFriendsExpensesStatus(username);
        String groupsStatus = expenses.getGroupsExpensesStatus(username);

        String status = STATUS;
        if (friendsStatus.isEmpty() && groupsStatus.isEmpty()) {
            status += System.lineSeparator() + NOTHING_TO_SHOW;
        }
        if (!friendsStatus.isEmpty()) {
            status += System.lineSeparator() + FRIENDS + friendsStatus;
        }
        if (!groupsStatus.isEmpty()) {
            status += System.lineSeparator() + GROUPS + groupsStatus;
        }
        return status;
    }


}
