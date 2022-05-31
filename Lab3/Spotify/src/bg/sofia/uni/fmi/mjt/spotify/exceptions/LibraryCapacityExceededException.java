package bg.sofia.uni.fmi.mjt.spotify.exceptions;

// The Exception class is the superclass of checked exceptions
public class LibraryCapacityExceededException extends Exception{

    public LibraryCapacityExceededException(String message) {
        super(message);
    }

    public LibraryCapacityExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
