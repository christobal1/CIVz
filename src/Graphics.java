import java.awt.*;
import javax.swing.*;

public class Graphics {
    
    //Basic Interface
    public static void graphicSetup(int numRows, int numCols, int numHotBarItems, JLabel[][] visualWorldmap){

        JFrame frame = new JFrame();
        frame.setSize(1000,625);
        frame.setLayout(new BorderLayout());

        //HOT BAR ON TOP
        JPanel hotbar = new JPanel(new GridLayout(1, 5));
        hotbar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        for(int i=0; i<numHotBarItems; i++){
            if(i==0){
                JButton settingsButton = new JButton("⚙");

                //EventListener here !!!!!

                hotbar.add(settingsButton);
            } else {
                JLabel slot = new JLabel(Integer.toString(i));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                hotbar.add(slot);
            }
            
            
        }

        frame.add(hotbar, BorderLayout.NORTH);

        //GRID FOR WORLDMAP
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(numRows,numCols));
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        for(int row=0; row<numRows; row++){
            for(int col=0; col<numCols; col++){
                String s = "";
                JLabel squareLabel = new JLabel(s, SwingConstants.CENTER);
        
                squareLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                squareLabel.setForeground(Color.RED);
                squareLabel.setBackground(Color.BLACK);
                squareLabel.setOpaque(true);

                visualWorldmap[row][col] = squareLabel;
                panel.add(squareLabel);
            }
        }

        frame.add(panel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("CIVz 1.0");
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }







    //SYNCHRONIZE worldmap with visual worldmap
    public static void synchronizeVisual(JLabel[][] visualWorldMap, int x, int y, Field.FieldType fType){
        switch(fType){
            case NONE:
                changeSquareColor(visualWorldMap, x, y, Color.LIGHT_GRAY);
                break;
            case SEA:
                changeSquareColor(visualWorldMap, x, y, Color.BLUE);
                break;
        }
    }













    //COLOR CHANGE

    //fill all squares with chosen color
    public static void changeAllSquaresColor(JLabel[][] visualWorldmap, int numRows, int numCols, Color color){
        
        for(int row=0; row<numRows; row++){
            for(int col=0; col<numCols; col++){
                visualWorldmap[row][col].setBackground(color);
            }
        }
    }


    //fill one square with chosen color
    public static void changeSquareColor(JLabel[][] visualWorldmap, int row, int col, Color color){
        visualWorldmap[row][col].setBackground(color);
    }
















    

    //TESTS:

    //fill one square after the other
    public static void testFill(JLabel[][] visualWorldmap, int numRows, int numCols){

       final int[] index = {0};

       Timer timer = new Timer(30, e->{
            int row = index[0] / numCols;
            int col = index[0] % numCols;

            visualWorldmap[row][col].setBackground(Color.GREEN);
            index[0]++;

            if(index[0] >= numRows * numCols){
                ((Timer) e.getSource()).stop();
            }
       });
       timer.start();
    }

    public static void testBlink(JLabel[][] visualWorldmap, int numRows, int numCols){
        final int[] index = {0};

       Timer timer = new Timer(600, e->{

            index[0]++;
            if(index[0] % 2 == 0) changeAllSquaresColor(visualWorldmap, numRows, numCols, Color.GREEN);
            else changeAllSquaresColor(visualWorldmap, numRows, numCols, Color.black);
            if(index[0] >= numRows * numCols){
                ((Timer) e.getSource()).stop();
            }
       });
       timer.start();
    }




}
