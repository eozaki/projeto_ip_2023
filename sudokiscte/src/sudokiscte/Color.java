package sudokiscte;

/**
 * Represents RGB colors. RGB values are stored in a 3-position array, with
 * values in the interval [0, 255]. rgb[0] - Red rgb[1] - Green rgb[2] - Blue
 */
class Color {

	private final int[] rgb; // @color

	/**
	 * Creates an RGB color. Provided values have to be in the interval [0, 255]
	 */
	Color(int r, int g, int b) {
		if (!valid(r) || !valid(g) || !valid(b))
			throw new IllegalArgumentException("invalid RGB values: " + r + ", " + g + ", " + b);

		this.rgb = new int[] { r, g, b };
	}

	/**
	 * Red value [0, 255]
	 */
	int getR() {
		return rgb[0];
	}

	/**
	 * Green value [0, 255]
	 */
	int getG() {
		return rgb[1];
	}

	/**
	 * Blue value [0, 255]
	 */
	int getB() {
		return rgb[2];
	}

	/**
	 * Obtains the luminance in the interval [0, 255].
	 */
	int getLuminance() {
		return (int) Math.round(rgb[0] * .21 + rgb[1] * .71 + rgb[2] * .08);
	}

	static boolean valid(int value) {
		return value >= 0 && value <= 255;
	}

	boolean sameAs(Color c) {
		return this.getR() == c.getR() && this.getG() == c.getG() && this.getB() == c.getB();
	}

	static final int MAX = 255;
	static final int MIN = 0;

	static final Color RED = new Color(MAX, MIN, MIN);
	static final Color GREEN = new Color(MIN, MAX, MIN);
	static final Color BLUE = new Color(MIN, MIN, MAX);
	static final Color BLACK = new Color(MIN, MIN, MIN);
	static final Color WHITE = new Color(MAX, MAX, MAX);

	static final Color SOLARIZED_BACKGROUND = new Color(0, 43, 54);
	static final Color SOLARIZED_LINES = new Color(30, 100, 110);
	static final Color SOLARIZED_FONT = new Color(238, 232, 213);
	static final Color SOLARIZED_ERROR = new Color(211, 1, 2);
}
