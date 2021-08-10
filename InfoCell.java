package mnkgame;

public class InfoCell{

    //distances to next symbol in each direction; 0 if no symbols are present at k distance, negative as distance for enemy symbols, positive as distance for friendly symbol
    public int distanceUp;
    public int distanceDown;
    public int distanceLeft;
    public int distanceRight;
    public int distanceTopRight;
    public int distanceBottomRight;
    public int distanceTopLeft;
    public int distanceBottomLeft;

    public int i;
    public int j;
    
    public int help; // -10 se simbolo amico; -20 se simbolo nemico;

    public InfoCell next; //punta alla prossima semiBoard dell'albero alphabeta

    public InfoCell(int i, int j){  //--------TOGLIAMO I E J SE NON SERVONO PIù AVANTI
        this.distanceUp = 0;
        this.distanceDown = 0;
        this.distanceLeft = 0;
        this.distanceRight = 0;
        this.distanceTopRight = 0;
        this.distanceBottomRight = 0;
        this.distanceTopLeft = 0;
        this.distanceBottomLeft = 0;

        this.i = i;
        this.j = j;

        this.help = 0;
    }
}