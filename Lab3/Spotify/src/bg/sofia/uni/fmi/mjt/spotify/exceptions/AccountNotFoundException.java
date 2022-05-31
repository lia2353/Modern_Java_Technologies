package bg.sofia.uni.fmi.mjt.spotify.exceptions;

// The Exception class is the superclass of checked exceptions
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException (String message) {
        super(message);
    }

    // Provides the cause (which is saved for later retrieval by the getCause() method). A null value is permitted, and indicates that the cause is nonexistent or unknown.
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
