package com.sbt.homework;
/**
 * Created by SerP on 20.09.2016.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.*;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class BeatBox {
    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    JTextField userMessage;
    ArrayList<JCheckBox> checkBoxList;
    int nextNum;
    Vector<String> listVector = new Vector<>();
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[]> otherSeqsMap = new HashMap<>();
    static String socketSrever;
    static int port;

    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;

    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi_Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
            "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        String name = checkName(args);
        checkSocket(args);
        new BeatBox().startUp(name);
    }

    private static void checkSocket(String[] socket){
        if (socket.length < 2){
            System.out.println("Введите хост и порт сервера через пробел");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            try {
                while((line = reader.readLine()).equals("")){
                    System.out.println("введите хост и порт сервера через пробел");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] s = line.split(" ");
            socketSrever = s[0];
            port = Integer.parseInt(s[1]);
        }else{
            socketSrever = socket[1];
            port = Integer.parseInt(socket[2]);
        }
    }

    private static String checkName(String[] name ){
        if (name.length == 0){
            System.out.println("вы не указали имя пользовотеля. Введите имя:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            try {
                while((line = reader.readLine()).equals("")){
                    System.out.println("вы не указали имя пользовотеля. Введите имя:");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return line;
        }else{
            return name[0];
        }
    }

    private void startUp(String name) {
        userName = name;
        //соединение с сервером
        try {
            Socket sock = new Socket(socketSrever, port);
            System.out.println("Соединение с сервером установлено!");
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        } catch (IOException e) {
            System.out.println("По задонному хосту и порту не удалось подключиться к серверу!!!");
        }
        setUpMidi();
        buildGUI();
    }

    //интерфейс
    public void buildGUI(){
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkBoxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        buttonBox.setSize(100,200);

        JButton start = new JButton("         Start          ");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("          Stop          ");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton clean = new JButton("         Clean          ");
        clean.addActionListener(new MyCleanListener());
        buttonBox.add(clean);

        JButton upTempo = new JButton("     Tempo Up     ");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("  Tempo Down   ");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton generateMusic = new JButton("      Generate      ");
        generateMusic.addActionListener(new MyGenerateMusicListener());
        buttonBox.add(generateMusic);

        JButton sendIt = new JButton("         sendIt         ");
        sendIt.addActionListener(new MySendListener());
        buttonBox.add(sendIt);

        JLabel labelIn = new JLabel("Введите сообщение:");
        buttonBox.add(labelIn);

        userMessage = new JTextField();
        buttonBox.add(userMessage);


        JLabel labelOut = new JLabel("Переписка");
        buttonBox.add(labelOut);

        incomingList = new JList();
        incomingList.addListSelectionListener(new MyListSelectionListener());
        incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane theList = new JScrollPane(incomingList);
        buttonBox.add(theList);
        incomingList.setListData(listVector);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i = 0; i < 16; i++){
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for(int i = 0; i <256; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setUpMidi(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //слушатель для кнопки start
    public class MyStartListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    //слушатель для кнопки stop
    public class MyStopListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    //слушатель для кнопки tempoUp
    public class MyUpTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    //слушатель для кнопки tempoDown
    public class MyDownTempoListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }

    //слушатель для поля приема сообщений JList
    private class MyListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent le) {
            if(le.getValueIsAdjusting()){
                String selected = (String) incomingList.getSelectedValue();
                boolean[] selectedState = (boolean[]) otherSeqsMap.get(selected);
                changeSequence(selectedState);
                sequencer.stop();
                buildTrackAndStart();
            }
        }
    }

    //слушатель для кнопки sendIt
    public class MySendListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean[] checkBoxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox check = (JCheckBox) checkBoxList.get(i);
                if (check.isSelected()){
                    checkBoxState[i] = true;
                }
            }
            String messageToSend = null;
            try {
                out.writeObject(userName + nextNum + ": " + userMessage.getText());
                out.writeObject(checkBoxState);
            } catch (IOException e1) {
                System.out.println("Невозможно отправить на сервер");
            }
            userMessage.setText("");
        }
    }

    private class MyGenerateMusicListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < 256; i++) {
                checkBoxList.get(i).setSelected(false);
            }

            for (int i = 0; i < 50; i++) {
                int check = (int) (Math.random() * 256);
                checkBoxList.get(check).setSelected(true);
            }
        }
    }


    private class MyCleanListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < 256; i++) {
                checkBoxList.get(i).setSelected(false);
            }
        }
    }

    private void changeSequence(boolean[] checkboxState) {
        for (int i = 0; i < 256; i++) {
            JCheckBox check = (JCheckBox)checkBoxList.get(i);
            if(checkboxState[i]){
                check.setSelected(true);
            }else{
                check.setSelected(false);
            }
        }
    }

    //создание трека для проигрывания
    public void buildTrackAndStart(){
        ArrayList<Integer> trackList = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i = 0; i <16; i++){
            trackList = new ArrayList<>();
            for(int j = 0; j <16; j++){
                JCheckBox jc = (JCheckBox) checkBoxList.get(j + (16 * i));
                if(jc.isSelected()){
                    int key = instruments[i];
                    trackList.add(new Integer(key));
                }else{
                    trackList.add(null);
                }
            }

            makeTracks(trackList);
            track.add(makeEvent(192, 9, 1, 0, 15));
            try{
                sequencer.setSequence(sequence);
                sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
                sequencer.start();
                sequencer.setTempoInBPM(120);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void makeTracks(ArrayList list){
        Iterator it = list.iterator();
        for(int i = 0; i < 16; i++){
            Integer num = (Integer) it.next();
            if (num != null){
                int numKey = num.intValue();
                track.add(makeEvent(144, 9, numKey, 100, i));
                track.add(makeEvent(128, 9, numKey, 100, i + 1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try{
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        }catch(Exception e){
            e.printStackTrace();
        }
        return event;
    }


    private class RemoteReader implements Runnable {
        boolean[] checkboxState = null;
        String nameToShow = null;
        Object obj = null;
        @Override
        public void run() {
            try {
                while((obj = in.readObject()) != null){
                    System.out.println("Получен объект от сервера");
                    System.out.println(obj.getClass());
                    String nameToThrow = (String) obj;
                    checkboxState = (boolean[]) in.readObject();
                    otherSeqsMap.put(nameToThrow, checkboxState);
                    listVector.add(nameToThrow);
                    incomingList.setListData(listVector);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
