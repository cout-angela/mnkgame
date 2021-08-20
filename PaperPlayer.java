/*
 *  Copyright (C) 2021 Pietro Di Lena
 *
 *  This file is part of the MNKGame v2.0 software developed for the
 *  students of the course "Algoritmi e Strutture di Dati" first
 *  cycle degree/bachelor in Computer Science, University of Bologna
 *  A.Y. 2020-2021.
 *
 *  MNKGame is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This  is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this file.  If not, see <https://www.gnu.org/licenses/>.
 */

package mnkgame;

import java.util.LinkedList;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Totally random software player.
 */
public class PaperPlayer implements MNKPlayer {
	private int mode; // 0 -> m = n = k

	private int TIMEOUT;
	private MNKBoard B;
	private int[][] helpBoard;
	boolean[][] hasMark; // i = 0 is friendly, i = 1 is enemy; [0...k-1] j = rows, [k...2k-1] j = cols,
							// [2k] j = diagonal, [2k+1] j = adiagonal;
	private int m, n, k;
	private MNKCellState paperSymbol;
	private MNKCellState enemySymbol;
	private MNKGameState paperWin;
	private MNKGameState enemyWin;

	//////// -- UTIL -- ////////

	public String printSymbol(MNKCellState tmp) {
		String symbolo = " ";
		if (tmp == MNKCellState.P1) {
			symbolo = "p1";
		} else if (tmp == MNKCellState.P2) {
			symbolo = "p2";
		} else if (tmp == MNKCellState.FREE) {
			symbolo = "free";
		} else {
			symbolo = "no";
		}
		return symbolo;
	}

	public void cout(String llllllllllll) {
		System.out.print(llllllllllll);
	}

	public void printHelpBoard() {
		for (int i = 0; i < m; i++) {
			System.out.print("\n");
			for (int j = 0; j < n; j++) {
				System.out.print(helpBoard[i][j] + "\t");
			}
		}
	}

	public void printValues(int[] values, int halfLength) {
		System.out.print("\n");
		for (int i = 0; i < halfLength; i++) {
			System.out.print(values[i] + "\t");
		}
		System.out.print("\n");
	}

	public int max(int a, int b) {
		return ((a < b) ? b : a);
	}

	public int min(int a, int b) {
		return ((a > b) ? b : a);
	}

	//////// -- END UTIL -- ////////

	//////// -- DON'T TOUCH -- ////////

	public PaperPlayer() {
		// Costruttore vuoto
	}

	//////// -- END DON'T TOUCH -- ////////

	//////// -- CANTIERE -- ////////

	public MNKCell calc_best_cell(MNKCell[] FC) {
		int best = -1, i = 0;
		boolean gotBestFromHelpBoard = false;
		int row = -1, col = -1;
		MNKCell chosen;

		// one cell win per Paper
		while (i < FC.length && B.markCell(FC[i].i, FC[i].j) != paperWin) {
			i = i + 1;
			B.unmarkCell();
		}
		if (i < FC.length) {
			B.unmarkCell();
			return FC[i];
		}

		// one cell win per enemy
		i = 1;
		if (FC.length > 1) {
			// Check se la prima cella vuota è una one move win per il nemico
			B.markCell(FC[1].i, FC[1].j);
			if (B.markCell(FC[0].i, FC[0].j) == enemyWin) {
				B.unmarkCell();
				B.unmarkCell();
				best = 0;
			} else {
				B.unmarkCell();
				B.unmarkCell();
			}

			// Check one move win per il resto delle celle
			B.markCell(FC[0].i, FC[0].j);

			while (i < FC.length && B.markCell(FC[i].i, FC[i].j) != enemyWin) {
				i = i + 1;
				B.unmarkCell();
			}
			B.unmarkCell();
			if (i < FC.length) {
				B.unmarkCell();
				best = i;
			}
		}

		// Scegliamo la mossa se non stiamo per perdere
		if (best == -1) {
			for (i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					if (best < helpBoard[i][j]) {
						best = helpBoard[i][j];
						row = i;
						col = j;
					}
				}
			}
			gotBestFromHelpBoard = true;
		}

		// Aggiorniamo la board in base alla mossa da noi scelta
		if (gotBestFromHelpBoard) {
			chosen = new MNKCell(row, col, MNKCellState.FREE);
		} else {
			row = FC[best].i;
			col = FC[best].j;
			chosen = FC[best];
		}

		switch (mode) {
			case 0:
				updateSquaredBoard(row, col, -10);
				break;
			default:
				updateBoard(row, col, -10);
				break;
		}

		return chosen;
	}

	//////// -- END CANTIERE -- ////////

	//////// -- INIZIALIZZAZIONE -- ////////

	public void initSquaredHelpBoard() {
		// imposta i valori di base nella board
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < k; j++) {
				helpBoard[i][j] = 4;
			}
			helpBoard[i][i] = helpBoard[i][i] + 2;
			helpBoard[i][k - 1 - i] = helpBoard[i][i];
		}
		// se la board ha m n k dispari allora il centro prende + 2
		if (k % 2 == 1) {
			helpBoard[k / 2][k / 2] = helpBoard[k / 2 + 1][k / 2 + 1] + 2;
		}
	}

	public void initHasMark() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < hasMark.length; j++) {
				hasMark[i][j] = false;
			}
		}
	}

	// riempie di valori la helpboard assumendo che non ci siano simboli in B
	public void initHelpBoard() {
		int quarter_width = n % 2 == 0 ? n / 2 : (n + 1) / 2;
		int quarter_height = m % 2 == 0 ? m / 2 : (m + 1) / 2;
		// ma se m o n sono dispari la diagonale funzia? e complete_board funzia? Non
		// credo
		// h_window(quarter_width, quarter_height);
		// v_window(quarter_width, quarter_height);
		// diagonal_quarter(quarter_width, quarter_height);

		// zona di prova
		int longest = (m > n) ? m : n;
		int[] values = new int[longest];

		diagonal_quarter(quarter_width, quarter_height, values);
		vertical_quarter(quarter_width, quarter_height, values);
		horizontal_quarter(quarter_width, quarter_height, values);
		complete_board(quarter_width, quarter_height);

	}

	public void horizontal_quarter(int quarter_width, int quarter_height, int[] values) {
		values_generator(values, quarter_width, n);
		for (int i = 0; i < quarter_height; i++) {
			for (int j = 0; j < quarter_width; j++) {
				helpBoard[i][j] = helpBoard[i][j] + values[j];
			}
		}
	}

	public void vertical_quarter(int quarter_width, int quarter_height, int[] values) {
		values_generator(values, quarter_height, m);
		for (int i = 0; i < quarter_height; i++) {
			for (int j = 0; j < quarter_width; j++) {
				helpBoard[i][j] = helpBoard[i][j] + values[i];
			}
		}
	}

	public void diagonal_quarter(int quarter_width, int quarter_height, int[] values) {
		int wall_distance = 0; // the wall_distance is the current distance from the nearest quarter wall
		int diagonal = 0;
		int half_diagonal = 0;
		// *
		for (int i = m - 1; i > 0; i--) { // scorriamo le caselle da (quarter_height 0) a (1 0)

			if (wall_distance + 1 <= quarter_width) {
				wall_distance = wall_distance + 1;
			}
			if (diagonal + 1 <= n) {
				diagonal = diagonal + 1;
				if (diagonal % 2 == 1) {
					half_diagonal = half_diagonal + 1;
				}
			}

			values_generator(values, half_diagonal, diagonal);
			for (int slider = 0; slider < wall_distance; slider++) {

				if (slider < half_diagonal) {
					helpBoard[i + slider][slider] = values[slider];
				} else {
					helpBoard[i + slider][slider] = values[((diagonal - 1) - half_diagonal) - (slider - half_diagonal)];// (diagonal
																														// -
																														// 2)
																														// -
																														// (slider)
				}
			}
		}
		wall_distance = (m == n) ? wall_distance + 1 : wall_distance;
		diagonal = (n < m) ? n : m;
		half_diagonal = (diagonal + diagonal % 2) / 2;
		for (int j = 0; j < quarter_width; j++) { // scorriamo le caselle da (0 0) a (0 quarter_width)
			if (wall_distance > quarter_width - j) {
				wall_distance = wall_distance - 1;
			}

			if (diagonal > n - j) {
				diagonal = diagonal - 1;
				if (diagonal % 2 == 0) {
					half_diagonal = half_diagonal - 1;
				}
			}

			values_generator(values, half_diagonal, diagonal);

			for (int slider = 0; slider < wall_distance; slider++) {
				if (slider < half_diagonal) {
					helpBoard[slider][j + slider] = values[slider];
				} else {
					helpBoard[slider][j + slider] = values[((diagonal - 1) - half_diagonal) - (slider - half_diagonal)];
				}
			}
		}
		for (int i = 0; i < quarter_height; i++) {
			for (int j = 0; j < quarter_width; j++) {
				helpBoard[i][j] = helpBoard[i][j] + helpBoard[m - 1 - i][j];
			}
		}
	}

	// given the values array, values_generator sets growing values into it capping
	// at k or k-length (if values is very short) and only filling values up to
	// halfLength
	public void values_generator(int[] values, int halfLength, int length) {
		if (length < k) {
			for (int i = 0; i < halfLength; i++) {
				values[i] = 0;
			}
		} else if (length == k) {
			for (int i = 0; i < halfLength; i++) {
				values[i] = 2;
			}
		} else {

			int inc = 1;
			int cap = (length < (2 * k)) ? (length % k) : (k - 1);

			for (int i = 0; i < halfLength; i++) {
				values[i] = inc * 2;
				if (inc <= cap) {
					inc = inc + 1;
				}
			}
		}
	}

	public void complete_board(int quarter_width, int quarter_height) {

		int copy_height = (m % 2 == 0) ? quarter_height : quarter_height - 1;
		int copy_width = (n % 2 == 0) ? quarter_width : quarter_width - 1;

		for (int i = 0; i <= copy_height; i++) {
			for (int j = 0; j <= quarter_width; j++) {
				helpBoard[m - 1 - i][j] = helpBoard[i][j];
			}
		}

		for (int i = 0; i < m; i++) {
			for (int j = 0; j <= copy_width; j++) {
				helpBoard[i][n - 1 - j] = helpBoard[i][j];
			}
		}
	}

	//////// -- END INIZIALIZZAZIONE -- ////////

	//////// -- AGGIORNAMENTO -- ////////

	public void updateSquaredBoard(int row, int col, int mark) {
		int friendly = (mark == -10) ? 0 : 1;
		int enemy = (friendly + 1) % 2;
		int enemyMark = (mark == -10) ? -20 : -10;
		int enemySymbols = 0;

		// aggiustiamo horizontal
		if (hasMark[friendly][row] && !(hasMark[enemy][row])) {
			for (int j = 0; j < k; j++) {
				if (helpBoard[row][j] >= 0) {
					helpBoard[row][j] = helpBoard[row][j] + 1;
				}
			}
		} else if (hasMark[enemy][row] && !(hasMark[friendly][row])) {
			enemySymbols = 0; // azzeriamo enemySymbols

			for (int j = 0; j < k; j++) { // contiamo quanti simboli nemici ci sono
				if (helpBoard[row][j] == enemyMark) {
					enemySymbols = enemySymbols + 1;
				}
			}
			enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

			for (int j = 0; j < k; j++) { // applichiamo la sottrazione
				if (helpBoard[row][j] >= 0) {
					helpBoard[row][j] = helpBoard[row][j] - enemySymbols;
				}
			}
		}
		hasMark[friendly][row] = true;

		// aggiustiamo vertical
		int column = k + col;
		if (hasMark[friendly][column] && !(hasMark[enemy][column])) {
			for (int i = 0; i < k; i++) {
				if (helpBoard[i][col] >= 0) {
					helpBoard[i][col] = helpBoard[i][col] + 1;
				}
			}
		} else if (hasMark[enemy][column] && !(hasMark[friendly][column])) {
			enemySymbols = 0; // azzeriamo enemySymbols

			for (int i = 0; i < k; i++) { // contiamo quanti simboli nemici ci sono
				if (helpBoard[i][col] == enemyMark) {
					enemySymbols = enemySymbols + 1;
				}
			}
			enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

			for (int i = 0; i < k; i++) {
				if (helpBoard[i][col] >= 0) {
					helpBoard[i][col] = helpBoard[i][col] - enemySymbols;
				}
			}
		}
		hasMark[friendly][column] = true;

		// aggiustiamo diagonal
		int diagonal = 2 * k;
		if (col == row) {
			if (hasMark[friendly][diagonal] && !(hasMark[enemy][diagonal])) {
				for (int i = 0; i < k; i++) {
					if (helpBoard[i][i] >= 0) {
						helpBoard[i][i] = helpBoard[i][i] + 1;
					}
				}
			} else if (hasMark[enemy][diagonal] && !(hasMark[friendly][diagonal])) {
				enemySymbols = 0; // azzeriamo enemySymbols

				for (int i = 0; i < k; i++) { // contiamo quanti simboli nemici ci sono
					if (helpBoard[i][i] == enemyMark) {
						enemySymbols = enemySymbols + 1;
					}
				}
				enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

				for (int i = 0; i < k; i++) {
					if (helpBoard[i][i] >= 0) {
						helpBoard[i][i] = helpBoard[i][i] - enemySymbols;
					}
				}
			}
			hasMark[friendly][diagonal] = true;
		}

		// aggiustiamo adiagonal
		int adiagonal = 2 * k + 1;
		if (k - 1 - col == row) {
			if (hasMark[friendly][adiagonal] && !(hasMark[enemy][adiagonal])) {
				for (int i = 0; i < k; i++) {
					if (helpBoard[i][k - 1 - i] >= 0) {
						helpBoard[i][k - 1 - i] = helpBoard[i][k - 1 - i] + 1;
					}
				}
			} else if (hasMark[enemy][adiagonal] && !(hasMark[friendly][adiagonal])) {
				enemySymbols = 0; // azzeriamo enemySymbols

				for (int i = 0; i < k; i++) { // contiamo quanti simboli nemici ci sono
					if (helpBoard[i][k - 1 - i] == enemyMark) {
						enemySymbols = enemySymbols + 1;
					}
				}
				enemySymbols = enemySymbols + 1; // togliamo 1 in più per window non più vincibile

				for (int i = 0; i < k; i++) {
					if (helpBoard[i][k - 1 - i] >= 0) {
						helpBoard[i][k - 1 - i] = helpBoard[i][k - 1 - i] - enemySymbols;
					}
				}
			}
			hasMark[friendly][adiagonal] = true;
		}

		helpBoard[row][col] = mark;
	}

	public void updateBoard(int row, int col, int mark) {
		int enemyMark = (mark == -20) ? -10 : -20;
		helpBoard[row][col] = mark;
		helpBoard[row][col + 1] = mark;
		helpBoard[row - 1][col] = enemyMark;
		LinkedList<MNKCell> updateList = new LinkedList<MNKCell>();
		LinkedList<MNKCell> updateEnemyList = new LinkedList<MNKCell>();
		int fullWindow = 0;
		int w = 0, wenemy = 0;
		int i = 0;
		int j = 0;

		int iStart = max(0, row - (k - 1));
		int jStart = max(0, col - (k - 1));
		int iEnd = min(m - 1, row + (k - 1));
		int jEnd = min(n - 1, col + (k - 1));

		// horizontal points adjustment for friendly mark
		j = col + 1;
		w = col - 1;
		while (j <= jEnd && helpBoard[row][j] != enemyMark) {
			j = j + 1;
		}
		j = j - 1;
		while (w >= jStart && helpBoard[row][w] != enemyMark) {
			w = w - 1;
		}
		w = w + 1;
		if (j - w >= (k - 1)) {
			while (w <= j) {
				if (helpBoard[row][w] >= 0) {
					helpBoard[row][w] = helpBoard[row][w] + 1;
				}
				w = w + 1;
			}
		}

		// horizontal points adjustment for enemy mark
		j = col + 1;
		w = col - 1;
		while (j <= jEnd && helpBoard[row][j] != mark) {
			j = j + 1;
		}
		j = j - 1;
		while (w >= jStart && helpBoard[row][w] != mark) {
			w = w - 1;
		}
		w = w + 1;

		fullWindow = j - w;
		if (fullWindow >= k - 1) {
			int maxMeno = min(fullWindow - (k - 1), k);
			i = 1;
			while (w < j) {
				if (helpBoard[row][w] >= 0) {
					helpBoard[row][w] = helpBoard[row][w] - i;

				}
				if (helpBoard[row][j] >= 0) {
					helpBoard[row][j] = helpBoard[row][j] - i;

				}
				w = w + 1;
				j = j - 1;
				if (i < maxMeno) {
					i = i + 1;
				}

			}
			if (w == j && helpBoard[row][w] >= 0) {
				helpBoard[row][w] = helpBoard[row][w] - i;

			}
		}

		// vertical points adjustment for friendly mark
		i = row + 1;
		w = row - 1;
		while (i <= iEnd && helpBoard[i][col] != enemyMark) {
			i = i + 1;
		}
		i = i - 1;
		while (w >= iStart && helpBoard[w][col] != enemyMark) {
			w = w - 1;
		}
		w = w + 1;
		if (i - w >= (k - 1)) {
			while (w <= i) {
				if (helpBoard[w][col] >= 0) {
					helpBoard[w][col] = helpBoard[w][col] + 1;
				}
				w = w + 1;
			}
		}

		// vertical points adjustment for enemy mark
		i = row + 1;
		w = row - 1;
		while (i <= iEnd && helpBoard[i][col] != mark) {
			i = i + 1;
		}
		i = i - 1;
		while (w >= iStart && helpBoard[w][col] != mark) {
			w = w - 1;
		}
		w = w + 1;
		fullWindow = i - w;
		if (fullWindow >= k - 1) {
			int maxMeno = min(fullWindow - (k - 1), k);
			j = 1;
			while (w < i) {
				if (helpBoard[w][col] >= 0) {
					helpBoard[w][col] = helpBoard[w][col] - j;

				}
				if (helpBoard[i][col] >= 0) {
					helpBoard[i][col] = helpBoard[i][col] - j;

				}
				w = w + 1;
				i = i - 1;
				if (j < maxMeno) {
					j = j + 1;
				}

			}
			if (w == i && helpBoard[w][col] >= 0) {
				helpBoard[w][col] = helpBoard[w][col] - j;

			}
		}
	}
	/*
	 * 
	 * 
	 * MNKCell tmp = new MNKCell(row, j, MNKCellState.P1); if (value != mark){
	 * updateList.add(tmp); updateEnemyList.add(tmp); }else{ if(j != col) {
	 * updateEnemyList.clear(); wenemy = j + 1; } }
	 * 
	 * 
	 * 
	 */

	//////// -- END AGGIORNAMENTO -- ////////

	//////// -- NODE, TREE E ALPHABETA -- ////////

	public class Node {
		public int[][] board;
		public boolean[][] hasMark; // i = 0 is friendly, i = 1 is enemy; [0...k-1] j = rows, [k...2k-1] j = cols,
							// [2k] j = diagonal, [2k+1] j = adiagonal;
		private Node parent;
		private List<Node> children;
		private float eval;

		// COSTRUTTORE
		public Node(int[][] b, boolean[][] hM, Node p) {
			this.board = b;
			this.hasMark = hM;
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
		// fa una copia della board in input e la ritorna
		public int[][] copyBoard(int[][] b) {
			int[][] tmp = new int[m][n];
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					tmp[i][j] = b[i][j];
				}
			}
			
			return (tmp);
		}

		// fa una copia di hasMark che riceve in input e la ritorna
		public boolean[][] copyHasMark(boolean[][] hM) {
			boolean[][] tmp = new boolean[m][n];
			for (int j = 0; j < (2*k + 2); j++) {
				tmp[0][j] = b[0][j];
				tmp[1][j] = b[1][j];
			}
			
			return (tmp);
		}

		public boolean isGameOpen(int[][] b){	//////// DA FARE USANDO HAS MARK PER VEDERE SE LA PARTITA è VINTA PERSA PATTA O APERTA

		}

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
			MNKGameState tmp = this.board.gameState();
			if (tmp == MNKGameState.OPEN) {
				this.eval = 0;
				return (0);
			} else if (tmp == MNKGameState.DRAW) {
				this.eval = 5;
				return (5);
			} else if (tmp == MNKGameState.WINP1) {
				// if(mynode == true){
				this.eval = 10;
				return (10);
				// } else {
				// this.eval = -10;
				// return(-10);
				// }
			} else {
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
			if (h > 0 && isGameOpen(this.board)) {
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

		// PRINTERS
		// printa la cella ij della board del nodo chiamante
		public void printCell(int i, int j) {
			if (this.board.cellState(i, j) == MNKCellState.FREE) {
				System.out.print("[ ]");
			} else if (this.board.cellState(i, j) == MNKCellState.P1) {
				System.out.print("[X]");
			} else if (this.board.cellState(i, j) == MNKCellState.P2) {
				System.out.print("[O]");
			} else {
				System.out.print("[?]");
			}
		}

		// printa la board del nodo chiamante
		public void printBoard() {
			// BAKINO DA FARE (I LOB U)
			for (int i = 0; i < m; i++) {
				if (i % 3 == 0) {
					System.out.println("");
				}
				for (int j = 0; j < n; j++) {
					if (j % 3 == 0) {
						System.out.println("");
					}
					if (this.board.cellState(i, j) == MNKCellState.FREE) {
						System.out.print("[ ]");
					} else if (this.board.cellState(i, j) == MNKCellState.P1) {
						System.out.print("[X]");
					} else if (this.board.cellState(i, j) == MNKCellState.P2) {
						System.out.print("[O]");
					} else {
						System.out.print("[?]");
					}
				}
			}
		}

		// printa tutto il sotto albero che ha come radice il nodo chiamante
		public void printNodes() {
			this.printBoard();
			System.out.println("Eval della board sopra" + this.eval);
			for (Node child : this.children) {
				child.printNodes();
			}
		}
	}

	// minimo
	public static float minimum(float a, float b) {
		if (a > b) {
			return b;
		} else {
			return a;
		}
	}

	// massimo
	public static float maximum(float a, float b) {
		if (a < b) {
			return b;
		} else {
			return a;
		}
	}

	public static float alphabeta(Node N, boolean mynode, int depth, float alpha, float beta) {

		if (depth == 0 || N.butIsItLeaf()) {
			return N.evalNode(mynode);
		} else if (mynode == true) {
			N.setEval(1000);
			for (Node c : N.getChildren()) {
				N.setEval(minimum(N.getEval(), alphabeta(c, false, depth - 1, alpha, beta)));
				beta = minimum(N.getEval(), beta);
				if (beta <= alpha) {
					break;
				}
			}
			return N.getEval();
		} else {
			N.setEval(-1000);
			for (Node c : N.getChildren()) {
				N.setEval(maximum(N.getEval(), alphabeta(c, true, depth - 1, alpha, beta)));
				alpha = maximum(N.getEval(), alpha);
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

		B = new MNKBoard(M, N, K);
		helpBoard = new int[M][N];

		m = M;
		n = N;
		k = K;
		if (first) {
			paperSymbol = MNKCellState.P1;
			paperWin = MNKGameState.WINP1;
			enemySymbol = MNKCellState.P2;
			enemyWin = MNKGameState.WINP2;
		} else {
			paperSymbol = MNKCellState.P2;
			paperWin = MNKGameState.WINP2;
			enemySymbol = MNKCellState.P1;
			enemyWin = MNKGameState.WINP1;
		}

		if (m == n && n == k) {
			mode = 0;
			initSquaredHelpBoard();
			hasMark = new boolean[2][2 * k + 2];
			initHasMark();
		} else {
			initHelpBoard();
		}
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
