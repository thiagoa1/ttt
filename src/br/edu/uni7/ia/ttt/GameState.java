package br.edu.uni7.ia.ttt;

public enum GameState {
	NOT_FINISHED, X_WON, O_WON, DRAW;
	
	public static GameState getByValue(int value) {
		switch (value) {
		case GridState.X:
			return X_WON;
		case GridState.O:
			return O_WON;
		case GridState.E:
			return DRAW;
		default:
			throw new IllegalArgumentException("Value " + value + " is not valid. Must be the GridState representation of X, O or E");
		}
	}

	public int getWinnerValue() {
		switch (this) {
		case X_WON:
			return GridState.X;
		case O_WON:
			return GridState.O;
		default:
			return GridState.E;
		}
	}
	
	public String getWinnerSymbol() {
		switch (this) {
		case X_WON:
			return "X";
		case O_WON:
			return "O";
		default:
			return "E";
		}
	}

	public boolean isFinished() {
		switch (this) {
		case NOT_FINISHED:
			return false;
		default:
			return true;
		}
	}

}