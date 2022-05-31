package bg.sofia.uni.fmi.mjt.spotify.exceptions;

// The Exception class is the superclass of checked exceptions
public class StreamingServiceException extends Exception {
    public StreamingServiceException(String message) {
        super(message);
    }

    public StreamingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
