import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8020);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("Connected to chat server. Start typing...");

            new Thread(() -> {
                try {
                    String message;
                    while ((message = fromServer.readLine()) != null) {
                        System.out.println("Server: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                toServer.println(userMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
