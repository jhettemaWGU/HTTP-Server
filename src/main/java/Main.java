import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");
     try (ServerSocket serverSocket = new ServerSocket(4221)) {
       serverSocket.setReuseAddress(true);
       System.out.println("Server is running and waiting for connections.");

       while (true) {
         final Socket clientSocket = serverSocket.accept();
         System.out.println("Accepted new connection");

         new Thread(() -> handleClient(clientSocket)).start();
       }

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }

  private static void handleClient(Socket clientSocket) {
    try (clientSocket) {
      InputStream input = clientSocket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      String line = reader.readLine();
      System.out.println(line);
      String[] HttpRequest = line.split(" ");
      OutputStream output = clientSocket.getOutputStream();
      String requestPath = HttpRequest[1];
      String userAgent = null;

      if (requestPath.equals("/")) {
        output.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());

      } else if (requestPath.startsWith("/echo/")) {
        String echoString = requestPath.substring("/echo/".length());
        output.write("HTTP/1.1 200 OK\r\n".getBytes());
        output.write("Content-Type: text/plain\r\n".getBytes());
        String lengthParam = "Content-Length: " + echoString.length() + "\r\n";
        output.write(lengthParam.getBytes());
        output.write("\r\n".getBytes());
        output.write(echoString.getBytes());
      } else if (requestPath.startsWith("/user-agent")) {
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
          System.out.println(line);

          if (line.startsWith("User-Agent: ")) {
            userAgent = line.substring("User-Agent: ".length()).trim();
            break;
          }
        }
      } else {
        output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
      }

      if (userAgent != null) {
        String httpResponse =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: " + userAgent.length() + "\r\n\r\n" +
                        userAgent;
        output.write(httpResponse.getBytes());
      } else if (requestPath.startsWith("/user-agent")){
        String httpResponse = "HTTP/1.1 400 Bad Request\r\n\r\nUser-Agent header missing\r\n";
        output.write(httpResponse.getBytes());
      }
      clientSocket.close();
    } catch (IOException e) {
      System.out.println("Error handling client: " + e.getMessage());
    }
  }
}


