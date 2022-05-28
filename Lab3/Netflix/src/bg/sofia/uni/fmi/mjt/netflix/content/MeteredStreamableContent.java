package bg.sofia.uni.fmi.mjt.netflix.content;

//wrapper class for Streamable (to avoid cast in mostViewed )
public class MeteredStreamableContent {
    private final Streamable content;
    private int totalTimesWatched;

    public MeteredStreamableContent(Streamable content) {
        this.content = content;
    }

    public Streamable getContent() {
        return content;
    }

    public int getTotalTimesWatched() {
        return totalTimesWatched;
    }

    public void incrementWatchedTime() {
        totalTimesWatched++;
    }
}
