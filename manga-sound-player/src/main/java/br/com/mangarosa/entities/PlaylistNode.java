package br.com.mangarosa.entities;

public class PlaylistNode {
    private final Song song;

    private PlaylistNode next;

    public PlaylistNode(Song song) {
        this.song = song;
        this.next = null;
    }

    public Song getSong() {
        return song;
    }

    public PlaylistNode getNext() {
        return next;
    }

    public void setNext(PlaylistNode next) {
        this.next = next;
    }
}
