package medialab.minesweeper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Game {
    static private Scenario scenario;
    private int timeLeft;
    private int flagsLeft;
    private int tries;
    int remainingSeconds;
    private boolean hasEnded;
    private boolean wasWon;
    final private boolean[][] mineBoard;
    final private char[][] revealedBoard;
    private final int boardSize;
    private int revealedTiles;
    private final int totalTiles;
    Mine[] mines;
    Mine superMine =null;
    int flagsPlaced;

    Game(Scenario sc){
        scenario=new Scenario(sc);
        flagsLeft=sc.getMines();
        timeLeft=sc.getTime();
        if (sc.getDiff()==1) boardSize=9;
        else boardSize=16;
        mineBoard=new boolean[boardSize][boardSize];
        revealedBoard = new char[boardSize][boardSize];
        revealedTiles = 0;
        totalTiles=boardSize*boardSize;
        flagsPlaced=0;
        Mine m;
        mines = new Mine[sc.getMines()];

        for(int i=0;i<sc.getMines();i++){
            do {
                m= new Mine(boardSize);
            } while(mineBoard[m.x][m.y]==true);
            mines[i]=m;
            mineBoard[m.x][m.y]=true;
        }
        if (sc.getSuperMine()){
            int pick=new Random().nextInt(0, mines.length);
            mines[pick].isSuper =true;
            superMine =mines[pick];
            System.out.println(superMine.x+" "+ superMine.y);
        }
        tries=0;
        hasEnded=false;
        wasWon=false;
        StringBuilder mineOutput= new StringBuilder();
        for (Mine mine: mines){
            char s;
            if (mine.isSuper) s='1';
            else s='0';
            mineOutput.append(mine.x+","+ mine.y +","+s+"\n");
        }
        try{
            FileWriter out= new FileWriter("medialab\\mines.txt",false);
            out.write(mineOutput.toString());
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getFlagsLeft() {
        return flagsLeft;
    }
    /**
     * Checks if the box with coordinates (x,y) we selected with left-click contains a mine
     * @param x
     * @param y
     */
    public void checkClickedBox(int x, int y){
        tries += 1;
        boolean noBoom= revealBox(x,y,false);
        if (noBoom) {
            if(revealedTiles >= totalTiles-scenario.getMines()){
                hasEnded=true;
                wasWon=true;
            }
        }else{
            hasEnded=true;
            wasWon = false;
        }
    }

    private boolean revealBox(int x, int y, boolean safe){
        if (x<0 || x>boardSize-1) return true;
        if (y<0 || y>boardSize-1) return true;
        if (!safe && revealedBoard[x][y]=='F') return true;  // the box is not revealed as it is flagged
        if (revealedBoard[x][y]!='\u0000' && revealedBoard[x][y]!='F')  return true;

        if (mineBoard[x][y]==true) {
            if (!safe){

                revealedBoard[x][y]='M';
            }
            return false;
        }
        int minesAround=calcMinesAround(x,y);
        if (revealedBoard[x][y]=='F') flagsPlaced-=1;
        if (minesAround==0) {
            revealedBoard[x][y]='E';
            for (int i = x - 1; i < x + 2; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    if (i == x && j == y) continue;
                    this.revealBox(i, j,true);
                }
            }
        }else{
            revealedBoard[x][y]= (char) (minesAround+'0');
        }
        revealedTiles+=1;
        return true;
    }


    private void endgameReveal(){
        int x,y;
        for (Mine m :mines){
            x=m.x;
            y=m.y;
            revealedBoard[x][y]='M';
        }
    }

    /**
     * Marks the box with coordinates (x,y) we selected with left-click as flagged if there are any flags left or unmarks it
     * if it was flagged
     * @param x
     * @param y
     */
    public void mark(int x, int y){
        if (x<0 || x>boardSize-1) return;
        if (y<0 || y>boardSize-1) return;
        if (revealedBoard[x][y]=='F') {
            revealedBoard[x][y] = '\u0000';
            flagsPlaced-=1;
            return;
        }
        flagsLeft= mines.length-flagsPlaced;
        boolean removeOnly= flagsLeft <= 0;
        if (revealedBoard[x][y]!='\u0000' || removeOnly) return;
        revealedBoard[x][y]='F';
        flagsPlaced+=1;
        if (superMine !=null) {
            if (x == superMine.x && y == superMine.y && tries <= 4) {
                flagsPlaced-=1;
                revealedBoard[x][y] = 'M';
                for (int i = 0; i < boardSize; i++) {
                    if (i!=x) superReveal(i, y);
                    if (i!=y) superReveal(x, i);
                }
            }
        }


        flagsLeft= mines.length-flagsPlaced;
    }

    private void superReveal(int x, int y) {
        if (mineBoard[x][y]==true ){
            revealedBoard[x][y]='M'; // we denote mines revealed by the super mine as neutralized
            return;
        }
        if (revealedBoard[x][y]=='\u0000'){
            int minesAround=calcMinesAround(x,y);
            if (minesAround==0) {
                revealedBoard[x][y]='E';
            }else{
                revealedBoard[x][y]= (char) (minesAround+'0');
            }
            revealedTiles+=1;
        }
    }

    private int calcMinesAround (int x, int y) {
        int minesAround=0;
        for (int i=x-1;i<x+2;i++){
            for (int j=y-1;j<y+2;j++){
                if(i==x && j==y) continue;
                if(i>-1 && i<boardSize-1 && j>-1 && j< boardSize-1){
                    if (mineBoard[i][j]==true) minesAround+=1;
                }
            }
        }
        return minesAround;
    }

    /**
     * Returns the tries of the player so far
     * @return
     */
    public int getTries(){
        return tries;
    }

    /**
     * Returnes the size of the board of the current game
     * @return
     */
    int getBoardSize(){
        return boardSize;
    }

    /**
     * Determines the end of the game, updates wasWon variable according to the game results, saves the remaining seconds
     * before the game was over and reveals the positions of all the mines with the private method endGameReveal()
     * @param win
     * @param remSec
     */
    void endGame(boolean win, int remSec){
        wasWon = win;
        hasEnded=true;
        remainingSeconds = remSec;
        endgameReveal();
    }

    /**
     * Returns the status of the game according to the values of the game state variables hasEnded,wasWon
     * @return
     */
    String getStatus(){
        if (hasEnded){
            if (wasWon) return "win";
            else return "loss";
        }
        else return "running";
    }

    /**
     * Return whether a specified (x,y) board tile is revealed, empty or a mine
     * @param x
     * @param y
     * @return
     */
    char getRevealedBoardAt(int x, int y) {
        return revealedBoard[x][y];
    }
}

class Mine{
    int x;
    int y;
    boolean isSuper;
    Mine (int tableSize){
        Random gen = new Random();
        x=gen.nextInt(0, tableSize);
        y=gen.nextInt(0, tableSize);
        isSuper =false;
    }
}
