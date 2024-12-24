// https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
import java.io.*;
import java.net.*;

public class EchoServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        try (var serverSocket = new ServerSocket(portNumber);
             var clientSocket = serverSocket.accept();
             var out = new PrintWriter(clientSocket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("got: "+line);
                out.println(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
