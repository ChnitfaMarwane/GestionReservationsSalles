package util;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {
    private static final String LOG_FILE = "logs.txt";

    public static synchronized void log(String user, String operation, String result) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.printf("[%s] User: %s | Op: %s | Result: %s%n", ts, user, operation, result);
        } catch (IOException e) { e.printStackTrace(); }
    }
}