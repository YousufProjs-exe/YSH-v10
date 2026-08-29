
package network;

import java.io.*;
import java.net.*;

public class ChatClient {

    private Socket socket;

    private PrintWriter writer;

    private BufferedReader reader;

    private boolean connected;

    public String connect(
        String host,
        int port
    ) {

        try {

            socket =
                new Socket(host, port);

            writer =
                new PrintWriter(
                    socket.getOutputStream(),
                    true
                );

            reader =
                new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream()
                    )
                );

            connected = true;

            startListener();

            return "network: connected";

        } catch (IOException e) {

            return "network: "
                + e.getMessage();
        }
    }

    private void startListener() {

        Thread listener =
            new Thread(() -> {

                try {

                    String message;

                    while (
                        connected &&
                        (message =
                            reader.readLine())
                            != null
                    ) {

                        System.out.println(
                            "[NETWORK] "
                            + message
                        );
                    }

                } catch (IOException ignored) {

                } finally {

                    connected = false;
                }

            });

        listener.setDaemon(true);

        listener.start();
    }

    public String send(String message) {

        if (!connected) {

            return "network: not connected";
        }

        writer.println(message);

        return "sent";
    }

    public void disconnect() {

        connected = false;

        try {

            if (socket != null) {

                socket.close();
            }

        } catch (IOException ignored) {
        }
    }

    public boolean isConnected() {

        return connected;
    }

    public String setName(String name) {

        if (!connected) {
            return "network: not connected";
        }
    
        writer.println("/name " + name);
    
        return "network: name set to " + name;
    }
}
