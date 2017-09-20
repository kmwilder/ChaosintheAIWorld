package com.rakrak;

public class Main {

    public static void main(String[] args) {
		Rules rules = new Rules();
		
		GameState gameState = new GameState(rules);
		
		gameState.loadStateFromFile("TODO.txt");
		
		int whoseTurn = gameState.whoseTurn();
		
		Action action = gameState.getBestMove(whoseTurn, true);
		
		if(action != null) {
			action.printInfo();
		} else {
			System.out.println("No action found...");
		}

		System.out.println("Sim completed.");
    }
}
