package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WishListClient {
    private static final int SERVER_PORT = 7777;
    private static final String LOCALHOST = "localhost";
    private static final String PROMPT = "=> ";
    private static final String DISCONNECT_COMMAND = "disconnect";

    public static void main(String[] args) {
        try (Socket socket = new Socket(LOCALHOST, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            //System.out.println("Connected to the server.");

            while (true) {
                System.out.print(PROMPT);
                String message = scanner.nextLine(); // read a line from the console

                writer.println(message); // send the message to the server

                String reply = reader.readLine(); // read the response from the server
                System.out.print(reply + System.lineSeparator());

                if (DISCONNECT_COMMAND.equals(message)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
