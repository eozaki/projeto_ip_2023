package sudokiscte;

/**
 * Represents color images. Image data is represented as a matrix: - the number
 * of lines corresponds to the image height (data.length) - the length of the
 * lines corresponds to the image width (data[*].length) - pixel color is
 * encoded as integers (ARGB)
 */

class ColorImage {

	private int[][] data; // @colorimage

	// Construtors

	ColorImage(int width, int height) {
		data = new int[height][width];
	}

	ColorImage(String file) {
		this.data = ImageUtil.readColorImage(file);
	}

	ColorImage(int[][] data) {
		this.data = data;
	}

	ColorImage(int width, int height, Color color) {
		data = new int[height][width];

		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				this.setColor(x, y, color);
			}
		}
	}

	// Metods

	int getWidth() {
		return data[0].length;
	}

	int getHeight() {
		return data.length;
	}

	void setColor(int x, int y, Color c) {
		data[y][x] = ImageUtil.encodeRgb(c.getR(), c.getG(), c.getB());
	}

	Color getColor(int x, int y) {
		int[] rgb = ImageUtil.decodeRgb(data[y][x]);
		return new Color(rgb[0], rgb[1], rgb[2]);
	}

	void whiteBoard() {
		for (int i = 0; i < data.length; i++)
			for (int j = 0; j < data[i].length; j++)
				setColor(i, j, Color.WHITE);
	}

	void wipeCell(int line, int column, int size) {
		paintCell(line, column, size, Color.SOLARIZED_BACKGROUND);
	}

	void paintCell(int line, int column, int size, Color color) {
		for (int i = (line * 60); i < (line * 60) + size; i++)
			for (int j = (column * 60); j < (column * 60) + size; j++)
				if (!getColor(j, i).sameAs(Color.SOLARIZED_LINES))
					setColor(j, i, color);
	}

	void drawMargin() {
		drawHorizontalLine(0, Color.SOLARIZED_LINES);
		drawHorizontalLine(data.length - 1, Color.SOLARIZED_LINES);

		drawVerticalLine(0, Color.SOLARIZED_LINES);
		drawVerticalLine(data[0].length - 1, Color.SOLARIZED_LINES);
	}

	void drawHorizontalLine(int line, Color color) {
		for (int i = 0; i < data.length; i++) {
			setColor(i, line, color);
		}
	}

	void drawVerticalLine(int column, Color color) {
		for (int j = 0; j < data[column].length; j++) {
			setColor(column, j, color);
		}
	}

	void drawGrid(int lines, int columns, int regionSize) {
		int columnPixels = data.length / columns;
		int linePixels = data.length / lines;

		while (columns > 0) {
			drawVerticalLine((columns - 1) * columnPixels, Color.SOLARIZED_LINES);

			if (columns % regionSize == 1)
				drawVerticalLine(Math.max((columns - 1) * columnPixels - 1, 0), Color.SOLARIZED_LINES);

			columns--;
		}

		while (lines > 0) {
			drawHorizontalLine((lines - 1) * linePixels, Color.SOLARIZED_LINES);

			if (lines % regionSize == 1)
				drawHorizontalLine(Math.max((lines - 1) * linePixels - 1, 0), Color.SOLARIZED_LINES);

			lines--;
		}
	}

	// Text functions

	void drawText(int textX, int textY, String text, int textSize, Color textColor) {
		drawText(textX, textY, text, textSize, textColor, false);
	}

	void drawCenteredText(int textX, int textY, String text, int textSize, Color textColor) {
		drawText(textX, textY, text, textSize, textColor, true);
	}

	private void drawText(int textX, int textY, String text, int textSize, Color textColor, boolean isCentered) {
		int r = 255 - textColor.getR();
		int g = 255 - textColor.getG();
		int b = 255 - textColor.getB();

		Color maskColor = new Color(r, g, b);

		int encodedMaskRGB = ImageUtil.encodeRgb(r, g, b);

		int[][] aux = ImageUtil.createColorImageWithText(getWidth(), getHeight(), maskColor, textX, textY, text, textSize,
		    textColor, isCentered);

		for (int i = 0; i < aux.length; i++) {
			for (int j = 0; j < aux[i].length; j++) {
				int value = aux[i][j];
				if (value != encodedMaskRGB) {
					data[i][j] = aux[i][j];
				}
			}
		}
	}
}