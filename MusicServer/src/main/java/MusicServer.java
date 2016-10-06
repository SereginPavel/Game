import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by SerP on 24.09.2016.
 */
public class MusicServer {
    ArrayList<ObjectOutputStream> clientOutputStreams;

    public class ClientHandler implements Runnable{
        ObjectInputStream in;
        Socket clientSocket;

        public ClientHandler(Socket sock) {
            try {
                clientSocket = sock;
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Object o1 = null;
            Object o2 = null;

            try {
                while((o1 = in.readObject()) != null){
                    o2 = in.readObject();
                    System.out.println("Прочитаны два объекта");
                    tellEveryone(o1, o2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tellEveryone(Object one, Object two) {
        Iterator it = clientOutputStreams.iterator();
        while(it.hasNext()){
            try {
                ObjectOutputStream out = (ObjectOutputStream) it.next();
                out.writeObject(one);
                out.writeObject(two);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new MusicServer().go();
    }

    private void go() {
        clientOutputStreams =  new ArrayList();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true){
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(out);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("Получено соединение с клиентом");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
