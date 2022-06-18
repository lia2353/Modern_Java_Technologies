package bg.sofia.uni.fmi.mjt.spellchecker;

public record Metadata(int characters, int words, int mistakes) {
    public Metadata combine(Metadata md) {
        return new Metadata(characters + md.characters(), words + md.words(), mistakes + md.mistakes());
    }
}