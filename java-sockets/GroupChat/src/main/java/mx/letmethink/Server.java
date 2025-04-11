package mx.letmethink;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        try {
            var executor = Executors.newFixedThreadPool(3);
            ServerSocket serverSocket = new ServerSocket(1234);
            while (!serverSocket.isClosed()) {
                var socket = serverSocket.accept();

                var handler = new ClientHandler(socket);
                ClientHandler.handlers.add(handler);
                executor.submit(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
