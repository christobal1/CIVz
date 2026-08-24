import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JLabel;

public class Logic{
    
    public static Field[][] worldMap = new Field[Main.numRows][Main.numCols];


    //Create the nations, their "owned field array" and "start field"
    public static void createNations(JLabel[][] visualWorldMap){

        ArrayList<Field> n1Fields = new ArrayList<>();
        Nation n1 = new Nation("Coolistan", n1Fields, Color.RED);

        int n1StartX = 0;
        int n1StartY = 0;
        changeFieldOwner(visualWorldMap, n1StartX, n1StartY, n1);



        ArrayList<Field> n2Fields = new ArrayList<>();
        Nation n2 = new Nation("Fooleria", n2Fields, Color.GREEN);

        int n2StartX = 19;
        int n2StartY = 39;
        changeFieldOwner(visualWorldMap, n2StartX, n2StartY, n2);

        

    }



    //Reads all Field information from the Map.txt
    //INFO: fType is fieldType form Field.java
    public static void completeWorldMapReset(JLabel[][] visualWorldMap){

        try (Scanner myReader = new Scanner(Main.map)){
            while(myReader.hasNext()){

                String line = myReader.nextLine().trim();
                if(line.isEmpty()){
                    continue;
                }

                String[] parts = line.split("\\s+");

                if(parts.length != 5){
                    System.out.println("Wrong format of numbers in map file");
                    continue;
                }


                try{
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int pop = Integer.parseInt(parts[2]);
                    double moneyDaily = Double.parseDouble(parts[3]);
                    Field.FieldType fType = Field.FieldType.valueOf(parts[4]);

                    Field field = new Field(x, y, pop, moneyDaily);
                    field.fType = fType;

                    worldMap[x][y] = field;

                    changeOneSquareOnWorldMap(visualWorldMap, x, y, fType);
                } catch (NumberFormatException e){
                    System.out.println("Error in format of information in file");
                }
            }

        } catch (FileNotFoundException e){
            System.out.println("An error occured while reading the file");
            e.printStackTrace();
        }



    }




    //Changes information of one square, like a setter
    //Only changes visual aspect, owner has to be set seperatly for now!!!
    public static void changeOneSquareOnWorldMap(JLabel[][] visualWorldMap, int x, int y, Field.FieldType fType){

        worldMap[x][y].fType = fType; //does this work ???
        Graphics.synchronizeVisual(visualWorldMap, x, y, fType);
    }




    public static void changeFieldOwner(JLabel[][] visualWorldMap, int x, int y, Nation newOwner){

        Field field = worldMap[x][y];

        if(field.ownerNation != null){
            (field.ownerNation).removeFieldFromOwned(field);
        }

        field.ownerNation = newOwner;
        newOwner.addFieldToOwned(field);

        Graphics.changeSquareColor(visualWorldMap, x, y, newOwner.color);
    }













    
    //Attacks square based on search
    public void attack(){

    }

    //Search next best square to attack based on surrounding squares
    public void search(){
        
    }


    //Check N,E,W,S squares for best choice
    public void checkSurrounding(){

    }


    public void updateInfo(){
        
    }











    //TESTS

    //Write a map.txt
    public static void writeMap(){

        try{
            FileWriter myWriter = new FileWriter("Map.txt", false);
            for(int i=0; i<Main.numRows; i++){
                for(int j=0; j<Main.numCols; j++){
                    int randomPop = (int)(Math.random() * 101);
                    double randomMoneyDaily = (double)(Math.random() * 200);
                    
                    if((i == Main.numRows - 1) && (j == Main.numCols - 1)) {
                        myWriter.write(i + " " + j + " " + randomPop + " " + randomMoneyDaily + " " + landTypeCalc(i, j));
                    } else {
                        myWriter.write(i + " " + j + " " + randomPop + " " + randomMoneyDaily + " " + landTypeCalc(i, j) + "\n");
                    }
                }
            }
            
            myWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //Works currently for 20x40 map
    //Flexibility for other types necessary
    public static String landTypeCalc(int i, int j){

        if((i == 2)&&(j < 4)) {
            return "SEA";
        } else if ((i > 10 && i < 18) && (j > 4 && j < 10)){
            return "SEA";
        } else if ((i > 16 && i < 22) && (j > 14 && j < 19)){
            return "SEA";
        } else if ((i > 5 && i < 16) && (j > 29 && j < 40)){
            return "SEA";
        }

        return "NONE";
    }


    
}