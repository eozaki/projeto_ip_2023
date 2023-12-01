package sudokiscte;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Sudoku {
	private SudokuBoard sudokuBoard;
	public ColorImage boardImg;

	public Sudoku(String fileName, double difficulty) {
		int[][] fileContent = new int[SudokuAux.BOARD_SIZE][SudokuAux.BOARD_SIZE];

		readGameFile(fileName, fileContent);

		this.sudokuBoard = new SudokuBoard(fileContent, difficulty);
	}

	private void readGameFile(String fileName, int[][] fileContent) {
		try {
			int i = 0;
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNextLine()) {
				for (int j = 0; !scanner.hasNextInt(); j++)
					fileContent[i][j] = scanner.nextInt();
				i++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("ficheiro " + fileName + " não encontrado");
		}
	}

	public void play(int i, int j, int value) {
		sudokuBoard.play(i, j, value);

		validateAndPaint(i, j);
	}

	public void undo() {
		sudokuBoard.undo();
	}

	private void validateAndPaint(int i, int j) {
		for (int k = 0; k < SudokuAux.BOARD_SIZE; k++) {
			SudokuAux.paintLine(boardImg, k, sudokuBoard.board, sudokuBoard.validateLine(k));

			SudokuAux.paintColumn(boardImg, k, sudokuBoard.board, sudokuBoard.validateColumn(k));
		}

		SudokuAux.paintSector(boardImg, i, j, sudokuBoard.board, sudokuBoard.validateSector(i, j));
	}
}
