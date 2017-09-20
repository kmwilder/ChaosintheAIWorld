package com.rakrak;

import java.util.*;

import static com.rakrak.Action.ActionType.*;
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

    enum GamePhase { OLDWORLD, DRAW, SUMMON, BATTLE, CORRUPTION, END }
    enum EndGamePhase { HERO, OLDWORLD, DIALS }

    private Rules rules;
    private Region[] regions;
    private Player[] players;
	private int activePlayer;

	// phases of the game
    private GamePhase gamePhase;
	private EndGamePhase endGamePhase;
	private int currentRegion;

	// Probability of this gameState existing
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
	
	public ArrayList<Action> getLegalActions(int playerIndex) {
	    ArrayList<Action> actions = new ArrayList<Action>();
		Player player = players[playerIndex];

		// It's a person's turn, which means they must:
		// * Choose where to place an old world token

		// What's the phase of the game?
		switch (gamePhase) {
			case OLDWORLD:
				// Choose where to place an Old World Token
				// FIXME TODO
				break;

			case DRAW:
				if (player.canDiscard()) {
					actions.add(new Action(playerIndex, DISCARD_CARD));
				}
				if (player.canDraw()) {
					actions.add(new Action(playerIndex, DRAW_CARD));
				}
				break;

			case SUMMON:
				for(int dstRegionIndex = 0; dstRegionIndex < NUM_REGIONS; dstRegionIndex++) {
					Region dstRegion = regions[dstRegionIndex];

					// Place plastic if adjacent
					if(playerAdjacentTo(playerIndex, dstRegionIndex)) {
						// Place from reserve
						for(Plastic p : player.reserve) {
							if(player.pp >= dstRegion.plasticCost(playerIndex, p)) {
								actions.add(new Action(playerIndex, MOVE_PLASTIC, p, RESERVE, dstRegionIndex));
							}
						}

						// Or from another region
						for(int srcRegionIndex = 0; srcRegionIndex < NUM_REGIONS; srcRegionIndex++) {
							Region srcRegion = regions[srcRegionIndex];
							if(srcRegionIndex != dstRegionIndex && srcRegion.canSummonOut()) {
								for(Plastic p : srcRegion.getPlasticControlledBy(playerIndex)) {
									if(player.pp >= dstRegion.plasticCost(playerIndex, p)) {
										actions.add(new Action(playerIndex, MOVE_PLASTIC, p, srcRegionIndex, dstRegionIndex));
									}
								}
							}
						}
					}

					// Play a card to available slots
					Set<ChaosCard> hand = player.handKnown ? player.hand : player.deck;
					for (ChaosCard card : hand) {
						for (int slot = 0; slot < dstRegion.getCardSlots(); slot++) {
							if (dstRegion.canPlayCard(playerIndex, slot) && player.getPP() >= dstRegion.cardCost(playerIndex, card)) {
								actions.add(new Action(playerIndex, PLACE_CARD, card, dstRegionIndex, slot));
							}
						}
					}
				}
				// Can also simply pass.
				actions.add(new Action(playerIndex, PASS));
				break;

			case BATTLE:
				// Choose hit allocation
				boolean hit_allocatable = false;
				Region region = regions[currentRegion];
				for(Plastic p : region.getPlasticNotControlledBy(playerIndex)) {
					if(player.num_hits[currentRegion] >= region.getDefense(playerIndex, p)) {
						actions.add(new Action(playerIndex, KILL_PLASTIC, p, currentRegion));
						hit_allocatable = true;
					}
				}
				if(region.peasants > 0) {
					actions.add(new Action(playerIndex, KILL_PEASANT));
					hit_allocatable = true;
				}
				if(!hit_allocatable) {
					actions.add(new Action(playerIndex, PASS));
				}

				break;

			case CORRUPTION:
				// No choices here...
				break;

			case END:
				switch (endGamePhase) {
					case HERO:
						// FIXME TODO

						break;
					case OLDWORLD:
						// FIXME TODO
						break;

					case DIALS:
						switch(player.getDialTick().getDialType()) {
							case UPGRADE:
								for(Upgrade upgrade: player.upgrades_possible) {
									actions.add(new Action(playerIndex, PICK_UPGRADE, upgrade));
								}
								break;
							case TOKEN:

								// FIXME TODO

								break;
							default:
								System.out.println("Hit default END.DIALS case, something went wrong.");
						}
						break;

					default:
						System.out.println("Fell through endGamePhase switch statement...");
				}
				// resolve Hero tokens (choose death?)

				// resolve end phase old world


				break;
		}

		return actions;
	}

	public boolean playerAdjacentTo(int playerIndex, int regionIndex) {
		// FIXME TODO
		return true;
	}
	
	// generateSuccessors
	// apply an action, then generate all possible successor gamestates
	// 	until the next player action;
	public ArrayList<GameState> generateSuccessors(int player, Action action) {
	    ArrayList<GameState> successors = new ArrayList<GameState>();
		// FIXME TODO
        return successors;
	}
	
	public Action getBestMove(int player, boolean print) {

		// From all successor moves...
		ArrayList<Action> actions = getLegalActions(player);
		
		Action bestAction = null;
		double bestProb = 0;

		// For each action...
		for (Action a: actions) {
			// Generate all successor states.
			ArrayList<GameState> nextStates = generateSuccessors(player, a);
			double totalProb = 0;

			for(GameState nextState: nextStates) {
				// Calc probability of successor state, add it to this action's prob
				totalProb += nextState.getProbability() * nextState.winProbability(player);
			}

			// Store calc'd data, flag this one if it's the best
			a.setWinProbability(totalProb);
			if(totalProb > bestProb) {
				bestProb = totalProb;
				bestAction = a;
			}
		}

		if(print) {
			// Print actions in order of win probability.
			actions.sort(Comparator.comparing(Action::getWinProbability));
			Collections.reverse(actions);
			for (Action a: actions) {
				a.printInfo();
			}
		}

		// Return the best one.
		return bestAction;
	}
	
	public double winProbability(int playerID) {
		if (gameOver() && winner() == playerID) {
			return 1;
		} else if(gameOver() && winner() != playerID) {
			return 0;
		} else {
			// find likely next player's state
			Action a = getBestMove(activePlayer, false);
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
