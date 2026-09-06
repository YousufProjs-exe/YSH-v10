
package network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

public class ChatServer {

    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running;

    private Consumer<String> messageListener;

    private final List<ClientHandler> clients =
        Collections.synchronizedList(new ArrayList<>());

    public ChatServer(int port) {
        this.port = port;
    }

    public void setMessageListener(
        Consumer<String> listener
    ) {
        this.messageListener = listener;
    }

    public void start() throws IOException {

        if (running) {
            return;
        }

        serverSocket = new ServerSocket(port);
        running = true;

        Thread serverThread =
            new Thread(this::acceptClients);

        serverThread.setDaemon(true);
        serverThread.start();
    }

    private void acceptClients() {

        while (running) {

            try {

                Socket socket =
                    serverSocket.accept();

                ClientHandler client =
                    new ClientHandler(socket);

                Thread clientThread =
                    new Thread(client);

                clientThread.setDaemon(true);
                clientThread.start();

            } catch (IOException e) {

                if (running) {
                    notifyListener(
                        "network: server error: "
                        + e.getMessage()
                    );
                }
            }
        }
    }

    private void notifyListener(String message) {

        if (messageListener != null) {
            messageListener.accept(message);
        }
    }

    public void broadcast(String message) {

        List<ClientHandler> snapshot;

        synchronized (clients) {
            snapshot =
                new ArrayList<>(clients);
        }

        for (ClientHandler client : snapshot) {
            client.send(message);
        }
    }

    public void hostMessage(
        String name,
        String message
    ) {

        broadcast(
            "[" + name + "] "
            + message
        );
    }

    public String getUsers() {

        List<ClientHandler> snapshot;

        synchronized (clients) {
            snapshot =
                new ArrayList<>(clients);
        }

        if (snapshot.isEmpty()) {
            return "No users connected";
        }

        StringBuilder output =
            new StringBuilder();

        output.append("Connected users:\n");

        for (ClientHandler client : snapshot) {

            output.append(
                client.getName()
            ).append("\n");
        }

        return output.toString();
    }

    public String kick(String name) {

        ClientHandler target = null;

        synchronized (clients) {

            for (
                ClientHandler client : clients
            ) {

                if (
                    client.getName()
                        .equalsIgnoreCase(name)
                ) {

                    target = client;
                    break;
                }
            }
        }

        if (target == null) {
            return "network: user not found";
        }

        target.send(
            "[YSH] You were kicked"
        );

        target.close();

        return
            "network: user kicked";
    }

    public void stop() {

        running = false;

        List<ClientHandler> snapshot;

        synchronized (clients) {

            snapshot =
                new ArrayList<>(clients);

            clients.clear();
        }

        for (ClientHandler client : snapshot) {
            client.close();
        }

        try {

            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException ignored) {
        }

        serverSocket = null;
    }

    public boolean isRunning() {
        return running;
    }


    private class ClientHandler
        implements Runnable {

        private final Socket socket;

        private BufferedReader reader;
        private PrintWriter writer;

        private String name = "Guest";

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {

                reader =
                    new BufferedReader(
                        new InputStreamReader(
                            socket.getInputStream()
                        )
                    );

                writer =
                    new PrintWriter(
                        socket.getOutputStream(),
                        true
                    );

                String firstMessage =
                    reader.readLine();

                if (
                    firstMessage == null ||
                    !firstMessage.startsWith(
                        "/name "
                    )
                ) {

                    close();
                    return;
                }

                String newName =
                    firstMessage
                        .substring(6)
                        .trim();

                if (!newName.isEmpty()) {
                    name = newName;
                }

                synchronized (clients) {
                    clients.add(this);
                }

                String joinMessage =
                    "[YSH] "
                    + name
                    + " joined";

                broadcast(joinMessage);

                notifyListener(joinMessage);

                String message;

                while (
                    running &&
                    (message =
                        reader.readLine())
                        != null
                ) {

                    if (
                        message.startsWith(
                            "/name "
                        )
                    ) {

                        String oldName =
                            name;

                        String requestedName =
                            message
                                .substring(6)
                                .trim();

                        if (
                            !requestedName.isEmpty()
                        ) {

                            name =
                                requestedName;

                            String renameMessage =
                                "[YSH] "
                                + oldName
                                + " is now "
                                + name;

                            broadcast(
                                renameMessage
                            );

                            notifyListener(
                                renameMessage
                            );
                        }

                        continue;
                    }

                    broadcast(
                        "[" + name + "] "
                        + message
                    );
                }

            } catch (IOException ignored) {

            } finally {

                boolean removed;

                synchronized (clients) {
                    removed =
                        clients.remove(this);
                }

                if (removed && running) {

                    String leaveMessage =
                        "[YSH] "
                        + name
                        + " left";

                    broadcast(
                        leaveMessage
                    );

                    notifyListener(
                        leaveMessage
                    );
                }

                close();
            }
        }

        public String getName() {
            return name;
        }

        public void send(String message) {

            if (writer != null) {
                writer.println(message);
            }
        }

        public void close() {

            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
