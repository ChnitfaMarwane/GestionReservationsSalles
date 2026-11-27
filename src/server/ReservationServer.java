package server;
import dao.DatabaseConnection;
import java.io.IOException;
import java.net.*;

public class ReservationServer {
    public static final int PORT = 5000;

    public static void main(String[] args) {
        DatabaseConnection.initDatabase(); // Initialise SQLite automatiquement
        System.out.println("Serveur démarré sur le port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connecté : " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}