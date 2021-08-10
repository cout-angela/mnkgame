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

import java.util.List;
import java.util.ArrayList;
/**
 * Totally random software player.
 */
public class AlphaBetaPlayer  implements MNKPlayer {
	private int TIMEOUT;
	private MNKBoard B;
	private int m, n, k;
	private Tree gameTree;
	private boolean myNode;


	public class Node{
		private MNKBoard board;
		private Node parent;
		private List<Node> children;
		private float eval;

		//COSTRUTTORE
		public Node(MNKBoard b, Node p){
			this.board = b;
			this.parent = p;
			this.children = new ArrayList<Node>();
		}

		//SETTERS
		public void setEval(float e){
			this.eval = e;
		}

		//GETTERS
		public float getEval(){
			return this.eval;
		}
		public List<Node> getChildren(){
			return this.children;
		}
		//ritorna l'ultima cella marcata sulla board del nodo chiamante
		public MNKCell getLastMarked(){
			MNKCell[] history = this.board.getMarkedCells();
			return history[history.length-1];
		}
		//imposta il nodo con un certo eval come radice
		public Node getChildByEval(float e){
			for(Node child : this.children){
				if(child.getEval() == e){
					return child;
				}
			}
			//nel caso in cui ci sia un errore e non trovi il figlio con eval = e allora ritorna il primo figlio
			return this.children.get(0);
		}
		//UTILS
		//fa una copia della board in input e la ritorna
		public MNKBoard copyBoard(MNKBoard b){
			MNKBoard tmp = new MNKBoard(m, n, k);
			MNKCell[] history = b.getMarkedCells();
			for(MNKCell cell : history){
				tmp.markCell(cell.i, cell.j);
			}
			return(tmp);
		}
		//ritorna true se il nodo è una foglia
		public boolean butIsItLeaf(){
			boolean iris = false;
			if(this.children.size() < 1){
				iris = true;
			}
			return iris;
		}
		//da un valore float in base allo stato della board del nodo chiamante
		public float evalNode(boolean mynode){
			MNKGameState tmp = this.board.gameState();
			if(tmp == MNKGameState.OPEN){
				this.eval = 0;
				return(0);
			} else if(tmp == MNKGameState.DRAW){
				this.eval = 5;
				return(5);
			} else if(tmp == MNKGameState.WINP1){
				//if(mynode == true){
					this.eval = 10;
					return(10);
				//} else {
				//	this.eval = -10;
				//	return(-10);
				//}
			} else {
				//if(mynode == false){
					this.eval = -10;
					return(-10);
				//} else {
				//	this.eval = -10;
				//	return(-10);
				//}
			}
		}

		//ADDERS
		//aggiunge un solo figlio
		public void addChild(MNKBoard b){
			this.children.add(new Node(b, this));
		}
		//genera i figli di un nodo in base alle posizioni vuote della sua board
		public void generateChildren(Integer h){
			if(h > 0){
				for(int i = 0; i < m; i++){
					for(int j = 0; j < n; j++){
						if(this.board.gameState() == MNKGameState.OPEN && this.board.cellState(i, j) == MNKCellState.FREE){
							MNKBoard tmp = new MNKBoard(m, n, k);
							tmp = this.copyBoard(this.board);
							tmp.markCell(i, j);

							this.addChild(tmp);
						}
					}
				}
				for(Node child : this.children){
					child.generateChildren(h - 1);

				}
			}
		}

		//PRINTERS
		//printa la cella ij della board del nodo chiamante
		public void printCell(int i, int j){
			if(this.board.cellState(i, j) == MNKCellState.FREE){
				System.out.print("[ ]");
			}else
			if(this.board.cellState(i, j) == MNKCellState.P1){
				System.out.print("[X]");
			}else
			if(this.board.cellState(i, j) == MNKCellState.P2){
				System.out.print("[O]");
			}else{
				System.out.print("[?]");
			}
		}
		//printa la board del nodo chiamante
		public void printBoard(){
			//BAKINO DA FARE (I LOB U)
			for(int i = 0; i < m; i++){
				if(i%3 == 0){
					System.out.println("");
				}
				for(int j = 0; j < n; j++){
					if(j%3 == 0){
						System.out.println("");
					}
					if(this.board.cellState(i, j) == MNKCellState.FREE){
						System.out.print("[ ]");
					}else
					if(this.board.cellState(i, j) == MNKCellState.P1){
						System.out.print("[X]");
					}else
					if(this.board.cellState(i, j) == MNKCellState.P2){
						System.out.print("[O]");
					}else{
						System.out.print("[?]");
					}
				}
			}
		}
		//printa tutto il sotto albero che ha come radice il nodo chiamante
		public void printNodes(){
			this.printBoard();
			System.out.println("Eval della board sopra" + this.eval);
			for(Node child : this.children){
				child.printNodes();
			}
		}
	}

	//minimo
	public static float minimum(float a, float b){
		if(a > b){
			return b;
		} else {
			return a;
		}
	}
	//massimo
	public static float maximum(float a, float b){
		if(a < b){
			return b;
		} else {
			return a;
		}
	}

	public static float alphabeta(Node N, boolean mynode, int depth, float alpha, float beta){

		if(depth == 0 || N.butIsItLeaf()){
			return N.evalNode(mynode);
		} else if (mynode == true){
			N.setEval(1000);
			for(Node c : N.getChildren()){
				N.setEval(minimum(N.getEval(), alphabeta(c, false, depth-1, alpha, beta)));
				beta = minimum(N.getEval(), beta);
				if(beta <= alpha){
					break;
				}
			}
			return N.getEval();
		} else {
			N.setEval(-1000);
			for(Node c : N.getChildren()){
				N.setEval(maximum(N.getEval(), alphabeta(c, true, depth-1, alpha, beta)));
				alpha = maximum(N.getEval(), alpha);
				if(beta <= alpha){
					break;
				}
			}
			return N.getEval();
		}
	}

	public class Tree{
		private Node root;
		//COSTRUTTORE
		public Tree(MNKBoard b){
			root = new Node(b, null);
		}
		//GETTERS
		public Node getRoot(){
			return this.root;
		}
		//SETTERS
		public void setRoot(Node n){
			this.root = n;
		}
		//UTIL
		//imposta come rdice il primo nodo figlio con un certo eval
		public void advanceTree(float e){
			this.root = this.root.getChildByEval(e);
		}
		//printa tutto l'albero
		public void printTree(){
			root.printNodes();
		}
		//fa crescere l'albero di h livelli(?)
		public void growTree(Integer h){
			root.generateChildren(h);
		}
	}





	public AlphaBetaPlayer() {
		//Costruttore vuoto
	}





	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {

		TIMEOUT = timeout_in_secs;
		B       = new MNKBoard(M,N,K);
		m = M;
		n = N;
		k = K;
		myNode = first;

	}





	public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {

		//teniamo aggiornata la nostra board
		if(MC.length > 0) {
			MNKCell lastMarked = MC[MC.length-1];
			B.markCell(lastMarked.i,lastMarked.j);
		}

		//creiamo l'albero da zero
		gameTree = new Tree(B);
		gameTree.growTree(9);
		float follow = alphabeta(gameTree.getRoot(), !this.myNode, 9, -1000, 1000);
		//gameTree.printTree();
		gameTree.advanceTree(follow);
		//prendiamo la cella da marcare, aggiorniamo la board locale e la mandiamo
		

		//gameTree.getRoot().printBoard();
		MNKCell ourmove = gameTree.getRoot().getLastMarked();
		System.out.println("");
		System.out.println(follow);
		gameTree.getRoot().printBoard();
		B.markCell(ourmove.i, ourmove.j);
		return(ourmove);






		//nel caso in cui le cose non vadano bene e del codice salti or something ritorniamo la prima cella della FC come cella da marcare
		//System.out.println("Non è andata bbene");
		//return FC[0];
	}






	public String playerName() {
		return "ab_bakiniStyle";
	}
}
