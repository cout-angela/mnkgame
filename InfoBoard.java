package mnkgame;

import java.util.LinkedList;

public class InfoBoard {

    int m;
    int n;

    public enum State{
        WON,
        LOST,
        DRAWN,
        OPEN
    }

    public class BestCell {
        public int i;
        public int j;

        public BestCell(int i, int j){
            this.i = i;
            this.j = j;
        }
    }
    
    public InfoCell[][] cell;

    public int lastMoveI;   //-1 se non ci sono mosse precedenti
    public int lastMoveJ;   //-1 se non ci sono mosse precedenti

    public State state;
    
    public LinkedList<BestCell> bestCells;  //best 3 moves in a simple list
    

    public InfoBoard(int m, int n){
        this.m = m;
        this.n = n;

        this.cell = new InfoCell[m][n];

        this.state = State.OPEN;

        this.lastMoveI = -1;
        this.lastMoveJ = -1;
        

        bestCells = new LinkedList<BestCell>();
    }
}