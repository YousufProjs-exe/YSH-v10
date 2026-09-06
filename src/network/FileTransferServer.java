
package network;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FileTransferServer {

    private final int port;
    private ServerSocket serverSocket;
    private boolean running;

    public FileTransferServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        if (running) {
            return;
        }

        serverSocket = new ServerSocket(port);
        running = true;

        Thread thread = new Thread(() -> {

            while (running) {

                try {

                    Socket socket =
                        serverSocket.accept();

                    Thread clientThread =
                        new Thread(
                            () -> handleClient(socket)
                        );

                    clientThread.setDaemon(true);
                    clientThread.start();

                } catch (IOException e) {

                    if (running) {
                        e.printStackTrace();
                    }
                }
            }

        });

        thread.setDaemon(true);
        thread.start();
    }

    private void handleClient(Socket socket) {

        try (
            DataInputStream input =
                new DataInputStream(
                    socket.getInputStream()
                );

            DataOutputStream output =
                new DataOutputStream(
                    socket.getOutputStream()
                )
        ) {

            String command =
                input.readUTF();

            if (!command.equals("DOWNLOAD")) {
                return;
            }

            String filePath =
                input.readUTF();

            Path file =
                Paths.get(filePath);

            if (
                !Files.exists(file) ||
                !Files.isRegularFile(file)
            ) {

                output.writeBoolean(false);

                output.writeUTF(
                    "file not found"
                );

                return;
            }

            output.writeBoolean(true);

            output.writeUTF(
                file.getFileName().toString()
            );

            output.writeLong(
                Files.size(file)
            );

            try (
                InputStream fileInput =
                    Files.newInputStream(file)
            ) {

                byte[] buffer =
                    new byte[8192];

                int bytesRead;

                while (
                    (bytesRead =
                        fileInput.read(buffer)
                    ) != -1
                ) {

                    output.write(
                        buffer,
                        0,
                        bytesRead
                    );
                }
            }

            output.flush();

        } catch (IOException ignored) {

        } finally {

            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void stop() {

        running = false;

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
}
