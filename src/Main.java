import java.io.File;
import javax.swing.*;


public class Main{

    static String gameTitle = "Nations 1.0";
    static File map = new File("Map.txt");
    /** 
    public static int numCols = 40; //x
    public static int numRows = 20; //y
    
    public static int numHotBarItems = 5;
    public static int rounds = 2000;

    static JButton[][] visualWorldMap = new JButton[numCols][numRows];
    static JLabel[] hotBar = new JLabel[numHotBarItems];
     */

    public static int numCols = 40;
    public static int numRows = 20;

    public static int numHotBarItems = 5; //m-1 Nations will spawn
    public static int rounds = 2000;
    
    static JButton[][] visualWorldMap;
    static JLabel[] hotBar;

    static boolean debugMode = false; //Used for removal of menu



    public static void main(String[] args) throws InterruptedException{
        //Menu Setup can be commented out
        AudioManagement.playMusic(AudioManagement.menuMusic);
        

        Graphic.menuSetup(() ->{

            visualWorldMap = new JButton[numCols][numRows];
            hotBar = new JLabel[numHotBarItems];
            Logic.worldMap = new Field[numCols][numRows];

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
        });


        //Graphics.testFill(visualWorldMap, numRows, numCols);
        //Graphics.testBlink(visualWorldMap, numRows, numCols);
    }
}