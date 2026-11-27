package server;

import dao.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;

public class ReservationServer {
    public static final int PORT = 5000;
    private static JTextArea logArea;

    public static void log(String message) {
        System.out.println(message);
        if (logArea != null) {
            SwingUtilities.invokeLater(() -> {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }
    }

    public static void main(String[] args) {
        // Création de la fenêtre console du serveur
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Serveur de Réservations (Port " + PORT + ")");
            logArea = new JTextArea(18, 50);
            logArea.setEditable(false);
            logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            frame.add(new JScrollPane(logArea), BorderLayout.CENTER);

            JButton stopBtn = new JButton("Arrêter le serveur");
            stopBtn.addActionListener(e -> System.exit(0));
            frame.add(stopBtn, BorderLayout.SOUTH);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        DatabaseConnection.initDatabase();
        log("Base de données ReservationDB.db prête.");
        log("Serveur démarré sur le port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                log("Client connecté : " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            log("Erreur serveur : " + e.getMessage());
        }
    }
}