package com.rakrak;

public class Main {

    public static void main(String[] args) {
	// write your code here
		Rules rules = new Rules();
		
		GameState gameState = new GameState(rules);
		
		gameState.loadStateFromFile("TODO.txt");
		
		Player whoseTurn = gameState.whoseTurn();
		
		Action action = gameState.getBestMove(whoseTurn);
		
		action.printInfo();
    }
}
