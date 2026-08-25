import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Nation{
    
    String name;
    ArrayList<Field> ownedFields;
    Color color;
    int startX; //Coordinates of first field, will be given to the Nation in Logic.startGame()
    int startY; 
    Point lastLostField;
    int lastLostCooldown = 0;

    int armySize;
    double totalMoney;
    
    Nation(String name, ArrayList<Field> ownedFields, Color color, int startX, int startY){
        this.name = name;
        this.ownedFields = ownedFields;
        this.color = color;
        this.startX = startX;
        this.startY = startY;
        this.lastLostField = null;
        this.armySize = 0;
        this.totalMoney = 0.0;
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

    public int getArmySize(){

        armySize = 0;

        for (Field f: ownedFields){
            armySize += f.getPopulation();
        }

        return (int) Math.round(armySize * 0.25);
    }


    public double getTotalMoney(){

        totalMoney = 0.0;

        for (Field f: ownedFields){
            totalMoney += f.getDailyMoney();
        }

        return totalMoney;
    }


}
