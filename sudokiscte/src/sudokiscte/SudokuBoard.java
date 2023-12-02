package sudokiscte;

class SudokuBoard {
	static final int BOARD_SIZE = SudokuAux.BOARD_SIZE;
	static final int SECTOR_SIZE = SudokuAux.SECTOR_SIZE;

	static final int PLAYED_LINE_INDEX = 0;
	static final int PLAYED_COLUMN_INDEX = 1;

	public int[][] board;
	private final int[][] initialBoard;

	// Array of integers with as many lines as blank positions in the board, and 2
	// positions for each of those (line and column, respectively)
	private int[][] plays;

	// To be used as index of the last move played (offset by one from the position
	// the play has been stored in)
	// Eg. No move played, index is 0, and undo returns without doing a thing
	// Eg. Index 1, one move played, gets coordinates from row 0 in **plays** array
	// sets it back to 0 in board and deduces 1 from the index, making it 0 again
	private int playedPositions = 0;

	public SudokuBoard(int[][] initialBoard, double blankProportion) {
		this.initialBoard = initialBoard;
		SudokuAux.blankBoardProportionally(this.initialBoard, blankProportion);

		copyInitialBoard();

		this.plays = new int[countBlankPositions()][2];
	}

	private int countBlankPositions() {
		int count = 0;
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				if (board[i][j] == 0)
					count++;

		return count;
	}

	private void copyInitialBoard() {
		this.board = new int[BOARD_SIZE][BOARD_SIZE];

		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++)
				this.board[i][j] = initialBoard[i][j];
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

		board[i][j] = value;
		storePlay(i, j);

		validatePosition(i, j);

		return true;
	}

	void storePlay(int line, int column) {
		plays[playedPositions][0] = line;
		plays[playedPositions][1] = column;

		playedPositions++;
	}

	void undo() {
		if (playedPositions == 0)
			return;

		if (playedPositions >= plays.length)
			playedPositions = plays.length - 1;

		int[] lastPlay = plays[playedPositions - 1];
		int line = lastPlay[PLAYED_LINE_INDEX];
		int column = lastPlay[PLAYED_COLUMN_INDEX];
		this.board[line][column] = 0;

		validatePosition(line, column);

		playedPositions--;
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

	boolean validateSector(int sectorV, int sectorH) {
		for (int i = (sectorV / SECTOR_SIZE) * SECTOR_SIZE; i < (sectorV / SECTOR_SIZE) * SECTOR_SIZE + SECTOR_SIZE; i++) {
			for (int j = (sectorH / SECTOR_SIZE) * SECTOR_SIZE; j < (sectorH / SECTOR_SIZE) * SECTOR_SIZE
			    + SECTOR_SIZE; j++) {
				if (!uniqueValueInSector(board[i][j], i, j))
					return false;
			}
		}

		return true;
	}

	boolean uniqueValueInSector(int value, int sectorV, int sectorH) {
		if (value == 0)
			return true;

		for (int i = (sectorV / SECTOR_SIZE) * SECTOR_SIZE; i < (sectorV / SECTOR_SIZE) * SECTOR_SIZE + SECTOR_SIZE; i++) {
			for (int j = (sectorH / SECTOR_SIZE) * SECTOR_SIZE + 1; j < (sectorH / SECTOR_SIZE) * SECTOR_SIZE
			    + SECTOR_SIZE; j++) {
				if (board[i][j] == value && (i != sectorV || j != sectorH))
					return false;
			}
		}
		return true;
	}

	boolean validateColumn(int column) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = i + 1; j < BOARD_SIZE; j++) {
				if (board[i][column] == board[j][column] && board[i][column] != 0)
					return false;
			}
		}

		return true;
	}

	boolean validateLine(int line) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = i + 1; j < BOARD_SIZE; j++) {
				if (board[line][i] == board[line][j] && board[line][i] != 0)
					return false;
			}
		}

		return true;
	}

	boolean validatePosition(int line, int column) {
		return (validateLine(line) && validateColumn(column) && validateSector(line, column));
	}

	boolean isGameFinished() {
		for (int i = 0; i < BOARD_SIZE; i++)
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (board[i][j] == 0)
					return false;

				if (!validateLine(i) || !validateColumn(i) || !validateSector(i, j))
					return false;
			}

		return true;
	}
}