package util;
import model.Reservation;
import java.io.*;
import java.util.List;

public class FileManager {
    public static void exportToCSV(List<Reservation> list, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID,IdUtilisateur,IdSalle,Date,HeureDebut,HeureFin,Motif,Statut");
            for (Reservation r : list) {
                pw.printf("%d,%d,%d,%s,%s,%s,\"%s\",%s%n",
                        r.getId(), r.getIdUtilisateur(), r.getIdSalle(), r.getDate(),
                        r.getHeureDebut(), r.getHeureFin(), r.getMotif(), r.getStatut());
            }
        }
    }

    public static void serializeReservations(List<Reservation> list, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Reservation> deserializeReservations(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Reservation>) ois.readObject();
        }
    }
}