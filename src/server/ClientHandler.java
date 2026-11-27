package server;
import dao.*;
import model.*;
import protocol.*;
import util.LogManager;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final UtilisateurDAO userDAO = new UtilisateurDAO();
    private final SalleDAO salleDAO = new SalleDAO();
    private static final ReservationDAO resDAO = new ReservationDAO();

    public ClientHandler(Socket socket) { this.socket = socket; }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                Request req = (Request) in.readObject();
                Response res = processRequest(req);
                out.writeObject(res);
                out.flush();
            }
        } catch (EOFException e) {
            ReservationServer.log("Client déconnecté : " + socket.getRemoteSocketAddress());
        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }

    private Response processRequest(Request req) {
        String user = req.getUserEmail() != null ? req.getUserEmail() : "Anonyme";
        try {
            switch (req.getAction()) {
                case "LOGIN":
                    String[] creds = (String[]) req.getPayload();
                    Utilisateur u = userDAO.authenticate(creds[0], creds[1]);
                    LogManager.log(creds[0], "LOGIN", u != null ? "SUCCES" : "ECHEC");
                    return new Response(u != null, u != null ? "Connexion réussie" : "Identifiants invalides", u);

                case "GET_SALLES":
                    return new Response(true, "OK", salleDAO.getAllSalles());

                case "ADD_SALLE":
                    boolean addS = salleDAO.addSalle((Salle) req.getPayload());
                    LogManager.log(user, "ADD_SALLE", addS ? "SUCCES" : "ECHEC");
                    return new Response(addS, addS ? "Salle ajoutée" : "Erreur", null);

                case "UPDATE_SALLE":
                    boolean updS = salleDAO.updateSalle((Salle) req.getPayload());
                    LogManager.log(user, "UPDATE_SALLE", updS ? "SUCCES" : "ECHEC");
                    return new Response(updS, updS ? "Salle modifiée" : "Erreur", null);

                case "DELETE_SALLE":
                    boolean delS = salleDAO.deleteSalle((Integer) req.getPayload());
                    LogManager.log(user, "DELETE_SALLE", delS ? "SUCCES" : "ECHEC");
                    return new Response(delS, delS ? "Salle supprimée" : "Erreur", null);

                case "GET_RESERVATIONS":
                    return new Response(true, "OK", resDAO.getAllReservations());

                case "ADD_RESERVATION":
                    boolean addR = resDAO.addReservation((Reservation) req.getPayload());
                    LogManager.log(user, "ADD_RESERVATION", addR ? "SUCCES" : "CONFLIT");
                    return new Response(addR, addR ? "Réservation confirmée" : "Salle indisponible sur ce créneau", null);

                case "UPDATE_RESERVATION":
                    boolean updR = resDAO.updateReservation((Reservation) req.getPayload());
                    LogManager.log(user, "UPDATE_RESERVATION", updR ? "SUCCES" : "CONFLIT");
                    return new Response(updR, updR ? "Réservation modifiée" : "Conflit sur ce créneau", null);

                case "DELETE_RESERVATION":
                    boolean delR = resDAO.deleteReservation((Integer) req.getPayload());
                    LogManager.log(user, "DELETE_RESERVATION", delR ? "SUCCES" : "ECHEC");
                    return new Response(delR, delR ? "Réservation supprimée" : "Erreur", null);

                default:
                    return new Response(false, "Action inconnue", null);
            }
        } catch (Exception e) {
            LogManager.log(user, req.getAction(), "ERREUR: " + e.getMessage());
            return new Response(false, "Erreur serveur : " + e.getMessage(), null);
        }
    }
}