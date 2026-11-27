package ui;
import client.ReservationClient;
import model.Utilisateur;
import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame(ReservationClient client, Utilisateur user) {
        super("Gestion des Réservations - " + user.getNom() + " (" + user.getRole() + ")");
        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP);

        DashboardPanel dashPanel = new DashboardPanel(client, user);
        ReservationPanel resPanel = new ReservationPanel(client, user, dashPanel::refreshStats);

        tabs.addTab("Tableau de bord", dashPanel);
        tabs.addTab("Réservations", resPanel);
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            tabs.addTab("Administration Salles", new AdminSallePanel(client, user, dashPanel::refreshStats));
        }

        add(tabs);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}