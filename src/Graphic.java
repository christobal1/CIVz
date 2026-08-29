import java.awt.*;
import javax.swing.*;

public class Graphic{

    //For Nation creation in Logic.java and synchronizeVisual() in this class
    static Color dirt = new Color(207, 192, 180);
    static Color green = new Color(116, 207, 121);
    static Color darkGreen = new Color(39, 107, 57);
    static Color red = new Color(227, 38, 0);
    static Color darkRed = new Color(150, 41, 9);
    static Color darkBlue = new Color(0, 65, 176);
    static Color magenta = new Color(121, 0, 191);
    static Color pink = new Color(245, 34, 252);

//?
    static JLabel[] hotBarArray = new JLabel[Main.numHotBarItems];

    public static void menuSetup(Runnable onStart){ //Runnable for starting the Logic through the start button, but still having the overview of it in Main.java

        JFrame frame = new JFrame();
        frame.setSize(1000, 625);
        frame.setLayout(new BorderLayout());

        //Content
        Image menuImage = new ImageIcon("ressources/images/menu.png").getImage();

        JPanel panel = new JPanel(new BorderLayout()){
        @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g); // wichtig!
                g.drawImage(menuImage, 0, 0, getWidth(), getHeight(), this);
            }
        };


        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(10, 40));
        panel.add(startButton, BorderLayout.SOUTH);
        startButton.addActionListener(e ->{
            frame.dispose(); //closes Menu
            onStart.run(); //runs the content which you can see in Main.java, only if button is pressed
        });
        //Event Listener for start button

        

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Main.gameTitle);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        
    }
    
    //Basic Interface
    public static void graphicSetup(int numCols, int numRows, int numHotBarItems, JButton[][] visualWorldMap){

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

                hotbar.add(settingsButton); //the panel hotbar
            } else {
                JLabel slot = new JLabel(Integer.toString(i));
                slot.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                hotBarArray[i] = slot; //the array for the items logically
                hotbar.add(slot); //the panel hotbar
                
            }
        }

        frame.add(hotbar, BorderLayout.NORTH);

        //GRID FOR WORLDMAP
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(numRows, numCols)); //swing wants y then x coord
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        for(int row=0; row<numRows; row++){
            for(int col=0; col<numCols; col++){
                String s = "";
                JButton squareButton = new JButton(s);
        
                squareButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                squareButton.setForeground(Color.BLACK); //text color
                squareButton.setBackground(Color.RED); //never actually visible, only if map generating/reading goes wrong
                squareButton.setOpaque(true);


                visualWorldMap[col][row] = squareButton;
                panel.add(squareButton);

                final int x = col; //only for event listener
                final int y = row; //only for event listener

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
        frame.setTitle(Main.gameTitle);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    //Updates the hotbar information: Population, Army size, total $
    public static void updateHotbarVisual(Nation n){

        Color c = n.getColor();
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());

        hotBarArray[n.getNationID()].setText(
            "<html>" +
            "<font color='" + hex + "'>" +
            "pop: " + Integer.toString(n.getTotalPopulation()) + "<br>"
            + "army: " + Integer.toString(n.getArmySize()) + "<br>"
            + "$: " + Double.toString(n.getBankMoney())
            + "</html>"
        );
    }





    //SYNCHRONIZE worldmap with visual worldmap
    public static void synchronizeVisual(JButton[][] visualWorldMap, int x, int y, Field.FieldType fType){
        switch(fType){

            case LAND:
                changeSquareColor(visualWorldMap, x, y, dirt); //own creation at first lines
                break;
            case SEA:
                changeSquareColor(visualWorldMap, x, y, Color.BLUE);
                break;
            case CITY:
                changeSquareColor(visualWorldMap, x, y, Color.BLACK); //better give text on the button to signal city status
                visualWorldMap[x][y].setText("o");
                break;
            case MOUNTAIN:
                changeSquareColor(visualWorldMap, x, y, Color.DARK_GRAY);
        }
    }
















    //COLOR CHANGE

    //fill all squares with chosen color
    public static void changeAllSquaresColor(JButton[][] visualWorldmap, int numCols, int numRows, Color color){
        
        for(int col=0; col<numCols; col++){
            for(int row=0; row<numRows; row++){
                visualWorldmap[col][row].setBackground(color);
            }
        }
    }


    //fill one square with chosen color
    public static void changeSquareColor(JButton[][] visualWorldmap, int col, int row, Color color){
        visualWorldmap[col][row].setBackground(color);
    }
















    

    //TESTS:

    //fill one square after the other
    public static void testFill(JButton[][] visualWorldmap, int numCols, int numRows){

       final int[] index = {0};

       Timer timer = new Timer(30, e->{
            int col = index[0] / numRows;
            int row = index[0] % numRows;

            visualWorldmap[col][row].setBackground(Color.GREEN);
            index[0]++;

            if(index[0] >= numCols * numRows){
                ((Timer) e.getSource()).stop();
            }
       });
       timer.start();
    }

    public static void testBlink(JButton[][] visualWorldmap, int numCols, int numRows){
        final int[] index = {0};

        Timer timer = new Timer(600, e->{

            index[0]++;
            if(index[0] % 2 == 0) changeAllSquaresColor(visualWorldmap, numCols, numRows, Color.GREEN);
            else changeAllSquaresColor(visualWorldmap, numCols, numRows, Color.black);
            if(index[0] >= numCols * numRows){
                ((Timer) e.getSource()).stop();
            }
       });
       timer.start();
    }




}
