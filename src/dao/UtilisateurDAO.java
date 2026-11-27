package dao;
import model.Utilisateur;
import java.sql.*;

public class UtilisateurDAO {
    public Utilisateur authenticate(String email, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE email = ? AND motDePasse = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Utilisateur(rs.getInt("id"), rs.getString("nom"),
                        rs.getString("email"), rs.getString("motDePasse"), rs.getString("role"));
            }
        }
        return null;
    }
}