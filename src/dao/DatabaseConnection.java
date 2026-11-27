package dao;
import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:ReservationDB.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initDatabase() {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS Utilisateur (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, motDePasse TEXT NOT NULL, role TEXT NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS Salle (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, nom TEXT NOT NULL, " +
                    "type TEXT NOT NULL, capacite INTEGER NOT NULL, disponible INTEGER DEFAULT 1)");

            st.execute("CREATE TABLE IF NOT EXISTS Reservation (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, idUtilisateur INTEGER NOT NULL, " +
                    "idSalle INTEGER NOT NULL, date TEXT NOT NULL, heureDebut TEXT NOT NULL, " +
                    "heureFin TEXT NOT NULL, motif TEXT NOT NULL, statut TEXT DEFAULT 'CONFIRMEE', " +
                    "FOREIGN KEY(idUtilisateur) REFERENCES Utilisateur(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(idSalle) REFERENCES Salle(id) ON DELETE CASCADE)");

            // Insertion des données de test si la base est vide
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Utilisateur");
            if (rs.next() && rs.getInt(1) == 0) {
                st.execute("INSERT INTO Utilisateur (nom, email, motDePasse, role) VALUES " +
                        "('Admin Principal', 'admin@uca.ma', 'admin123', 'ADMIN')," +
                        "('Etudiant 1', 'user1@uca.ma', '1234', 'USER')," +
                        "('Etudiant 2', 'user2@uca.ma', '1234', 'USER')");

                st.execute("INSERT INTO Salle (nom, type, capacite, disponible) VALUES " +
                        "('Amphi A', 'Amphithéâtre', 200, 1)," +
                        "('Salle B12', 'TD', 40, 1)," +
                        "('Labo Info 1', 'TP', 25, 1)");
            }
            System.out.println("Base de données ReservationDB.db prête.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}