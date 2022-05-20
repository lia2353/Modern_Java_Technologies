package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;
import java.util.Objects;

public abstract class BaseCard implements Card {
    private static final int MAX_INCORRECT_PIN_ATTEMPTS = 3;

    private String number;
    private int pin;
    private LocalDate expirationDate;
    private boolean isBlocked;  //primitive type -> set automatically to false
    private int countIncorrectPinAttempts; //primitive type -> set automatically to 0

    public BaseCard(String number, int pin, LocalDate expirationDate) {
        this.number = number;
        this.pin = pin;
        this.expirationDate = expirationDate;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public abstract String getType();

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean checkPin(int pin) {
        if (this.pin != pin) {
            if (++countIncorrectPinAttempts >= MAX_INCORRECT_PIN_ATTEMPTS) {
                block();
            }
            return false;
        }
        countIncorrectPinAttempts = 0;
        return true;
    }

    @Override
    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public void block() {
        isBlocked = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseCard baseCard = (BaseCard) o;
        return number.equals(baseCard.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
