package sudokiscte;

class SudokuBoard {
	static final int BOARD_SIZE = SudokuAux.BOARD_SIZE;
	static final int SECTOR_SIZE = SudokuAux.SECTOR_SIZE;

	static final int PLAYED_LINE_INDEX = 0;
	static final int PLAYED_COLUMN_INDEX = 1;

	public int[][] board;
	public int[][] initialBoard;

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

	int getInitialValue(int i, int j) {
		return this.initialBoard[i][j];
	}

	// For loading saved games, no validations in place
	public void setInBoard(int i, int j, int value) {
		this.board[i][j] = value;
	}

	// For loading saved games, no validations in place
	public void setInInitial(int i, int j, int value) {
		this.initialBoard[i][j] = value;
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
		plays[playedPositions][PLAYED_LINE_INDEX] = line;
		plays[playedPositions][PLAYED_COLUMN_INDEX] = column;

		playedPositions++;
	}

	void undo() {
		if (playedPositions <= 0) {
			playedPositions = 0;
			return;
		}

		if (playedPositions > plays.length + 1)
			playedPositions = plays.length;

		int[] lastPlay = plays[playedPositions - 1];
		int line = lastPlay[PLAYED_LINE_INDEX];
		int column = lastPlay[PLAYED_COLUMN_INDEX];
		this.board[line][column] = 0;

		playedPositions--;
	}

	void randomPlay() {
		int count = 0, i = 0, j = 0, value = 0;

		while (count < 1000) {
			i = (int) (Math.random() * BOARD_SIZE);
			j = (int) (Math.random() * BOARD_SIZE);

			if (getValue(i, j) == 0)
				break;

			count++;
		}

		if (count >= 1000) {
			i = 0;
			j = 0;
			while (i < BOARD_SIZE) {
				while (j < BOARD_SIZE) {
					if (board[i][j] == 0)
						break;
					j++;
				}
				i++;
			}
			count++;
		}

		count = 0;
		while (count < 1000) {
			value = (int) ((Math.random() * BOARD_SIZE) + 1);
			boolean result = uniqueValueInSector(value, i, j);
			count++;

			if (result)
				break;
		}

		if (count >= 1000) {
			for (value = 0; value <= BOARD_SIZE; value++) {
				boolean result = SudokuAux.validSectorPlay(i, j, value, board);
				count++;
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

		for (int i = 0; i < plays.length; i++) {
			plays[i][PLAYED_LINE_INDEX] = 0;
			plays[i][PLAYED_COLUMN_INDEX] = 0;
		}

		playedPositions = 0;
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
			for (int j = (sectorH / SECTOR_SIZE) * SECTOR_SIZE; j < (sectorH / SECTOR_SIZE) * SECTOR_SIZE
			    + SECTOR_SIZE; j++) {
				if (board[i][j] == value && (i != sectorV || j != sectorH))
					return false;
			}
		}
		return true;
	}

	boolean validateColumn(int column) {
		try {
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = i + 1; j < BOARD_SIZE; j++) {
					if (board[i][column] == board[j][column] && board[i][column] != 0)
						throw new IllegalArgumentException("Jogada inválida na coluna " + column);
				}
			}

			return true;
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	boolean validateLine(int line) {
		try {
			for (int i = 0; i < BOARD_SIZE; i++) {
				for (int j = i + 1; j < BOARD_SIZE; j++) {
					if (board[line][i] == board[line][j] && board[line][i] != 0) {
						throw new IllegalArgumentException("Jogada inválida na linha " + line);
					}
				}
			}

			return true;
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return false;
		}
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