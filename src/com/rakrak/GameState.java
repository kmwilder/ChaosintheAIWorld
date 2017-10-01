package com.rakrak;

import java.util.*;

import static com.rakrak.Action.ActionType.*;
import static com.rakrak.GameState.GamePhase.*;
import static com.rakrak.Rules.Defines.*;

/**
 * Created by Wilder on 9/6/2017.
 * Represents full game state, used by:
 *      Game object, to capture actual state of game.
 *      Agents, to reason about the game.
 */
public class GameState {

    enum GamePhase { OLDWORLD, DRAW, SUMMON, BATTLE, DOMINATE, CORRUPTION, END }
    enum EndGamePhase { HERO, OLDWORLD, DIALS }

    public Region[] regions;
    public Player[] players;

	// phases of the game
    private int activePlayer;
    private GamePhase gamePhase;
	private EndGamePhase endGamePhase;
	private int currentRegion;

	// Probability of this gameState existing
    private double probability;

	// Action queue for sideband actions
	private Queue<Action> actionQueue;

	GameState lastRound;

    GameState() {
        this.regions = Rules.defineBoard();
		
		players = new Player[NUM_PLAYERS];
		players[KHORNE] = new Player(KHORNE);
		players[NURGLE] = new Player(NURGLE);
		players[TZEENTCH] = new Player(TZEENTCH);
		players[SLAANESH] = new Player(SLAANESH);
		
		activePlayer = KHORNE;
		
		gamePhase = OLDWORLD;

		probability = 1.0;

		actionQueue = new LinkedList<Action>();
		lastRound = null;
    }

    GameState(GameState source) {
		this.regions = new Region[NUM_REGIONS];
		for(int i = 0; i < NUM_REGIONS; i++) {
			this.regions[i] = new Region(source.regions[i]);
		}
		this.players = new Player[NUM_PLAYERS];
		for(int i = 0; i < NUM_PLAYERS; i++) {
			this.players[i] = new Player(source.players[i]);
		}
		this.activePlayer = source.activePlayer;
		this.gamePhase = source.gamePhase;
		this.endGamePhase = source.endGamePhase;
		this.currentRegion = source.currentRegion;
		this.probability = source.probability;
		this.actionQueue = new LinkedList<Action>(source.actionQueue);
		lastRound = source.lastRound;
	}

	public List<GameState> listify() {
		List<GameState> list = new ArrayList<GameState>();
		list.add(this);
		return list;
	}

	public void queue(Action action) {
		actionQueue.add(action);
	}
	
	public boolean loadStateFromFile(String filename) {
		// FIXME TODO
		
		// Simply support start of play for now.
		
		return true;
	}

	public boolean loadDummyState() {
	    // populate this with the start state of citow-pbf008
        regions[NORSCA].warpstones = 1;
        regions[TROLLCOUNTRY].warpstones = 1;
        regions[EMPIRE].heroes = 1;
        regions[BRETONNIA].heroes = 1;
        regions[BRETONNIA].warpstones = 1;
        regions[ESTALIA].nobles = 1;
        regions[TILEA].peasants = 1;
        regions[BORDERPRINCES].nobles = 1;
        regions[BADLANDS].peasants = 1;

        // FIXME TODO left off here
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
						for(int i = 0; i < player.reserve.size(); i++) {
							Plastic p = player.reserve.get(i);
							if(player.pp >= p.getCost(this, dstRegionIndex)) {
								actions.add(new Action(playerIndex, MOVE_PLASTIC, i, RESERVE, dstRegionIndex));
							}
						}

						// Or from another region
						for(int srcRegionIndex = 0; srcRegionIndex < NUM_REGIONS; srcRegionIndex++) {
							Region srcRegion = regions[srcRegionIndex];
							if(srcRegionIndex != dstRegionIndex && srcRegion.canSummonOut()) {
								for(int i = 0; i < srcRegion.plastic.size(); i++) {
									Plastic p = srcRegion.plastic.get(i);
									if(player.pp >= p.getCost(this, dstRegionIndex)) {
										actions.add(new Action(playerIndex, MOVE_PLASTIC, i, srcRegionIndex, dstRegionIndex));
									}
								}
							}
						}
					}

					// Play a card to available slots
					List<ChaosCard> hand = player.handKnown ? player.hand : player.deck;
                    for(int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
                        ChaosCard card = hand.get(cardIndex);
						for (int slot = 0; slot < dstRegion.getCardSlots(); slot++) {
							if (dstRegion.canPlayCard(playerIndex, slot) && player.getPP() >= card.getCost(player, dstRegion)) {
								actions.add(new Action(playerIndex, PLACE_CARD, cardIndex, dstRegionIndex, slot));
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
                for(int plasticIndex = 0; plasticIndex < region.plastic.size(); plasticIndex++) {
                    Plastic p = region.plastic.get(plasticIndex);
					if(p.controlledBy != playerIndex && player.num_hits[currentRegion] >= p.getDefense(this, currentRegion)) {
						actions.add(new Action(playerIndex, KILL_PLASTIC, plasticIndex, currentRegion));
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
	public List<GameState> generateSuccessors(int player, Action action) {
	    List<GameState> successors = new ArrayList<GameState>();

		GameState nextGameState;

		switch(action.actionType) {
			case MOVE_PLASTIC:
				// FIXME TODO
				break;
			case MOVE_TOKEN:
				// FIXME TODO
				break;
			case KILL_PLASTIC:

				break;
			case DISCARD_PLASTIC:
				break;
			case DISCARD_TOKEN:
				break;
			case PLACE_CARD:

				break;

			case DISCARD_CARD:
				for (int i = 0; i < players[player].hand.size(); i++) {
					nextGameState = new GameState(this);
					Player p = nextGameState.players[player];
					ChaosCard cc = p.hand.get(i);
					p.discard.add(p.hand.remove(i));
					p.discardedThisTurn++;
					successors.addAll(nextGameState.resolve());
				}
				break;

			case DRAW_CARD:
				for(int i = 0; i < players[player].deck.size(); i++) {
					nextGameState = new GameState(this);
					Player p = nextGameState.players[player];
					p.hand.add(p.deck.remove(i));
					p.drawnThisTurn++;
					successors.addAll(nextGameState.resolve());
				}
				break;

			case PICK_UPGRADE:
				for(int i = 0; i < players[player].upgrades_possible.size(); i++) {
					nextGameState = new GameState(this);
					Player p = nextGameState.players[player];
					p.upgrades_taken.add(p.upgrades_possible.remove(i));
					// FIXME amend rules here?
					successors.addAll(nextGameState.resolve());
				}
				break;

			case PASS:
				nextGameState = new GameState(this);
				nextGameState.players[player].vp = 0;
				successors.addAll(nextGameState.resolve());
				break;
		}

        return successors;
	}

	// Now that the action is complete, move the game forward.
	public List<GameState> resolve() {
		// discard any must discards immediately

        // FIXME TODO
        return null;
	}
	
	public Action getBestMove(int player, boolean print) {

		// From all successor moves...
		ArrayList<Action> actions = getLegalActions(player);
		
		Action bestAction = null;
		double bestProb = 0;

		// For each action...
		for (Action a: actions) {
			// Generate all successor states.
			List<GameState> nextStates = generateSuccessors(player, a);
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
			List<GameState> nextGameStates = generateSuccessors(activePlayer, a);
			
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
}
