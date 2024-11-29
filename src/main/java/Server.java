import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static ServerSocket server;
    private static final int PORT = 8080;

    static {
        try {
            // Ensure the directory exists
            File logDir = new File("/app/logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            FileHandler fileHandler = new FileHandler("/app/logs/server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to set up logger: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            server = new ServerSocket(PORT);
            LOGGER.info("Server started and listening on port " + PORT);

            while (true) {
                try (Socket socket = server.accept();
                     ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                    LOGGER.info("Connection established with a client.");
                    String message = (String) ois.readObject();
                    LOGGER.info("Received message from client: " + message);

                    if (message.equalsIgnoreCase("exit")) {
                        oos.writeObject("Hi Client, terminate request received, connection will terminate. Goodbye :)");
                        LOGGER.info("Server is terminating the connection as per client's request.");
                        break;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < message.length(); i++) {
                            stringBuilder.append((int) message.charAt(i));
                        }
                        String output = stringBuilder.toString();
                        oos.writeObject("Hi Client, the ASCII equivalent for: " + message + " is: " + output);
                        LOGGER.info("Sent response to client: " + "Hi Client, the ASCII equivalent for: " + message + " is: " + output);
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.SEVERE, "An error occurred during communication with the client: ", e);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "An IO error occurred during communication with the client: ", e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while starting the server: ", e);
        } finally {
            try {
                if (server != null) {
                    server.close();
                    LOGGER.info("Server closed.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "An error occurred while closing the server: ", e);
            }
        }

        // Keep the server running indefinitely to avoid container exit
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Server interrupted: ", e);
        }
    }
}

