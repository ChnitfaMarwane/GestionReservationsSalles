package dao;
import model.Salle;
import java.sql.*;
import java.util.*;

public class SalleDAO {
    public List<Salle> getAllSalles() throws SQLException {
        List<Salle> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM Salle")) {
            while (rs.next()) {
                list.add(new Salle(rs.getInt("id"), rs.getString("nom"),
                        rs.getString("type"), rs.getInt("capacite"), rs.getInt("disponible") == 1));
            }
        }
        return list;
    }

    public boolean addSalle(Salle s) throws SQLException {
        String sql = "INSERT INTO Salle (nom, type, capacite, disponible) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getNom()); ps.setString(2, s.getType());
            ps.setInt(3, s.getCapacite()); ps.setInt(4, s.isDisponible() ? 1 : 0);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSalle(Salle s) throws SQLException {
        String sql = "UPDATE Salle SET nom=?, type=?, capacite=?, disponible=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getNom()); ps.setString(2, s.getType());
            ps.setInt(3, s.getCapacite()); ps.setInt(4, s.isDisponible() ? 1 : 0);
            ps.setInt(5, s.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSalle(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM Salle WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}