package bg.sofia.uni.fmi.mjt.revolut.account;

import java.util.Objects;

public abstract class Account {
    private double amount;
    private String IBAN;

    public Account(String IBAN) {
        this(IBAN, 0);
    }

    public Account(String IBAN, double amount) {
        this.IBAN = IBAN;
        this.amount = amount;
    }

    public abstract String getCurrency();

    public double getAmount() {
        return amount;
    }

    public String getIBAN() {
        return IBAN;
    }

    //Withdraw money from the account; if there are no enough money, then return false
    public void withdraw(double amount) {
        this.amount -= amount;
    }

    //Add money to the account
    public void debit(double amount) {
        this.amount += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return IBAN.equals(account.IBAN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IBAN);
    }
}