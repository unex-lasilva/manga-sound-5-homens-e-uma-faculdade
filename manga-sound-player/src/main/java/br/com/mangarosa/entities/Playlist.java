package br.com.mangarosa.entities;

public class Playlist {
    private final String name;

    private PlaylistNode head;

    private int size;

    public Playlist(String name) {
        this.name = name;
        this.head = null;
        this.size = 0;
    }

    public String getName() {
        return name;
    }

    public void addSong(Song song) {
        PlaylistNode newNode = new PlaylistNode(song);
        if (head == null) {
            head = newNode;
        } else {
            PlaylistNode current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    public void insertSongAt(Song song, int position) {
        if (position < 0 || position > size) {
            throw new IllegalArgumentException("Invalid position");
        }
        PlaylistNode newNode = new PlaylistNode(song);
        if (position == 0) {
            newNode.setNext(head);
            head = newNode;
        } else {
            PlaylistNode current = head;
            for (int i = 0; i < position - 1; i++) {
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            current.setNext(newNode);
        }
        size++;
    }

    public PlaylistNode getHead() {
        return head;
    }

    public int getSize() {
        return size;
    }
}