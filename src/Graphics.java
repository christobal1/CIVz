import java.awt.*;
import javax.swing.*;

public class Graphics {
    
        //Basic Interface
        public static void graphicSetup(int numRows, int numCols, JLabel[][] worldmap){
        JFrame frame = new JFrame();
        frame.setSize(800,500);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(numRows,numCols));
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        for(int row=0; row<numRows; row++){
            for(int col=0; col<numCols; col++){
                //String s = Integer.toString((row * 20) + col +1);
                String s = "";
                JLabel squareLabel = new JLabel(s, SwingConstants.CENTER);
        
                squareLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                squareLabel.setForeground(Color.RED);
                squareLabel.setBackground(Color.BLACK);
                squareLabel.setOpaque(true);

                worldmap[row][col] = squareLabel;
                panel.add(squareLabel);
            }
        }

        frame.add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("CIVz 1.0");
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    

    //Tests:

    //fill one square after the other
    public static void testFill(JLabel[][] worldmap, int numRows, int numCols){

       final int[] index = {0};

       Timer timer = new Timer(50, e->{
            int row = index[0] / numCols;
            int col = index[0] % numCols;

            worldmap[row][col].setBackground(Color.GREEN);
            index[0]++;

            if(index[0] >= numRows * numCols){
                ((Timer) e.getSource()).stop();
            }
       });
       timer.start();
    }

    public static void testBlink(JLabel[][] worldmap, int numRows, int numCols){
        final int[] index = {0};

       Timer timer = new Timer(600, e->{

            index[0]++;
            if(index[0] % 2 == 0) changeSquareColor(worldmap, numRows, numCols, Color.GREEN);
            else changeSquareColor(worldmap, numRows, numCols, Color.black);
            if(index[0] >= numRows * numCols){
                ((Timer) e.getSource()).stop();
            }
       });
       timer.start();
    }

    //fill all squares, then unfill
    public static void changeSquareColor(JLabel[][] worldmap, int numRows, int numCols, Color color){
        
        for(int row=0; row<numRows; row++){
            for(int col=0; col<numCols; col++){
                worldmap[row][col].setBackground(color);
            }
        }
    }


}
