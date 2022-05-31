package bg.sofia.uni.fmi.mjt.spotify.playable;

public class Audio extends PlayableContent {

    private static final String TYPE = "audio";

    public Audio(String title, String artist, int year, double duration) {
        super(title, artist, year, duration);
    }

    @Override
    public String play() {
        super.incrementTotalPlaysCount();
        return String.format("Currently playing %s content: %s", TYPE, getTitle());
    }
}
