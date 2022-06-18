package bg.sofia.uni.fmi.mjt.socialmedia.content.enums;

public enum ContentType {
    POST(30),
    STORY(1);

    private final int activePeriodDays;

    ContentType(int activePeriodDays) {
        this.activePeriodDays = activePeriodDays;
    }

    public int getActivePeriodDays() {
        return activePeriodDays;
    }
}
