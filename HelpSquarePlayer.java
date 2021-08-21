package mnkgame;

import java.util.LinkedList;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

public class HelpSquarePlayer {
	
	private int m, n, k;
	private MNKCellState ourSymbol;
	private MNKCellState enemySymbol;
	private MNKGameState ourWin;
	private MNKGameState enemyWin;

    public class HelpBoard{
	    public int[][] hb;
        public boolean[][] hm;// i = 0 is friendly, i = 1 is enemy; [0...k-1] j = rows, [k...2k-1] j = cols,
        // [2k] j = diagonal, [2k+1] j = adiagonal;
		public MNKGameState stat;

		//costruttore
        public HelpBoard (int k){
            this.hb = new int[k][k];
			this.hm = new boolean[2][2*k + 2];
			this.stat = MNKGameState.OPEN;
        }

		//util
		public void printHelpBoard() {
			for (int i = 0; i < m; i++) {
				System.out.print("\n");
				for (int j = 0; j < n; j++) {
					System.out.print(this.hb[i][j] + "\t");
				}
			}
		}

		// ritorna una copia della board del chiamante
		public int[][] copyBoard() {
			int[][] tmp = new int[k][k];
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < k; j++) {
					tmp[i][j] = this.hb[i][j];
				}
			}

			return (tmp);
		}

		// ritorna una copia della hm del chiamante
		public boolean[][] copyHasMark() {
			boolean[][] tmp = new boolean[2][2 * k + 2];
			for (int j = 0; j < (2 * k + 2); j++) {
				tmp[0][j] = this.hm[0][j];
				tmp[1][j] = this.hm[1][j];
			}

			return (tmp);
		}

		//init helpfullness values
		public void initSquaredHelpBoard() {
			// imposta i valori di base nella board
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < k; j++) {
					this.hb[i][j] = 4;
				}
				this.hb[i][i] = this.hb[i][i] + 2;
				this.hb[i][k - 1 - i] = this.hb[i][i];
			}
			// se la board ha m n k dispari allora il centro prende + 2
			if (k % 2 == 1) {
				this.hb[k / 2][k / 2] = this.hb[k / 2 + 1][k / 2 + 1] + 2;
			}
		}

		public void initHasMark() {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < this.hm.length; j++) {
					this.hm[i][j] = false;
				}
			}
		}


		//scegliere mossa migliore
		private int oneWinCol(int col){
			int i = 0;
			int spaces = 0;
			int move = -1;
			while(i < k && spaces < 2){
				if(this.hb[i][col] >= 0){
					spaces = spaces + 1;
					move = i;
				}
				i = i + 1;
			}
			if(spaces > 2){
				move = -1;
			}
			return move;
		}

		private int oneWinRow(int row){
			int j = 0;
			int spaces = 0;
			int move = -1;
			while(j < k && spaces < 2){
				if(this.hb[row][j] >= 0){
					spaces = spaces + 1;
					move = j;
				}
				j = j + 1;
			}
			if(spaces > 2){
				move = -1;
			}
			return move;
		}

		private int oneWinDiag(){
			int i = 0;
			int spaces = 0;
			int move = -1;
			while(i < k && spaces < 2){
				if(this.hb[i][i] >= 0){
					spaces = spaces + 1;
					move = i;
				}
				i = i + 1;
			}
			if(spaces > 2){
				move = -1;
			}
			return move;
		}

		private int oneWinAdiag(){
			int i = 0;
			int spaces = 0;
			int move = -1;
			while(i < k && spaces < 2){
				if(this.hb[k-1-i][i] >= 0){
					spaces = spaces + 1;
					move = i;
				}
				i = i + 1;
			}
			if(spaces > 2){
				move = -1;
			}
			return move;
		}

		public MNKCell calc_best_cell(MNKCell[] FC) {
			int best = -1, i = 0;
			boolean gotBestFromHelpBoard = false;
			int row = -1, col = -1;
			MNKCell chosen;

			// one cell win for US
			while (i < (2*k + 2) && (row == -1 || col == -1)) {
				int index = 0;
				if(this.hm[0][i] == true && this.hm[1][i] == false){
					if(i < k){
						row = this.oneWinRow(i);
						col = i;
					} else if(i < 2 * k){
						row = i - (k - 1);
						col = this.oneWinCol(i - (k - 1));
					} else if(i == 2*k){
						row = this.oneWinDiag();
						col = row;
					} else {
						col = this.oneWinAdiag();
						row = k - 1 - col;
					}
				}
				i = i + 1;
			}
			if(row != -1 && col != -1){
				chosen = new MNKCell(row, col, MNKCellState.FREE);
				return chosen;
			}
				

			// check one move win for ENEMY
			i = 0;
			while (i < (2*k + 2) && (row == -1 || col == -1)) {
				int index = 0;
				if(this.hm[1][i] == true && this.hm[0][i] == false){
					if(i < k){
						row = this.oneWinRow(i);
						col = i;
					} else if(i < 2 * k){
						row = i - (k - 1);
						col = this.oneWinCol(i - (k - 1));
					} else if(i == 2*k){
						row = this.oneWinDiag();
						col = row;
					} else {
						col = this.oneWinAdiag();
						row = k - 1 - col;
					}
				}
				i = i + 1;
			}
			if(row == -1 || col == -1){
				//DA FARE
				// fare struttura dati con migliori punteggi
				//row e col == bestCell dalla struttura
			}

			updateSquaredBoard(row, col, -10);
			chosen = new MNKCell(row, col, MNKCellState.FREE);
		
			return chosen;
		}
		
		//aggiorna la board in base a quello

		// DA FARE updateSquaredBoard && dentro mettere anche l'aggiornamento della miglior cella && tiene sempre lo stato aggiornato



		
    }

	//////// -- UTIL -- ////////

	public void cout(String llllllllllll) {
		System.out.print(llllllllllll);
	}

	//////// -- END UTIL -- ////////

	//////// -- DON'T TOUCH -- ////////

	public HelpSquarePlayer() {
		// Costruttore vuoto
	}

	//////// -- END DON'T TOUCH -- ////////

	//////// -- NODE, TREE E ALPHABETA -- ////////

	public class Node {
		public HelpBoard board;
		private Node parent;
		private List<Node> children;
		private float eval;

		// COSTRUTTORE
		public Node(int[][] b, Node p) {
			this.board = b;
			this.parent = p;
			this.children = new ArrayList<Node>();
		}

		// SETTERS
		public void setEval(float e) {
			this.eval = e;
		}

		// GETTERS
		public float getEval() {
			return this.eval;
		}

		public List<Node> getChildren() {
			return this.children;
		}

		// imposta il nodo con un certo eval come radice
		public Node getChildByEval(float e) {
			for (Node child : this.children) {
				if (child.getEval() == e) {
					return child;
				}
			}
			// nel caso in cui ci sia un errore e non trovi il figlio con eval = e allora
			// ritorna il primo figlio
			return this.children.get(0);
		}

		// UTILS

		// ritorna true se il nodo è una foglia
		public boolean butIsItLeaf() {
			boolean iris = false;
			if (this.children.size() < 1) {
				iris = true;
			}
			return iris;
		}

		// da un valore float in base allo stato della board del nodo chiamante
		public float evalNode(boolean mynode) {
			MNKGameState tmp = this.calcGameState();
			if (tmp == MNKGameState.OPEN) { // ritornare il valore migliore della miglior cella della helpboard
				this.eval = 0;
				return (0);
			} else if (tmp == MNKGameState.DRAW) { // ritornare qualcosa
				this.eval = 5;
				return (5);
			} else if (tmp == MNKGameState.WINP1) { // valore alto(?)
				// if(mynode == true){
				this.eval = 10;
				return (10);
				// } else {
				// this.eval = -10;
				// return(-10);
				// }
			} else { // valore basso(?)
				// if(mynode == false){
				this.eval = -10;
				return (-10);
				// } else {
				// this.eval = -10;
				// return(-10);
				// }
			}
		}

		// ADDERS
		// aggiunge un solo figlio
		public void addChild(int[][] b) {
			this.children.add(new Node(b, this));
		}

		// genera i figli di un nodo in base alle posizioni vuote della sua board
		public void generateChildren(Integer h) {
			if (h > 0 && this.calcGameState() == MNKGameState.OPEN) {
				for (int i = 0; i < m; i++) {
					for (int j = 0; j < n; j++) {
						if (this.board[i][j] >= 0) {
							MNKBoard tmp = new MNKBoard(m, n, k);
							tmp = this.copyBoard(this.board);
							tmp.markCell(i, j);

							this.addChild(tmp);
						}
					}
				}
				for (Node child : this.children) {
					child.generateChildren(h - 1);

				}
			}
		}

	}

	public static float alphabeta(Node N, boolean mynode, int depth, float alpha, float beta) {

		if (depth == 0 || N.butIsItLeaf()) {
			return N.evalNode(mynode);
		} else if (mynode == true) {
			N.setEval(1000);
			for (Node c : N.getChildren()) {
				N.setEval(Util.min(N.getEval(), alphabeta(c, false, depth - 1, alpha, beta)));
				beta = Util.min(N.getEval(), beta);
				if (beta <= alpha) {
					break;
				}
			}
			return N.getEval();
		} else {
			N.setEval(-1000);
			for (Node c : N.getChildren()) {
				N.setEval(Util.max(N.getEval(), alphabeta(c, true, depth - 1, alpha, beta)));
				alpha = Util.max(N.getEval(), alpha);
				if (beta <= alpha) {
					break;
				}
			}
			return N.getEval();
		}
	}

	public class Tree {
		private Node root;

		// COSTRUTTORE
		public Tree(MNKBoard b) {
			root = new Node(b, null);
		}

		// GETTERS
		public Node getRoot() {
			return this.root;
		}

		// SETTERS
		public void setRoot(Node n) {
			this.root = n;
		}

		// UTIL
		// imposta come rdice il primo nodo figlio con un certo eval
		public void advanceTree(float e) {
			this.root = this.root.getChildByEval(e);
		}

		// printa tutto l'albero
		public void printTree() {
			root.printNodes();
		}

		// fa crescere l'albero di h livelli(?)
		public void growTree(Integer h) {
			root.generateChildren(h);
		}
	}

	//////// -- END NODE, TREE E ALPHABETA -- ////////

	//////// -- MAIN -- ////////

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {

		//DA FARE -- da inizializzare l'albero, il primo nodo e della helpboard fare che la best cell è una di quelle centrali
		B = new MNKBoard(M, N, K);
		helpBoard = new int[M][N];

		m = M;
		n = N;
		k = K;
		if (first) {
			ourSymbol = MNKCellState.P1;
			ourWin = MNKGameState.WINP1;
			enemySymbol = MNKCellState.P2;
			enemyWin = MNKGameState.WINP2;
		} else {
			ourSymbol = MNKCellState.P2;
			ourWin = MNKGameState.WINP2;
			enemySymbol = MNKCellState.P1;
			enemyWin = MNKGameState.WINP1;
		}

		
		initSquaredHelpBoard();
		hasMark = new boolean[2][2 * k + 2];
		initHasMark();
	}

	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {

		// NON TOCCARE, INIT
		MNKCell ourMove = FC[0];
		if (MC.length > 0) {
			MNKCell lastMarked = MC[MC.length - 1];
			B.markCell(lastMarked.i, lastMarked.j);
			switch (mode) {
				case 0:
					updateSquaredBoard(lastMarked.i, lastMarked.j, -20);
					break;
				default:
					updateBoard(lastMarked.i, lastMarked.j, -20);
					break;
			}

		}

		// METTI LA MOSSA IN OURMOVE
		cout("\nprima di calc_best_cell\n");
		printHelpBoard();

		ourMove = calc_best_cell(FC);

		cout("\n\ndopo di calc_best_cell\n");
		printHelpBoard();

		// NON TOCCARE
		B.markCell(ourMove.i, ourMove.j);
		return ourMove;
	}

	//////// -- END MAIN -- ////////

	public String playerName() {
		return "papereeeeeeeeeeeeee";
	}

}
