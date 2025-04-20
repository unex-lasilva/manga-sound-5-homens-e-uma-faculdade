package br.com.mangarosa;

import br.com.mangarosa.entities.Playlist;
import br.com.mangarosa.entities.PlaylistNode;
import br.com.mangarosa.entities.Song;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MangaSoundApplication {
    private static final Repository repository = new Repository();

    private static final List<Playlist> playlists = new ArrayList<>();

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    addSongToRepository();
                    break;
                case 2:
                    createPlaylist();
                    break;
                case 3:
                    editPlaylist();
                    break;
                case 4:
                    executePlaylist();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\nMangaSound Menu:");
        System.out.println("1. Add Song to Repository");
        System.out.println("2. Create Playlist");
        System.out.println("3. Edit Playlist");
        System.out.println("4. Execute Playlist");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addSongToRepository() {
        System.out.print("Enter the path of the .wav file: ");
        String path = scanner.nextLine();
        repository.addSong(path);
    }

    private static void createPlaylist() {
        System.out.print("Enter playlist name: ");
        String name = scanner.nextLine();
        playlists.add(new Playlist(name));
        System.out.println("Playlist created: " + name);
    }

    private static void editPlaylist() {
        if (playlists.isEmpty()) {
            System.out.println("No playlists available!");
            return;
        }
        listPlaylists();
        System.out.print("Select playlist number: ");
        int playlistIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (playlistIndex < 0 || playlistIndex >= playlists.size()) {
            System.out.println("Invalid playlist number!");
            return;
        }
        Playlist playlist = playlists.get(playlistIndex);
        List<Song> songs = repository.getSongs();
        if (songs.isEmpty()) {
            System.out.println("No songs in repository!");
            return;
        }
        listSongs(songs);
        System.out.print("Enter song number to add: ");
        int songIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (songIndex < 0 || songIndex >= songs.size()) {
            System.out.println("Invalid song number!");
            return;
        }
        Song song = songs.get(songIndex);
        System.out.print("Enter position to insert (0-" + playlist.getSize() + "): ");
        int position = scanner.nextInt();
        scanner.nextLine();
        try {
            playlist.insertSongAt(song, position);
            System.out.println("Song added to playlist at position " + position);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void executePlaylist() {
        if (playlists.isEmpty()) {
            System.out.println("No playlists available!");
            return;
        }
        listPlaylists();
        System.out.print("Select playlist number: ");
        int playlistIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        if (playlistIndex < 0 || playlistIndex >= playlists.size()) {
            System.out.println("Invalid playlist number!");
            return;
        }
        Playlist playlist = playlists.get(playlistIndex);
        if (playlist.getSize() == 0) {
            System.out.println("Playlist is empty!");
            return;
        }
        playPlaylist(playlist);
    }

    private static void playPlaylist(@org.jetbrains.annotations.NotNull Playlist playlist) {
        PlaylistNode current = playlist.getHead();
        Clip clip;
        long startTime;
        boolean isPaused = false;

        while (current != null) {
            try {
                File audioFile = new File(current.getSong()
                        .getFilePath());
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Verificar se é a última música
                if (current.getNext() == null) {
                    System.out.println("Now playing: " + current.getSong()
                            .getTitle() + " (ÚLTIMA MÚSICA)");
                } else {
                    System.out.println("Now playing: " + current.getSong()
                            .getTitle());
                }

                clip.start();
                startTime = System.currentTimeMillis();

                boolean trackPlaying = true;
                while (trackPlaying) {
                    if (System.currentTimeMillis() - startTime > 10000) {
                        startTime = System.currentTimeMillis();
                    }
                    if (System.in.available() > 0) {
                        String input = scanner.nextLine()
                                .toLowerCase();
                        switch (input) {
                            case "n":
                                clip.stop();
                                trackPlaying = false;
                                current = current.getNext();
                                break;
                            case "p":
                                long elapsed = System.currentTimeMillis() - startTime;
                                if (elapsed < 10000) {
                                    if (current != playlist.getHead()) {
                                        clip.stop();
                                        trackPlaying = false;
                                        current = getPreviousNode(playlist, current);
                                    }
                                } else {
                                    clip.setMicrosecondPosition(0);
                                    startTime = System.currentTimeMillis();
                                }
                                break;
                            case "u": // Pausar/Resumir
                                if (isPaused) {
                                    clip.start();
                                    System.out.println("Música resumida.");
                                    isPaused = false;
                                } else {
                                    clip.stop();
                                    System.out.println("Música pausada. Pressione 'u' novamente para resumir.");
                                    isPaused = true;
                                }
                                break;
                            case "r": // Reiniciar playlist
                                clip.stop();
                                trackPlaying = false;
                                current = playlist.getHead(); // Volta para o início
                                System.out.println("Reiniciando playlist do início.");
                                break;
                            case "s":
                                clip.stop();
                                return;
                            default:
                                System.out.println("Comandos: 'n' (próxima), 'p' (anterior), 'u' (pausar/resumir), 'r' (reiniciar), 's' (parar)");
                        }
                    }

                    // Se a música estiver pausada, aguarde
                    if (isPaused) {
                        Thread.sleep(100);
                    }
                }
                clip.close();
            } catch (Exception e) {
                System.err.println("Erro ao reproduzir áudio: " + e.getMessage());
            }
        }

        System.out.println("Fim da playlist.");
    }

    private static PlaylistNode getPreviousNode(Playlist playlist, PlaylistNode currentNode) {
        PlaylistNode temp = playlist.getHead();
        PlaylistNode prev = null;
        while (temp != null && temp != currentNode) {
            prev = temp;
            temp = temp.getNext();
        }
        return prev;
    }

    private static void listPlaylists() {
        System.out.println("\nPlaylists:");
        for (int i = 0; i < playlists.size(); i++) {
            System.out.println((i + 1) + ". " + playlists.get(i)
                    .getName());
        }
    }

    private static void listSongs(List<Song> songs) {
        System.out.println("\nSongs in Repository:");
        for (int i = 0; i < songs.size(); i++) {
            System.out.println((i + 1) + ". " + songs.get(i)
                    .getTitle());
        }
    }
}