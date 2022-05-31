package bg.sofia.uni.fmi.mjt.spotify.exceptions;

// The Exception class is the superclass of checked exceptions
public class PlaylistNotFoundException extends Exception {

    public PlaylistNotFoundException(String message) {
        super(message);
    }

    public PlaylistNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
