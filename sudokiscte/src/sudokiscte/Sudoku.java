package sudokiscte;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

class Sudoku {
	private SudokuBoard sudokuBoard;
	public ColorImage boardImg;
	private String winMessage;

	public Sudoku(String fileName, double difficulty) {
		int[][] fileContent = new int[SudokuAux.BOARD_SIZE][SudokuAux.BOARD_SIZE];

		ColorImage boardImg = new ColorImage(SudokuAux.BOARD_RESOLUTION, SudokuAux.BOARD_RESOLUTION,
		    Color.SOLARIZED_BACKGROUND);

		boardImg.drawMargin();
		boardImg.drawGrid(SudokuAux.BOARD_SIZE, SudokuAux.BOARD_SIZE, SudokuAux.SECTOR_SIZE);

		readGameFile(fileName, fileContent);

		winMessage = readMessageFile("src/fixtures/win_message");

		this.boardImg = boardImg;

		this.sudokuBoard = new SudokuBoard(fileContent, difficulty);

		for (int i = 0; i < SudokuAux.BOARD_SIZE; i++)
			validateAndPaint(i, i);
	}

	private String readMessageFile(String fileName) {
		String text = new String();
		try {
			Scanner scanner = new Scanner(new File(fileName));
			while (scanner.hasNextLine())
				text += scanner.nextLine() + "\n";
			scanner.close();
			return text;
		} catch (FileNotFoundException e) {
			System.out.println("ficheiro " + fileName + " não encontrado");
			return "Parabéns por concluir o jogo!";
		}
	}

	private boolean isGameFinished() {
		return sudokuBoard.isGameFinished();
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

	public void randomPlay() {
		sudokuBoard.randomPlay();

		for (int i = 0; i < SudokuBoard.BOARD_SIZE; i++)
			validateAndPaint(i, i);
		if (isGameFinished())
			System.out.println(winMessage);
	}

	public void reset() {
		sudokuBoard.reset();

		for (int i = 0; i < SudokuBoard.BOARD_SIZE; i++)
			validateAndPaint(i, i);
	}

	public void play(int i, int j, int value) {
		sudokuBoard.play(i, j, value);

		validateAndPaint(i, j);
		if (isGameFinished())
			System.out.println(winMessage);
	}

	public void save(String filename) {
		try {
			PrintWriter pw = new PrintWriter(new File(filename + ".sudgame"));

			for (int i = 0; i < SudokuBoard.BOARD_SIZE; i++) {
				for (int j = 0; j < SudokuBoard.BOARD_SIZE; j++)
					pw.print(sudokuBoard.getInitialValue(i, j) + (j == SudokuBoard.BOARD_SIZE - 1 ? "" : " "));

				pw.print("\n");
			}
			pw.print("\n"); // One extra line to separate initial board and game state

			for (int i = 0; i < SudokuBoard.BOARD_SIZE; i++) {
				for (int j = 0; j < SudokuBoard.BOARD_SIZE; j++)
					pw.print(sudokuBoard.getValue(i, j) + (j == SudokuBoard.BOARD_SIZE - 1 ? "" : " "));

				pw.print("\n");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("o ficheiro data.txt não pode ser escrito");
		}
	}

	public void loadSavedGame(String fileName) {
		try {
			Scanner scanner = new Scanner(new File(fileName)); // Add `+ ".sudgame"` to force file type
			for (int i = 0; i < SudokuAux.BOARD_SIZE; i++) {
				String s = scanner.nextLine();
				if (s.equals(""))
					throw new IllegalArgumentException("O ficheiro chegou ao fim antes do esperado");

				int numbers = 0;
				for (int j = 0; j < s.length(); j++) {
					String charValue = String.valueOf(s.charAt(j));
					if (charValue.equals(" "))
						continue;

					int value = Integer.parseInt(charValue);
					sudokuBoard.setInInitial(i, numbers, value);
					numbers++;
				}
			}

			// Next line after initial board should be blank, according to format
			if (!scanner.nextLine().equals(""))
				throw new IllegalArgumentException("O ficheiro está fora do formato esperado");

			for (int i = 0; i < SudokuAux.BOARD_SIZE; i++) {
				String s = scanner.nextLine();
				if (s.equals(""))
					throw new IllegalArgumentException("O ficheiro chegou ao fim antes do esperado");

				int numbers = 0;
				for (int j = 0; j < s.length(); j++) {
					String charValue = String.valueOf(s.charAt(j));
					if (charValue.equals(" "))
						continue;

					int value = Integer.parseInt(charValue);
					sudokuBoard.setInBoard(i, numbers, value);
					numbers++;
				}
			}
			scanner.close();

			for (int i = 0; i < SudokuAux.BOARD_SIZE; i++)
				validateAndPaint(i, i);
		} catch (FileNotFoundException e) {
			System.out.println("ficheiro " + fileName + " não encontrado");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	public void undo() {
		sudokuBoard.undo();

		for (int i = 0; i < SudokuAux.BOARD_SIZE; i++)
			validateAndPaint(i, i);
	}

	private void validateAndPaint(int i, int j) {
		SudokuAux.paintLine(boardImg, i, sudokuBoard.board, true);
		SudokuAux.paintColumn(boardImg, j, sudokuBoard.board, true);

		SudokuAux.paintSector(boardImg, i, j, sudokuBoard.board, sudokuBoard.validateSector(i, j));

		for (int k = 0; k < SudokuAux.BOARD_SIZE; k++) {
			if (!sudokuBoard.validateLine(k))
				SudokuAux.paintLine(boardImg, k, sudokuBoard.board, false);
			if (!sudokuBoard.validateColumn(k))
				SudokuAux.paintColumn(boardImg, k, sudokuBoard.board, false);
		}
	}
}
