package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishListServer_dummy {
    private static final int MAX_EXECUTOR_THREADS = 10;
    private static int serverPort = 4444;

    public WishListServer_dummy(int serverPort) {
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server started and listening for connect requests");

            Socket clientSocket;

            while (true) {
                // Calling accept() blocks and waits for connection request by a client
                // When a request comes, accept() returns a socket to communicate with this client
                clientSocket = serverSocket.accept();
                //System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

                // We want each client to be processed in a separate thread
                // to keep the current thread free to accept() requests from new clients
                ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket);

                //launch a thread
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("There is a problem with the server socket");
            e.printStackTrace();
        }
    }
}
