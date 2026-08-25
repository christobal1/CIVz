import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class Logic{
    
    public static Field[][] worldMap = new Field[Main.numRows][Main.numCols];


    
    public static void startGame(JButton[][] visualWorldMap){

        createNations(visualWorldMap);

    }


    //Create the nations, their "owned field array" and "start field"
    public static void createNations(JButton[][] visualWorldMap){

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

        makeMove(n1);

    }



    //Reads all Field information from the Map.txt
    //INFO: fType is fieldType from Field.java
    public static void completeWorldMapReset(JButton[][] visualWorldMap){

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




    //Changes information of one square
    //Meant for map & terrain building
    //Because no owner will be set, only field type (fType)
    public static void changeOneSquareOnWorldMap(JButton[][] visualWorldMap, int x, int y, Field.FieldType fType){

        worldMap[x][y].fType = fType; //does this work ???
        Graphics.synchronizeVisual(visualWorldMap, x, y, fType);
    }


    //Changes information of one square
    //Meant for changing of ownership after fight
    //Not for map & terrain building
    //Doesnt go into synchronizeVisual, because that method is currently meant for map building
    //Goes through changeSquareColor directly. Similar mechanism
    public static void changeFieldOwner(JButton[][] visualWorldMap, int x, int y, Nation newOwner){

        Field field = worldMap[x][y];

        if(field.ownerNation != null){
            (field.ownerNation).removeFieldFromOwned(field);
        }

        field.ownerNation = newOwner;
        newOwner.addFieldToOwned(field);

        Graphics.changeSquareColor(visualWorldMap, x, y, newOwner.color);
    }





    //Field Logic
    //Info shown when click on fields
    //The Event Listeners in Graphics.java use this method
    //Is it weird to have this method? Maybe there's a better way ?
    public static String matchVisualCoordsToRealCoords(int x, int y){

        return worldMap[x][y].getFieldInfo();
    }










    //move to other square based on search
    public static void makeMove(Nation n){
        searchNextSquare(n);

    }


    //Search next best square to attack based on surrounding fields
    //Put the coordinates of the surrounding fields in a 3d array
    //After that rank them and sort some out according to legality
    //An other method will be invoked for this!
    public static void searchNextSquare(Nation n){
        int sizeOfOwnedArray = (n.ownedFields).size();
        int[][][] potentialNextCoords = new int[sizeOfOwnedArray][4][2];
        int[][][] rankedNextCoords = new int[sizeOfOwnedArray][4][2];

        for(int i=0; i<sizeOfOwnedArray; i++){
            //Get the coordinates of one of the owned fields
            Field temp = (n.ownedFields).get(i);
            int x = temp.getFieldCoordX();
            int y = temp.getFieldCoordY();

            int northX = x;
            potentialNextCoords[i][0][0] = northX;
            int northY = y-1;
            potentialNextCoords[i][0][1] = northY;
            
            int eastX = x-1;
            potentialNextCoords[i][1][0] = eastX;
            int eastY = y;
            potentialNextCoords[i][1][1] = eastY;

            int southX = x;
            potentialNextCoords[i][2][0] = southX;
            int southY = y-1;
            potentialNextCoords[i][2][1] = southY;

            int westX = x+1;
            potentialNextCoords[i][3][0] = westX;
            int westY = y;
            potentialNextCoords[i][3][1] = westY;
        }



        

        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                if(compareFields(n, potentialNextCoords[0][j][0], potentialNextCoords[0][j][1], potentialNextCoords[0][j+1][0], potentialNextCoords[0][j+1][1]) == 1){
                    int tempX = potentialNextCoords[0][j][0];
                    int tempY = potentialNextCoords[0][j][1];


                }
            }
        }



        

        for(int i=0; i<sizeOfOwnedArray; i++){
            for(int j=0; j<4; j++){
                for(int k=0; k<2; k++){
                    System.out.print(rankedNextCoords[i][j][k] + " ");
                }
                System.out.println();
            }
        }






        int array[] = {1,2,99,5,7,2,9,4};

        for(int i=0; i<(array.length)-1; i++){
            for(int j=0; j<(array.length)-1; j++){
                if(array[j] > array[j+1]){
                    int temp = array[j+1];
                    array[j+1] = array[j];
                    array[j] = temp;
                }
            }
        }

        for(int i=0; i<array.length; i++){
            System.out.print(array[i] + " ");
        }
    }

    public static int compareFields(Nation n, int x1, int y1, int x2, int y2){

        //Add: If is mine, give rating 0


        double money1 = (worldMap[x1][y1]).getDailyMoney();
        double money2 = (worldMap[x2][y2]).getDailyMoney();

        int pop1 = (worldMap[x1][y1].getPopulation());
        int pop2 = (worldMap[x2][y2].getPopulation());

        //Add: Better bot decisions maybe by setting focus in Nation playstyle
        if((money1 > 1.5 * money2) && (pop1 > 10)){
            return 1;
        } else if ((money1 == money2) && (pop1 == pop2)){
            return 1;
        } else {
            return 2;
        }
    }


    //Checks whether square is legal
    public static boolean checkSquareLegality(int x, int y){
        if(x < 0 || y < 0){
            return false;
        }
        return true;
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
                    randomMoneyDaily = Math.round((randomMoneyDaily * 100) / 100);
                    
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