import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Nation{
    
    String name;
    ArrayList<Field> ownedFields;
    Color color;
    int startX; //Coordinates of first field, will be given to the Nation in Logic.startGame()
    int startY; 

    Point lastLostField;
    int lastLostCooldown = 0;

    int armySize;
    int armyEquipmentLevel;
    int preferedArmySize;
    double totalMoney;

    HashMap<Nation, Integer> truces = new HashMap<>();
    
    Nation(String name, ArrayList<Field> ownedFields, Color color, int startX, int startY){
        this.name = name;
        this.ownedFields = ownedFields;
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        this.lastLostField = null;
        this.armySize = 0;
        this.totalMoney = 0.0;
        this.preferedArmySize = calculatePreferedArmySize();
        this.armyEquipmentLevel = 0;
    }



    //Getter
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

    public Point getLastLostField(){
        return lastLostField;
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




    //Setter
    public void setName(String newName){
        this.name = newName;
    }

    public void setColor(Color newColor){
        this.color = newColor;
    }

    public void setLastLostField(Point newlyLostField){
        this.lastLostField = newlyLostField;
        this.lastLostCooldown = 5;
    }

    public void setArmySize(int newArmySize){
        int maxArmy = calculatePreferedArmySize();
        this.armySize = Math.max(0, Math.min(newArmySize, maxArmy));
    }




    //Methods

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




    //WAR METHODS

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

            if(f.getPopulation() > 10){
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





    public double getTotalMoney(){

        totalMoney = 0.0;

        for (Field f: ownedFields){
            totalMoney += f.getDailyMoney();
        }

        return totalMoney;
    }





}
