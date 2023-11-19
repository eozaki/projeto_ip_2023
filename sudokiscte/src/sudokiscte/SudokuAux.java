package sudokiscte;

public class SudokuAux {
	static final int BOARD_SIZE = 9;
	static final int SQUARE_SIZE = 3;
	
	static boolean validGame(int[][] board) {
		// Check the length of board lines and columns; if any doesn't match the expected length, the board is not valid
		if(board.length != BOARD_SIZE || boardLinesOutOfSize(board)) return false;
		
		// Sweeps through every position on the board
		for(int i = 0; i < board.length; i++)
			for(int j = 0; j < board[i].length; j++) {
				int value = board[i][j];
				
				// Search in the same line and same column for matching values; if that happens, the board is not valid
				for(int k = 0; k < board.length; k++)
					if((board[k][j] == value && k != i) || (board[i][k] == value && k != j)) return false;
				
				// Search in the quadrant of the cell we have in hand for repeated values
				for(int k = (i / SQUARE_SIZE) * SQUARE_SIZE; k < (i / SQUARE_SIZE) * SQUARE_SIZE + SQUARE_SIZE; k++) 
					for(int l = (j / SQUARE_SIZE) * SQUARE_SIZE; l < (j / SQUARE_SIZE) * SQUARE_SIZE + SQUARE_SIZE; l++)
						if(board[k][l] == value && k != i && l != j) return false;

			}
		
		return true;
	}
	
	// Check for board lines out of specified size for a sudoku board
	static boolean boardLinesOutOfSize(int[][] board) {
		for(int i = 0; i < board.length; i++) if(board[i].length != BOARD_SIZE) return true;
		
		return false;
	}
}
