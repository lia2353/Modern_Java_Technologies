package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CommandEnum {
    REGISTER_COMMAND("register <username> <password> <email>",
            "Register new user: " + System.lineSeparator()
                    + "<username> should contain at least 3 symbols (alpha-numeric, underscore and dot)"
                    + System.lineSeparator()
                    + "<password> should contain at least 8 symbols. Should contain at least one number, one upper and "
                    + "one lower case character, one spechial character (@#$%^&+=)" + System.lineSeparator()
                    + "<email> should be valid email"),
    LOGIN_COMMAND("login <username> <password>",
            "Login to account with valid <username> and <password> combination"),
    LOGOUT_COMMAND("logout", "Logout from account"),
    EXIT_COMMAND("exit", "Close Split(NotSo)Wise application"),
    ADD_FRIEND_COMMAND("add-friend <username>",
            "Add new friend to your friend list:" + System.lineSeparator()
                    + "<username> should be valid username of registered user"),
    CREATE_GROUP_COMMAND("create-group <group_name> <member_username> ... <member_username>",
            "Create new group with at least two members:" + System.lineSeparator()
                    + "<group_name> should be unique group name. (Contains NO dashes)" + System.lineSeparator()
                    + "<member_username> should be valid username from your friend list"),
    SPLIT_COMMAND("split <amount> <username> <reason_for_payment>",
            "Split expense with friend:" + System.lineSeparator()
                    + "<amount> is the amount of money to be split" + System.lineSeparator()
                    + "<username> should be valid username from your friend list" + System.lineSeparator()
                    + "<reason_for_payment> is payment description"),
    SPLIT_GROUP_COMMAND("split-group <amount> <group_name> <reason_for_payment>",
            "Split expense with group:" + System.lineSeparator()
                    + "<amount> is the amount of money to be split" + System.lineSeparator()
                    + "<group_name> should be valid group name from your group list" + System.lineSeparator()
                    + "<reason_for_payment> is payment description"),
    REPAYED_COMMAND("repayed <amount> <username>",
            "Add expense repayment from friend:" + System.lineSeparator()
                    + "<amount> is the repayed amount of money; should be equal or less than the money owed by the friend"
                    + System.lineSeparator()
                    + "<username> should be valid username from your friend list"),
    REPAYED_GROUP_COMMAND("repayed-group <amount> <group_name> <username>",
            "Add expense repayment for group from group member:" + System.lineSeparator()
                    + "<amount> is the repayed amount of money; should be equal or less than the money owed by the member"
                    + System.lineSeparator()
                    + "<group_name> should be valid group name from your group list" + System.lineSeparator()
                    + "<username> should be valid username name from your group members list"),
    GET_STATUS_COMMAND("get-status",
            "Show your expenses status in Friends and Groups categories (money you owe and money you are owed)"),
    SHOW_FRIEND_LIST_COMMAND("show-friends", "Show your friend list"),
    SHOW_GROUP_LIST_COMMAND("show-groups", "Show your group list"),
    SHOW_PAYMENTS_LOG_COMMAND("show-payments",
            "Show all your payments:" + System.lineSeparator()
                    + "Format: [Split <amount> LV expense with <friend/group> (group) on <date>. "
                    + "Reason for payment: <reason_for_payment>"),
    HELP_COMMAND("help [<command>]",
            "Provides information about available commands: " + System.lineSeparator()
                    + "<command> should be valid command (optional argument); "
                    + "If provided, information about this command is shown");

    private final String fullForm;
    private final String description;

    CommandEnum(String fullForm, String description) {
        this.fullForm = fullForm;
        this.description = description;
    }

    public String getFullForm() {
        return fullForm;
    }

    public String getName() {
        return fullForm.split(" ")[0];
    }

    public String getDescription() {
        return description;
    }

    public static String getAllCommandsFullNames() {
        return Stream.of(CommandEnum.values())
                .map(CommandEnum::getFullForm)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public static String getFullNameAndDescriptionForCommand(String commaand) {
        return Stream.of(CommandEnum.values())
                .filter(e -> e.getName().equals(commaand))
                .findFirst()
                .map(e -> e.getFullForm() + System.lineSeparator() + e.getDescription())
                .orElse(null);
    }
}