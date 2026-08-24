import java.io.File;
import javax.swing.*;

public class Main{

    static File map = new File("Map.txt");

    public static int numRows = 20;
    public static int numCols = 40;
    public static int numHotBarItems = 5;

    static JLabel[][] visualWorldMap = new JLabel[numRows][numCols];
    static JLabel[] hotBar = new JLabel[numHotBarItems];

    public static void main(String[] args){
        Logic.writeMap();
        Graphics.graphicSetup(numRows, numCols, numHotBarItems, visualWorldMap);
        Logic.completeWorldMapReset(visualWorldMap);

        //Graphics.testFill(worldmap, numRows, numCols);
        //Graphics.testBlink(worldmap, numRows, numCols);

        






        
    }
}