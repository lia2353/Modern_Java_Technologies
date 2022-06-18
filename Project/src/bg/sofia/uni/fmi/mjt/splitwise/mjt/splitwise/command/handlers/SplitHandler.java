package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.Group;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.PaymentsLogData;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SplitHandler {

    // Commands
    private static final String SPLIT_COMMAND = "split";
    private static final String SPLIT_GROUP_COMMAND = "split-group";
    private static final String SHOW_PAYMENTS_LOG_COMMAND = "show-payments";

    // Messages
    private static final String SUCCESSFUL_SPLIT_MESSAGE = "Split {0} LV between you and {1}.";
    private static final String SUCCESSFUL_GROUP_SPLIT_MESSAGE = "Split {0} LV between members in {1} group";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String NO_SUCH_FRIEND_MESSAGE = "You are not friends with {0}.";
    private static final String NO_SUCH_GROUP_MESSAGE = "You have no group named {0}.";
    private static final String NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE
            = "Provide valid number for amount argument. Refer to help command for more information.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ZERO_ARGUMENTS = 0;
    private static final int TWO_ARGUMENTS = 2;
    private static final int THREE_ARGUMENTS = 3;
    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;
    private static final String PAYMENTS = "*** Payments ***";
    private static final String NO_PAYMENTS_TO_SHOW_MESSAGE = "There are no payments.";


    private static final String DECIMAL_NUMBER = "[0-9]+(\\.[0-9]{1,2})?";

    private final LoginData loginData;
    private final ExpensesData expenses;
    private final PaymentsLogData payments;
    private final NotificationsData notifications;

    public SplitHandler(LoginData loginData, ExpensesData expenses, PaymentsLogData payments,
                        NotificationsData notifications) {
        this.loginData = loginData;
        this.expenses = expenses;
        this.payments = payments;
        this.notifications = notifications;
    }

    public String split(SocketChannel socketOfExecutor, Command command) {
        if (command.getArgumentsCount() < THREE_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SPLIT_COMMAND, THREE_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        if (!command.getArguments()[FIRST_ARGUMENT].matches(DECIMAL_NUMBER)) {
            return NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        }
        Double amount = Double.parseDouble(command.getArguments()[FIRST_ARGUMENT]);
        String friendName = command.getArguments()[SECOND_ARGUMENT];
        String reasonForPayment = Stream.of(command.getArguments())
                .skip(TWO_ARGUMENTS)
                .collect(Collectors.joining(" "));
        String username = loginData.getLoggedUsername(socketOfExecutor);
        if (!expenses.areFriends(username, friendName)) {
            return MessageFormat.format(NO_SUCH_FRIEND_MESSAGE, friendName);
        }

        Double splitAmount = expenses.splitFriendExpense(username, friendName, amount);
        payments.addPayment(false, username, amount, friendName, reasonForPayment, LocalDate.now());
        if (!loginData.isUsernameLogged(friendName)) {
            notifications.addPayment(friendName, username, splitAmount, reasonForPayment, null);
        }
        return MessageFormat.format(SUCCESSFUL_SPLIT_MESSAGE, amount, friendName);
    }

    public String splitGroup(SocketChannel socketOfExecutor, Command command) {
        if (command.getArgumentsCount() < THREE_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SPLIT_GROUP_COMMAND, THREE_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        if (!command.getArguments()[FIRST_ARGUMENT].matches(DECIMAL_NUMBER)) {
            return NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        }
        Double amount = Double.parseDouble(command.getArguments()[FIRST_ARGUMENT]);
        String groupName = command.getArguments()[SECOND_ARGUMENT];
        String reasonForPayment = Stream.of(command.getArguments())
                .skip(TWO_ARGUMENTS)
                .collect(Collectors.joining(" "));
        String username = loginData.getLoggedUsername(socketOfExecutor);
        if (!expenses.hasGroup(username, groupName)) {
            return MessageFormat.format(NO_SUCH_GROUP_MESSAGE, groupName);
        }

        Double splitAmount = expenses.splitGroupExpense(username, groupName, amount);
        payments.addPayment(true, username, amount, groupName, reasonForPayment, LocalDate.now());
        Group group = expenses.getGroup(username, groupName);
        for (String m : group.getMembers()) {
            if (!loginData.isUsernameLogged(m)) {
                notifications.addPayment(m, username, splitAmount, reasonForPayment, groupName);
            }
        }
        return MessageFormat.format(SUCCESSFUL_GROUP_SPLIT_MESSAGE, amount, groupName);
    }

    public String showPayments(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasNoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SHOW_PAYMENTS_LOG_COMMAND, ZERO_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }
        String username = loginData.getLoggedUsername(socketOfExecutor);
        if (payments.hasNoPayments(username)) {
            return NO_PAYMENTS_TO_SHOW_MESSAGE;
        }

        return PAYMENTS + System.lineSeparator() + payments.getPayments(username);
    }
}
