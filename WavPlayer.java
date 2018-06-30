
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;


/*
Class to play audio (wav files only) in the background for Game class.
JFrame can not be Serialized, therefore this class can't be used (as is)
if you want to save games.
 */
public class WavPlayer extends JFrame {
    private Clip currentClip = null;


    WavPlayer() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Test Sound Clip");
        this.setSize(200, 300);
        this.setVisible(false);  //wavPlayer wont be shown on screen.
    }


    /*
    Plays wav files on a loop until another time run out or another
    wav filed is played.  The method is set up to play 1 wav file at
    a time to avoid overlapping audio.
     */
    public void playSoundFile(String wavFileName) {

        if (currentClip != null) {  //If a File is already playing, stop it.
            currentClip.close();
        }

        try {
            // Open an audio input stream.  You could also get the sound file with an URL.
            File soundFile = new File(wavFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            // Get a sound currentClip resource.
            currentClip = AudioSystem.getClip();
            // Open audio currentClip and load samples from the audio input stream.
            currentClip.open(audioIn);
            currentClip.start();
            currentClip.loop(100);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    //Driver for testing the class.
    public static void main(String[] args) {
        WavPlayer wp = new WavPlayer();
        wp.playSoundFile("plasticGangster.wav");
    }


}






