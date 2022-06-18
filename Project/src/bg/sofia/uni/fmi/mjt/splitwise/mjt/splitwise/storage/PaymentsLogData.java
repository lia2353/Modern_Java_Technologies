package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentsLogData {
    private final Map<String, List<String>> userPaymentsLog;

    public PaymentsLogData() {
        this.userPaymentsLog = new HashMap<>();
    }

    public void addPayment(boolean isGroupPayment, String username, Double amount,
                           String whom, String reasonForPayment, LocalDate date) {
        if (username == null || amount == null || whom == null || reasonForPayment == null || date == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        userPaymentsLog.computeIfAbsent(username, k -> new ArrayList<>());
        String log = "Split " + amount + " LV expense with " + whom;
        log += isGroupPayment ? " group on " : " on ";
        log += date.toString() + ". Reason for payment: " + reasonForPayment;
        userPaymentsLog.get(username).add(log);
    }

    public String getPayments(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        if (userPaymentsLog.get(username) == null) {
            return null;
        }

        return "Your payments: " + System.lineSeparator()
            + userPaymentsLog.get(username).stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public boolean hasNoPayments(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        if (userPaymentsLog.get(username) == null) {
            return true;
        }
        return  userPaymentsLog.get(username).isEmpty();
    }
}
