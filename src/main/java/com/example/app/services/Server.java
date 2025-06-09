package com.example.app.services;

// Server.java
import java.io.*;
import java.net.*;
import java.util.List;
import com.example.app.models.Song;
import com.example.app.database.DatabaseManager;

public class Server {
    private static final int PORT = 12345;
    private DatabaseManager dbManager;

    public Server() {
        dbManager = new DatabaseManager();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    // Read search query from client
                    String searchQuery = (String) in.readObject();

                    // Perform search in database
                    List<Song> results = dbManager.searchSongs(searchQuery);

                    // Send results back to client
                    out.writeObject(results);
                    out.flush();
                } catch (ClassNotFoundException e) {
                    System.err.println("Error reading object from client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
