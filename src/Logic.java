import java.awt.Color;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;


public class Logic{

    //stage 1: everyone grabs land and no war, stage 2: wars possible
    static int stage = 1;
    
    public static Field[][] worldMap = new Field[Main.numCols][Main.numRows];
    public static ArrayList <Nation> nationList = new ArrayList<>();
    public static int dayCounter = 0; //tracks i from round loop, makes it public

    public static enum Direction{
        north,
        east,
        south,
        west
    }

    
    public static void startGame(JButton[][] visualWorldMap) throws InterruptedException {
        
        Nation n1 = createNation(visualWorldMap, 1, "Coolistan", 0, 0, Graphic.red);
        Nation n2 = createNation(visualWorldMap, 2, "Fooleria", Main.numCols-1, Main.numRows-1, Graphic.darkBlue);
        Nation n3 = createNation(visualWorldMap, 3, "Usbonia", 0, Main.numRows-1, Graphic.pink);
        Nation n4 = createNation(visualWorldMap, 4, "Giantopia", Main.numCols-1, 0, Graphic.green);


        for(int i=0; i<Main.rounds; i++){ // 2000
            dayCounter++;

            if(i == Math.round(100)){
                stage = 2;
            }

            Thread.sleep(100); // 50

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
        nationList.add(n);

        moveToField(visualWorldMap, startX, startY, n); //Was once: changeFieldOwner method, normally meant for attacking, but here used as setup for startpoint
        n.moveArmyTo(new Point(startX, startY));

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
    




    /* -------------------------------------------------------------------- */

    //Overall WAR



    //BOT WAR METHODS








public static void makeMove(JButton[][] visualWorldMap, Nation n){

    n.updateLostFieldsCooldown();

    for (Iterator<Map.Entry<Nation, Integer>> it = n.truces.entrySet().iterator(); it.hasNext();) {
        Map.Entry<Nation, Integer> entry = it.next();
        int days = entry.getValue() - 1;
        if(days <= 0){
            it.remove();
        } else {
            entry.setValue(days);
        }
    }

    Nation enemy;
    Graphic.updateHotbarVisual(n);

    if(stage == 1){ //stage 1: just free land grabbing
        Point p = searchForField(n, null);
        if(p != null){
            moveToField(visualWorldMap, p.x, p.y, n);
        }
    } else {

        if(!n.getAtWar()){ //if not at war
            n.considerWar();
        } else { //if at war

            n.considerPeace();

            for(Nation nEnemy: n.atWarWith){
                int id = nEnemy.getNationID() -1;

                n.warInfo[id][0]++;
                n.warInfo[id][1]+= n.getCasualties();
            }

            if(n.atWarWith.isEmpty()){
                return;
            } else {
                enemy = n.atWarWith.get(0);
            }

            if((n.armyPosition[0] == enemy.armyPosition[0]) && (n.armyPosition[1] == enemy.armyPosition[1])){
                fightForField(n.armyPosition[0], n.armyPosition[1], n);
            }

            if(n.currentPath == null || n.currentPath.isEmpty()){
                Point target = searchForField(n, n.atWarWith.get(0));
                if(target == null) return;

                n.currentPath = findPath(
                    n,
                    new Point(n.armyPosition[0], n.armyPosition[1]),
                    target
                );
            }

            if(!n.currentPath.isEmpty()){
                Point nextStep = n.currentPath.get(0);

                n.clearPreviousArmyPosition(new Point(n.armyPosition[0], n.armyPosition[1]));
                n.moveArmyTo(nextStep);

                moveToField(visualWorldMap, nextStep.x, nextStep.y, n);

                n.currentPath.remove(0);
            }
        }
    }

    n.draft();
}




    //returns the way as an arraylist between two points
    public static ArrayList<Point> findPath(Nation n, Point start, Point end){

        Field startField = worldMap[start.x][start.y];

        ArrayList<Point> path = new ArrayList<>();

        Point current = start;

        while(!current.equals(end)){

            int x = current.x;
            int y = current.y;
    
            Point north = new Point(x, y+1);
            Point east = new Point(x+1, y);
            Point south = new Point(x, y-1);
            Point west = new Point(x-1, y);

            //Compare Which is closer to end:
            
            
            Point[] dirs = {north, east, south, west};
            Point best = dirs[0];

            for(int i=1; i<dirs.length; i++){
                best = whichIsCloserTo(best, dirs[i], end);
            }
            
            
            path.add(best);
            current = best;

        }

        return path;

    }

    public static Point whichIsCloserTo(Point a, Point b, Point goal){

        boolean validA = checkFieldLegality(a.x, a.y);
        boolean validB = checkFieldLegality(b.x, b.y);

        if (!validA && !validB) return a; // fallback (both bad)
        if (!validA) return b;
        if (!validB) return a;

        double distA = Math.pow(a.x - goal.x, 2) + Math.pow(a.y - goal.y, 2);
        double distB = Math.pow(b.x - goal.x, 2) + Math.pow(b.y - goal.y, 2);

        return (distA < distB) ? a : b;
    }


    /** 
    //Universally useable
    public static Point findClosestFieldToAPoint(Nation n, Point p, boolean hasToBeOwnedByN){
    
        Field f1 = worldMap[p.x][p.y];

        Point closest = null;
        double minDistance = Double.MAX_VALUE;

        int targetX = f1.getFieldCoordX();
        int targetY = f1.getFieldCoordY();


        if(hasToBeOwnedByN){
            //Only owned fields
            for(Field f: n.ownedFields){
                int x = f.getFieldCoordX();
                int y = f.getFieldCoordY();

                double dist = Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));

                if(dist < minDistance){
                    minDistance = dist;
                    closest = new Point(x, y);
                }
            }
        } else {
            for(int x=0; x<Main.numCols; x++){
                for(int y=0; y<Main.numRows; y++){
                    
                    Field f = worldMap[x][y];

                    int fx = f.getFieldCoordX();
                    int fy = f.getFieldCoordY();

                    double dist = Math.sqrt(Math.pow(fx - targetX, 2) + Math.pow(fy - targetY, 2));

                    if(dist < minDistance){
                        minDistance = dist;
                        closest = new Point(fx, fy);
                    }
                }
            }
        }
        return closest;
    }*/
    



    

    //Changes information of one square, kind of like changeOneSquareOnWorldMap
    //Meant for changing of ownership after fight
    //Not for map & terrain building
    //Doesnt go into synchronizeVisual, because that method is currently meant for map building
    //Goes through changeSquareColor directly. Similar mechanism
    public static void moveToField(JButton[][] visualWorldMap, int x, int y, Nation newOwner){

        Field field = worldMap[x][y];

        //If field has no owner
        if(field.ownerNation == null){
            field.setOwnerNation(newOwner);
            newOwner.addFieldToOwned(field);
            Graphic.changeSquareColor(visualWorldMap, x, y, newOwner.color);

        } else if (field.ownerNation != newOwner){ //If field has owner
            (field.ownerNation).removeFieldFromOwned(field);
            field.setOwnerNation(newOwner);
            newOwner.addFieldToOwned(field);
            Graphic.changeSquareColor(visualWorldMap, x, y, newOwner.color);
        }
    }




    public static String fightForField(int x, int y, Nation attacker){

        AudioManagement.playSFX(AudioManagement.warA);

        Nation defender = worldMap[x][y].getOwnerNation();
        int attackerArmy = attacker.getArmySize();
        int defenderArmy = defender.getArmySize();

        System.out.println(attacker.getName() + " attacks " + defender.getName() + " at (" + x + ", " + y + ")");
        attacker.startWarWith(defender); //do it mututally
        defender.startWarWith(attacker);

        //attacker wins
        if(attackerArmy > defenderArmy){
            fightOutCome(attacker, defender, "and wins", 100, 150, x, y);
            return "successful";

        } else if (attackerArmy == defenderArmy){
            int randomNum = (int)(Math.random() * 10); //random chance of winning / losing
            if(randomNum % 2 == 0){
                fightOutCome(attacker, defender, "and wins", 170, 170, x, y);
                return "successful";
            }
        }

        //attacker loses
        fightOutCome(attacker, defender, "and wins", 160, 70, x, y);
        return "failed";
    }


    //Frame for when a fight is finished
    public static void fightOutCome(Nation winner, Nation loser, String message, int winnerArmyReduction, int loserArmyReduction, int x, int y){
        int winnerArmy = winner.getArmySize();
        int loserArmy = loser.getArmySize();
        
        System.out.println("... and wins");
        winner.setArmySize((int)Math.max(1, Math.round(winnerArmy - winnerArmyReduction)));
        loser.setArmySize((int)Math.max(1, Math.round(loserArmy - loserArmyReduction))); //attacker loses less army if he wins, extract these method invocations to other method because mountain and city combat will differ even more
        winner.truces.put(loser, 20); //20 days of truce
        loser.truces.put(winner, 20);

        loser.removeFieldFromOwned(worldMap[x][y]);
        loser.addLostField((new Point(x, y)));
        winner.addFieldToOwned(worldMap[x][y]);
        worldMap[x][y].setOwnerNation(winner);
    }



    /** 
    //If at war with a nation, n will look for fields of its enemy
    //Build a list of fields that lie to a certain direction of n
    public static Direction searchEnemyFront(Nation n){

        int sizeOfOwnedArray = (n.ownedFields).size();
        ArrayList<Field> enemyIsNorth = new ArrayList<>();
        ArrayList<Field> enemyIsEast = new ArrayList<>();
        ArrayList<Field> enemyIsSouth = new ArrayList<>();
        ArrayList<Field> enemyIsWest = new ArrayList<>();

        //How many enemy fields are in a certain direction
        HashMap<Direction, Integer> dirs = new HashMap<>();

        //For every field that n owns, check where the enemy is: N,E,S,W
        //No need for legality check, because here only owned fields get checked
        //sea or out of map cant be owned = no risk.
        for(int i=0; i<sizeOfOwnedArray; i++){
            Field temp = (n.ownedFields).get(i);
            int x = temp.getFieldCoordX();
            int y = temp.getFieldCoordY();

            Field north = worldMap[x][y+1];
            Field east = worldMap[x+1][y];
            Field south = worldMap[x][y-1];
            Field west = worldMap[x-1][y];

            //If the list of enemies of Nation n contains the owner of the neighbor field of n, add it to the list.
            if((n.atWarWith).contains(north.getOwnerNation())){
                enemyIsNorth.add(north);
                dirs.put(Direction.north, enemyIsNorth.size());
            } else if ((n.atWarWith).contains(east.getOwnerNation())){
                enemyIsEast.add(east);
                dirs.put(Direction.east, enemyIsEast.size());
            } else if ((n.atWarWith).contains(south.getOwnerNation())){
                enemyIsSouth.add(south);
                dirs.put(Direction.south, enemyIsSouth.size());
            } else if ((n.atWarWith).contains(west.getOwnerNation())){
                enemyIsWest.add(west);
                dirs.put(Direction.west, enemyIsWest.size());
            }

            //dirs now contains the direction and how many enemy fields are there
            //now get where the most fields are:
            
        }

        return Collections.max(dirs.entrySet(), Map.Entry.comparingByValue()).getKey();
    }*/

    /** 
    //plans out the moves an army makes
    //used by makeMove() after getting the direction of enemy-front from searchEnemyFront()
    public static ArrayList<Point> planArmyMovement(Nation n, Direction dir){

        ArrayList <Point> movePlan = new ArrayList<>();

        if(dir == Direction.north){
            Point start = findClosestFieldToAPoint(n, new Point(0,0), true); //upper left corner
            movePlan.add(start);
            //movePlan.add();

        } else if (dir == Direction.east){
            Point start = findClosestFieldToAPoint(n, new Point(Main.numCols, 0), true); //upper right corner
            movePlan.add(start);
            //movePlan.add();

        } else if (dir == Direction.south){
            Point start = findClosestFieldToAPoint(n, new Point(Main.numCols, Main.numRows), true); //lower right corner
            movePlan.add(start);
            //movePlan.add();

        } else if (dir == Direction.west){
            Point start = findClosestFieldToAPoint(n, new Point(0, Main.numRows), true); //lower left corner
            movePlan.add(start);
            //movePlan.add();
        }

        return movePlan;
    }*/

    
    




    //Search next best square to attack based on surrounding fields
    //Put the coordinates of the surrounding fields in a 3d array
    //After that rank them and sort some out according to legality
    //An other method will be invoked for this!
    public static Point searchForField(Nation n, Nation hasToBeOwner){
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
                    if(compareFields(n, x1, y1, x2, y2, hasToBeOwner) == 2){
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
            double score = evaluateField(n, x, y, hasToBeOwner);
            if(score == Double.NEGATIVE_INFINITY) continue;

            if(bestX == -1){
                bestX = x;
                bestY = y;
                continue;
            }

            if(compareFields(n, bestX, bestY, x, y, hasToBeOwner) == 2){
                bestX = x;
                bestY = y;
            }
        }

        //System.out.println("Best Move: " + bestX + " " + bestY);

        if (bestX == -1){
            return null;
        }

        return new Point(bestX, bestY);
    }



    //1 for first, 2 for second field
    public static int compareFields(Nation n, int x1, int y1, int x2, int y2, Nation hasToBeOwner){

        if(evaluateField(n, x1, y1, hasToBeOwner) > evaluateField(n, x2, y2, hasToBeOwner)){
            return 1;
        } else {
            return 2;
        }
    }


    //Gives a field a score, needed in compare fields. Both of the methods are used in searching of next field
    //Useable for peace times when only free landgrabbing happens in stage 1
    //As well as stage 2, where wars are possible
    public static double evaluateField(Nation n, int x, int y, Nation hasToBeOwner){
        

        double score = 0.0;

        Field f = worldMap[x][y];
        
        double money = f.getDailyMoney();
        int pop = f.getPopulation();
        int dist = (Math.abs(n.getStartX() - x)) + Math.abs(n.getStartY() - y);

        //closer to start of Nation n = better, more money = slightly better, more pop = bit better
        score += 300.0 / (dist + 1);
        score += money * 0.5;
        score += pop * 0.2;

        Nation owner = f.getOwnerNation();

        //If lost the field recently
        if(n.lastLostFields != null && n.lastLostFields.containsKey(new Point(x, y))){
            score -= 5000;
        }

        //Case 1: only free fields
        if(hasToBeOwner == null){
            if(owner != null && owner != n){
                return Double.NEGATIVE_INFINITY;
            }
        
        //Case 2: only enemy fields
        } else if (n.atWarWith.contains(hasToBeOwner)){
            if(owner == hasToBeOwner){
                score += 10000;
            }

        //Case 3: only own fields
        } else if (hasToBeOwner == n){
            if(owner == n){
                score += 10000;
            } else {
                return Double.NEGATIVE_INFINITY;
            }
        }

 
        return score;
        
    }


    //Swapping of array content for Bubble Sort
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

        return true;
    }


    //Checks whether it is a nations own field
    public static boolean isOwnField(int x, int y, Nation n){

        return (worldMap[x][y].getOwnerNation() == n);
    }
    










    /* -------------------------------------------------------------------- */



    //Weird

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
                    randomMoneyDaily = Math.round((randomMoneyDaily * 100.0) / 100.0);

                    String type = "LAND";

                    for(int k=0; k<numberOfSeas; k++){
                        if(seaSpawnCoords[k][0] == i && seaSpawnCoords[k][1] == j){
                            type = "SEA";
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