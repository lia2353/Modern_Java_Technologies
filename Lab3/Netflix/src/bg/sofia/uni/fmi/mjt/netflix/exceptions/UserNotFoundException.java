package bg.sofia.uni.fmi.mjt.netflix.exceptions;

//The RuntimeException class is the superclass of all unchecked exceptions
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
