import java.awt.Color;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class Logic{
    
    public static Field[][] worldMap = new Field[Main.numRows][Main.numCols];

    
    public static void startGame(JButton[][] visualWorldMap) throws InterruptedException{

        /**
        Nation n1 = createNation(visualWorldMap, "Coolistan", 0, 0, Color.RED);
        Nation n2 = createNation(visualWorldMap, "Fooleria", 19, 39, Color.GREEN);
        Nation n3 = createNation(visualWorldMap, "Usbonia", 0, 39, Color.MAGENTA);
        Nation n4 = createNation(visualWorldMap, "Giantopia", 19, 0, Color.YELLOW);

        for(int i=0; i<400; i++){

            Thread.sleep(50);

            makeMove(visualWorldMap, n1);
            System.out.println("n1 army:" + n1.getArmySize());

            makeMove(visualWorldMap, n2);
            System.out.println("n2 army " + n2.getArmySize());
            makeMove(visualWorldMap, n3);
            makeMove(visualWorldMap, n4); 
        }*/

    }


    //Create the nations, their "owned field array" and "start field"
    public static Nation createNation(JButton[][] visualWorldMap, String name, int startX, int startY, Color color){

        ArrayList<Field> nFields = new ArrayList<>();
        Nation n = new Nation(name, nFields, color, startX, startY);

        changeFieldOwner(visualWorldMap, startX, startY, n);
        return n;


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

        //If field has no owner
        if(field.ownerNation == null){
            field.setOwnerNation(newOwner);
            newOwner.addFieldToOwned(field);
            Graphics.changeSquareColor(visualWorldMap, x, y, newOwner.color);
        }

        //If field has owner
        if(field.ownerNation != newOwner){
            if(fightForField(x, y, newOwner).equals("successful")){
                (field.ownerNation).removeFieldFromOwned(field);
                field.setOwnerNation(newOwner);
                newOwner.addFieldToOwned(field);
                Graphics.changeSquareColor(visualWorldMap, x, y, newOwner.color);
            }
        }

        
    }


    public static String fightForField(int x, int y, Nation attacker){

        Nation defender = worldMap[x][y].getOwnerNation();
        int defenderArmy = defender.getArmySize();

        System.out.println(attacker.getName() + " attacks ");

        int attackerArmy = attacker.getArmySize();

        System.out.println(defender.getName() + " at (" + x + ", " + y + ")");

        if(attackerArmy > defenderArmy){
            System.out.println("... and wins");
            defender.setLastLostField(new Point(x, y));
            return "successful";

        } else if (attackerArmy == defenderArmy){
            int randomNum = (int)(Math.random() * 10);
            if(randomNum % 2 == 0){
                System.out.println("... and wins.");
                defender.setLastLostField(new Point(x, y));
                return "successful";
            }
        }

        System.out.println("... and loses.");
        attacker.setLastLostField(new Point(x, y));
        return "failed";
    }


    //Field Logic
    //Info shown when click on fields
    //The Event Listeners in Graphics.java use this method
    //Is it weird to have this method? Maybe there's a better way ?
    public static String matchVisualCoordsToRealCoords(int x, int y){

        if(checkFieldLegality(x, y) == true){
            return worldMap[x][y].getFieldInfo();
        } else {
            return "illegal coordinates";
        }
    }










    //move to other square based on search
    public static void makeMove(JButton[][] visualWorldMap, Nation n){
        
        if(n.lastLostCooldown > 0){
            n.lastLostCooldown --;
            if(n.lastLostCooldown == 0){
                n.lastLostField = null;
            }
        }
        
        Point p = searchNextSquare(n);

        changeFieldOwner(visualWorldMap, p.x, p.y, n);
    }



    //Search next best square to attack based on surrounding fields
    //Put the coordinates of the surrounding fields in a 3d array
    //After that rank them and sort some out according to legality
    //An other method will be invoked for this!
    public static Point searchNextSquare(Nation n){
        int sizeOfOwnedArray = (n.ownedFields).size();
        int[][][] potentialNextCoords = new int[sizeOfOwnedArray][4][2];
        int[][][] rankedNextCoords = new int[sizeOfOwnedArray][4][2];

        int bestX = -1;
        int bestY = -1;

        for(int i=0; i<sizeOfOwnedArray; i++){
            //Get the coordinates of one of the owned fields
            Field temp = (n.ownedFields).get(i);
            int x = temp.getFieldCoordX();
            int y = temp.getFieldCoordY();

            int northX = x;
            potentialNextCoords[i][0][0] = northX;
            int northY = y+1;
            potentialNextCoords[i][0][1] = northY;
            
            int eastX = x+1;
            potentialNextCoords[i][1][0] = eastX;
            int eastY = y;
            potentialNextCoords[i][1][1] = eastY;

            int southX = x;
            potentialNextCoords[i][2][0] = southX;
            int southY = y-1;
            potentialNextCoords[i][2][1] = southY;

            int westX = x-1;
            potentialNextCoords[i][3][0] = westX;
            int westY = y;
            potentialNextCoords[i][3][1] = westY;
        }

        //Before sorting
        /**
        for(int i=0; i<sizeOfOwnedArray; i++){
            System.out.println("Coordinates unranked:");
            for(int j=0; j<4; j++){
                for(int k=0; k<2; k++){
                    System.out.print(potentialNextCoords[i][j][k] + " ");
                }
                System.out.println(matchVisualCoordsToRealCoords(potentialNextCoords[i][j][0], potentialNextCoords[i][j][1]));
            }
        } */


        //Copy Array 
        for(int i=0; i<sizeOfOwnedArray; i++){
            for(int j=0; j<4; j++){
                rankedNextCoords[i][j][0] = potentialNextCoords[i][j][0];
                rankedNextCoords[i][j][1] = potentialNextCoords[i][j][1];
            }
        }


        //Bubble Sort inside each of the NESW fields
        for(int i=0; i<sizeOfOwnedArray; i++){
            for(int pass=0; pass<3; pass++){
                for(int j=0; j<3; j++){
                    int x1 = rankedNextCoords[i][j][0];
                    int y1 = rankedNextCoords[i][j][1];

                    int x2 = rankedNextCoords[i][j+1][0];
                    int y2 = rankedNextCoords[i][j+1][1];

    
                    //Sort by the following rules, illegal fields get pushed back
                    boolean illegal1 = !checkFieldLegality(x1, y1) || isOwnField(x1, y1, n);
                    boolean illegal2 = !checkFieldLegality(x2, y2) || isOwnField(x2, y2, n);
                    //If both illegal, dont bother

                    if(illegal1 && illegal2){
                        continue;
                    }

                    //x1 illegal -> push it back
                    if(illegal1){
                        swap(rankedNextCoords, i, j);
                        continue;
                    }

                    //x2 illegal -> x1 stays
                    if(illegal2){
                        continue;
                    }

                    //Both legal -> compare normally
                    if(compareFields(n, x1, y1, x2, y2) == 2){
                        swap(rankedNextCoords, i, j);
                    }
                }
            }
        }


        //Now find the best of the sorted Coordinates
        for(int i=0; i<sizeOfOwnedArray; i++){
            int x = rankedNextCoords[i][0][0];
            int y = rankedNextCoords[i][0][1];

            if(!checkFieldLegality(x, y) || isOwnField(x, y, n)) continue;

            if(bestX == -1){
                bestX = x;
                bestY = y;
                continue;
            }

            if(compareFields(n, bestX, bestY, x, y) == 2){
                bestX = x;
                bestY = y;
            }
        }

        //System.out.println("Best Move: " + bestX + " " + bestY);


        
        //After sorting
        /*
        for(int i=0; i<sizeOfOwnedArray; i++){
            System.out.println("Coordinates ranked:");
            for(int j=0; j<4; j++){
                for(int k=0; k<2; k++){
                    System.out.print(rankedNextCoords[i][j][k] + " ");
                }
                System.out.println(matchVisualCoordsToRealCoords(rankedNextCoords[i][j][0], rankedNextCoords[i][j][1]));
            }
        } */

        return new Point(bestX, bestY);

    }



    public static int compareFields(Nation n, int x1, int y1, int x2, int y2){

        if(evaluateField(n, x1, y1) > evaluateField(n, x2, y2)){
            return 1;
        } else {
            return 2;
        }
    }

    public static double evaluateField(Nation n, int x, int y){
        
        double score = 0.0;

        Field f = worldMap[x][y];
        Point lastLost = n.getLastLostField();

        double money = f.getDailyMoney();
        int pop = f.getPopulation();
        int dist = (Math.abs(n.getStartX() - x)) + Math.abs(n.getStartY() - y);

        //closer to start of Nation n = better, more money = slightly better, more pop = bit better
        score += 300.0 / (dist + 1);
        score += money * 0.5;
        score += pop * 0.2;

        if (lastLost != null && (lastLost.x == x && lastLost.y == y)){
            return -9999;
        }
        

        return score;

    }


    //Swap Array for Bubble Sort
    public static void swap(int array[][][], int i, int j){
        //swap X
        int tempX = array[i][j][0];
        array[i][j][0] = array[i][j+1][0];
        array[i][j+1][0] = tempX;

        //swap y
        int tempY = array[i][j][1];
        array[i][j][1] = array[i][j+1][1];
        array[i][j+1][1] = tempY;
    }





    //Checks whether field is legal kind of field (no sea, no outside of map)
    public static boolean checkFieldLegality (int x, int y){

        if(x < 0 || y < 0 || x >= Main.numRows || y >= Main.numCols){
            return false;

        } else if (worldMap[x][y] == null){
            return false;

        } else if ((worldMap[x][y]).getFieldType() == Field.FieldType.SEA){
            return false;
        }

        //Add: if my field, then illegal to claim

        return true;
    }

    //Checks whether it is a nations own field
    public static boolean isOwnField(int x, int y, Nation n){

        Field field = worldMap[x][y];
        
        return field.ownerNation == n;
    }
    























    //SETUP

    //Write a map.txt
    public static void writeMap(){

        //Calculate number of seas and sea spawn coordinates
        int numberOfSeas = (int) Math.round(Math.sqrt(Main.numRows * Main.numCols) / 4);
        int seaSpawnCoords[][] = new int[numberOfSeas][2];
        for(int i=0; i<numberOfSeas; i++){
            seaSpawnCoords[i][0] = (int)(Math.random() * Main.numRows);
            seaSpawnCoords[i][1] = (int)(Math.random() * Main.numCols);
        }

        //Write the map
        try{
            FileWriter myWriter = new FileWriter("Map.txt", false);
            for(int i=0; i<Main.numRows; i++){
                for(int j=0; j<Main.numCols; j++){
                    int randomPop = (int)(Math.random() * 101);
                    double randomMoneyDaily = (double)(Math.random() * 200);
                    randomMoneyDaily = Math.round((randomMoneyDaily * 100) / 100);

                    String type = "NONE";

                    for(int k=0; k<numberOfSeas; k++){
                        if(seaSpawnCoords[k][0] == i && seaSpawnCoords[k][1] == j){
                            type = "SEA";
                        }
                    }



                    if((i == Main.numRows - 1) && (j == Main.numCols - 1)) {
                        myWriter.write(i + " " + j + " " + randomPop + " " + randomMoneyDaily + " " + type);
                    } else {
                        myWriter.write(i + " " + j + " " + randomPop + " " + randomMoneyDaily + " " + type + "\n");
                    }
                }
            }
            
            myWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }





}