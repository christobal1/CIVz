
public class Field {
    
    int fieldCoordX;
    int fieldCoordY;
    int population;
    double dailyMoney;

    Nation ownerNation;

    FieldType fType;

    enum FieldType{
        LAND,
        SEA,
        MOUNTAIN,
        CITY
    }

    Field(int fieldCoordX, int fieldCoordY, int population, double dailyMoney){
        this.fieldCoordX = fieldCoordX;
        this.fieldCoordY = fieldCoordY;
        this.population = population;
        this.dailyMoney = dailyMoney;
        this.fType = FieldType.LAND;
        this.ownerNation = null;
    }






    //Getter
    public int getFieldCoordX(){
        return fieldCoordX;
    }

    public int getFieldCoordY(){
        return fieldCoordY;
    }

    public int getPopulation(){
        return population;
    }

    public double getDailyMoney(){
        return dailyMoney;
    }

    public FieldType getFieldType(){
        return fType;
    }

    public Nation getOwnerNation(){
        return ownerNation;
    }

    public String getFieldInfo(){
        String s = ("(" + Integer.toString(fieldCoordX) + ", " + Integer.toString(fieldCoordY) + ") \nPOPULATION: " + Integer.toString(population) + "\nDAILY $: " + Double.toString(dailyMoney) + "\n");
        return s;
    }

    //Setter
    public void setPopulation(int newPopulation){
        this.population = newPopulation;
    }

    public void setDailyMoney(double newDailyMoney){
        this.dailyMoney = newDailyMoney;
    }

    public void setOwnerNation(Nation newOwner){
        this.ownerNation = newOwner;
    }







    //Methods

    public void printFieldInfo(){
        System.out.println("FIELD at: (" + fieldCoordX + ", " + fieldCoordY + ")\nPOPULATION: " + population + "\nDAILY $: " + dailyMoney + "\n");
    }


    public void naturalPopulationGrowth(){
        //setPopulation((int)(getPopulation() * 1.03));
        int population = ((int)Math.round((getPopulation() * 1.003)));
        setPopulation(Math.min(population, 10000));
    }

}
