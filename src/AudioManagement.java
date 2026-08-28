import java.io.File;
import javax.sound.sampled.*;

public class AudioManagement {

    static File menuMusic = new File("ressources/audio/music/menu.wav");
    static File backgroundMusic = new File("ressources/audio/music/background.wav");

    static File warA = new File("ressources/audio/sfx/warA.wav");
    static File warB = new File("ressources/audio/sfx/warB.wav");
    static File warC = new File("ressources/audio/sfx/warC.wav");
    static File warD = new File("ressources/audio/sfx/warD.wav");
    static File warE = new File("ressources/audio/sfx/warE.wav");
    static File warF = new File("ressources/audio/sfx/warF.wav");
    static File warG = new File("ressources/audio/sfx/warG.wav");
    static File warH = new File("ressources/audio/sfx/warH.wav");
    static File warI = new File("ressources/audio/sfx/warI.wav");
    static File warJ = new File("ressources/audio/sfx/warJ.wav");
    static File warK = new File("ressources/audio/sfx/warK.wav");

    private static Clip currentClip;

    public static void playSFX(File file){
        try{
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void playMusic(File file){

        try {
            stopSound(); //stops previous sound

            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            currentClip = clip;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void stopSound(){
        if(currentClip != null && currentClip.isRunning()){
            currentClip.stop();
            currentClip.close();
        }
    }
}