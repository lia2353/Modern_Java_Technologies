package bg.sofia.uni.fmi.mjt.wish.list.server;

import bg.sofia.uni.fmi.mjt.wish.list.command.Command;
import bg.sofia.uni.fmi.mjt.wish.list.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.wish.list.storage.LoginData;
import bg.sofia.uni.fmi.mjt.wish.list.storage.RegistrationsData;
import bg.sofia.uni.fmi.mjt.wish.list.storage.WishlistsData;

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

public class WishListServer {
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;

    private final int serverPort;
    private boolean isServerWorking;

    private Selector selector;
    private ByteBuffer buffer;
    private final CommandExecutor commandExecutor;

    public WishListServer(int serverPort) {
        this.serverPort = serverPort;
        commandExecutor = new CommandExecutor(new RegistrationsData(), new LoginData(), new WishlistsData());
    }

    public void start() {
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress(SERVER_HOST, serverPort));
            serverSocket.configureBlocking(false); // Configure server in non-blocking mode
            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            buffer = ByteBuffer.allocate(BUFFER_SIZE); // non-direct buffer
            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select(); // blocking method
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

                            String replay = commandExecutor.execute(command, clientSocket);
                            replay += System.lineSeparator();

                            writeClientOutput(clientSocket, replay);
                        } else if (key.isAcceptable()) {
                            accept(key);
                        }
                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    public void stop() {
        isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
        // try {
        //  selector.close();
        // } catch (IOException e) {
        //  e.printStackTrace();
        // }
    }

    private String getClientInput(SocketChannel clientSocket) throws IOException {
        buffer.clear(); // switch to writing mode
        int r = clientSocket.read(buffer);
        if (r < 0) {
            clientSocket.close();
            return null;
        }

        buffer.flip(); // switch to reading mode
        return new String(buffer.array(), 0, buffer.limit());
    }

    private void writeClientOutput(SocketChannel clientSocket, String replay) throws IOException {
        buffer.clear(); // switch to writing mode
        buffer.put(replay.getBytes()); //buffer fill

        buffer.flip(); // switch to reading mode
        clientSocket.write(buffer);
        buffer.clear();
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false); // configure in non-blocking mode
        accept.register(selector, SelectionKey.OP_READ);
    }
}
