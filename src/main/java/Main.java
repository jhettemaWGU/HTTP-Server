
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
     try {
       serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept(); // Wait for connection from client.
       InputStream input = clientSocket.getInputStream();
       BufferedReader reader = new BufferedReader(new InputStreamReader(input));
       String line = reader.readLine();
       System.out.println(line);
       String[] HttpRequest = line.split(" ");
       OutputStream output = clientSocket.getOutputStream();
       String requestPath = HttpRequest[1];

       if (requestPath.startsWith("/echo/")) {
         String echoString = requestPath.substring("/echo/".length());


         output.write("HTTP/1.1 200 OK\r\n".getBytes());
         output.write("Content-Type: text/plain\r\n".getBytes());
         String lengthParam = "Content-Length: " + echoString.length() + "\r\n";
         output.write(lengthParam.getBytes());
         output.write("\r\n".getBytes());
         output.write(echoString.getBytes());
       } else {
         output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
       }

       System.out.println("accepted new connection");

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     } finally {
       try {
         if (serverSocket != null) serverSocket.close();
         if (clientSocket != null) clientSocket.close();
       } catch (IOException e) {
         System.out.println("Error closing sockets: " + e.getMessage());
       }
     }
  }
}
