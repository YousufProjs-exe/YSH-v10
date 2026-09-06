
package network;

import java.util.function.Consumer;

public class NetworkManager {

    private ChatServer server;
    private ChatClient client;

    private Consumer<String> messageListener;

    private String username = "Guest";

    public void setMessageListener(
        Consumer<String> listener
    ) {

        this.messageListener = listener;

        if (client != null) {
            client.setMessageListener(listener);
        }

        if (server != null) {
            server.setMessageListener(listener);
        }
    }

    public String setName(String name) {

        if (
            name == null ||
            name.trim().isEmpty()
        ) {

            return
                "network: invalid name";
        }

        username = name.trim();

        return
            "network: name set to "
            + username;
    }

    public String startServer(int port) {

        if (
            server != null &&
            server.isRunning()
        ) {

            return
                "network: server already running";
        }

        try {

            server =
                new ChatServer(port);

            server.setMessageListener(
                messageListener
            );

            server.start();

            return
                "network: server started on port "
                + port;

        } catch (Exception e) {

            server = null;

            return
                "network: "
                + e.getMessage();
        }
    }

    public String stopServer() {

        if (
            server == null ||
            !server.isRunning()
        ) {

            return
                "network: no server running";
        }

        server.stop();
        server = null;

        return
            "network: server stopped";
    }

    public String connect(
        String host,
        int port
    ) {

        if (
            client != null &&
            client.isConnected()
        ) {

            return
                "network: already connected";
        }

        client =
            new ChatClient();

        client.setMessageListener(
            messageListener
        );

        String result =
            client.connect(
                host,
                port,
                username
            );

        if (
            !result.equals(
                "network: connected"
            )
        ) {

            client = null;
        }

        return result;
    }

    public String disconnect() {

        if (
            client == null ||
            !client.isConnected()
        ) {

            return
                "network: not connected";
        }

        client.disconnect();
        client = null;

        return
            "network: disconnected";
    }

    public String send(String message) {

        if (
            message == null ||
            message.trim().isEmpty()
        ) {

            return
                "network: empty message";
        }

        if (
            client != null &&
            client.isConnected()
        ) {

            return client.send(
                message
            );
        }

        if (
            server != null &&
            server.isRunning()
        ) {

            server.hostMessage(
                username,
                message
            );

            return "sent";
        }

        return
            "network: not connected";
    }

    public boolean isServerRunning() {

        return
            server != null &&
            server.isRunning();
    }

    public boolean isConnected() {

        return
            client != null &&
            client.isConnected();
    }

    public String announce(
        String message
    ) {

        if (
            server == null ||
            !server.isRunning()
        ) {

            return
                "network: server not running";
        }

        server.broadcast(
            "[ANNOUNCEMENT] "
            + message
        );

        return
            "announcement sent";
    }

    public String listUsers() {

        if (
            server == null ||
            !server.isRunning()
        ) {

            return
                "network: server not running";
        }

        return server.getUsers();
    }

    public String kick(String name) {

        if (
            server == null ||
            !server.isRunning()
        ) {

            return
                "network: server not running";
        }

        return server.kick(name);
    }
}
