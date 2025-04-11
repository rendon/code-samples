package mx.letmethink;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            var socket = new Socket("localhost", 1234);
            new Client(socket).run();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    Runnable serverListener() {
        return () -> {
            while (socket.isConnected()) {
                try {
                    var line = reader.readLine();
                    System.out.println(line);
                } catch (IOException e) {
                    closeEverything(e);
                    break;
                }
            }
        };
    }

    Runnable userListener() {
        return () -> {
            var scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                try {
                    var line = scanner.nextLine();
                    sendMessage(line);
                } catch (IOException e) {
                    closeEverything(e);
                    break;
                }
            }
        };
    }

    void run() throws InterruptedException {
        // Join the server
        var scanner = new Scanner(System.in);
        try {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            sendMessage(username);
        } catch (IOException e) {
            closeEverything(e);
        }

        var userListener = new Thread(userListener());
        var serverListener = new Thread(serverListener());
        userListener.start();
        serverListener.start();
        userListener.join();
        serverListener.join();
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
