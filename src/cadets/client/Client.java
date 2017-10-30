package cadets.client;

import cadets.util.Message;

import java.io.*;
import java.net.Socket;

public class Client {
    private String username;
    private String hostName;
    private int portNumber;
    private Socket socket;
    private BufferedReader serverInputReader;
    private BufferedReader commandLineReader;
    private PrintWriter serverWriter;

    private Client(String hostName, int portNumber, String username) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.username = username;

        try {
            socket = new Socket(this.hostName, this.portNumber);
            serverInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            commandLineReader = new BufferedReader(new InputStreamReader(System.in));
            serverWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java Client <host name> <port number> <username>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String username = args[2];

        Client client = new Client(hostName, portNumber, username);

        client.start();
    }

    private void start() {
        boolean messageReceived = false;
        String commandLineInput;

        ServerListener serverListener = new ServerListener();

        try {
            while ((commandLineInput = commandLineReader.readLine()) != null) {
                serverWriter.println(new Message(username, commandLineInput).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUsernameFromKeyboard() {
        System.out.println("Please input your name.");
        try {
            return commandLineReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getUsernameFromKeyboard();
    }

    class ServerListener implements Runnable {
        Thread thread;

        ServerListener() {
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            String serverInput;
            try {
                while ((serverInput = serverInputReader.readLine()) != null) {
                    System.out.println(serverInput);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
