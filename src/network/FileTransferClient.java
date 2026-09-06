
package network;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FileTransferClient {

    public String download(
        String host,
        int port,
        String remotePath,
        String destination
    ) {

        try (
            Socket socket =
                new Socket(host, port);

            DataOutputStream output =
                new DataOutputStream(
                    socket.getOutputStream()
                );

            DataInputStream input =
                new DataInputStream(
                    socket.getInputStream()
                )
        ) {

            output.writeUTF("DOWNLOAD");

            output.writeUTF(
                remotePath
            );

            output.flush();

            boolean exists =
                input.readBoolean();

            if (!exists) {

                return
                    "download: "
                    + input.readUTF();
            }

            String fileName =
                input.readUTF();

            long fileSize = 
                input.readLong();

            Path target =
                Paths.get(destination)
                    .resolve(fileName);

            try (
                OutputStream fileOutput =
                    Files.newOutputStream(target)
            ) {

                byte[] buffer =
                    new byte[8192];

                long remaining =
                    fileSize;

                while (remaining > 0) {

                    int bytesRead =
                        input.read(
                            buffer,
                            0,
                            (int) Math.min(
                                buffer.length,
                                remaining
                            )
                        );

                    if (bytesRead == -1) {
                        break;
                    }

                    fileOutput.write(
                        buffer,
                        0,
                        bytesRead
                    );

                    remaining -=
                        bytesRead;
                }
            }

            return
                "downloaded: "
                + target;

        } catch (IOException e) {

            return
                "download: "
                + e.getMessage();
        }
    }
}
