package bg.sofia.uni.fmi.mjt.spotify.playable;

public class Video extends PlayableContent {
    private static final String TYPE = "video";

    public Video(String title, String artist, int year, double duration) {
        super(title, artist, year, duration);
    }

    @Override
    public String play() {
        super.incrementTotalPlaysCount();
        return String.format("Currently playing %s content: %s", TYPE, getTitle());
    }
}
