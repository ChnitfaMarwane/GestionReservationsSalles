package model;
import java.io.Serializable;

public class Salle implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id, capacite;
    private String nom, type;
    private boolean disponible;

    public Salle(int id, String nom, String type, int capacite, boolean disponible) {
        this.id = id; this.nom = nom; this.type = type;
        this.capacite = capacite; this.disponible = disponible;
    }
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getType() { return type; }
    public int getCapacite() { return capacite; }
    public boolean isDisponible() { return disponible; }
}