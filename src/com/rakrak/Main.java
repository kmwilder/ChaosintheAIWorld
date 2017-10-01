package com.rakrak;

import java.util.List;

public class Main {

    public static void main(String[] args) {
		
		GameState gameState = new GameState();
        boolean test_process = true;

        if(!test_process) {
            gameState.loadStateFromFile("TODO.txt");

            int whoseTurn = gameState.whoseTurn();

            Action action = gameState.getBestMove(whoseTurn, true);

            if (action != null) {
                System.out.println(action.info());
            } else {
                System.out.println("No action found...");
            }

            System.out.println("Sim completed.");
        } else {
            gameState.loadDummyState();
            while(!gameState.gameOver()) {

                int whoseTurn = gameState.whoseTurn();
                Action action = gameState.getBestMove(whoseTurn, true);
                System.out.println(action.info());

                List<GameState> gameStatesList = gameState.generateSuccessors(whoseTurn, action);

                double maxProb = 0;
                for(GameState nextGameState: gameStatesList) {
                    if(nextGameState.getProbability() > maxProb) {
                        gameState = nextGameState;
                        maxProb = nextGameState.getProbability();
                    }
                }
                gameState.setProbability(1.0);
            }
            System.out.println("Winner: " + Rules.Defines.getPlayerName(gameState.winner()));
        }
    }
}
