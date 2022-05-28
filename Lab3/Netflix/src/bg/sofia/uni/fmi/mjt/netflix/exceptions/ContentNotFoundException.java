package bg.sofia.uni.fmi.mjt.netflix.exceptions;

// The RuntimeException class is the superclass of all unchecked exceptions
public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException(String message) {
        super(message);
    }
}
