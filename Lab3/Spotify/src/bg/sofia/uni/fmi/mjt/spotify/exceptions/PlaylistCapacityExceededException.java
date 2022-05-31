package bg.sofia.uni.fmi.mjt.spotify.exceptions;

// The Exception class is the superclass of checked exceptions
public class PlaylistCapacityExceededException extends Exception {

    public PlaylistCapacityExceededException(String message) {
        super(message);
    }

    public PlaylistCapacityExceededException(String message, Throwable cause) {
        super(message, cause);
    }

}
