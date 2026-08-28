import java.io.File;
import javax.swing.*;


public class Main{

    static String gameTitle = "Nations 1.0";
    static File map = new File("Map.txt");

    public static int numCols = 40; //x
    public static int numRows = 20; //y
    
    public static int numHotBarItems = 5;

    static JButton[][] visualWorldMap = new JButton[numCols][numRows];
    static JLabel[] hotBar = new JLabel[numHotBarItems];



    public static void main(String[] args) throws InterruptedException{
        //Menu Setup can be commented out
        AudioManagement.playMusic(AudioManagement.menuMusic);

        //Graphic.menuSetup(() ->{
            Graphic.graphicSetup(numCols, numRows, numHotBarItems, visualWorldMap);

            Logic.writeMap();
            Logic.completeWorldMapReset(visualWorldMap);
            AudioManagement.playMusic(AudioManagement.backgroundMusic);

            new Thread(() -> {
                try {
                    Logic.startGame(visualWorldMap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        //});


        //Graphics.testFill(visualWorldMap, numRows, numCols);
        //Graphics.testBlink(visualWorldMap, numRows, numCols);
    }
}