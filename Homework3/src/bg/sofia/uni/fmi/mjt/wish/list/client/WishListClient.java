package bg.sofia.uni.fmi.mjt.wish.list.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WishListClient {
    private static final String LOCALHOST = "localhost";
    private static final String PROMPT = "=> ";
    private static final String DISCONNECT_COMMAND = "disconnect";

    private final int serverPort;
    private boolean isClientWorking;

    public WishListClient(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() {
        try (Socket socket = new Socket(LOCALHOST, serverPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            isClientWorking = true;
            while (isClientWorking) {
                System.out.print(PROMPT);
                String message = scanner.nextLine(); // read a line from the console

                writer.println(message); // send the message to the server

                String reply = reader.readLine(); // read the response from the server
                System.out.print(reply + System.lineSeparator());

                if (DISCONNECT_COMMAND.equals(message)) {
                    stop();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isClientWorking = false;
    }

    public static void main(String[] args) {
        WishListClient client = new WishListClient(7777);
        client.start();
        client.stop();
    }
}
