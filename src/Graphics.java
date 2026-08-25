import java.awt.*;
import javax.swing.*;

public class Graphics{
    
    //Basic Interface
    public static void graphicSetup(int numRows, int numCols, int numHotBarItems, JButton[][] visualWorldMap){

        JFrame frame = new JFrame();
        frame.setSize(1000,625);
        frame.setLayout(new BorderLayout());

        //HOT BAR ON TOP
        JPanel hotbar = new JPanel(new GridLayout(1, 5));
        hotbar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        for(int i=0; i<numHotBarItems; i++){
            if(i==0){
                JButton settingsButton = new JButton("⚙");
                //settingsButton.addActionListener(settingsEvent());
                settingsButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt){
                        String[] options = {"Option1", "Option2"};
                        int choice = JOptionPane.showOptionDialog(null,
                            "Choose",
                            "Title",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null, options,
                            options);
                    }
                });

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
                JButton squareButton = new JButton(s);
        
                squareButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                squareButton.setForeground(Color.RED);
                squareButton.setBackground(Color.BLACK);
                squareButton.setOpaque(true);

                visualWorldMap[row][col] = squareButton;
                panel.add(squareButton);

                final int x = row; //only for event listener
                final int y = col; //only for event listener

                squareButton.addActionListener(new java.awt.event.ActionListener(){
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt){
                        String msg = Logic.matchVisualCoordsToRealCoords(x, y); //Use of Logic.java in Graphics.java
                        JOptionPane.showMessageDialog(null, msg);
                    }
                });
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
    public static void synchronizeVisual(JButton[][] visualWorldMap, int x, int y, Field.FieldType fType){
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
    public static void changeAllSquaresColor(JButton[][] visualWorldmap, int numRows, int numCols, Color color){
        
        for(int row=0; row<numRows; row++){
            for(int col=0; col<numCols; col++){
                visualWorldmap[row][col].setBackground(color);
            }
        }
    }


    //fill one square with chosen color
    public static void changeSquareColor(JButton[][] visualWorldmap, int row, int col, Color color){
        visualWorldmap[row][col].setBackground(color);
    }
















    

    //TESTS:

    //fill one square after the other
    public static void testFill(JButton[][] visualWorldmap, int numRows, int numCols){

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

    public static void testBlink(JButton[][] visualWorldmap, int numRows, int numCols){
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
