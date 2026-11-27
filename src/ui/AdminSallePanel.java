package ui;
import client.ReservationClient;
import model.*;
import protocol.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminSallePanel extends JPanel {
    private final ReservationClient client;
    private final Utilisateur user;
    private final Runnable onDataChanged;
    private final DefaultTableModel model;
    private final JTable table;

    public AdminSallePanel(ReservationClient client, Utilisateur user, Runnable onDataChanged) {
        this.client = client; this.user = user; this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(8, 8));

        String[] cols = { "ID", "Nom", "Type", "Capacité", "Disponible" };
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton delBtn = new JButton("Supprimer");
        bottom.add(addBtn); bottom.add(editBtn); bottom.add(delBtn);
        add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> openSalleDialog(null));
        editBtn.addActionListener(e -> editSelectedSalle());
        delBtn.addActionListener(e -> deleteSelectedSalle());
        loadSalles();
    }

    @SuppressWarnings("unchecked")
    private void loadSalles() {
        new SwingWorker<Response, Void>() {
            @Override protected Response doInBackground() throws Exception {
                return client.sendRequest(new Request("GET_SALLES", null, user.getEmail()));
            }
            @Override protected void done() {
                try {
                    Response res = get();
                    if (res.isSuccess()) {
                        model.setRowCount(0);
                        for (Salle s : (List<Salle>) res.getData()) {
                            model.addRow(new Object[]{ s.getId(), s.getNom(), s.getType(), s.getCapacite(), s.isDisponible() });
                        }
                        onDataChanged.run();
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void editSelectedSalle() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        Salle s = new Salle((int) model.getValueAt(row, 0), (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2), (int) model.getValueAt(row, 3), (boolean) model.getValueAt(row, 4));
        openSalleDialog(s);
    }

    private void openSalleDialog(Salle existing) {
        JTextField nomF = new JTextField(existing == null ? "" : existing.getNom());
        JTextField typeF = new JTextField(existing == null ? "TD" : existing.getType());
        JTextField capF = new JTextField(existing == null ? "30" : String.valueOf(existing.getCapacite()));
        JCheckBox dispC = new JCheckBox("Disponible", existing == null || existing.isDisponible());

        Object[] fields = { "Nom:", nomF, "Type:", typeF, "Capacité:", capF, dispC };
        if (JOptionPane.showConfirmDialog(this, fields, existing == null ? "Ajouter Salle" : "Modifier Salle", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Salle s = new Salle(existing == null ? 0 : existing.getId(), nomF.getText(), typeF.getText(), Integer.parseInt(capF.getText()), dispC.isSelected());
            String action = existing == null ? "ADD_SALLE" : "UPDATE_SALLE";
            new SwingWorker<Response, Void>() {
                @Override protected Response doInBackground() throws Exception {
                    return client.sendRequest(new Request(action, s, user.getEmail()));
                }
                @Override protected void done() { loadSalles(); }
            }.execute();
        }
    }

    private void deleteSelectedSalle() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);
        new SwingWorker<Response, Void>() {
            @Override protected Response doInBackground() throws Exception {
                return client.sendRequest(new Request("DELETE_SALLE", id, user.getEmail()));
            }
            @Override protected void done() { loadSalles(); }
        }.execute();
    }
}