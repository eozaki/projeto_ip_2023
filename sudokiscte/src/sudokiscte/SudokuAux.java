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

	static void writeToCellWithFontColor(ColorImage img, int cellLine, int cellColumn, String content, Color c) {
		img.paintCell(cellLine, cellColumn, CELL_RESOLUTION, Color.SOLARIZED_BACKGROUND);
		img.drawText((cellColumn * CELL_RESOLUTION) + CELL_RESOLUTION / 4,
		    (cellLine * CELL_RESOLUTION) - CELL_RESOLUTION / 12, (content.equals("0")) ? "" : content, CELL_RESOLUTION, c);
	}

	static void paintLine(ColorImage img, int line, int[][] board, boolean valid) {
		Color c = valid ? Color.SOLARIZED_FONT : Color.SOLARIZED_ERROR;

		for (int i = 0; i < BOARD_SIZE; i++) {
			img.paintCell(line, i, CELL_RESOLUTION, Color.SOLARIZED_ERROR);
			writeToCellWithFontColor(img, line, i, board[line][i] + "", c);
		}
	}

	static void paintColumn(ColorImage img, int column, int[][] board, boolean valid) {
		Color c = valid ? Color.SOLARIZED_FONT : Color.SOLARIZED_ERROR;

		for (int i = 0; i < BOARD_SIZE; i++) {
			img.paintCell(i, column, CELL_RESOLUTION, Color.SOLARIZED_ERROR);
			writeToCellWithFontColor(img, i, column, board[i][column] + "", c);
		}
	}

	static void paintSector(ColorImage img, int verticalIndex, int horizontalIndex, int[][] board, boolean valid) {
		Color c = valid ? Color.SOLARIZED_FONT : Color.SOLARIZED_ERROR;

		for (int k = (verticalIndex / SECTOR_SIZE) * SECTOR_SIZE; k < (verticalIndex / SECTOR_SIZE) * SECTOR_SIZE
		    + SECTOR_SIZE; k++)
			for (int l = (horizontalIndex / SECTOR_SIZE) * SECTOR_SIZE; l < (horizontalIndex / SECTOR_SIZE) * SECTOR_SIZE
			    + SECTOR_SIZE; l++) {
				img.paintCell(k, l, CELL_RESOLUTION, Color.SOLARIZED_ERROR);
				writeToCellWithFontColor(img, k, l, board[k][l] + "", c);
			}
	}
}
