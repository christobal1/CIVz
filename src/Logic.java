import java.awt.Color;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;

public class Logic{
    
    public static Field[][] worldMap = new Field[Main.numCols][Main.numRows];

    
    public static void startGame(JButton[][] visualWorldMap) throws InterruptedException {

        
        Nation n1 = createNation(visualWorldMap, 1, "Coolistan", 0, 0, Graphic.red);
        Nation n2 = createNation(visualWorldMap, 2, "Fooleria", Main.numCols-1, Main.numRows-1, Graphic.darkBlue);

        Nation n3 = createNation(visualWorldMap, 3, "Usbonia", 0, Main.numRows-1, Graphic.pink);
        Nation n4 = createNation(visualWorldMap, 4, "Giantopia", Main.numCols-1, 0, Graphic.green);

        for(int i=0; i<3000; i++){ // 2000

            Thread.sleep(20); // 50

            for(int j=0; j<Main.numCols; j++){
                for(int k=0; k<Main.numRows; k++){
                    worldMap[j][k].naturalPopulationGrowth();
                }
            }

            System.out.println("---\nRound " + i + "\n");
            makeMove(visualWorldMap, n1);
            makeMove(visualWorldMap, n2);
            makeMove(visualWorldMap, n3);
            makeMove(visualWorldMap, n4);

        }

    }



    //Create the nations, their "owned field array" and "start field"
    public static Nation createNation(JButton[][] visualWorldMap, int nationID, String name, int startX, int startY, Color color){

        ArrayList<Field> nFields = new ArrayList<>();
        Nation n = new Nation(nationID, name, nFields, color, startX, startY);

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
        Graphic.synchronizeVisual(visualWorldMap, x, y, fType);
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
            Graphic.changeSquareColor(visualWorldMap, x, y, newOwner.color);
        }

        //If field has owner
        if(field.ownerNation != newOwner){
            if(fightForField(x, y, newOwner).equals("successful")){
                (field.ownerNation).removeFieldFromOwned(field);
                field.setOwnerNation(newOwner);
                newOwner.addFieldToOwned(field);
                Graphic.changeSquareColor(visualWorldMap, x, y, newOwner.color);
            }
        }

        
    }





    /* -------------------------------------------------------------------- */







    //BOT WAR METHODS

    public static String fightForField(int x, int y, Nation attacker){

        Nation defender = worldMap[x][y].getOwnerNation();
        int defenderArmy = defender.getArmySize();

        System.out.println(attacker.getName() + " attacks ");

        int attackerArmy = attacker.getArmySize();

        System.out.println(defender.getName() + " at (" + x + ", " + y + ")");

        //attacker wins
        if(attackerArmy > defenderArmy){
            System.out.println("... and wins");
            defender.setLastLostField(new Point(x, y));
            attacker.setArmySize((int)Math.max(1, Math.round(attackerArmy * 0.7)));
            defender.setArmySize((int)Math.max(1, Math.round(defenderArmy * 0.5))); //attacker loses less army if he wins, extract these method invocations to other method because mountain and city combat will differ even more
            attacker.truces.put(defender, 10);
            defender.truces.put(attacker, 10);

            return "successful";

        //random chance of winning / losing
        } else if (attackerArmy == defenderArmy){
            int randomNum = (int)(Math.random() * 10);
            if(randomNum % 2 == 0){
                System.out.println("... and wins.");
                defender.setLastLostField(new Point(x, y));
                attacker.setArmySize((int)Math.round(attackerArmy * 0.4));
                defender.setArmySize((int)Math.round(defenderArmy * 0.4));
                attacker.truces.put(defender, 10);
                defender.truces.put(attacker, 10);
 
                return "successful";
            }
        }

        //attacker loses
        System.out.println("... and loses.");
        attacker.setLastLostField(new Point(x, y));
        attacker.setArmySize(Math.max(1, (int)Math.round(attackerArmy * 0.5)));
        defender.setArmySize(Math.max(1, (int)Math.round(defenderArmy * 0.9)));
        attacker.truces.put(defender, 10);
        defender.truces.put(attacker, 10);

        return "failed";
    }


    //Field Logic
    //Info shown when click on fields
    //The Event Listeners in Graphic.java use this method
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

        n.draft();
        Graphic.updateHotbarVisual(n);
        
        if(n.lastLostCooldown > 0){
            n.lastLostCooldown --;
            if(n.lastLostCooldown == 0){
                n.lastLostField = null;
            }
        }



        //Update truces every time
        //Reduce, but when 0 then remove Nation from truces HashMap completely
        Iterator<Map.Entry<Nation, Integer>> it = n.truces.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Nation, Integer> entry = it.next();

            int newVal = entry.getValue() - 1;

            if(newVal <= 0){
                it.remove();
            } else {
                entry.setValue(newVal);
            }
        }


        
        Point p = searchNextSquare(n);

        if(p == null){ //do nothing
            return;
        }

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

            //When score unusable, ignore it
            double score = evaluateField(n, x, y);
            if(score == Double.NEGATIVE_INFINITY) continue;


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
       
        //Do nothing if situation not good
        if (bestX == -1){
            return null;
        }

        return new Point(bestX, bestY);

    }



    //1 for first, 2 for second field
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
        score += 1000.0 / (dist + 1);
        score += money * 0.5;
        score += pop * 0.2;

        //If this coordinate is the last lost one, change to HashMap for more avoidance of those last lost fields, perhaps link to playstyle of bot
        if (lastLost != null && (lastLost.x == x && lastLost.y == y)){
            return Double.NEGATIVE_INFINITY;
        }
        
        //Evaluate by checking truces with the owner nation
        //Use getOrDefault because otherwise Null Pointer Exception, if owner is not in the map.
        Nation owner = f.getOwnerNation();
        if((n.truces).getOrDefault(owner, 0) > 0){
            return Double.NEGATIVE_INFINITY;
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

        if(x < 0 || y < 0 || x >= Main.numCols || y >= Main.numRows){
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
    










    /* -------------------------------------------------------------------- */












    //SETUP

    //Write a map.txt
    public static void writeMap(){

        //Calculate number of seas and sea spawn coordinates
        int numberOfSeas = (int) Math.round(Math.sqrt(Main.numCols * Main.numRows) / 4);
        int seaSpawnCoords[][] = new int[numberOfSeas][2];
        for(int i=0; i<numberOfSeas; i++){
            seaSpawnCoords[i][0] = (int)(Math.random() * Main.numCols);
            seaSpawnCoords[i][1] = (int)(Math.random() * Main.numRows);
        }

        //Write the map
        try{
            FileWriter myWriter = new FileWriter("Map.txt", false);
            for(int i=0; i<Main.numCols; i++){
                for(int j=0; j<Main.numRows; j++){
                    //int randomPop = 61 + (int)(Math.random() * 40); before, was different, had effect on the decisions of the nations. looks off now.
                    int randomPop = ThreadLocalRandom.current().nextInt(61, 101); //pop between 61 and 100
                    double randomMoneyDaily = (double)(Math.random() * 20);
                    randomMoneyDaily = Math.round((randomMoneyDaily * 100) / 100);

                    String type = "LAND";

                    for(int k=0; k<numberOfSeas; k++){
                        if(seaSpawnCoords[k][0] == i && seaSpawnCoords[k][1] == j){
                            type = "CITY";
                        }
                    }



                    if((i == Main.numCols - 1) && (j == Main.numRows - 1)) {
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