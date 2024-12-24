// https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (var echoSocket = new Socket(hostName, portNumber);
             var out = new PrintWriter(echoSocket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
             var stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String line;
            while ((line = stdIn.readLine()) != null) {
                out.println(line);
                System.out.println("echo: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
