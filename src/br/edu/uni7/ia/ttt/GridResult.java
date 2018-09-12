package br.edu.uni7.ia.ttt;

import java.util.ArrayList;
import java.util.List;

import br.edu.uni7.ia.util.Position;

public class GridResult {

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

	public enum GameStateLine {
		HORIZONTAL, VERTICAL, DESCENDING_DIAGONAL, ASCENDING_DIAGONAL, NA
	}

	private GameState gameState;
	private GameStateLine gameStateLine;
	private int lineIndex;
	
	List<Position> positions;

	public GridResult(GameState gameState) {
		this(gameState, GameStateLine.NA, 0);
		switch (gameState) {
		case X_WON:
		case O_WON:
			throw new IllegalArgumentException("This constructor gameState must be only used in not won cases");
		case NOT_FINISHED:
		case DRAW:
			// Dont do nothing
		}
	}

	public GridResult(GameState gameState, GameStateLine gameStateLine) {
		this(gameState, gameStateLine, 0);
		switch (gameStateLine) {
		case HORIZONTAL:
		case VERTICAL:
			throw new IllegalArgumentException("This constructor gameStateLine must be only used in diagonals");
		case DESCENDING_DIAGONAL:
		case ASCENDING_DIAGONAL:
		case NA:
			// Dont do nothing
		}
	}

	public GridResult(GameState gameState, GameStateLine gameStateLine, int lineIndex) {
		this.gameState = gameState;
		this.gameStateLine = gameStateLine;
		this.lineIndex = lineIndex;
	}
	
	public List<Position> getWinnerPositions() {
		List<Position> positions = new ArrayList<>();
		
		if (gameState.isFinished()) {
			switch (gameStateLine) {
			case HORIZONTAL:
				positions.add(new Position(0, lineIndex));
				positions.add(new Position(1, lineIndex));
				positions.add(new Position(2, lineIndex));
				break;
			case VERTICAL:
				positions.add(new Position(lineIndex, 0));
				positions.add(new Position(lineIndex, 1));
				positions.add(new Position(lineIndex, 2));
				break;
			case DESCENDING_DIAGONAL:
				positions.add(new Position(0,0));
				positions.add(new Position(1,1));
				positions.add(new Position(2,2));
				break;
			case ASCENDING_DIAGONAL:
				positions.add(new Position(2,0));
				positions.add(new Position(1,1));
				positions.add(new Position(0,2));
				break;
			}
		}
		
		return positions;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public GameStateLine getGameStateLine() {
		return gameStateLine;
	}

	public void setGameStateLine(GameStateLine gameStateLine) {
		this.gameStateLine = gameStateLine;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}
}
