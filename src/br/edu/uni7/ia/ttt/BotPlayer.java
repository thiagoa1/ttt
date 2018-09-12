package br.edu.uni7.ia.ttt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.uni7.ia.ttt.GridResult.GameState;
import br.edu.uni7.ia.util.Node;
import br.edu.uni7.ia.util.Position;

public class BotPlayer implements Player {

	private Map<GridState, StateChance> stateChances;
	private final Node<GridState> rootNode;
	private final int botSymbol;
	private final boolean startPlayer;

	public BotPlayer(GridState rootState, int botSymbol, boolean startPlayer) {
		this.rootNode = new Node<GridState>(rootState);
		this.botSymbol = botSymbol;
		this.startPlayer = startPlayer;

		stateChances = new HashMap<>();

		generateTree();
	}

	@Override
	public GridState play(GridState currentState) {
		GridState choosenState = null;
		StateChance choosenStateChance = null;

		if (currentState.getGridResult().getGameState().equals(GameState.NOT_FINISHED)) {
			Node<GridState> currentNode = findStateOnTree(currentState, rootNode);
			if (currentNode != null) {
				List<Node<GridState>> children = currentNode.getChildren();

				// Valores iniciais de comparação
				float choosenStateLossChance = 0f;
				float choosenStateWinChance = 0f;
				float choosenStateDrawChance = 0f;

				for (Node<GridState> child : children) {
					StateChance childChance = stateChances.get(child.getData());
					if (choosenState == null) {
						choosenState = child.getData();
						choosenStateChance = childChance;
						choosenStateLossChance = childChance.lossChance;
						choosenStateWinChance = childChance.winChance;
						choosenStateDrawChance = childChance.drawChance;
					} else {
						// Primeiro escolhe o caminho de mais vitorias
//						if (childChance.winChance > choosenStateWinChance) {
//							choosenState = child.getData();
//							choosenStateChance = childChance;
//							choosenStateLossChance = childChance.lossChance;
//							choosenStateWinChance = childChance.winChance;
//							choosenStateDrawChance = childChance.drawChance;
//							// Após escolhe o caminho com mais vitórias
//						}
						
						// Primeiro escolhe o caminho de menos derrotas
						if (childChance.lossChance < choosenStateLossChance) {
							choosenState = child.getData();
							choosenStateChance = childChance;
							choosenStateLossChance = childChance.lossChance;
							choosenStateWinChance = childChance.winChance;
							choosenStateDrawChance = childChance.drawChance;
							// Após escolhe o caminho com mais vitórias
						} else if (childChance.lossChance == choosenStateLossChance
								&& childChance.winChance > choosenStateWinChance) {
							choosenState = child.getData();
							choosenStateChance = childChance;
							choosenStateLossChance = childChance.lossChance;
							choosenStateWinChance = childChance.winChance;
							choosenStateDrawChance = childChance.drawChance;
						}
					}
				}
				System.out.println("Possibilidade do estado: vitória=" + choosenStateWinChance + "% empate="
						+ choosenStateDrawChance + "% derrota=" + choosenStateLossChance + "%");
			} else {
				// TODO tratar para erro onde o estado atual não se encontra na árvore
			}
		} else {
			// TODO Tratar quando o jogo já está finalizado
		}
		return choosenState;
	}

	private Node<GridState> findStateOnTree(GridState currentState, Node<GridState> currentNode) {
		if (currentState.equals(currentNode.getData())) {
			return currentNode;
		} else {
			for (Node<GridState> childNode : currentNode.getChildren()) {
				if (currentState.equals(childNode.getData())) {
					return childNode;
				} else {
					Node<GridState> foundOnChildren = findStateOnTree(currentState, childNode);
					if (foundOnChildren != null) {
						return foundOnChildren;
					}
				}
			}

			// Not found
			return null;
		}
	}

	private void generateTree() {
		int symbol = botSymbol;
		if (!startPlayer) {
			// Para gerar a árvore do segundo jogador
			symbol = flipSymbol(botSymbol);
		}

		generateSubNodes(rootNode, symbol);
	}

	// Retorna a chance daquele nó ganhar o jogo
	private StateChance generateSubNodes(Node<GridState> node, int symbol) {
		GridState nodeState = node.getData();

		StateChance stateChance = null;

		if (nodeState.getGridResult().getGameState().equals(GridResult.GameState.NOT_FINISHED)) {
			List<Position> emptyPositions = nodeState.getEmptyPositionList();

			int otherSymbol = flipSymbol(symbol);

			float childrenWinChance = 0f;
			float childrenDrawsChance = 0f;
			float childrenLossChance = 0f;

			boolean iWon = false;
			boolean iLose = false;
			boolean draw = false;

			for (Position position : emptyPositions) {
				GridState newState = nodeState.getDerivatedState(position, symbol);
				Node<GridState> newNode = new Node<GridState>(newState);
				node.addChild(newNode);
				
				// Se estado final
				if (!nodeState.getGridResult().getGameState().equals(GridResult.GameState.NOT_FINISHED)) {
					if (nodeState.getGridResult().getGameState().getWinnerValue() == botSymbol) {
						// Em caso de vitoria
						iWon = true;
					} else if (nodeState.getGridResult().getGameState().getWinnerValue() == GridState.E) {
						draw = true;
					} else {
						// Em caso de derrota
						iLose = true;
					}
					break;
				} else {
					StateChance childStateChance = generateSubNodes(newNode, otherSymbol);

					childrenWinChance += childStateChance.winChance;
					childrenDrawsChance += childStateChance.drawChance;
					childrenLossChance += childStateChance.lossChance;
				}
			}

			if (iWon) {
				stateChance = new StateChance(100f, 0f, 0f);
			} else if (draw) {
				stateChance = new StateChance(0f, 100f, 0f);
			} else if (iLose) {
				stateChance = new StateChance(0f, 0f, 100f);
			} else {
				if (childrenWinChance != 0) {
					childrenWinChance = childrenWinChance / emptyPositions.size();
				}
				if (childrenDrawsChance != 0) {
					childrenDrawsChance = childrenDrawsChance / emptyPositions.size();
				}
				if (childrenLossChance != 0) {
					childrenLossChance = childrenLossChance / emptyPositions.size();
				}
				stateChance = new StateChance(childrenWinChance, childrenDrawsChance, childrenLossChance);
			}
		} else {
			if (nodeState.getGridResult().getGameState().getWinnerValue() == botSymbol) {
				// Em caso de vitoria
				stateChance = new StateChance(100f, 0f, 0f);
			} else if (nodeState.getGridResult().getGameState().getWinnerValue() == GridState.E) {
				// Em caso de empate
				stateChance = new StateChance(0f, 100f, 0f);
			} else {
				// Em caso de derrotz
				stateChance = new StateChance(0f, 0f, 100f);
			}
//			stateChances; // TODO chance do estado atual. acima é a chance dos filhos
		}
		stateChances.put(nodeState, stateChance);
		return stateChance;
	}

	private int flipSymbol(int symbol) {
		switch (symbol) {
		case GridState.X:
			return GridState.O;
		case GridState.O:
			return GridState.X;
		case GridState.E:
		default:
			throw new IllegalArgumentException("Game symbol must be X or O");
		}
	}
}