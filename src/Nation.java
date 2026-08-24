import java.awt.Color;
import java.util.ArrayList;

public class Nation{
    
    String name;
    ArrayList<Field> ownedFields;
    Color color;
    
    Nation(String name, ArrayList<Field> ownedFields, Color color){
        this.name = name;
        this.ownedFields = ownedFields;
        this.color = color;
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

    //Setter
    public void setName(String newName){
        this.name = newName;
    }

    public void setColor(Color newColor){
        this.color = newColor;
    }


}
