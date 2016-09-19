package com.git.sereginpavel;

import javax.sound.midi.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by SerP on 19.09.2016.
 */
public class MiniMiniMusicApp {
    public static void main(String[] args) throws InterruptedException {

        MiniMiniMusicApp mini = new MiniMiniMusicApp();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = reader.readLine();
            if (!input.equals("q")){
                String[] var = input.split(" ");
                int instrument = Integer.parseInt(var[0]);
                int note = Integer.parseInt(var[1]);
                for (int i = 0; i < 3; i++) {
                    mini.play(2, 90);
                    Thread.sleep(300);
                    mini.play(2, 87);
                    Thread.sleep(300);
                    mini.play(2, 90);
                    Thread.sleep(300);
                    mini.play(2, 87);
                    Thread.sleep(300);
                    mini.play(2, 90);

                    Thread.sleep(300);
                    mini.play(2, 89);
                    Thread.sleep(300);
                    mini.play(2, 89);
                    Thread.sleep(300);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(int instrument, int note){
        try{
            Sequencer player = MidiSystem.getSequencer();
            player.open();
            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            MidiEvent event = null;

            ShortMessage first = new ShortMessage();
            first.setMessage(192, 1, instrument, 0);
            MidiEvent changeInstrument = new MidiEvent(first, 1);
            track.add(changeInstrument);


            ShortMessage a = new ShortMessage();
            a.setMessage(144, 1, note, 50);
            MidiEvent noteOn = new MidiEvent(a, 1);
            track.add(noteOn);

            ShortMessage b = new ShortMessage();
            b.setMessage(128, 1, note, 100);
            MidiEvent noteOff = new MidiEvent(b, 16);
            track.add(noteOff);

            player.setSequence(seq);

            player.start();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
