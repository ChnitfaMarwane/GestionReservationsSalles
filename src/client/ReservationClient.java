package client;
import protocol.*;
import java.io.*;
import java.net.Socket;

public class ReservationClient {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ReservationClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized Response sendRequest(Request req) throws IOException, ClassNotFoundException {
        out.writeObject(req);
        out.flush();
        return (Response) in.readObject();
    }

    public void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}