package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by SerP on 24.09.2016.
 */
public class VerySimpleChatServer {
    ArrayList clientOutputStreams;

    public class ClientHandler implements Runnable{
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket sock) {
            try {
                this.sock = sock;
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String message;
            try {
                while((message = reader.readLine()) != null){
                    System.out.println("read " + message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        while(it.hasNext()){
            PrintWriter write = (PrintWriter) it.next();
            write.println(message);
            write.flush();
        }
    }

    public static void main(String[] args) {
        new VerySimpleChatServer().go();
    }

    private void go() {
        clientOutputStreams =  new ArrayList();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true){
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
