
package mnkgame;

import java.util.Random;

public class UltimatePlayer  implements MNKPlayer {
	
    private int m, n, k, first;
    private PointerBoard pboard;

    //Math.min(a, b) to do min and or max

    private class Pcell{
        int value; //valore, utilizzabile per varie cose
        int up, down, left, right, upright, upleft, downright, downleft; //distanza dalla prossima cella marcata: se non ce n'e' = 0, se e' friendly e' positiva, altrimenti negativa
        int i, j;

        public Pcell(int i, int j){
            this.i = i;
            this.j = j;
            this.value = 0;
            this.up = 0;
            this.down = 0;
            this.left = 0;
            this.right = 0;
            this.upright = 0;
            this.upleft = 0;
            this.downright = 0;
            this.downleft = 0;
        }

        public Pcell(int i, int j, int value){
            this.i = i;
            this.j = j;
            this.value = value;
            this.up = 0;
            this.down = 0;
            this.left = 0;
            this.right = 0;
            this.upright = 0;
            this.upleft = 0;
            this.downright = 0;
            this.downleft = 0;
        }

        public Pcell(int i, int j, int value, int up, int down, int left, int right, int upright, int upleft, int downright, int downleft){
            this.i = i;
            this.j = j;
            this.value = value;
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.upright = upright;
            this.upleft = upleft;
            this.downright = downright;
            this.downleft = downleft;
        }
    }

    private enum State{
        WON,
        LOST,
        DRAW,
        OPEN
    }

    private class PointerBoard{
        Pcell[][] board;
        State state;
        Pcell lastMove;
    }

    //empty constructor
	public UltimatePlayer() {

	}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		m = M;
		n = N;
		k = K;
        this.first = first;


	}

	
	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
		
		
	}

	public String playerName() {
		return "boh";
	}
}
