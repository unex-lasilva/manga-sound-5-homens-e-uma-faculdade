package br.com.mangarosa;

import br.com.mangarosa.entities.Song;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

public class Repository {
    private static final String REPO_DIR = "repository/";
    private final List<Song> songs;

    public Repository() {
        this.songs = new LinkedList<>();
        ensureRepoDirectoryExists();
    }

    private void ensureRepoDirectoryExists() {
        Path path = Paths.get(REPO_DIR);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                System.err.println("Failed to create repository directory: " + e.getMessage());
            }
        }
    }

    public void addSong(String sourcePath) {
        try {
            Path source = Paths.get(sourcePath);
            String fileName = source.getFileName().toString();
            Path target = Paths.get(REPO_DIR + fileName);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            Song song = new Song(target.toString(), fileName.replace(".wav", ""));
            songs.add(song);
            System.out.println("Song added successfully: " + fileName);
        } catch (IOException e) {
            System.err.println("Error adding song: " + e.getMessage());
        }
    }

    public List<Song> getSongs() {
        return songs;
    }
}