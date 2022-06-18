package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class SplitWiseClient {
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
    private static final int SERVER_PORT = 9999;
    private static final String PROMPT = "=> ";
    private static final String EXIT_COMMAND = "exit";

    private final int serverPort;
    private final ByteBuffer buffer;
    private boolean isClientWorking;

    public SplitWiseClient() {
        this.serverPort = SERVER_PORT;
        buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public void start() {
        try (SocketChannel socket = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            // System.out.print("Connected to the server." + System.lineSeparator());

            isClientWorking = true;
            while (isClientWorking) {
                System.out.print(PROMPT);
                String message = scanner.nextLine();
                writeMessageToBuffer(socket, message);

                String reply = getServerReply(socket);
                System.out.print(reply);

                if (EXIT_COMMAND.equals(message)) {
                    stop();
                }
            }
        } catch (IOException e) {
            System.err.print("An error occurred in the client I/O: " + e.getMessage() + System.lineSeparator());
            System.err.print(e + System.lineSeparator());
        }
    }

    private void writeMessageToBuffer(SocketChannel socketChannel, String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
    }

    private String getServerReply(SocketChannel socketChannel) throws IOException {
        socketChannel.read(buffer);
        buffer.flip();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return new String(byteArray, "UTF-8");
    }

    public void stop() {
        this.isClientWorking = false;
    }

    public static void main(String[] args) {
        SplitWiseClient client = new SplitWiseClient();
        client.start();
        client.stop();
    }
}
