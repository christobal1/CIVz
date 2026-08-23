import java.awt.Point;

public class Field {
    
    Point fieldCoord;
    int population;
    double dailyMoney;

    Field(Point fieldCoord, int population, double dailyMoney){
        this.fieldCoord = fieldCoord;
        this.population = population;
        this.dailyMoney = dailyMoney;
    }

    public void printFieldInfo(){
        System.out.println("FIELD at: (" + fieldCoord.x + ", " + fieldCoord.y + ")\nPOPULATION: " + population + "\nDAILY $: " + dailyMoney + "\n");
    }

}
