import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Nation{
    
    int nationID;
    String name;
    ArrayList<Field> ownedFields;
    Color color;
    int startX; //Coordinates of first field, will be given to the Nation in Logic.startGame()
    int startY; 

    HashMap<Point, Integer> lastLostFields = new HashMap<>();
    int lastLostCooldown = 0;

    int armySize;
    int armyEquipmentLevel;
    int preferedArmySize;
    double bankMoney;

    int technologyLevel; //for war
    int warInfo[][] = new int[Main.numHotBarItems-1][2]; //[] = id of other nation, [][0] = day of war, [][1] = own casualties
    boolean atWar; //whether the nation is at war or not
    boolean readyForPeace; //for peace propositions, acceptations...
    int casualties; //tracks casualties for Logic.fightForField() and Logic.makeMove()
    int armyPosition[] = new int[2]; //tracks army on worldMap
    ArrayList<Point> currentPath = new ArrayList<>(); //for Logic.findPath()

    ArrayList<Nation> atWarWith = new ArrayList<>();
    HashMap<Nation, Integer> truces = new HashMap<>();
    boolean completelySurrendered;
    
    
    
    Nation(int nationID, String name, ArrayList<Field> ownedFields, Color color, int startX, int startY){
        this.nationID = nationID;
        this.name = name;
        this.ownedFields = ownedFields;
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        this.armySize = 0;
        this.bankMoney = 0.0;
        this.preferedArmySize = calculatePreferedArmySize();
        this.armyEquipmentLevel = 0;
        this.atWar = false;
        this.casualties = 0;
        this.armyPosition[0] = startX;
        this.armyPosition[1] = startY;
        this.completelySurrendered = false;
    }



    //Getter

    public int getNationID(){
        return nationID;
    }
    
    public String getName(){
        return name;
    }

    public Color getColor(){
        return color;
    }

    public int getStartX(){
        return startX;
    }

    public int getStartY(){
        return startY;
    }

    public int getTechnology(){
        return technologyLevel;
    }

    public int getArmySize(){
        return armySize;
    }

    public int getTotalPopulation(){

        int totalPopulation = 0;

        for(Field f: ownedFields){
            totalPopulation += f.getPopulation();
        }

        return totalPopulation;
    }

    //Returns whether nation is at war or not
    public boolean getAtWar(){
        return atWar;
    }

    public int getFieldCount(){
        return this.ownedFields.size();
    }

    public double getBankMoney(){
        return bankMoney;
    }

    public int getCasualties(){
        return casualties;
    }



    /* -------------------------------------------------------------------- */



    //Setter
    public void setName(String newName){
        this.name = newName;
    }

    public void setColor(Color newColor){
        this.color = newColor;
    }

    public void setArmySize(int newArmySize){
        int maxArmy = calculatePreferedArmySize();
        this.armySize = Math.max(0, Math.min(newArmySize, maxArmy));
    }

    public void setTechnology(int newTechnologyLevel){
        this.technologyLevel = newTechnologyLevel;
    }

    public void setWarInfo(Nation otherN, int dayOfWar, int ownCasualties){
        warInfo[otherN.getNationID()-1][0] = dayOfWar;
        warInfo[otherN.getNationID()-1][1] = ownCasualties;
    }

    public void setAtWar(boolean state){
        this.atWar = state;
    }

    public void setCasualties(int newCasualtyCount){
        this.casualties = newCasualtyCount;
    }





    /* -------------------------------------------------------------------- */




    //Methods

    public void addLostField(Point p){
        lastLostFields.put(p, 10);
    }

    public void updateLostFieldsCooldown(){
        ArrayList<Point> toRemove = new ArrayList<>();

        for(Point p: lastLostFields.keySet()){
            int cooldown = lastLostFields.get(p) - 1;

            if(cooldown <= 0){
                toRemove.add(p);
            } else {
                lastLostFields.put(p, cooldown);
            }
        }

        for(Point p: toRemove){
            lastLostFields.remove(p);
        }
    }

    public boolean isRecentlyLost(Point p){
        return lastLostFields.containsKey(p);
    }


    public void addFieldToOwned(Field f){
        ownedFields.add(f);
    }

    public void removeFieldFromOwned(Field f){
        ownedFields.remove(f);
    }


    public void printNation(){
        System.out.println("NATION: " + name + "");

        for(Field f: ownedFields){
            f.printFieldInfo();
        }
    }




    //WAR METHODS, more similar to Logic.java, but decided to put it in here.
    //Seems more personal to each Nation 

    public void draft(){

        preferedArmySize = calculatePreferedArmySize();

        if(armySize >= preferedArmySize){
            return;
        }

        int diff = preferedArmySize - armySize;
        int numOfOwnedFields = ownedFields.size();

        if(numOfOwnedFields == 0){
            return;
        }

        int totalPopulation = 0;
        for(Field f: ownedFields){
            totalPopulation += f.getPopulation();
        }

        if(totalPopulation == 0){
            return;
        }

        int draftedSoldiers = 0;

        //Now remove those draftees from the population and add to the army
        for (Field f: ownedFields){
        
            int available = f.getPopulation();
            //available * totalPopulation gives the fraction of the population that lives in a field. Example: available = 200, totalPopulation = 1000, then: 0.2 of total pop
            // *diff calculates how many soldiers, means: 0.2 * 100 = 20 people from this field will be drafted into the army
            // But only if more than 10 people live there
            int toDraft;

            if(f.getPopulation() > 50){
                toDraft = (int) Math.ceil((double) available / totalPopulation * diff);
            } else {
                toDraft = 0;
            }

            //Prevent overdrafting
            toDraft = Math.min(available, toDraft);

            f.setPopulation(available - toDraft);
            draftedSoldiers += toDraft;
        }

        this.armySize += draftedSoldiers;
    }


    public int calculatePreferedArmySize(){

        return (int)Math.round(this.getTotalPopulation() * 0.25);
        
    }


    public void moveArmyTo(Point p){
        armyPosition[0] = p.x;
        armyPosition[1] = p.y;

        Main.visualWorldMap[p.x][p.y].setText("A" + this.getNationID());
    }

    public static void clearPreviousArmyPosition(Point p){
        Main.visualWorldMap[p.x][p.y].setText("");
    }


    public void considerWar(){
        
        for(Nation otherN: Logic.nationList){
            //If both are at peace and dont have a peace treaty
            if(this.getAtWar() == false && otherN.getAtWar() == false && (!this.truces.containsKey(otherN))){
                double score = 0.0;

                int aggressorPop = this.getTotalPopulation();
                double aggressorMoney = this.getBankMoney();
                int aggressorArmy = this.getArmySize();
                int aggressorFieldCount = this.getFieldCount();

                int otherPop = otherN.getTotalPopulation();
                double otherMoney = otherN.getBankMoney();
                int otherArmy = otherN.getArmySize();
                int otherFieldCount = otherN.getFieldCount();

                if(aggressorPop > otherPop){
                    score += 30;
                }

                if(aggressorMoney >  otherMoney){
                    score += 30;
                }

                if(aggressorArmy > otherArmy){
                    score += 101;
                }

                if(aggressorFieldCount <  otherFieldCount){
                    score += 30;
                }

                if(score > 100){
                    this.startWarWith(otherN);
                    otherN.startWarWith(this);
                }
            }
        }
    }


    public boolean isAtWarWith(Nation other){
        return (other != null && atWarWith.contains(other));
    }

     //Instead of declareWar() and getDeclaredOn() use this method which combines both. Both nations have to call it.
    public void startWarWith(Nation n2){

        if(n2 == null || n2 == this || isAtWarWith(n2)){
            return;
        }

        atWarWith.add(n2);
        this.setAtWar(true);
        this.setWarInfo(n2, 0, 0); //prepare war info before attacking

        if(!n2.isAtWarWith(this)){
            n2.startWarWith(this);
        }

        System.out.println("A war started between:" + this.getName() + " and " + n2.getName());
        //...
    }


    //To end the war
    public void considerPeace(){

        if(atWarWith.isEmpty()){
            setAtWar(false);
            return;
        }
        
        Nation enemy = atWarWith.get(0);

        double myStrength = this.getArmySize() + this.getTotalPopulation();
        double enemyStrength = enemy.getArmySize() + enemy.getTotalPopulation();

        if(myStrength < enemyStrength * 0.6){
            readyForPeace = true;
            proposePeace(enemy);
        } else {
            readyForPeace = false;
        }
    }


    //Winner gets returned
    public void proposePeace(Nation n2){

        if(n2 == null || n2 == this || !isAtWarWith(n2)){
            return;
        }

        double myStrength = this.getArmySize() + this.getTotalPopulation();
        double enemyStrength = n2.getArmySize() + n2.getTotalPopulation();

        Nation winner;
        Nation loser;
        
        if(myStrength > enemyStrength){
            winner = this;
            loser = n2;
        } else {
            winner = n2;
            loser = this;
        }

        winner.acceptPeace(loser);
        loser.acceptPeace(winner);

    }



    public void acceptPeace(Nation other){

        if(other == null) return;

        atWarWith.remove(other);
        if(other.atWarWith.contains(this)){
            other.atWarWith.remove(this);
        }

        readyForPeace = false;
        this.setAtWar(!atWarWith.isEmpty());
        other.setAtWar(!other.atWarWith.isEmpty());

        this.setWarInfo(other, 0, 0); //reset war info
        other.setWarInfo(this, 0, 0);
        this.resetArmyPosition();
        other.resetArmyPosition();

        System.out.println(this.getName() + " made peace with " + other.getName());
    }



    public void surrenderCompletely(){

        completelySurrendered = true;

        for(Field f: ownedFields){
            f.setOwnerNation(null);
        }
    }


    public void resetArmyPosition(){
        clearPreviousArmyPosition(new Point(armyPosition[0], armyPosition[1]));

        moveArmyTo(new Point(startX, startY));
    }


}
