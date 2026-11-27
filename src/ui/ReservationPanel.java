package ui;
import client.ReservationClient;
import model.*;
import protocol.*;
import util.FileManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ReservationPanel extends JPanel {
    private final ReservationClient client;
    private final Utilisateur user;
    private final Runnable onDataChanged;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField searchField = new JTextField(15);
    private List<Reservation> currentList = new ArrayList<>();

    public ReservationPanel(ReservationClient client, Utilisateur user, Runnable onDataChanged) {
        this.client = client; this.user = user; this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Rechercher");
        JButton refreshBtn = new JButton("Actualiser");
        top.add(new JLabel("Filtre (Motif/Date) :")); top.add(searchField);
        top.add(searchBtn); top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        String[] cols = { "ID", "ID User", "ID Salle", "Date", "Début", "Fin", "Motif", "Statut" };
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton delBtn = new JButton("Supprimer");
        JButton csvBtn = new JButton("Exporter CSV");
        JButton saveBinBtn = new JButton("Sauvegarder (.dat)");
        JButton loadBinBtn = new JButton("Lire (.dat)");

        bottom.add(addBtn); bottom.add(editBtn); bottom.add(delBtn);
        bottom.add(csvBtn); bottom.add(saveBinBtn); bottom.add(loadBinBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadReservations());
        searchBtn.addActionListener(e -> filterTable(searchField.getText().trim().toLowerCase()));
        addBtn.addActionListener(e -> openFormDialog(null));
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());
        csvBtn.addActionListener(e -> exportCSV());
        saveBinBtn.addActionListener(e -> saveBinary());
        loadBinBtn.addActionListener(e -> loadBinary());

        loadReservations();
    }

    @SuppressWarnings("unchecked")
    private void loadReservations() {
        new SwingWorker<Response, Void>() {
            @Override protected Response doInBackground() throws Exception {
                return client.sendRequest(new Request("GET_RESERVATIONS", null, user.getEmail()));
            }
            @Override protected void done() {
                try {
                    Response res = get();
                    if (res.isSuccess()) {
                        currentList = (List<Reservation>) res.getData();
                        populateTable(currentList);
                        onDataChanged.run();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReservationPanel.this, "Connexion perdue avec le serveur.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void populateTable(List<Reservation> list) {
        model.setRowCount(0);
        for (Reservation r : list) {
            model.addRow(new Object[]{ r.getId(), r.getIdUtilisateur(), r.getIdSalle(),
                    r.getDate(), r.getHeureDebut(), r.getHeureFin(), r.getMotif(), r.getStatut() });
        }
    }

    private void filterTable(String q) {
        List<Reservation> filtered = new ArrayList<>();
        for (Reservation r : currentList) {
            if (r.getMotif().toLowerCase().contains(q) || r.getDate().contains(q)) filtered.add(r);
        }
        populateTable(filtered);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);
        currentList.stream().filter(r -> r.getId() == id).findFirst().ifPresent(this::openFormDialog);
    }

    private void openFormDialog(Reservation existing) {
        JTextField salleF = new JTextField(existing == null ? "1" : String.valueOf(existing.getIdSalle()));
        JTextField dateF = new JTextField(existing == null ? "2026-12-01" : existing.getDate());
        JTextField debutF = new JTextField(existing == null ? "09:00" : existing.getHeureDebut());
        JTextField finF = new JTextField(existing == null ? "11:00" : existing.getHeureFin());
        JTextField motifF = new JTextField(existing == null ? "Cours Java" : existing.getMotif());

        Object[] fields = { "ID Salle:", salleF, "Date (YYYY-MM-DD):", dateF,
                "Début (HH:MM):", debutF, "Fin (HH:MM):", finF, "Motif:", motifF };

        String title = existing == null ? "Nouvelle Réservation" : "Modifier Réservation #" + existing.getId();
        if (JOptionPane.showConfirmDialog(this, fields, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Reservation r = new Reservation(existing == null ? 0 : existing.getId(), user.getId(),
                    Integer.parseInt(salleF.getText()), dateF.getText(), debutF.getText(),
                    finF.getText(), motifF.getText(), "CONFIRMEE");
            String action = existing == null ? "ADD_RESERVATION" : "UPDATE_RESERVATION";

            new SwingWorker<Response, Void>() {
                @Override protected Response doInBackground() throws Exception {
                    return client.sendRequest(new Request(action, r, user.getEmail()));
                }
                @Override protected void done() {
                    try {
                        Response res = get();
                        JOptionPane.showMessageDialog(ReservationPanel.this, res.getMessage());
                        if (res.isSuccess()) loadReservations();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReservationPanel.this, "Erreur réseau.");
                    }
                }
            }.execute();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);
        new SwingWorker<Response, Void>() {
            @Override protected Response doInBackground() throws Exception {
                return client.sendRequest(new Request("DELETE_RESERVATION", id, user.getEmail()));
            }
            @Override protected void done() {
                try {
                    Response res = get();
                    JOptionPane.showMessageDialog(ReservationPanel.this, res.getMessage());
                    if (res.isSuccess()) loadReservations();
                } catch (Exception ex) { JOptionPane.showMessageDialog(ReservationPanel.this, "Erreur réseau."); }
            }
        }.execute();
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser(new File("."));
        fc.setSelectedFile(new File("reservations_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                FileManager.exportToCSV(currentList, fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Export CSV réussi.");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erreur CSV : " + ex.getMessage()); }
        }
    }

    private void saveBinary() {
        try {
            FileManager.serializeReservations(currentList, new File("reservations_backup.dat"));
            JOptionPane.showMessageDialog(this, "Sauvegarde binaire réussie (reservations_backup.dat).");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage()); }
    }

    private void loadBinary() {
        try {
            List<Reservation> loaded = FileManager.deserializeReservations(new File("reservations_backup.dat"));
            populateTable(loaded);
            JOptionPane.showMessageDialog(this, loaded.size() + " réservations chargées.");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erreur lecture : " + ex.getMessage()); }
    }
}