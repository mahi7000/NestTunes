package com.example.app.services;

import com.example.app.models.Song;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class SongSearchService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public List<Song> searchSongs(String query) throws IOException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Send search query to server
            out.writeObject(query);
            out.flush();

            // Receive results from server
            return (List<Song>) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error deserializing search results", e);
        }
    }
}
