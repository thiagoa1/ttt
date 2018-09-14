package br.edu.uni7.ia.ttt.ui;

import static br.edu.uni7.ia.ttt.GridState.E;
import static br.edu.uni7.ia.ttt.GridState.O;
import static br.edu.uni7.ia.ttt.GridState.X;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.edu.uni7.ia.ttt.BotPlayer;
import br.edu.uni7.ia.ttt.GameState;
import br.edu.uni7.ia.ttt.GridResult;
import br.edu.uni7.ia.ttt.GridState;
import br.edu.uni7.ia.ttt.Player;
import br.edu.uni7.ia.util.Position;

public class Mainframe extends JFrame {

	private static final long serialVersionUID = 1L;

	private final int PLAYER_1 = GridState.X;
	private final int PLAYER_2 = GridState.O;

	// TODO fixo em 3x3. Depois permitir tamanhos maiores
	private int[][] initialStateArray = { { E, E, E }, { E, E, E }, { E, E, E } };
//	private int[][] initialStateArray = { { E, X, E }, { X, E, X }, { E, X, E } };

	private GridState zeroGridState = new GridState(initialStateArray);

	private JButton[][] buttonGrid = { { getNewButton(), getNewButton(), getNewButton() },
			{ getNewButton(), getNewButton(), getNewButton() }, { getNewButton(), getNewButton(), getNewButton() } };

	private JPanel gamePanel;
	private JPanel statusPanel;

	private JLabel statusLabel;
	private JButton restartButton;

	private GridState currentState;
	private boolean currentHumanFirst;
	private boolean currentIsBotVsBot;
	private int currentPlayer;

	private Player botPlayer1;
	private Player botPlayer2;
	private HumanPlayer humanPlayer = new HumanPlayer();

	private Player[] players = new Player[2];

	public Mainframe() {
		super("Idosa");

		initGui();
		// TODO Mostrar espera
		startBots();
		setupGame();
	}
	
	private void setupGame() {
		initStartingConditions();

		setStateInFields(currentState);
		new Thread() {
			public void run() {
				startGame();
			};
		}.start();

	}

	private void startGame() {
		if (currentIsBotVsBot) {
			players[0] = botPlayer1;
			players[1] = botPlayer2;
		} else {
			if (currentHumanFirst) {
				// Quando nulo, � o humano
				// TODO criar classe para representar o humano
				players[0] = humanPlayer;
				humanPlayer.setSymbol(PLAYER_1);
				players[1] = botPlayer2;
			} else {
				players[0] = botPlayer1;
				players[1] = humanPlayer;
				humanPlayer.setSymbol(PLAYER_2);
			}
		}

		while (currentState != null) {
			currentState = players[currentPlayer].play(currentState);

			if (currentIsBotVsBot) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (currentState != null) {
				setStateInFields(currentState);

				System.out.println(currentPlayer + " jogou");
				System.out.println(currentState);

				if (currentState.getGridResult().getGameState().isFinished()) {
					if (currentState.getGridResult().getGameState().equals(GameState.DRAW)) {
						JOptionPane.showMessageDialog(this, "O jogo terminou em empate!", "Fim de jogo",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this,
								"O jogador " + currentPlayer + " de s�mbolo "
										+ currentState.getGridResult().getGameState().getWinnerSymbol() + " ganhou!",
								"Fim de jogo", JOptionPane.INFORMATION_MESSAGE);
						
//						currentState.getGridResult().getGameStateLine().
					}

					break; // Sai do gaming loop
				}
			}

			// Passa para o pr�ximo
			if (currentPlayer == 0) {
				currentPlayer = 1;
			} else {
				currentPlayer = 0;
			}
		}
	}
	
	private void paintWinnersButtons(GridResult gridResult) {
		// TODO
	}

	private class HumanPlayer implements Player {
		// Para fazer a espera do jogador
		public Semaphore sem = new Semaphore(0);

		private int symbol;
		private GridState currentState;

		@Override
		public GridState play(GridState currentState) {
			this.currentState = currentState;
			enableAvailableButtons(currentState);
			try {
				// Esperando pela jogada nos bot�es
				sem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			disableAllButtons();

			// Estado atualizado a partir da UI
			return this.currentState;
		}

		public void enableAvailableButtons(GridState currentState) {
			for (int y = 0; y < buttonGrid.length; y++) {
				for (int x = 0; x < buttonGrid.length; x++) {
					// Habilitando bot�es onde estados s�o vazios
					buttonGrid[y][x].setEnabled(currentState.getState()[y][x] == GridState.E);
				}
			}
		}

		public void disableAllButtons() {
			for (int y = 0; y < buttonGrid.length; y++) {
				for (int x = 0; x < buttonGrid.length; x++) {
					// Habilitando bot�es onde estados s�o vazios
					buttonGrid[y][x].setEnabled(false);
				}
			}
		}

		public void setSymbol(int symbol) {
			this.symbol = symbol;
		}

		public void humanPlayOnCurrentState(Position position) {
			currentState = currentState.getDerivatedState(position, symbol);
		}
	}

	private void startBots() {
		botPlayer1 = new BotPlayer(zeroGridState, PLAYER_1, true);
		botPlayer2 = new BotPlayer(zeroGridState, PLAYER_2, false);
	}

	private void initStartingConditions() {
		currentPlayer = 0;
		currentState = zeroGridState;
		currentIsBotVsBot = showBotOrHumanDialog();

		if (!currentIsBotVsBot) {
			currentHumanFirst = showFirstHumanDialog();
		}
	}

	// Se retornar true, ent�o � bot vs bot. Caso contr�rio � bot vs humano
	private boolean showBotOrHumanDialog() {
		Object[] options = { "Bot vs Bot", "Humano vs Bot" };
		int response = JOptionPane.showOptionDialog(this, "Quais dever�o ser o jogador desafiante?", "Bot ou humano?",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (response == 0) {
			return true;
		}
		return false;
	}

	private boolean showFirstHumanDialog() {
		Object[] options = { "Sim", "N�o" };
		int response = JOptionPane.showOptionDialog(this, "O humano deve jogar primeiro?", "Quem come�a?",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

		if (response == 0) {
			return true;
		}
		return false;
	}

	private void setStateInFields(GridState gridState) {
		for (int y = 0; y < buttonGrid.length; y++) {
			for (int x = 0; x < buttonGrid.length; x++) {
				switch (gridState.getState()[y][x]) {
				case X:
					buttonGrid[y][x].setText("X");
					break;
				case O:
					buttonGrid[y][x].setText("O");
					break;
				default:
					buttonGrid[y][x].setText(" ");
				}
			}
		}
	}

	private void initGui() {
		setSize(640, 480);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		getContentPane().add(getGamePanel(), BorderLayout.CENTER);
		getContentPane().add(getStatusPanel(), BorderLayout.SOUTH);

	}

	private JPanel getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new JPanel();

			statusPanel.add(getStatusLabel());
			statusPanel.add(getRestartButton());
		}
		return statusPanel;
	}

	public JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel();
		}
		return statusLabel;
	}

	public JButton getRestartButton() {
		if (restartButton == null) {
			restartButton = new JButton("Reiniciar");
			restartButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setupGame();
				}
			});
		}
		return restartButton;
	}

	private JPanel getGamePanel() {
		if (gamePanel == null) {
			gamePanel = new JPanel();
			GridBagLayout gbl_gamePanel = new GridBagLayout();
			gamePanel.setLayout(gbl_gamePanel);

			GridBagConstraints gbc_textField00 = new GridBagConstraints();
			gbc_textField00.insets = new Insets(0, 0, 5, 5);
			gbc_textField00.gridx = 0;
			gbc_textField00.gridy = 0;
			gamePanel.add(buttonGrid[0][0], gbc_textField00);

			GridBagConstraints gbc_textField01 = new GridBagConstraints();
			gbc_textField01.insets = new Insets(0, 0, 5, 5);
			gbc_textField01.gridx = 1;
			gbc_textField01.gridy = 0;
			gamePanel.add(buttonGrid[0][1], gbc_textField01);

			GridBagConstraints gbc_textField02 = new GridBagConstraints();
			gbc_textField02.insets = new Insets(0, 0, 5, 0);
			gbc_textField02.gridx = 2;
			gbc_textField02.gridy = 0;
			gamePanel.add(buttonGrid[0][2], gbc_textField02);

			GridBagConstraints gbc_textField10 = new GridBagConstraints();
			gbc_textField10.insets = new Insets(0, 0, 5, 5);
			gbc_textField10.gridx = 0;
			gbc_textField10.gridy = 1;
			gamePanel.add(buttonGrid[1][0], gbc_textField10);

			GridBagConstraints gbc_textField11 = new GridBagConstraints();
			gbc_textField11.insets = new Insets(0, 0, 5, 5);
			gbc_textField11.gridx = 1;
			gbc_textField11.gridy = 1;
			gamePanel.add(buttonGrid[1][1], gbc_textField11);

			GridBagConstraints gbc_textField12 = new GridBagConstraints();
			gbc_textField12.insets = new Insets(0, 0, 5, 0);
			gbc_textField12.gridx = 2;
			gbc_textField12.gridy = 1;
			gamePanel.add(buttonGrid[1][2], gbc_textField12);

			GridBagConstraints gbc_textField20 = new GridBagConstraints();
			gbc_textField20.insets = new Insets(0, 0, 0, 5);
			gbc_textField20.gridx = 0;
			gbc_textField20.gridy = 2;
			gamePanel.add(buttonGrid[2][0], gbc_textField20);

			GridBagConstraints gbc_textField21 = new GridBagConstraints();
			gbc_textField21.insets = new Insets(0, 0, 0, 5);
			gbc_textField21.gridx = 1;
			gbc_textField21.gridy = 2;
			gamePanel.add(buttonGrid[2][1], gbc_textField21);

			GridBagConstraints gbc_textField22 = new GridBagConstraints();
			gbc_textField22.gridx = 2;
			gbc_textField22.gridy = 2;
			gamePanel.add(buttonGrid[2][2], gbc_textField22);
		}
		return gamePanel;
	}

	private JButton getNewButton() {
		JButton button = new JButton(" ");

		button.addActionListener((event) -> {
			// Find this button on grid:
			boolean buttonPositionFound = false;
			for (int y = 0; y < buttonGrid.length && !buttonPositionFound; y++) {
				for (int x = 0; x < buttonGrid.length && !buttonPositionFound; x++) {
					if (button.equals(buttonGrid[y][x])) {
						buttonPositionFound = true;
						Position buttonPosition = new Position(x, y);

						humanPlayer.humanPlayOnCurrentState(buttonPosition);
						humanPlayer.sem.release();
					}
				}
			}
		});

		button.setEnabled(false);

		return button;
	}
}
