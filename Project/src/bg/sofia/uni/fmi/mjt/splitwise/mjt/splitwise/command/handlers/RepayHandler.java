package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

public class RepayHandler {

    // Commands
    private static final String REPAYED_COMMAND = "repayed";
    private static final String REPAYED_FOR_GROUP_COMMAND = "repayed-group";

    // Messages
    private static final String SUCCESSFUL_PAYMENT_MESSAGE = "{0} payed you {1} LV." + System.lineSeparator()
            + "Current status: Owes you {2} LV";
    private static final String SUCCESSFUL_GROUP_PAYMENT_MESSAGE = "{0} payed you {1} LV for {2} group."
            + System.lineSeparator() + "Current status: Owes you {3} LV for group {2}";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String NO_SUCH_FRIEND_MESSAGE = "You are not friends with {0}.";
    private static final String NO_USER_EXPENSE_MESSAGE = "There are no expenses for friend {0}.";
    private static final String FRIEND_OWES_LESS_MESSAGE = "Invalid operation. {0} owes you only {1} LV";
    private static final String NO_SUCH_GROUP_MESSAGE = "You have no group named {0}.";
    private static final String NO_SUCH_GROUP_MEMBER_MESSAGE = "{0} group has no member {1}.";
    private static final String NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE
            = "Provide valid number for amount argument. Refer to help command for more information.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int TWO_ARGUMENTS = 2;
    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;
    private static final int THIRD_ARGUMENT = 2;

    private static final String DECIMAL_NUMBER = "[0-9]+(\\.[0-9]{1,2})?";

    private final LoginData loginData;
    private final ExpensesData expenses;
    private final NotificationsData notifications;

    public RepayHandler(LoginData loginData, ExpensesData expenses, NotificationsData notifications) {
        this.loginData = loginData;
        this.expenses = expenses;
        this.notifications = notifications;
    }

    public String getRepayed(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasTwoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REPAYED_COMMAND, TWO_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        if (!command.getArguments()[FIRST_ARGUMENT].matches(DECIMAL_NUMBER)) {
            return NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        }
        Double payment = Double.parseDouble(command.getArguments()[FIRST_ARGUMENT]);
        String friendName = command.getArguments()[SECOND_ARGUMENT];
        String username = loginData.getLoggedUsername(socketOfExecutor);
        if (!expenses.areFriends(username, friendName)) {
            return MessageFormat.format(NO_SUCH_FRIEND_MESSAGE, friendName);
        }
        if (!expenses.isFriendOwing(username, friendName)) {
            return MessageFormat.format(NO_USER_EXPENSE_MESSAGE, friendName);
        }
        Double friendExpense = expenses.getFriendOwing(username, friendName);
        if (friendExpense < payment) {
            return MessageFormat.format(FRIEND_OWES_LESS_MESSAGE, friendName, friendExpense);
        }

        expenses.payedFriendExpense(username, friendName, payment);
        if (!loginData.isUsernameLogged(friendName)) {
            notifications.addApprovedPayment(friendName, username, payment, null);
        }
        Double newExpense = expenses.getFriendOwing(username, friendName);
        return MessageFormat.format(SUCCESSFUL_PAYMENT_MESSAGE, friendName, payment, newExpense);
    }

    public String getRepayedForGroup(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasThreeArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REPAYED_FOR_GROUP_COMMAND, TWO_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        if (!command.getArguments()[FIRST_ARGUMENT].matches(DECIMAL_NUMBER)) {
            return NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        }

        Double payment = Double.parseDouble(command.getArguments()[FIRST_ARGUMENT]);
        String groupName = command.getArguments()[SECOND_ARGUMENT];
        String member = command.getArguments()[THIRD_ARGUMENT];
        String username = loginData.getLoggedUsername(socketOfExecutor);
        if (!expenses.hasGroup(username, groupName)) {
            return MessageFormat.format(NO_SUCH_GROUP_MESSAGE, groupName);
        }
        if (!expenses.hasGroupMember(username, groupName, member)) {
            return MessageFormat.format(NO_SUCH_GROUP_MEMBER_MESSAGE, groupName, member);
        }

        if (!expenses.isMemberGroupOwing(username, groupName, member)) {
            return MessageFormat.format(NO_USER_EXPENSE_MESSAGE, member);
        }
        Double memberExpense = expenses.getMemberOwing(username, groupName, member);
        if (memberExpense < payment) {
            return MessageFormat.format(FRIEND_OWES_LESS_MESSAGE, member, memberExpense);
        }

        expenses.payedMemberExpense(username, groupName, member, payment);
        if (!loginData.isUsernameLogged(member)) {
            notifications.addApprovedPayment(member, username, payment, groupName);
        }
        Double newExpense = expenses.getMemberOwing(username, groupName, member);
        return MessageFormat.format(SUCCESSFUL_GROUP_PAYMENT_MESSAGE, member, payment, groupName, newExpense);
    }
}
