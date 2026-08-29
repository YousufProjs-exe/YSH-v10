
package network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

public class ChatServer {

    private final int port;

    private ServerSocket serverSocket;
    private Consumer<String> listener;
    private boolean running;
    private final List<ClientHandler> clients =
        Collections.synchronizedList(new ArrayList<>());

    public void setMessageListener(
        java.util.function.Consumer<String> listener
    ) {
        this.messageListener = listener;
    }

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        if (running) {
            return;
        }

        serverSocket = new ServerSocket(port);
        running = true;

        Thread serverThread = new Thread(() -> {

            while (running) {

                try {

                    Socket socket = serverSocket.accept();

                    ClientHandler client =
                        new ClientHandler(socket);

                    clients.add(client);

                    Thread clientThread =
                        new Thread(client);

                    clientThread.setDaemon(true);
                    clientThread.start();

                } catch (IOException e) {

                    if (running) {
                        System.out.println(
                            "Network error: "
                            + e.getMessage()
                        );
                    }
                }
            }

        });

        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void broadcast(String message) {

        synchronized (clients) {

            for (ClientHandler client : clients) {
                client.send(message);
            }
        }
    }

    public String getUsers() {

        if (clients.isEmpty()) {
            return "No users connected";
        }

        StringBuilder output =
            new StringBuilder();

        synchronized (clients) {

            for (ClientHandler client : clients) {

                output.append(
                    client.getName()
                ).append("\n");
            }
        }

        return output.toString();
    }

    public String kick(String name) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (
                    client.getName()
                        .equalsIgnoreCase(name)
                ) {

                    client.send(
                        "[YSH] You were disconnected"
                    );

                    client.close();

                    clients.remove(client);

                    return "network: user kicked";
                }
            }
        }

        return "network: user not found";
    }

    public void stop() {

        running = false;

        synchronized (clients) {

            for (ClientHandler client : clients) {
                client.close();
            }

            clients.clear();
        }

        try {

            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException ignored) {
        }
    }

    public boolean isRunning() {
        return running;
    }


    private class ClientHandler
        implements Runnable {

        private final Socket socket;

        private BufferedReader reader;

        private PrintWriter writer;

        private String name;

        ClientHandler(Socket socket) {

            this.socket = socket;

            name =
                socket.getInetAddress()
                    .getHostAddress();
        }

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

                String message;

                while (
                    (message = reader.readLine())
                    != null
                ) {

                    if (
                        message.startsWith(
                            "/name "
                        )
                    ) {

                        name =
                            message.substring(6);

                        continue;
                    }

                    broadcast(
                        "[" + name + "] "
                        + message
                    );
                }

            } catch (IOException ignored) {

            } finally {

                clients.remove(this);

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
