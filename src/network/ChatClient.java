
package network;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChatClient {

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private volatile boolean connected;

    private Consumer<String> messageListener;

    public void setMessageListener(
        Consumer<String> listener
    ) {

        this.messageListener = listener;
    }

    public String connect(
        String host,
        int port,
        String name
    ) {

        disconnect();

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

            writer.println(
                "/name " + name
            );

            startListener();

            return "network: connected";

        } catch (IOException e) {

            disconnect();

            return
                "network: "
                + e.getMessage();
        }
    }

    private void startListener() {

        Thread listenerThread =
            new Thread(() -> {

                try {

                    String message;

                    while (
                        connected &&
                        (message =
                            reader.readLine())
                            != null
                    ) {

                        if (
                            messageListener != null
                        ) {

                            messageListener.accept(
                                message
                            );
                        }
                    }

                } catch (IOException ignored) {

                } finally {

                    connected = false;
                }

            });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public String send(String message) {

        if (
            !connected ||
            writer == null
        ) {

            return
                "network: not connected";
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

        socket = null;
        writer = null;
        reader = null;
    }

    public boolean isConnected() {
        return connected;
    }
}
