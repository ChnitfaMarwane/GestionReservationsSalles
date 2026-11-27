package dao;
import model.Reservation;
import java.sql.*;
import java.util.*;

public class ReservationDAO {
    public synchronized boolean addReservation(Reservation r) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM Reservation WHERE idSalle = ? AND date = ? " +
                          "AND statut != 'ANNULEE' AND NOT (heureFin <= ? OR heureDebut >= ?)";
        String insertSql = "INSERT INTO Reservation (idUtilisateur, idSalle, date, heureDebut, heureFin, motif, statut) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement checkPs = c.prepareStatement(checkSql)) {
                checkPs.setInt(1, r.getIdSalle());
                checkPs.setString(2, r.getDate());
                checkPs.setString(3, r.getHeureDebut());
                checkPs.setString(4, r.getHeureFin());
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    c.rollback();
                    return false; // Conflit détecté
                }
            }
            try (PreparedStatement insPs = c.prepareStatement(insertSql)) {
                insPs.setInt(1, r.getIdUtilisateur()); insPs.setInt(2, r.getIdSalle());
                insPs.setString(3, r.getDate()); insPs.setString(4, r.getHeureDebut());
                insPs.setString(5, r.getHeureFin()); insPs.setString(6, r.getMotif());
                insPs.setString(7, "CONFIRMEE");
                insPs.executeUpdate();
            }
            c.commit();
            return true;
        }
    }

    public synchronized boolean updateReservation(Reservation r) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM Reservation WHERE idSalle = ? AND date = ? AND id != ? " +
                          "AND statut != 'ANNULEE' AND NOT (heureFin <= ? OR heureDebut >= ?)";
        String updSql = "UPDATE Reservation SET idSalle=?, date=?, heureDebut=?, heureFin=?, motif=?, statut=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement checkPs = c.prepareStatement(checkSql)) {
                checkPs.setInt(1, r.getIdSalle()); checkPs.setString(2, r.getDate());
                checkPs.setInt(3, r.getId()); checkPs.setString(4, r.getHeureDebut());
                checkPs.setString(5, r.getHeureFin());
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    c.rollback();
                    return false;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(updSql)) {
                ps.setInt(1, r.getIdSalle()); ps.setString(2, r.getDate());
                ps.setString(3, r.getHeureDebut()); ps.setString(4, r.getHeureFin());
                ps.setString(5, r.getMotif()); ps.setString(6, r.getStatut());
                ps.setInt(7, r.getId());
                ps.executeUpdate();
            }
            c.commit();
            return true;
        }
    }

    public boolean deleteReservation(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Reservation WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Reservation ORDER BY date DESC, heureDebut ASC")) {
            while (rs.next()) {
                list.add(new Reservation(rs.getInt("id"), rs.getInt("idUtilisateur"),
                        rs.getInt("idSalle"), rs.getString("date"), rs.getString("heureDebut"),
                        rs.getString("heureFin"), rs.getString("motif"), rs.getString("statut")));
            }
        }
        return list;
    }
}