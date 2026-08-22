import javax.swing.*;

public class Main extends JFrame{

    public static int numRows = 10;
    public static int numCols = 20;

    static JLabel[][] worldmap = new JLabel[numRows][numCols];


    public static void main(String[] args){
        Graphics.graphicSetup(numRows, numCols, worldmap);

        //Graphics.testFill(worldmap, numRows, numCols);
        Graphics.testBlink(worldmap, numRows, numCols);
    }



}