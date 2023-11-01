import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  Socket socket;

  public static void main(String[] args) throws IOException {

    // Create socket with server's IP and port
    Socket socket = new Socket("localhost", 11111);
    System.out.println("Connected to the server.");

    // Input and output streams for communication with the server.
    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    // Set username.
    Scanner nameScanner = new Scanner(System.in);
    System.out.print("Please enter your username: ");
    String username = nameScanner.nextLine();
    output.println(username);

    // Threads for sending and receiving messages.
    // Threading my beloved.
    Thread sendThread = new Thread(new SendMessageThread(output));
    Thread receiveThread = new Thread(new ReceiveMessageThread(input));

    // Start threads.
    sendThread.start();
    receiveThread.start();
  }
}

class SendMessageThread implements Runnable {

  private final PrintWriter output;

  public SendMessageThread(PrintWriter output) {

    this.output = output;
  }

  public void run() {

    try (Scanner scanner = new Scanner(System.in)) {

      String message;
      while (true) {

        message = scanner.nextLine();
        output.println(message);
      }
    }
  }
}

class ReceiveMessageThread implements Runnable {

  private final BufferedReader input;

  public ReceiveMessageThread(BufferedReader input) {

    this.input = input;
  }

  public void run() {

    String message;
    while (true) {
      try {
        if ((message = input.readLine()) == null) {
            break;
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      System.out.println(message);
    }
  }
}
