package sudokiscte;

class SudokuAux {
	static final int BOARD_SIZE = 9;
	static final int SECTOR_SIZE = 3;
	static final int BOARD_RESOLUTION = 540;
	static final int CELL_RESOLUTION = BOARD_RESOLUTION / BOARD_SIZE;

	static boolean validGame(int[][] board) {
		// Check the length of board lines and columns; if any doesn't match the
		// expected length, the board is not valid
		if (board.length != BOARD_SIZE || boardLinesOutOfSize(board))
			return false;

		// Sweeps through every position on the board
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++) {
				int value = board[i][j];

				// Check if value is in the correct range
				if (!validElement(value))
					return false;

				// Search in the same line and same column for matching values; if that happens,
				// the board is not valid
				for (int k = 0; k < board.length; k++)
					if ((board[k][j] == value && k != i) || (board[i][k] == value && k != j))
						return false;

				// Search in the quadrant of the cell we have in hand for repeated values
				if (!validSectorPlay(i, j, value, board))
					return false;
			}

		return true;
	}

	static boolean validSectorPlay(int i, int j, int value, int[][] board) {
		for (int k = (i / SECTOR_SIZE) * SECTOR_SIZE; k < (i / SECTOR_SIZE) * SECTOR_SIZE + SECTOR_SIZE; k++)
			for (int l = (j / SECTOR_SIZE) * SECTOR_SIZE; l < (j / SECTOR_SIZE) * SECTOR_SIZE + SECTOR_SIZE; l++)
				if (board[k][l] == value && k != i && l != j)
					return false;

		return true;
	}

	static boolean validElement(int value) {
		return value >= 0 && value <= 9;
	}

	static void blankBoardProportionally(int[][] board, double blankPercentage) {
		// Calculates the number (floored) of positions on the board to be marked as 0
		int toMakeBlank = (int) (blankPercentage * BOARD_SIZE * BOARD_SIZE);

		while (toMakeBlank > 0) {
			// Randomize a position on the board
			int i = (int) (Math.random() * BOARD_SIZE);
			int j = (int) (Math.random() * BOARD_SIZE);

			// If the position on the board is not blank
			if (board[i][j] != 0) {
				// Make it blank, and one less to position to make so
				board[i][j] = 0;
				toMakeBlank--;
			}
		}
	}

	// Check for board lines out of specified size for a sudoku board
	static boolean boardLinesOutOfSize(int[][] board) {
		for (int i = 0; i < board.length; i++)
			if (board[i].length != BOARD_SIZE)
				return true;

		return false;
	}

	// Creates a string from a board game
	static String stringify(int[][] board) {
		String result = "";

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				result += board[i][j];
				// Adds whitespace to every element except the last (index BOARD_SIZE - 2)
				if (j < BOARD_SIZE - 1)
					result += " ";
			}
			result += "\n";
		}

		return result;
	}

	static void writeToCell(ColorImage img, int cellLine, int cellColumn, String content) {
		img.wipeCell(cellLine, cellColumn, CELL_RESOLUTION);
		img.drawText((cellColumn * CELL_RESOLUTION) + CELL_RESOLUTION / 4,
		    (cellLine * CELL_RESOLUTION) - CELL_RESOLUTION / 12, content, CELL_RESOLUTION, Color.SOLARIZED_FONT);
	}

	static void writeToCellWithFontColor(ColorImage img, int cellLine, int cellColumn, String content, Color c) {
		img.paintCell(cellLine, cellColumn, CELL_RESOLUTION, Color.SOLARIZED_BACKGROUND);
		img.drawText((cellColumn * CELL_RESOLUTION) + CELL_RESOLUTION / 4,
		    (cellLine * CELL_RESOLUTION) - CELL_RESOLUTION / 12, content, CELL_RESOLUTION, c);
	}

	static void invalidLine(ColorImage img, int line, int[][] board) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			img.paintCell(line, i, CELL_RESOLUTION, Color.SOLARIZED_ERROR);
			writeToCellWithFontColor(img, line, i, board[line][i] + "", Color.SOLARIZED_ERROR);
		}
	}

	static void invalidColumn(ColorImage img, int column, int[][] board) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			img.paintCell(i, column, CELL_RESOLUTION, Color.SOLARIZED_ERROR);
			writeToCellWithFontColor(img, i, column, board[i][column] + "", Color.SOLARIZED_ERROR);
		}
	}

	static void invalidSector(ColorImage img, int verticalIndex, int horizontalIndex, int[][] board) {
		for (int k = (verticalIndex / SECTOR_SIZE) * SECTOR_SIZE; k < (verticalIndex / SECTOR_SIZE) * SECTOR_SIZE
		    + SECTOR_SIZE; k++)
			for (int l = (horizontalIndex / SECTOR_SIZE) * SECTOR_SIZE; l < (horizontalIndex / SECTOR_SIZE) * SECTOR_SIZE
			    + SECTOR_SIZE; l++) {
				img.paintCell(k, l, CELL_RESOLUTION, Color.SOLARIZED_ERROR);
				writeToCellWithFontColor(img, k, l, board[k][l] + "", Color.SOLARIZED_ERROR);
			}
	}

	static void test() {
		int[][] board = { { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 4, 5, 6, 7, 8, 9, 1, 2, 3 }, { 7, 8, 9, 1, 2, 3, 4, 5, 6 },
		    { 2, 1, 4, 3, 6, 5, 8, 9, 7 }, { 3, 6, 5, 8, 9, 7, 2, 1, 4 }, { 8, 9, 7, 2, 1, 4, 3, 6, 5 },
		    { 5, 3, 1, 6, 4, 2, 9, 7, 8 }, { 6, 4, 2, 9, 7, 8, 5, 3, 1 }, { 9, 7, 8, 5, 3, 1, 6, 4, 2 } };

		blankBoardProportionally(board, 0.0);

		ColorImage img = new ColorImage(BOARD_RESOLUTION, BOARD_RESOLUTION, Color.SOLARIZED_BACKGROUND);

		img.drawMargin();
		img.drawGrid(9, 9, SECTOR_SIZE);

		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++)
				writeToCell(img, i, j, "" + (board[i][j] == 0 ? "" : board[i][j]));

		invalidLine(img, 1, board);
//		invalidColumn(img, 2, board);
//		invalidSector(img, 7, 7, board);

		System.out.println(stringify(board));
	}
}
