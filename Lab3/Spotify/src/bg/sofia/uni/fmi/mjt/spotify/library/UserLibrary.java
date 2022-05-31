package bg.sofia.uni.fmi.mjt.spotify.library;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.EmptyLibraryException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.LibraryCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.playlist.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.playlist.UserPlaylist;

public class UserLibrary implements Library {

    private static final int MAX_PLAYLISTS_COUNT = 21;
    private static final String DEFAULT_PLAYLIST_NAME = "Liked Content";

    private Playlist[] playlists;
    private int size;

    public UserLibrary() {
        playlists = new Playlist[MAX_PLAYLISTS_COUNT];
        playlists[0] = new UserPlaylist(DEFAULT_PLAYLIST_NAME);
        size = 1;
    }

    @Override
    public void add(Playlist playlist) throws LibraryCapacityExceededException {
        if (playlist == null) {
            throw new IllegalArgumentException("The given argument is null and can not be added to the library");
        }
        if (size >= MAX_PLAYLISTS_COUNT) {
            throw new LibraryCapacityExceededException(String.format("Library capacity exceeded. Max capacity is %d", MAX_PLAYLISTS_COUNT));
        }

        if (!playlist.getName().equals(DEFAULT_PLAYLIST_NAME)) {
            playlists[size] = playlist;
            ++size;
        }
    }

    @Override
    public void remove(String name) throws EmptyLibraryException, PlaylistNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("The given argument is null and can not be removed from the library");
        }
        if (name.equals(DEFAULT_PLAYLIST_NAME)) {
            throw new IllegalArgumentException(String.format("%s playlist can not be removed", DEFAULT_PLAYLIST_NAME));
        }
        if (size == 1) {
            throw new EmptyLibraryException("Library contains only the default playlist. Can not remove");
        }

        for (int i = 1; i < size; ++i) {
            if (playlists[i].getName().equals(name)) {
                if (i != size - 1) {
                    playlists[i] = playlists[size - 1];
                }
                playlists[size - 1] = null;
                --size;
                return;
            }
        }

        throw new PlaylistNotFoundException(String.format("Playlist %s was not found", name));

    }

    @Override
    public Playlist getLiked() {
        return playlists[0];
    }
}
