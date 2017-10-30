package cadets.server;

import cadets.util.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private int port;
    private ArrayList<ClientThread> clientThreadList;
    private boolean serverStatus;
    private ServerSocket serverSocket;

   private Server(int port) {
        this.port = port;
        clientThreadList = new ArrayList<>();
    }

    public static void main(String args[]) {
        if(args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        Server server = new Server(portNumber);

        server.start();
    }

    private void start() {
        serverStatus = true;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(serverStatus) {
            try {
                Socket socketClient = serverSocket.accept();
                ClientThread newClient = new ClientThread(socketClient);
                clientThreadList.add(newClient);

                newClient.thread.start();

                System.out.println("***A new user has connected***");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized private void broadcast(String message) {
        for(ClientThread clientThread : clientThreadList) {
            clientThread.out.println(message);
        }
    }

    class ClientThread implements Runnable {
        Thread thread;
        Socket socket;
        BufferedReader in;
        PrintWriter out;

        ClientThread(Socket socket) {
            thread = new Thread(this);
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String input;
            try {
                while ((input = in.readLine()) != null) {
                    broadcast(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}