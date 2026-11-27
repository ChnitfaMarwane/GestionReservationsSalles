package ui;
import client.ReservationClient;
import model.Utilisateur;
import protocol.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final ReservationClient client;
    private final Utilisateur user;
    private final JLabel lblSalles = new JLabel("Total Salles : -", SwingConstants.CENTER);
    private final JLabel lblRes = new JLabel("Total Réservations : -", SwingConstants.CENTER);

    public DashboardPanel(ReservationClient client, Utilisateur user) {
        this.client = client; this.user = user;
        setLayout(new BorderLayout(10, 10));

        JPanel grid = new JPanel(new GridLayout(1, 2, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        lblSalles.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblRes.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblSalles.setBorder(BorderFactory.createTitledBorder("Salles"));
        lblRes.setBorder(BorderFactory.createTitledBorder("Réservations"));
        grid.add(lblSalles); grid.add(lblRes);

        JButton refreshBtn = new JButton("Actualiser");
        refreshBtn.addActionListener(e -> refreshStats());
        add(grid, BorderLayout.CENTER);
        add(refreshBtn, BorderLayout.SOUTH);
        refreshStats();
    }

    public void refreshStats() {
        new SwingWorker<int[], Void>() {
            @Override protected int[] doInBackground() throws Exception {
                Response r1 = client.sendRequest(new Request("GET_SALLES", null, user.getEmail()));
                Response r2 = client.sendRequest(new Request("GET_RESERVATIONS", null, user.getEmail()));
                return new int[]{
                    r1.isSuccess() ? ((List<?>) r1.getData()).size() : 0,
                    r2.isSuccess() ? ((List<?>) r2.getData()).size() : 0
                };
            }
            @Override protected void done() {
                try {
                    int[] s = get();
                    lblSalles.setText("Total Salles : " + s[0]);
                    lblRes.setText("Total Réservations : " + s[1]);
                } catch (Exception ex) { lblSalles.setText("Serveur déconnecté"); }
            }
        }.execute();
    }
}