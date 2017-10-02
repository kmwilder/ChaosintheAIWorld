package com.rakrak;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        GameState gameState = new GameState();

        //gameState.loadStateFromFile("TODO.txt");
        gameState.loadDummyState();

        System.out.println("\n\nSim completed. Surviving path as follows:");
        System.out.println(gameState.getBranchInfo());
    }
}
