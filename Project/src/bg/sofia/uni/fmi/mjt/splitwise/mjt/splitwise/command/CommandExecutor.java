package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.FriendsAndGroupsHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.HelpHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.LoginHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.RegistrationHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.RepayHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.SplitHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.StatusHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.PaymentsLogData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.RegistrationsData;

import java.nio.channels.SocketChannel;

public class CommandExecutor {
    //Commands
    private static final String REGISTER_COMMAND = "register";
    private static final String LOGIN_COMMAND = "login";
    private static final String LOGOUT_COMMAND = "logout";
    private static final String EXIT_COMMAND = "exit";
    private static final String ADD_FRIEND_COMMAND = "add-friend";
    private static final String CREATE_GROUP_COMMAND = "create-group";
    private static final String SPLIT_COMMAND = "split";
    private static final String SPLIT_GROUP_COMMAND = "split-group";
    private static final String REPAYED_COMMAND = "repayed";
    private static final String REPAYED_GROUP_COMMAND = "repayed-group";
    private static final String GET_STATUS_COMMAND = "get-status";
    private static final String SHOW_FRIEND_LIST_COMMAND = "show-friends";
    private static final String SHOW_GROUP_LIST_COMMAND = "show-groups";
    private static final String SHOW_PAYMENTS_LOG_COMMAND = "show-payments";
    private static final String HELP_COMMAND = "help";

    // Messages
    private static final String UNKNOWN_COMMAND_MESSAGE
            = "Unknown command. Refer to help command for more information.";

    private final RegistrationsData registrations;
    private final LoginData loginData;
    private final ExpensesData expenses;
    private final PaymentsLogData payments;
    private final NotificationsData notifications;

    private final RegistrationHandler registrationHandler;
    private final LoginHandler loginHandler;
    private final FriendsAndGroupsHandler friendsAndGroupsHandler;
    private final SplitHandler splitHandler;
    private final RepayHandler repayHandler;
    private final StatusHandler statusHandler;
    private final HelpHandler helpHandler;

    private Command command;
    private SocketChannel socketOfExecutor;

    public CommandExecutor(RegistrationsData registrations, LoginData loginData, ExpensesData expenses,
                           PaymentsLogData payments, NotificationsData notifications) {
        this.registrations = registrations;
        this.loginData = loginData;
        this.expenses = expenses;
        this.payments = payments;
        this.notifications = notifications;

        this.registrationHandler = new RegistrationHandler(registrations, loginData);
        this.loginHandler = new LoginHandler(registrations, loginData, notifications);
        this.friendsAndGroupsHandler = new FriendsAndGroupsHandler(registrations, loginData, expenses);
        this.splitHandler = new SplitHandler(loginData, expenses, payments, notifications);
        this.repayHandler = new RepayHandler(loginData, expenses, notifications);
        this.statusHandler = new StatusHandler(loginData,expenses);
        this.helpHandler = new HelpHandler();
    }

    public String execute(SocketChannel socketOfExecutor, Command cmd) {
        this.socketOfExecutor = socketOfExecutor;
        command = cmd;
        return switch (command.getName()) {
            //here common handler
            case REGISTER_COMMAND -> registrationHandler.register(socketOfExecutor, command);
            case LOGIN_COMMAND -> loginHandler.login(socketOfExecutor, command);
            case LOGOUT_COMMAND -> loginHandler.logout(socketOfExecutor, command);
            case EXIT_COMMAND -> loginHandler.exit(socketOfExecutor, command);
            case ADD_FRIEND_COMMAND -> friendsAndGroupsHandler.addFriend(socketOfExecutor, command);
            case CREATE_GROUP_COMMAND -> friendsAndGroupsHandler.createGroup(socketOfExecutor, command);
            case SPLIT_COMMAND -> splitHandler.split(socketOfExecutor, command);
            case SPLIT_GROUP_COMMAND -> splitHandler.splitGroup(socketOfExecutor, command);
            case REPAYED_COMMAND -> repayHandler.getRepayed(socketOfExecutor, command);
            case REPAYED_GROUP_COMMAND -> repayHandler.getRepayedForGroup(socketOfExecutor, command);
            case GET_STATUS_COMMAND -> statusHandler.getStatus(socketOfExecutor, command);
            case SHOW_FRIEND_LIST_COMMAND -> friendsAndGroupsHandler.showFriendList(socketOfExecutor, command);
            case SHOW_GROUP_LIST_COMMAND -> friendsAndGroupsHandler.showGroupList(socketOfExecutor, command);
            case SHOW_PAYMENTS_LOG_COMMAND -> splitHandler.showPayments(socketOfExecutor, command);
            case HELP_COMMAND -> helpHandler.help(command);
            default -> UNKNOWN_COMMAND_MESSAGE;
        };
    }

}
