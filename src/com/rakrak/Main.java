package com.rakrak;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        GameState gameState = new GameState();

        //gameState.loadStateFromFile("TODO.txt");
        gameState.loadDummyState();

        int whoseTurn = gameState.whoseTurn();
        Action action = gameState.getBestMove(whoseTurn, true);

        if (action != null) {
            System.out.println(action.info());
        } else {
            System.out.println("No action found...");
        }

        System.out.println("Sim completed.");
    }
}
