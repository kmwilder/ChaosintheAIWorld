package com.rakrak;

import java.util.EnumMap;

import static com.rakrak.Rules.RegionName.*;

/**
 * Created by Wilder on 9/6/2017.
 * Represents full game state, used by:
 *      Game object, to capture actual state of game.
 *      Agents, to reason about the game.
 */



public class GameState {

    enum GamePhase { OLDWORLD, DRAW, SUMMON, BATTLE, CORRUPTION, END };

    private Rules rules;
    private Region[] regions;
    private Player[] players;
	private PlayerIndex activePlayer;

    private GamePhase gamePhase;


    GameState(Rules rules) {
        this.rules = rules;
		
        this.regions = rules.defineBoard();
		
		players = new Player[NUM_PLAYERS];
		players[KHORNE] = new Player(rules, KHORNE);
		players[NURGLE] = new Player(rules, NURGLE);
		players[TZEENTCH] = new Player(rules, TZEENTCH);
		players[SLAANESH] = new Player(rules, SLAANESH);
		
		activePlayer = KHORNE;
		
		gamePhase = OLDWORLD;
    }
	
	public boolean loadStateFromFile(String filename) {
		// FIXME TODO
		
		// Simply support start of play for now.
		
		return true;
	}
	
	public ArrayList<Action> getLegalActions(Player player) {
		// FIXME TODO
		for (Region r in regions) {
			// can play a card
			
			// or play a piece
			
		}
	}
	
	// generateSuccessors
	// apply an action, then generate all possible successor gamestates
	// 	until the next player action;
	public ArrayList<GameState> generateSuccessors(Player player, Action action) {
		// FIXME TODO
	}
	
	public Action getBestMove(Player player) {
		// FIXME TODO player is mutable... risk of corrupting state while searching
		// need to fix this while maintaining equivalence
		
		// From all successor moves...
		ArrayList<Action> actions = getLegalActions(player);
		
		Action bestAction = null;
		double bestProb = 0;
		
		for (Action a in actions) {
			ArrayList<GameState> nextStates = generateSuccessors(player, a);
			for( GameState nextState in nextStates) {
				double thisProb = nextState.getProbability() * nextState.winProbability(player.getID);
				if(thisProb > bestProb) {
					bestProb = thisProb;
					bestAction = a;
				}
			}
		}
		
		return bestAction;
	}
	
	public double winProbability(PlayerIndex playerID) {
		if (gameOver() && winner == playerID) {
			return 1;
		} else if(isWin && winner != playerID) {
			return 0;
		} else {
			// find likely next player's state
			Action a = getBestMove(activePlayer);
			ArrayList<GameState> nextGameStates = generateSuccessors(activePlayer, a);
			
			double probability = 0;
			for( GameState nextGameState in nextGameStates ) {
				probability += nextGameState.getProbability() * nextGameState.winProbability(playerID);
			}
			return probability;
		}
	}
	
	public Player whoseTurn() {
		return activePlayer;
	}
	
	public boolean gameOver() {
		// FIXME TODO
	}
	public PlayerIndex winner() {
		// FIXME TODO
	}
	public getProbability() {
		return probability;
	}
	public setProbability(double probability) {
		this.probability = probability;
	}

    // Should store:
    // gameStateData (p much all counters, token placements, phase of game, etc)

    // Will need to implement:
    // getScore( agentIndex ) returns expected score (given successive gamestates?)

}
