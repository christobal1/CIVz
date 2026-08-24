
public class Field {
    
    int fieldCoordX;
    int fieldCoordY;
    int population;
    double dailyMoney;

    Nation ownerNation;

    FieldType fType;

    enum FieldType{
        NONE,
        SEA
    }

    Field(int fieldCoordX, int fieldCoordY, int population, double dailyMoney){
        this.fieldCoordX = fieldCoordX;
        this.fieldCoordY = fieldCoordY;
        this.population = population;
        this.dailyMoney = dailyMoney;
        this.fType = FieldType.NONE;
        this.ownerNation = ownerNation;
    }

    public void printFieldInfo(){
        System.out.println("FIELD at: (" + fieldCoordX + ", " + fieldCoordY + ")\nPOPULATION: " + population + "\nDAILY $: " + dailyMoney + "\n");
    }

}
