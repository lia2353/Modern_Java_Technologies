package bg.sofia.uni.fmi.mjt.spotify.playable;

public abstract class PlayableContent implements Playable {

    private String title;
    private String artist;
    private int year;
    private double duration;

    private int totalPlaysCount;

    public PlayableContent(String title, String artist, int year, double duration) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.duration = duration;
    }

    @Override
    public abstract String play();

    @Override
    public int getTotalPlays() {
        return totalPlaysCount;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public double getDuration() {
        return duration;
    }

    protected void incrementTotalPlaysCount() {
        ++totalPlaysCount;
    }
}
