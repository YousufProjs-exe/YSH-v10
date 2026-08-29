
package network;

public class NetworkManager {

    private ChatServer server;
    private ChatClient client;
    private java.util.function.Consumer<String> messageListener;

    public String startServer(int port) {

        if (
            server != null &&
            server.isRunning()
        ) {

            return
                "network: server already running";
        }

        server = new ChatServer(port);

        try {

            server.start();

            return
                "network: server started on port "
                + port;

        } catch (Exception e) {

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

        client = new ChatClient();
        client.setMessageListener(messageListener);

        return client.connect(
            host,
            port
        );
    }

    public String send(String message) {

        if (client == null) {

            return
                "network: not connected";
        }

        return client.send(message);
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

        return
            "network: disconnected";
    }

    public boolean isServerRunning() {

        return
            server != null &&
            server.isRunning();
    }

    public String setName(String name) {

        if (client == null) {
            return "network: not connected";
        }
    
        return client.setName(name);
    }
    
    
    public String announce(String message) {
    
        if (
            server == null ||
            !server.isRunning()
        ) {
        
            return
                "network: server not running";
        }
    
        server.broadcast(
            "[ANNOUNCEMENT] " + message
        );
    
        return "announcement sent";
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

    public void setMessageListener(
        java.util.function.Consumer<String> listener
    ) {

        this.messageListener = listener;

        if (client != null) {
            client.setMessageListener(listener);
        }
    }
}
