package com.rakrak;

import java.util.ArrayList;
import java.util.EnumMap;

import static com.rakrak.GameState.GamePhase.*;
import static com.rakrak.Rules.RegionName.*;
import static com.rakrak.PlayerIndex.*;

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
	private int activePlayer;

    private GamePhase gamePhase;
    private double probability;

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
	
	public ArrayList<Action> getLegalActions(int player) {
	    ArrayList<Action> actions = new ArrayList<Action>();
		// FIXME TODO
		for (Region r: regions) {
		    // FIXME TODO
			// can play a card
			
			// or play a piece
			
		}
		return actions;
	}
	
	// generateSuccessors
	// apply an action, then generate all possible successor gamestates
	// 	until the next player action;
	public ArrayList<GameState> generateSuccessors(int player, Action action) {
	    ArrayList<GameState> successors = new ArrayList<GameState>();
		// FIXME TODO
        return successors;
	}
	
	public Action getBestMove(int player) {
		// FIXME TODO player is mutable... risk of corrupting state while searching
		// need to fix this while maintaining equivalence
		
		// From all successor moves...
		ArrayList<Action> actions = getLegalActions(player);
		
		Action bestAction = null;
		double bestProb = 0;
		
		for (Action a: actions) {
			ArrayList<GameState> nextStates = generateSuccessors(player, a);
			for(GameState nextState: nextStates) {
				double thisProb = nextState.getProbability() * nextState.winProbability(player);
				if(thisProb > bestProb) {
					bestProb = thisProb;
					bestAction = a;
				}
			}
		}
		
		return bestAction;
	}
	
	public double winProbability(int playerID) {
		if (gameOver() && winner() == playerID) {
			return 1;
		} else if(gameOver() && winner() != playerID) {
			return 0;
		} else {
			// find likely next player's state
			Action a = getBestMove(activePlayer);
			ArrayList<GameState> nextGameStates = generateSuccessors(activePlayer, a);
			
			double probability = 0;
			for( GameState nextGameState: nextGameStates ) {
				probability += nextGameState.getProbability() * nextGameState.winProbability(playerID);
			}
			return probability;
		}
	}
	
	public int whoseTurn() {
		return activePlayer;
	}
	
	public boolean gameOver() {
		// FIXME TODO
        return false;
	}

	public int winner() {
		// FIXME TODO
        return 0;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
	    this.probability = probability;
	}

    // Should store:
    // gameStateData (p much all counters, token placements, phase of game, etc)

    // Will need to implement:
    // getScore( agentIndex ) returns expected score (given successive gamestates?)

}
