package mnkgame;

import java.util.LinkedList;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

public class HelpSquarePlayer {

	private boolean first;
	private int m, n, k;

	public class HelpBoard {
		public int[][] hb;
		public boolean isFirst;
		public int[] bestMove;// bestMove[0] = i della casella migliore, bestMove[1] = j della casella
								// migliore;
		public boolean[][] hm;// i = 0 is friendly, i = 1 is enemy; [0...k-1] j = rows, [k...2k-1] j = cols,
		// [2k] j = diagonal, [2k+1] j = adiagonal;
		public MNKGameState stat;
		public int FCcount; // number of free cells

		// costruttore
		public HelpBoard(int k, boolean isFirst) {
			this.hb = new int[k][k];
			this.bestMove = new int[2];
			this.hm = new boolean[2][2 * k + 2];
			this.stat = MNKGameState.OPEN;
			this.isFirst = isFirst;
			this.FCcount = k * k;
		}

		// util
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

		// ritorna una copia della bestMove del chiamante
		public int[] copyBestMove() {
			int[] tmp = new int[2];
			tmp[0] = this.bestMove[0];
			tmp[1] = this.bestMove[1];

			return (tmp);
		}

		// init helpfullness values
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

		public void initBestMove() {
			this.bestMove[0] = (k - 1) / 2 + 1;
			this.bestMove[1] = this.bestMove[0];
		}

		// scegliere mossa migliore
		private int oneWinCol(int col) {
			int i = 0;
			int spaces = 0;
			int move = -1;
			while (i < k && spaces < 2) {
				if (this.hb[i][col] >= 0) {
					spaces = spaces + 1;
					move = i;
				}
				i = i + 1;
			}
			if (spaces > 2) {
				move = -1;
			}
			return move;
		}

		private int oneWinRow(int row) {
			int j = 0;
			int spaces = 0;
			int move = -1;
			while (j < k && spaces < 2) {
				if (this.hb[row][j] >= 0) {
					spaces = spaces + 1;
					move = j;
				}
				j = j + 1;
			}
			if (spaces > 2) {
				move = -1;
			}
			return move;
		}

		private int oneWinDiag() {
			int i = 0;
			int spaces = 0;
			int move = -1;
			while (i < k && spaces < 2) {
				if (this.hb[i][i] >= 0) {
					spaces = spaces + 1;
					move = i;
				}
				i = i + 1;
			}
			if (spaces > 2) {
				move = -1;
			}
			return move;
		}

		private int oneWinAdiag() {
			int i = 0;
			int spaces = 0;
			int move = -1;
			while (i < k && spaces < 2) {
				if (this.hb[k - 1 - i][i] >= 0) {
					spaces = spaces + 1;
					move = i;
				}
				i = i + 1;
			}
			if (spaces > 2) {
				move = -1;
			}
			return move;
		}

		public MNKCell calc_best_cell() {
			int i = 0;
			int row = -1, col = -1;
			MNKCell chosen;
			int caller = this.isFirst ? 0 : 1;
			int nonCaller = (caller + 1) % 2;

			// one cell win for US
			while (i < (2 * k + 2) && (row == -1 || col == -1)) {
				int index = 0;
				if (this.hm[caller][i] == true && this.hm[nonCaller][i] == false) {
					if (i < k) {
						row = this.oneWinRow(i);
						col = i;
					} else if (i < 2 * k) {
						row = i - (k - 1);
						col = this.oneWinCol(i - (k - 1));
					} else if (i == 2 * k) {
						row = this.oneWinDiag();
						col = row;
					} else {
						col = this.oneWinAdiag();
						row = k - 1 - col;
					}
				}
				i = i + 1;
			}
			if (row != -1 && col != -1) {
				chosen = new MNKCell(row, col, MNKCellState.FREE);
				this.stat = (first) ? MNKGameState.WINP1 : MNKGameState.WINP2;
				return chosen;
			}

			// check one move win for ENEMY
			i = 0;
			while (i < (2 * k + 2) && (row == -1 || col == -1)) {
				int index = 0;
				if (this.hm[nonCaller][i] == true && this.hm[caller][i] == false) {
					if (i < k) {
						row = this.oneWinRow(i);
						col = i;
					} else if (i < 2 * k) {
						row = i - (k - 1);
						col = this.oneWinCol(i - (k - 1));
					} else if (i == 2 * k) {
						row = this.oneWinDiag();
						col = row;
					} else {
						col = this.oneWinAdiag();
						row = k - 1 - col;
					}
				}
				i = i + 1;
			}
			if (row == -1 || col == -1) {

				row = this.bestMove[0];
				col = this.bestMove[1];
			}

			this.updateSquaredBoard(row, col);
			chosen = new MNKCell(row, col, MNKCellState.FREE);

			return chosen;
		}

		// aggiorna la board in base a quello

		public void updateSquaredBoard(int row, int col) {

			int caller = this.isFirst ? 0 : 1;
			int nonCaller = (caller + 1) % 2;

			int enemyMark = (nonCaller + 1) * -10;
			int enemySymbols = 0;

			boolean space = false;

			// mark the board (potrebbe non funzionare)
			this.hb[row][col] = (caller + 1) * -10;
			this.FCcount = this.FCcount - 1;

			// aggiustiamo horizontal
			if (this.hm[caller][row] && !(this.hm[nonCaller][row])) {

				for (int j = 0; j < k; j++) {
					if (this.hb[row][j] >= 0) {
						this.hb[row][j] = this.hb[row][j] + 1;
						space = true;
					}
				}
				if (!space) {
					this.stat = (this.isFirst) ? MNKGameState.WINP1 : MNKGameState.WINP2;
					return;
				}
			} else if (this.hm[nonCaller][row] && !(this.hm[caller][row])) {
				enemySymbols = 0; // azzeriamo enemySymbols

				for (int j = 0; j < k; j++) { // contiamo quanti simboli nemici ci sono
					if (this.hb[row][j] == enemyMark) {
						enemySymbols = enemySymbols + 1;
					}
				}
				enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

				for (int j = 0; j < k; j++) { // applichiamo la sottrazione
					if (this.hb[row][j] >= 0) {
						this.hb[row][j] = this.hb[row][j] - enemySymbols;
					}
				}
			}
			this.hm[caller][row] = true;

			// aggiustiamo vertical
			space = false;
			int column = k + col;
			if (this.hm[caller][column] && !(this.hm[nonCaller][column])) {
				for (int i = 0; i < k; i++) {
					if (this.hb[i][col] >= 0) {
						this.hb[i][col] = this.hb[i][col] + 1;
						space = true;
					}
				}
				if (!space) {
					this.stat = (this.isFirst) ? MNKGameState.WINP1 : MNKGameState.WINP2;
					return;
				}
			} else if (this.hm[nonCaller][column] && !(this.hm[caller][column])) {
				enemySymbols = 0; // azzeriamo enemySymbols

				for (int i = 0; i < k; i++) { // contiamo quanti simboli nemici ci sono
					if (this.hb[i][col] == enemyMark) {
						enemySymbols = enemySymbols + 1;
					}
				}
				enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

				for (int i = 0; i < k; i++) {
					if (this.hb[i][col] >= 0) {
						this.hb[i][col] = this.hb[i][col] - enemySymbols;
					}
				}
			}
			this.hm[caller][column] = true;

			// aggiustiamo diagonal
			space = false;
			int diagonal = 2 * k;
			if (col == row) {
				if (this.hm[caller][diagonal] && !(this.hm[nonCaller][diagonal])) {
					for (int i = 0; i < k; i++) {
						if (this.hb[i][i] >= 0) {
							this.hb[i][i] = this.hb[i][i] + 1;
							space = true;
						}
					}
					if (!space) {
						this.stat = (this.isFirst) ? MNKGameState.WINP1 : MNKGameState.WINP2;
						return;
					}
				} else if (this.hm[nonCaller][diagonal] && !(this.hm[caller][diagonal])) {
					enemySymbols = 0; // azzeriamo enemySymbols

					for (int i = 0; i < k; i++) { // contiamo quanti simboli nemici ci sono
						if (this.hb[i][i] == enemyMark) {
							enemySymbols = enemySymbols + 1;
						}
					}
					enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

					for (int i = 0; i < k; i++) {
						if (this.hb[i][i] >= 0) {
							this.hb[i][i] = this.hb[i][i] - enemySymbols;
						}
					}
				}
				this.hm[caller][diagonal] = true;
			}

			// aggiustiamo adiagonal
			space = false;
			int adiagonal = 2 * k + 1;
			if (k - 1 - col == row) {
				if (this.hm[caller][adiagonal] && !(this.hm[nonCaller][adiagonal])) {
					for (int i = 0; i < k; i++) {
						if (this.hb[i][k - 1 - i] >= 0) {
							this.hb[i][k - 1 - i] = this.hb[i][k - 1 - i] + 1;
							space = true;
						}
					}
					if (!space) {
						this.stat = (this.isFirst) ? MNKGameState.WINP1 : MNKGameState.WINP2;
						return;
					}
				} else if (this.hm[nonCaller][adiagonal] && !(this.hm[caller][adiagonal])) {
					enemySymbols = 0; // azzeriamo enemySymbols

					for (int i = 0; i < k; i++) { // contiamo quanti simboli nemici ci sono
						if (this.hb[i][k - 1 - i] == enemyMark) {
							enemySymbols = enemySymbols + 1;
						}
					}
					enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

					for (int i = 0; i < k; i++) {
						if (this.hb[i][k - 1 - i] >= 0) {
							this.hb[i][k - 1 - i] = this.hb[i][k - 1 - i] - enemySymbols;
						}
					}
				}
				this.hm[caller][adiagonal] = true;
			}

			// controllo se e' patta
			int i = 0;
			while (space && i < 2 * k + 2) {
				space = space && this.hm[0][i] && this.hm[1][i];
			}
			if (space) {
				this.stat = MNKGameState.DRAW;
				return;
			}

			// aggiorno best move
			if (this.bestMove[0] == row && this.bestMove[1] == col) { // se la mossa appena fatta e' la bestMove
																		// dobbiamo cercarne una nuova su tutta la
																		// tabella
				this.bestMove[0] = 0;
				this.bestMove[1] = 0;
				for (int i = 0; i < k; i++) {
					for (int j = 0; j < k; j++) {
						if (this.hb[i][j] > this.hb[this.bestMove[0]][this.bestMove[1]]) {
							this.bestMove[0] = i;
							this.bestMove[1] = j;
						}
					}
				}
			} else { // se la mossa fatta non era bestMove allora basta controllare che quella appena
						// marcata non abbia creato una mossa migliore
				// righe
				for (int j = 0; j < k; j++) {
					if (this.hb[row][j] > this.hb[this.bestMove[0]][this.bestMove[1]]) {
						this.bestMove[0] = row;
						this.bestMove[1] = j;
					}
				}
				// colonne
				for (int i = 0; i < k; i++) {
					if (this.hb[i][col] > this.hb[this.bestMove[0]][this.bestMove[1]]) {
						this.bestMove[0] = i;
						this.bestMove[1] = col;
					}
				}
				// diagonale
				if (row == col) {
					for (int i = 0; i < k; i++) {
						if (this.hb[i][i] > this.hb[this.bestMove[0]][this.bestMove[1]]) {
							this.bestMove[0] = i;
							this.bestMove[1] = i;
						}
					}
				}
				// adiagonale
				if (k - 1 - col == row) {
					for (int i = 0; i < k; i++) {
						if (this.hb[k - 1 - i][i] > this.hb[this.bestMove[0]][this.bestMove[1]]) {
							this.bestMove[0] = k - 1 - i;
							this.bestMove[1] = i;
						}
					}
				}
			}
		}

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
		public Node(HelpBoard b, Node p) {
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
		public float evalNode() {
			if (this.board.stat == MNKGameState.OPEN) { // ritornare il valore migliore della miglior cella della
														// helpboard
				this.eval = this.board.hb[this.board.bestMove[0]][this.board.bestMove[1]];
				return (this.eval);
			} else if (this.board.stat == MNKGameState.DRAW) { // ritornare qualcosa
				this.eval = 0;
				return (0);
			} else if (this.board.stat == MNKGameState.WINP1) {
				this.eval = 4 * k;
				return (this.eval);
			} else {
				this.eval = -4 * k;
				return (this.eval);
			}
		}

		// ADDERS
		// aggiunge un solo figlio
		public void addChild(HelpBoard b) {
			this.children.add(new Node(b, this));
		}


		// DA FARE rileggerla per vedere se ha senso e rispiegarla ad angy and self

		// genera i figli di un nodo in base alle migliori mosse secondo paperPlayer
		public void generateChildren(Integer h) {
			if (h > 0 && this.board.stat == MNKGameState.OPEN) {
				HelpBoard calcSon = new HelpBoard(k, !this.board.isFirst);
				calcSon.hb = this.board.copyBoard();
				calcSon.hm = this.board.copyHasMark();
				calcSon.bestMove = this.board.bestMove();

				// un figlio viene creato grazie a calc_best_cell
				MNKCell calcCell = calcSon.calc_best_cell();

				this.addChild(calcSon);

				if (this.board.FCcount > 2) {
					// creiamo gli altri due figli
					int[][] bestMoves = new int[3][2];

					//impostiamo bestMoves come le prime 2 caselle della board
					bestMoves[0][0] = 0;
					bestMoves[1][0] = 0;
					bestMoves[2][0] = this.board.hb[0][0];

					bestMoves[0][1] = 0;
					bestMoves[1][1] = 1;
					bestMoves[2][1] = this.board.hb[0][1];

					for (int i = 0; i < k; i++) {
						for (int j = 0; j < k; j++) {
							if (this.board.hb[i][j] >= 0 && calcCell.i != i && calcCell.j != j && (this.board.hb[i][j] > bestMoves[2][0] || this.board.hb[i][j] > bestMoves[2][1])) {
								if (bestMoves[2][1] > bestMoves[2][0]) {
									bestMoves[0][0] = i;
									bestMoves[1][0] = j;
									bestMoves[2][0] = this.board.hb[i][j];
								} else {
									bestMoves[0][1] = i;
									bestMoves[1][1] = j;
									bestMoves[2][1] = this.board.hb[i][j];
								}
							}
						}
					}

					//aggiungiamo le board come figli
					HelpBoard tmp1 = new HelpBoard(k, !this.board.isFirst);
					tmp1.hb = this.board.copyBoard();
					tmp1.hm = this.board.copyHasMark();
					tmp1.bestMove = this.board.bestMove();

					tmp1.updateSquaredBoard(bestMoves[0][0], bestMoves[1][0]);
					
					this.addChild(calcSon);

					HelpBoard tmp2 = new HelpBoard(k, !this.board.isFirst);
					tmp2.hb = this.board.copyBoard();
					tmp2.hm = this.board.copyHasMark();
					tmp2.bestMove = this.board.bestMove();

					tmp2.updateSquaredBoard(bestMoves[0][1], bestMoves[1][1]);
					
					this.addChild(tmp2);

				} else if (this.board.FCcount > 1) {
					// creiamo l'altro figlio
					int[] secondBestMove = new int[3];

					//impostiamo secondBestMove come la prima casella della board
					secondBestMove[0] = 0;
					secondBestMove[1] = 0;
					secondBestMove[2] = this.board.hb[0][0];

					for (int i = 0; i < k; i++) {
						for (int j = 0; j < k; j++) {
							if (this.board.hb[i][j] >= 0 && calcCell.i != i && calcCell.j != j && this.board.hb[i][j] > secondBestMove[2]) {
								secondBestMove[0] = i;
								secondBestMove[1] = j;
								secondBestMove[2] = this.board.hb[i][j];
							}
						}
					}

					HelpBoard tmp = new HelpBoard(k, !this.board.isFirst);
					tmp.hb = this.board.copyBoard();
					tmp.hm = this.board.copyHasMark();
					tmp.bestMove = this.board.bestMove();

					tmp.updateSquaredBoard(secondBestMove[0], secondBestMove[1]);
					
					this.addChild(tmp);
				}

				// chiamata ricorsiva per andare fino a profondita' h
				for (Node child : this.children) {
					child.generateChildren(h - 1);

				}
			}
		}

	}

	public static float alphabeta(Node N, boolean mynode, int depth, float alpha, float beta) {

		if (depth == 0 || N.butIsItLeaf()) {
			return N.evalNode();
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

		// DA FARE -- da inizializzare l'albero, il primo nodo e della helpboard fare
		// che la best cell è una di quelle centrali
		B = new MNKBoard(M, N, K);
		helpBoard = new int[M][N];

		m = M;
		n = N;
		k = K;

		this.first = first;

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
