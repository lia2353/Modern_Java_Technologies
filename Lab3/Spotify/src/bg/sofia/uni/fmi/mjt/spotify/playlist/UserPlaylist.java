package bg.sofia.uni.fmi.mjt.spotify.playlist;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;

public class UserPlaylist implements Playlist {

    private static final int CAPACITY = 20;

    private String name;
    private Playable[] content;
    private int size;

    public UserPlaylist(String name) {
        this.name = name;
        this.content = new Playable[CAPACITY];
    }

    @Override
    public void add(Playable playable) throws PlaylistCapacityExceededException {

        if (playable == null) {
            throw new IllegalArgumentException("The given argument is null and can not be added to the playlist");
        }
        if (size >= CAPACITY) {
            throw new PlaylistCapacityExceededException("Playlist capacity exceeded. Max capacity is " + CAPACITY);
        }
        content[size++] = playable;
    }

    @Override
    public String getName() {
        return name;
    }

}
