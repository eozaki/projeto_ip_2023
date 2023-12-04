package sudokiscte;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

class Sudoku {
	private SudokuBoard sudokuBoard;
	public ColorImage boardImg;

	public Sudoku(String fileName, double difficulty) {
		int[][] fileContent = new int[SudokuAux.BOARD_SIZE][SudokuAux.BOARD_SIZE];

		ColorImage boardImg = new ColorImage(SudokuAux.BOARD_RESOLUTION, SudokuAux.BOARD_RESOLUTION,
		    Color.SOLARIZED_BACKGROUND);

		boardImg.drawMargin();
		boardImg.drawGrid(SudokuAux.BOARD_SIZE, SudokuAux.BOARD_SIZE, SudokuAux.SECTOR_SIZE);

		readGameFile(fileName, fileContent);

		this.boardImg = boardImg;

		this.sudokuBoard = new SudokuBoard(fileContent, difficulty);
		validateAndPaint(0, 0);
	}

	private void readGameFile(String fileName, int[][] fileContent) {
		try {
			Scanner scanner = new Scanner(new File(fileName));
			for (int i = 0; i < SudokuAux.BOARD_SIZE; i++) {
				for (int j = 0; j < SudokuAux.BOARD_SIZE; j++) {
					int value = scanner.nextInt();
					fileContent[i][j] = value;
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("ficheiro " + fileName + " não encontrado");
		}
	}

	public void play(int i, int j, int value) {
		sudokuBoard.play(i, j, value);

		validateAndPaint(i, j);
	}

	public void save(String filename) {
		try {
			PrintWriter pw = new PrintWriter(new File(filename + ".sudgame"));
			for (int i = 0; i < SudokuBoard.BOARD_SIZE; i++) {
				for (int j = 0; j < SudokuBoard.BOARD_SIZE; j++)
					pw.print(sudokuBoard.getValue(i, j) + (j == SudokuBoard.BOARD_SIZE - 1 ? "" : " "));
				if (i != SudokuBoard.BOARD_SIZE - 1)
					pw.print("\n");
			}

			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("o ficheiro data.txt não pode ser escrito");
		}
	}

	public void load(String filename) {
		readGameFile(filename, this.sudokuBoard.board);
	}

	public void undo() {
		sudokuBoard.undo();

		for (int i = 0; i < SudokuAux.BOARD_SIZE; i++)
			validateAndPaint(i, i);
	}

	private void validateAndPaint(int i, int j) {
		boolean[] lines = new boolean[SudokuAux.BOARD_SIZE];
		boolean[] columns = new boolean[SudokuAux.BOARD_SIZE];

		for (int k = 0; k < SudokuAux.BOARD_SIZE; k++) {
			lines[k] = sudokuBoard.validateLine(k);
			columns[k] = sudokuBoard.validateColumn(k);
			SudokuAux.paintLine(boardImg, k, sudokuBoard.board, true);
			SudokuAux.paintColumn(boardImg, k, sudokuBoard.board, true);
		}

		for (int k = 0; k < SudokuAux.BOARD_SIZE; k++) {
			if (!lines[k])
				SudokuAux.paintLine(boardImg, k, sudokuBoard.board, false);
			if (!columns[k])
				SudokuAux.paintColumn(boardImg, k, sudokuBoard.board, false);
		}

		SudokuAux.paintSector(boardImg, i, j, sudokuBoard.board, sudokuBoard.validateSector(i, j));
	}
}
