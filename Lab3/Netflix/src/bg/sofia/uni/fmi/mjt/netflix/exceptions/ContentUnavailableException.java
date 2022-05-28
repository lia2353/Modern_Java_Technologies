package bg.sofia.uni.fmi.mjt.netflix.exceptions;

// The Exception class is the superclass of checked exceptions
public class ContentUnavailableException extends Exception {
    public ContentUnavailableException(String message) {
        super(message);
    }

    // Provides the reason (which is saved for later retrieval by the getCause() method). A null value is permitted, and indicates that the cause is nonexistent or unknown.
    public ContentUnavailableException(String message, Throwable reason) {
        super(message, reason);
    }
}
