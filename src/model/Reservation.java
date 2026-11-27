package model;
import java.io.Serializable;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id, idUtilisateur, idSalle;
    private String date, heureDebut, heureFin, motif, statut;

    public Reservation(int id, int idUtilisateur, int idSalle, String date,
                       String heureDebut, String heureFin, String motif, String statut) {
        this.id = id; this.idUtilisateur = idUtilisateur; this.idSalle = idSalle;
        this.date = date; this.heureDebut = heureDebut; this.heureFin = heureFin;
        this.motif = motif; this.statut = statut;
    }
    public int getId() { return id; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public int getIdSalle() { return idSalle; }
    public String getDate() { return date; }
    public String getHeureDebut() { return heureDebut; }
    public String getHeureFin() { return heureFin; }
    public String getMotif() { return motif; }
    public String getStatut() { return statut; }
}