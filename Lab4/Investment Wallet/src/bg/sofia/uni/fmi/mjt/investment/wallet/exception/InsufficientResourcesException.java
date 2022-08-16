package bg.sofia.uni.fmi.mjt.investment.wallet.exception;

public class InsufficientResourcesException extends WalletException {

    public InsufficientResourcesException(String message) {
        super(message);
    }

    public InsufficientResourcesException(String message, Throwable cause) {
        super(message, cause);
    }

}
