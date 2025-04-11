package mx.letmethink;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientHandler implements Runnable {
    public static final List<ClientHandler> handlers = Collections.synchronizedList(new ArrayList<>());

    private Socket socket;
    private String username;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(e);
        }
    }

    @Override
    public void run() {
        try {
            username = reader.readLine();
            broadcastMessage(username + " has joined the chat");
        } catch (IOException e) {
            closeEverything(e);
        }
        System.out.println(username +" joined");

        while (socket.isConnected()) {
            try {
                var line = reader.readLine();
                System.out.println("read " +line+" from client");
                broadcastMessage(username + ": " + line);
            } catch (IOException e) {
                closeEverything(e);
                break;
            }
        }
    }

    void broadcastMessage(String message) throws IOException {
        synchronized (handlers) {
            for (var handler : handlers) {
                if (handler != this) {
                    handler.sendMessage(message);
                }
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    private void closeEverything(IOException e) {
        e.printStackTrace();
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
