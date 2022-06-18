package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.PaymentsLogData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.RegistrationsData;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SplitWiseServer {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9999;
    private static final int BUFFER_SIZE = 1024;

    private final int serverPort;
    private boolean isServerWorking;
    private Selector selector;
    private final ByteBuffer buffer;
    private final CommandExecutor commandExecutor;

    private final RegistrationsData registrations;

    public SplitWiseServer(int port) {
        this.serverPort = port;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

        this.registrations = new RegistrationsData();
        this.commandExecutor = new CommandExecutor(registrations, new LoginData(),
                new ExpensesData(), new PaymentsLogData(), new NotificationsData());
    }

    public void start() {
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {

            serverSocket.bind(new InetSocketAddress(SERVER_HOST, serverPort));
            serverSocket.configureBlocking(false);
            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            isServerWorking = true;
            while (isServerWorking) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    // select() is blocking, but in 3 scenarios it may still return with 0, check javadoc
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel clientSocket = (SocketChannel) key.channel();

                        String input = getClientInput(clientSocket);
                        if (input == null) {
                            continue;
                        }

                        Command command = new Command(input);

                        String response = commandExecutor.execute(clientSocket, command);
                        response += System.lineSeparator();

                        writeClientOutput(clientSocket, response);
                    } else if (key.isAcceptable()) {
                        handleKeyIsAcceptable(key);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); //here
            throw new UncheckedIOException("Failed to start server", e); //here not unchecked
        }
    }

    public void stop() {
        System.out.println("Server stopped");
        isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private String getClientInput(SocketChannel clientSocket) throws IOException {
        buffer.clear();
        int r = clientSocket.read(buffer);
        if (r <= 0) {
            clientSocket.close();
            return null;
        }

        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit());
    }

    private void writeClientOutput(SocketChannel clientSocket, String replay) throws IOException {
        buffer.clear();
        buffer.put(replay.getBytes());

        buffer.flip();
        clientSocket.write(buffer);
        buffer.clear();
    }


    private void handleKeyIsAcceptable(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);

        System.out.println("Connection accepted from client " + accept.getRemoteAddress());
    }

    public static void main(String[] args) {
        SplitWiseServer server = new SplitWiseServer(SERVER_PORT);
        server.start();
        server.stop();
    }

}
