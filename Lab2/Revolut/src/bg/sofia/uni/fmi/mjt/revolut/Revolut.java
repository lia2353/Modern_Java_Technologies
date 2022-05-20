package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;
import bg.sofia.uni.fmi.mjt.revolut.card.PhysicalCard;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualOneTimeCard;

import java.time.LocalDate;
import java.util.Arrays;

public class Revolut implements RevolutAPI {
    private static final String BGN_CURRENCY = "BGN";
    private static final String EUR_CURRENCY = "EUR";
    private static final double EXCHANGE_RATE_EUR_TO_BGN = 1.95583;
    private static final String BLOCKED_DOMAIN = ".biz";

    private Account[] accounts;
    private Card[] cards;

    public Revolut(Account[] accounts, Card[] cards) {
        this.accounts = Arrays.copyOf(accounts, accounts.length);
        this.cards = Arrays.copyOf(cards, cards.length);
    }

    @Override
    public boolean pay(Card card, int pin, double amount, String currency) {
        if (!card.getType().equals(PhysicalCard.TYPE)) {
            return false;
        }
        if (!cardExists(card) || !isCardValid(card, pin)) {
            return false;
        }
        for (Account account : accounts) {
            if (account.getCurrency().equals(currency) && account.getAmount() >= amount) {
                account.withdraw(amount);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean payOnline(Card card, int pin, double amount, String currency, String shopURL) {
        if (shopURL.endsWith(BLOCKED_DOMAIN)) {
            return false;
        }
        if (!cardExists(card) || !isCardValid(card, pin)) {
            return false;
        }
        for (Account account : accounts) {
            if (account.getCurrency().equals(currency) && account.getAmount() >= amount) {
                account.withdraw(amount);
                if (card.getType().equals(VirtualOneTimeCard.TYPE)) {
                    card.block();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addMoney(Account account, double amount) {
        if (!accountExists(account)) {
            return false;
        }
        account.debit(amount);
        return true;
    }

    @Override
    public boolean transferMoney(Account from, Account to, double amount) {
        if (!accountExists(from) || !accountExists(to) || from.equals(to)) {
            return false;
        }
        if (from.getAmount() < amount) {
            return false;
        }

        from.withdraw(amount);
        if (!from.getCurrency().equals(to.getCurrency())) {
            // Currency conversion of transferred amount
            amount = from.getCurrency().equals(EUR_CURRENCY) ? convertEURToBGN(amount) : convertBGNToEUR(amount);
        }
        to.debit(amount);

        return true;
    }

    @Override
    public double getTotalAmount() {
        double totalAmount = 0;

        for (Account account : accounts) {
            if (account.getCurrency().equals(EUR_CURRENCY)) {
                totalAmount += convertEURToBGN(account.getAmount());
            } else {
                // equals(BGN_CURRENCY))
                totalAmount += account.getAmount();
            }
        }

        return totalAmount;
    }

    //Currency conversion: BGN = EUR * course    EUR = BGN / course
    private double convertEURToBGN(double amountEUR) {
        return amountEUR * EXCHANGE_RATE_EUR_TO_BGN;
    }

    private double convertBGNToEUR(double amountBGN) {
        return amountBGN / EXCHANGE_RATE_EUR_TO_BGN;
    }

    //Returns true if there is such account
    private boolean accountExists(Account account) {
        for (Account value : accounts) {
            if (account.equals(value)) {
                return true;
            }
        }
        return false;
    }

    //Returns true if there is such card
    private boolean cardExists(Card card) {
        for (Card value : cards) {
            if (card.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCardValid(Card card, int pin) {
        return !card.isBlocked()
                && LocalDate.now().isBefore(card.getExpirationDate())
                && card.checkPin(pin);
    }
}
