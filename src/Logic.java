public class Logic{
    
    static int[][] worldmap;
    
    public void resetWorldMap(){
        for(int i=0; i<Main.numRows; i++){
            for(int j=0; j<Main.numCols; j++){
                worldmap[i][j] = 0;
            }
        }
    }

    public void changeStatusOnWorldMap(int x, int y, int status){
        //0 = free
        //1 = sea
        //2 = owned by me
        //3 = owned by someone else
        
        
    }







    //Attacks square based on search
    public void attack(){

    }

    //Search next best square to attack based on surrounding squares
    public void search(){
        
    }


    //Check N,E,W,S squares for best choice
    public void checkSurrounding(){

    }


    public void updateInfo(){
        
    }

}
