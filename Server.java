import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

  static List<ClientHandler> clients = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(11111); // Choose your port
    System.out.println("Listening for clients...");

    while (true) {

      Socket clientSocket = serverSocket.accept();
      System.out.println("Client connected: " + clientSocket.getInetAddress());
      ClientHandler clientHandler = new ClientHandler(clientSocket);
      clients.add(clientHandler);
      clientHandler.start();
    }
  }
}

class ClientHandler extends Thread {

  private PrintWriter output;
  private BufferedReader input;
  private String username;

  public ClientHandler(Socket socket) throws IOException {
    this.output = new PrintWriter(socket.getOutputStream(), true);
    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  public void run() {

    try {

      // Request the client to set a username
      // Add a username field
      username = input.readLine();

      // Inform other clients that a new user has joined
      broadcastMessage(username + " has joined the chat.");

      String message;
      while ((message = input.readLine()) != null) {

        System.out.println(username + " says: " + message);
        broadcastMessage(username + ": " + message);
      }

    } catch (IOException e) {

      System.out.println(username + " has left the chat.");
      broadcastMessage(username + " has left the chat.");
      Server.clients.remove(this);
    }
  }

  public void sendMessage(String message) {

    output.println(message);
  }

  private void broadcastMessage(String message) {

    for (ClientHandler client : Server.clients) {

      if (client != this) {

        client.sendMessage(message);
      }
    }
  }
}
