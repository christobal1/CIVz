import java.util.ArrayList;

public class Nation{
    
    String name;
    ArrayList<Field> ownedFields;
    
    Nation(String name, ArrayList<Field> ownedFields){
        this.name = name;
        this.ownedFields = ownedFields;
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


}
