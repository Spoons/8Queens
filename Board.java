/*
 * Written by 
 * Michael A. Ciociola
 * Prof. D. Jugan
 * ITCS-3153-001
 * Feb 16, 2017 
 *			 
 */

import java.util.*;
import java.lang.Math.*;
public class Board {
    class Vector {
        public int x, y;
    }
	
	//board stores the locations of the queens and empty spaces
	//image processing coordinate plane ie:
	//0,0 - - - - 1,0
	//|			   |
	//0,1 - - - - 1,1
    private int[][] board;
    public int boardX;
    public int boardY;
    Random rand;
    
    public static void main(String[] args) throws Exception {
        List<Board> possible = null;;
        Board currentNode = null;

        boolean victory = false;
		
		//resets game board when true
        boolean restart = true;
        int restartCount = 0;
		int stateChanges = 0;

        while (victory == false) {
			//resets game board when local min is found
            if (restart == true) {
                currentNode = new Board(8,8);
                restartCount++;
				restart = false;
            }
			//score current node
            int currentScore = currentNode.scoreBoard();
            if (currentScore == 0) { //h value should be zero upon victory
                victory = true;
                continue;
            }
			//store neighbor nodes
            possible = currentNode.returnAllMoves();
			
			
            int minScore = Integer.MAX_VALUE;
            int minIndex = Integer.MAX_VALUE;
			//counts neighbors that are lower than
			//rcurrent node
			int viableNeighbors = 0;
			//iterate through neighbor noodes
            for (int i = 0; i < possible.size(); i++) {
                Board next = possible.get(i);

                int newScore = next.scoreBoard();
                if (newScore < currentScore) {
					//find lowest heuristic
					//and store neighbor index
					viableNeighbors += 1;
                    minScore = newScore;
                    minIndex = i;
                }
            }
			
			//print out current nodes
			System.out.println("Heuristic: "+ currentScore);
			currentNode.printBoard();
			System.out.println("Num. neighbors with lower h value: " + viableNeighbors+"\n");
			
			
			
            if (currentScore > minScore) {
				//set nextNode to neighbor with lowest heuristic
                Board nextNode = possible.get(minIndex).deepCopy();
                currentNode = nextNode.deepCopy();
				stateChanges++; //just counts state changes
            } else {
				//otherwise a local minimum has been found so restart
				System.out.println("Local minimum found\nRestarting\n");
                restart = true;
                continue;
            }
            

        }
		
		//print solution
        System.out.println("Answer: \n");
        currentNode.printBoard();
		System.out.println("H:0\nRestarts: "+restartCount+"\nState changes: "+stateChanges);
		
    }
	//when not given a game board
	//generates a random one
    Board (int x, int y) {
        boardX = x;
        boardY = y;
        board = new int[y][x];
        rand = new Random();
        resetBoard();
    }
	
    Board(int x, int y, int[][] b0) {
        boardX = x;
        boardY = y;
        board = new int[y][x];
        rand = new Random();
        
		//'deep copies' the game board.
        for (int i = 0; i < b0.length; i++) {
            board[i] = Arrays.copyOf(b0[i], b0[i].length);
        }
    }
	//returns a new board with a unique board array
    Board deepCopy() {
        Board output = new Board(boardX, boardY, board);
        return output;
    }
	
	//returns true when there are no collisions
    public boolean victory() {
        if (scoreBoard() == 0) {
            return true;
        }
        return false;
    }
	
	//returns arraylist of all neigbor nodes next to
	//the current node
    public List<Board> returnAllMoves () throws Exception {
        List<Board> scope = new ArrayList<Board>();
		//there will never be more than one queen per column
		//so loop over each column
        for (int x = 0; x < boardX; x++) {
            //grab the location of the queen
            //and store into a vector
            Vector qp = new Vector();
            qp.x = x;
            qp.y = queenCurrentPos(x);
            
            Board nextMove;
			
			//add each neighbor move to array
			for (int y = 0; y < boardY; y++) {
				if (y != qp.y) {
					nextMove = deepCopy();
					nextMove.moveQueen(qp.x, y);
					scope.add(nextMove);
				}
			}
        }
        return scope;
    }

	//returns heuristic for current board
	//value is equal to the number of pieces 
	//in colision (n) * (n-1)
    int scoreBoard() {
        //qp stands for queen postition
        Vector qp = new Vector();
        int score = 0;
        for (int n = 0; n < boardX; n++) {
			
			//start by checking for colisions accross
			//the x axis
            qp.x = n;
            qp.y = queenCurrentPos(qp.x);
            for (int x = 0; x < boardX; x++) {
                if (board[qp.y][x] == 1 && x != qp.x) {
                    score++;
                }
			}
							
			//for the diagonols we first check to see if
			//the queen's 'travel' hits the top or side
			//of the grid first. this is the number of 
			//squares in which each piece can travel 
			//diagonally
			
			//checking diagonally up right
			int travel = Math.min(qp.y, boardX - qp.x-1);
			
			Vector checkPos = new Vector();
			checkPos.x = qp.x;
			checkPos.y = qp.y;
			for (int i = 0; i < travel; i++) {
				checkPos.y -= 1;
				checkPos.x += 1;
				
				if (board[checkPos.y][checkPos.x] == 1) {
					score++;
				}
			}
			
			//diagonal down right
			travel = Math.min(boardY - qp.y-1, boardX - qp.x-1);
			checkPos.x = qp.x;
			checkPos.y = qp.y;
			for (int i = 0; i < travel; i++) {
				checkPos.y +=1;
				checkPos.x +=1;
				if (board[checkPos.y][checkPos.x] == 1) {
					score++;
				}
			}
			
			//diagonol up left
			travel = Math.min(qp.y, qp.x);
			checkPos.x = qp.x;
			checkPos.y = qp.y;
			for (int i = 0; i < travel; i++) {
				checkPos.y -=1;
				checkPos.x -=1;
				if (board[checkPos.y][checkPos.x] == 1) {
					score++;
				}
			}
			//diag up right
			travel = Math.min(qp.y, boardX - qp.x -1);
			checkPos.x = qp.x;
			checkPos.y = qp.y;
			for (int i = 0; i < travel; i++) {
				checkPos.y -=1;
				checkPos.x +=1;
				if (board[checkPos.y][checkPos.x] == 1) {
					score++;
				}
			}
        }
		
        return score;
    }

	//searches through grid column for queens y position
    int queenCurrentPos(int col) {
        for (int i = 0; i < boardY; i++) {
            if (board[i][col] == 1) {
                return i;
            }
        }
        return -1;
    }
	
	//takes the current 'x' position of the queen
	//and the desired 'y' position. moves her there.
	//if an out of bound coord is sent throws exception
    void moveQueen(int col, int pos) throws Exception {
        Vector qp = new Vector();
		qp.y = queenCurrentPos(col);
		qp.x = col;
		
		if (pos >= 0 && pos < boardY) {
			board[qp.y][qp.x]=0;
			board[pos][qp.x]=1;
			return;
		} 
		throw new Exception();
    }
	//generates new board and places queens randomally
    public void resetBoard() {
        for (int i = 0; i < board[0].length; i++) {
            int queenPosition = rand.nextInt(boardY);
            for (int j = 0; j<board.length; j++) {
                board[j][i]=0;
            }
            board[queenPosition][i] = 1;
        }
    }
	
	//prints the game board
    public void printBoard() {
        String output = "";
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                output += board[i][j];
				if (j < board[i].length-1) {
					output+=", ";
				}
            }
			if (i < board.length - 1)
            	output += "\n";
        }
        System.out.println(output);
    }
}
