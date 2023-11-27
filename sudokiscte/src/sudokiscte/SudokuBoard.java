package sudokiscte;

class SudokuBoard {
	static final int BOARD_SIZE = SudokuAux.BOARD_SIZE;
	static final int SECTOR_SIZE = SudokuAux.SECTOR_SIZE;

	private int[][] board;
	private final int[][] initialBoard;

	public SudokuBoard() {
		this.board = new int[BOARD_SIZE][BOARD_SIZE];
		this.initialBoard = new int[BOARD_SIZE][BOARD_SIZE];
	}

	int getValue(int i, int j) {
		return this.board[i][j];
	}

	boolean play(int i, int j, int value) {
		// Check if value is a valid play
		if (!SudokuAux.validElement(value))
			return false;
		// Check if initial board position is empty or set; return false if set
		if (initialBoard[i][j] != 0)
			return false;

		initialBoard[i][j] = value;

		return true;
	}

	void randomPlay() {
		int count = 0, i = 0, j = 0, value = 0;

		while (count < 1000) {
			i = (int) (Math.random() * BOARD_SIZE);
			j = (int) (Math.random() * BOARD_SIZE);

			if (getValue(i, j) != 0)
				break;
		}

		i = 0;
		j = 0;
		if (count >= 1000) {
			while (i < BOARD_SIZE) {
				while (j < BOARD_SIZE) {
					if (board[i][j] == 0)
						break;
					j++;
				}
				i++;
			}
		}

		count = 0;
		while (count < 1000) {
			value = (int) (Math.random() * BOARD_SIZE);
			boolean result = SudokuAux.validSectorPlay(i, j, value, board);
			if (result)
				break;
		}

		if (count >= 1000) {
			for (value = 0; value <= BOARD_SIZE; value++) {
				boolean result = SudokuAux.validSectorPlay(i, j, value, board);
				if (result)
					break;
			}
		}

		play(i, j, value);
	}

	void reset() {
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++)
				board[i][j] = initialBoard[i][j];
	}
}