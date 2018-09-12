package br.edu.uni7.ia.ttt;

import static br.edu.uni7.ia.ttt.GridState.*;

// Tic-tac-toe
public class Anciana {
	
	public Anciana() {
//		int[][] initialStateArray = {
//				{O, X, E},
//				{E, O, X},
//				{E, O, X}
//			};
		int[][] initialStateArray = {
				{E, E, E},
				{E, E, E},
				{E, E, E}
			};
//		int[][] initialStateArray = {
//				{O, X, E},
//				{E, X, E},
//				{E, E, E}
//			};
//		int[][] initialStateArray = {
//			{E, O, E},
//			{E, X, E},
//			{X, E, E}
//		};
//		int[][] initialStateArray = {
//				{X, O, O},
//				{E, X, E},
//				{X, E, E}
//			};
		GridState initialState = new GridState(initialStateArray);
		System.out.println("Estado Inicial");
		System.out.println(initialState);
		
		Player o = new BotPlayer(initialState, GridState.O, true);
		Player x = new BotPlayer(initialState, GridState.X, false);
		
		Player[] players = {o, x};
		
		GridState playedState = players[0].play(initialState);
		System.out.println("Bot 0 jogou");
		System.out.println(playedState);
		
		int currentPlayer = 1;
		while (playedState != null) {
			playedState = players[currentPlayer].play(playedState);
			
			System.out.println("Bot " + currentPlayer + " jogou");
			System.out.println(playedState);
			
			if (currentPlayer == 0) {
				currentPlayer = 1;
			} else {
				currentPlayer = 0;
			}
		}
		
//		GridState playedState = o.play(initialState);
		
//		System.out.println(initialState);
//		System.out.println(playedState);
	}
	
	public static void main(String[] args) {
		new Anciana();
	}

}
