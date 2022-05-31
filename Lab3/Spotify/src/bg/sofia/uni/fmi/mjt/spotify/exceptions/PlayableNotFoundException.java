package bg.sofia.uni.fmi.mjt.spotify.exceptions;

// The Exception class is the superclass of checked exceptions
public class PlayableNotFoundException extends Exception {

    public PlayableNotFoundException(String message) {
        super(message);
    }

    public PlayableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
