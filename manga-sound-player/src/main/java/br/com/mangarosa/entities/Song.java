package br.com.mangarosa.entities;

public class Song {
    private final String filePath;

    private final String title;

    public Song(String filePath, String title) {
        this.filePath = filePath;
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }
}