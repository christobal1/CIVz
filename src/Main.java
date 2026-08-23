import java.awt.Point;
import java.util.ArrayList;
import javax.swing.*;

public class Main{

    public static int numRows = 20;
    public static int numCols = 40;
    public static int numHotBarItems = 5;

    static JLabel[][] visualWorldmap = new JLabel[numRows][numCols];
    static JLabel[] hotBar = new JLabel[numHotBarItems];

    public static void main(String[] args){
        Graphics.graphicSetup(numRows, numCols, numHotBarItems, visualWorldmap);

        //Graphics.testFill(worldmap, numRows, numCols);
        //Graphics.testBlink(worldmap, numRows, numCols);


        Field f1 = new Field(new Point(1,2), 4231,400.0);
        Field f2 = new Field(new Point (2, 5), 2300, 500.0);
        

        ArrayList<Field> n1Fields = new ArrayList();
        Nation n1 = new Nation("Coolistan", n1Fields);

        n1Fields.add(f1);
        n1Fields.add(f2);


        n1.printNation();
        //f1.printFieldInfo();
        
    }



}