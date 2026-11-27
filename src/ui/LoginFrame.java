package ui;
import client.ReservationClient;
import model.Utilisateur;
import protocol.*;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField emailField = new JTextField("admin@uca.ma", 18);
    private final JPasswordField passField = new JPasswordField("admin123", 18);
    private final JButton loginBtn = new JButton("Se connecter");

    public LoginFrame() {
        super("Authentification - Réservation de Salles");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0; c.gridy = 0; add(new JLabel("Email :"), c);
        c.gridx = 1; add(emailField, c);
        c.gridx = 0; c.gridy = 1; add(new JLabel("Mot de passe :"), c);
        c.gridx = 1; add(passField, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; add(loginBtn, c);

        loginBtn.addActionListener(e -> authenticate());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 180);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void authenticate() {
        loginBtn.setEnabled(false);
        new SwingWorker<Response, Void>() {
            private ReservationClient client;
            @Override protected Response doInBackground() throws Exception {
                client = new ReservationClient("localhost", 5000);
                String[] creds = { emailField.getText().trim(), new String(passField.getPassword()) };
                return client.sendRequest(new Request("LOGIN", creds, creds[0]));
            }
            @Override protected void done() {
                loginBtn.setEnabled(true);
                try {
                    Response res = get();
                    if (res.isSuccess()) {
                        dispose();
                        new MainFrame(client, (Utilisateur) res.getData());
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, res.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                        client.close();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Serveur inaccessible : " + ex.getMessage(), "Erreur Réseau", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}