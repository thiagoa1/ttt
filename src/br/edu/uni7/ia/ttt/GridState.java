package br.edu.uni7.ia.ttt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uni7.ia.util.Position;

public class GridState {

	public static final int E = 0;
	public static final int X = 1;
	public static final int O = 2;

	// row x column
	private int[][] state;

	private GridResult gridResult;

	public GridState() {
		this.state = new int[3][3];
	}

	public GridState(int tableSize) {
		this.state = new int[tableSize][tableSize];
	}

	public GridState(int[][] state) {
		// TODO - validar estado antes de criar
		this.state = state;
	}
	
	public int[][] getState() {
		return state;
	}

	public int tableSize() {
		return state.length;
	}

	public GridResult getGridResult() {
		if (gridResult == null) {
			gridResult = calcGameState();
		}
		return gridResult;
	}

	private GridResult calcGameState() {
		GridResult result = null;

		// Conferir horizontal
		for (int y = 0; y < tableSize(); y++) {
			if (state[y][0] == X && state[y][1] == X && state[y][2] == X) {
				result = new GridResult(GameState.X_WON, GameStateLine.HORIZONTAL, y);
				break;
			}
			if (state[y][0] == O && state[y][1] == O && state[y][2] == O) {
				result = new GridResult(GameState.O_WON, GameStateLine.HORIZONTAL, y);
				break;
			}
		}
		// Conferir vertical
		for (int x = 0; x < tableSize(); x++) {
			if (state[0][x] == X && state[1][x] == X && state[2][x] == X) {
				result = new GridResult(GameState.X_WON, GameStateLine.VERTICAL, x);
				break;
			}
			if (state[0][x] == O && state[1][x] == O && state[2][x] == O) {
				result = new GridResult(GameState.O_WON, GameStateLine.VERTICAL, x);
				break;
			}
		}
		// Conferir diagonais
		if (state[0][0] == X && state[1][1] == X && state[2][2] == X) {
			result = new GridResult(GameState.X_WON, GameStateLine.DESCENDING_DIAGONAL);
		} else if (state[0][2] == X && state[1][1] == X && state[2][0] == X) {
			result = new GridResult(GameState.X_WON, GameStateLine.ASCENDING_DIAGONAL);
		}

		if (state[0][0] == O && state[1][1] == O && state[2][2] == O) {
			result = new GridResult(GameState.O_WON, GameStateLine.DESCENDING_DIAGONAL);
		} else if (state[0][2] == O && state[1][1] == O && state[2][0] == O) {
			result = new GridResult(GameState.O_WON, GameStateLine.ASCENDING_DIAGONAL);
		}

		// Conferindo se o jogo empatou ou n�o terminou
		if (result == null) {
			for (int y = 0; y < tableSize(); y++) {
				for (int x = 0; x < tableSize(); x++) {
					if (state[y][x] == E) {
						result = new GridResult(GameState.NOT_FINISHED);
						break;
					}
					if (result != null) {
						break;
					}
				}
			}
			if (result == null) {
				result = new GridResult(GameState.DRAW);
			}
		}

		return result;
	}

	public List<Position> getEmptyPositionList() {
		List<Position> emptyPositionList = new ArrayList<>();
		for (int y = 0; y < state.length; y++) {
			for (int x = 0; x < state.length; x++) {
				if (state[y][x] == E) {
					emptyPositionList.add(new Position(x, y));
				}
			}
		}
		return emptyPositionList;
	}

	public boolean isConsistent() {
		for (int y = 0; y < tableSize(); y++) {
			for (int x = 0; x < tableSize(); x++) {
				if (state[y][x] != E && state[y][x] != X && state[y][x] != O) {
					return false;
				}
			}
		}
		return true;
	}

	public GridState getDerivatedState(Position position, int symbol) {
		// TODO Validar se x, y e symbol est�o dentro dos valores aceitos
		GridState derivated = clone();
		derivated.state[position.y][position.x] = symbol;
		return derivated;
	}

	@Override
	protected GridState clone() {
		int[][] clonedState = new int[state.length][state.length];
		for (int i = 0; i < state.length; i++) {
			int[] stateLine = state[i];
			clonedState[i] = stateLine.clone();
		}
		GridState clonedGridState = new GridState(clonedState);
		clonedGridState.gridResult = null;
		return clonedGridState;
	}

	public char valueToRepr(int value) {
		switch (value) {
		case E:
			return ' ';
		case X:
			return 'X';
		case O:
			return 'O';
		default:
			throw new IllegalArgumentException("value must be 0, 1 or 2, representing empty, X and O respectivly");
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int y = 0; y < state.length; y++) {
			for (int x = 0; x < state.length; x++) {
				sb.append(valueToRepr(state[y][x]));
				if (x < state.length - 1) {
					sb.append("|");
				}
			}
			sb.append(System.lineSeparator());
			if (y < state.length - 1) {
				sb.append("-----");
			}
			sb.append(System.lineSeparator());
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof GridState) {
			boolean areStateEquals = true;

			int[][] stateThat = ((GridState) other).state;

			for (int i = 0; i < state.length; i++) {
				int[] stateLineThis = state[i];
				int[] stateLineThat = stateThat[i];
				if (!Arrays.equals(stateLineThis, stateLineThat)) {
					areStateEquals = false;
					break;
				}
			}
			return areStateEquals;
		}
		return false;
	}
}