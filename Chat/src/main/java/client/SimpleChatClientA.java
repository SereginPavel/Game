package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * Created by SerP on 24.09.2016.
 */
public class SimpleChatClientA {
    JTextField outgoing;
    BufferedReader reader;
    JTextArea incomming;
    PrintWriter writer;
    Socket sock;

    public static void main(String[] args) {
        SimpleChatClientA chat = new SimpleChatClientA();
        chat.go();
    }
    public void go(){
        JFrame frame = new JFrame("Простой чат клиент");
        JPanel mainPanel = new JPanel();
        incomming = new JTextArea(15, 50);
        incomming.setLineWrap(true);
        incomming.setWrapStyleWord(true);
        incomming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incomming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new setButtonListener());
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        setUpNetwork();
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(400, 500);
        frame.setVisible(true);
    }

    private void setUpNetwork(){
        try {
            sock = new Socket("127.0.0.1", 5000);
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class setButtonListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            writer.println(outgoing.getText());
            writer.flush();
            outgoing.setText("");
            outgoing.requestFocus();
        }

    }

    private class IncomingReader implements Runnable {
        public void run() {
            String message;
            try{
                while((message = reader.readLine()) != null){
                    System.out.println("read " + message);
                    incomming.append(message + "\r\n");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
