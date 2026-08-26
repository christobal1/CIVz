import java.io.File;
import javax.sound.sampled.*;

public class RessourceManagement {

    //File backgroundMusic = new File("background.mp3");


    public static void playSound(){
        File file = new File("background.wav");

        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}