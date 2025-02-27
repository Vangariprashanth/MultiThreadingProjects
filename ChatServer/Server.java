import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 8020;
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("Chat server is running on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true)) {

                clientWriters.add(toClient);
                String message;

                while ((message = fromClient.readLine()) != null) {
                    System.out.println("Received: " + message);
                    broadcastMessage(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove disconnected client
                clientWriters.removeIf(writer -> writer.checkError());
            }
        }

        private void broadcastMessage(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
