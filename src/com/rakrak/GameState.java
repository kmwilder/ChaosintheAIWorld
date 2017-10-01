package com.rakrak;

import java.util.*;

import static com.rakrak.Action.ActionType.*;
import static com.rakrak.GameState.BranchType.DECISION;
import static com.rakrak.GameState.BranchType.PROBABILITY;
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
    enum BranchType { NONE, PROBABILITY, DECISION }

    public Region[] regions;
    public Player[] players;

	// phases of the game
    private int activePlayer;
    private GamePhase gamePhase;
	private EndGamePhase endGamePhase;
	private int currentRegion;

    // Old world track
    private List<OldWorldCard> oldWorldDeck;
    private int oldWorldDrawn;
    public OldWorldCard[] oldWorldTrack;
    private boolean[] oldWorldOngoing;
    private boolean[] oldWorldResolved;

	// Probability of this gameState existing
    private double probability;
    private int decidingPlayer;
    private List<GameState> nextStates;
    private BranchType branchType;
    private String branchInfo;

	// Action queue for sideband actions
	private Queue<Action> actionQueue;

	GameState lastRound;

    // FIXME TODO this needs review, too
    GameState() {
        this.regions = Rules.defineBoard();
		
		players = new Player[NUM_PLAYERS];
		players[KHORNE] = new Player(KHORNE);
		players[NURGLE] = new Player(NURGLE);
		players[TZEENTCH] = new Player(TZEENTCH);
		players[SLAANESH] = new Player(SLAANESH);

        oldWorldDeck = Rules.generateOldWorldDeck();
        oldWorldDrawn = 0;
        oldWorldTrack = new OldWorldCard[2];
        oldWorldOngoing = new boolean[2];
        oldWorldResolved = new boolean[2];
		
		activePlayer = KHORNE;
        decidingPlayer = KHORNE;
        nextStates = null;
        branchInfo = null;
		
		gamePhase = OLDWORLD;

		probability = 1.0;

		// FIXME TODO drop this action queue?
        actionQueue = new LinkedList<Action>();

		lastRound = null;
    }

    // FIXME TODO review copying mechanisms
    GameState(GameState source) {
		this.regions = new Region[NUM_REGIONS];
		for(int i = 0; i < NUM_REGIONS; i++) {
			this.regions[i] = new Region(source.regions[i]);
		}
		this.players = new Player[NUM_PLAYERS];
		for(int i = 0; i < NUM_PLAYERS; i++) {
			this.players[i] = new Player(source.players[i]);
		}

		this.oldWorldDeck = new ArrayList<OldWorldCard>(source.oldWorldDeck);
        this.oldWorldDrawn = 0;
        this.oldWorldTrack = new OldWorldCard[2];
        this.oldWorldTrack[0] = source.oldWorldTrack[0];
        this.oldWorldTrack[1] = source.oldWorldTrack[1];

		this.activePlayer = source.activePlayer;
        this.decidingPlayer = source.decidingPlayer;
        this.options = new ArrayList<Object>(options);
		this.gamePhase = source.gamePhase;
		this.endGamePhase = source.endGamePhase;
		this.currentRegion = source.currentRegion;
		this.probability = source.probability;
		this.actionQueue = new LinkedList<Action>(source.actionQueue);
		lastRound = source.lastRound;
	}

	public void queue(Action action) {
		actionQueue.add(action);
	}
	
	public boolean loadStateFromFile(String filename) {
		// FIXME TODO
		
		// Simply support start of play for now.
		
		return true;
	}

	public void loadDummyState() {
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

        gamePhase = OLDWORLD;
        for(OldWorldCard owc : oldWorldDeck) {
            if(owc.getName().equals("Bretonnian Knights")) {
                oldWorldDeck.remove(owc);
                oldWorldTrack[0] = owc;
            }
        }
        regions[BRETONNIA].heroes++;
        regions[BORDERPRINCES].heroes++;

        gamePhase = DRAW;
        for(int i = 0; i < 5; i++) {
            players[KHORNE].drawCard();
            players[NURGLE].drawCard();
            players[TZEENTCH].drawCard();
            players[SLAANESH].drawCard();
        }

        gamePhase = SUMMON;
        activePlayer = KHORNE;

        summonPhase();
    }

    // Move the game state forward until it forks, due to some action or random event
    public void oldWorldPhase() {
        newTurn();

        // Draw a card
        branchType = PROBABILITY;
        for(int i = 0; i < oldWorldDeck.size(); i++) {
            GameState nextState = new GameState(this);
            nextState.setProbability(1.0/oldWorldDeck.size());
            nextState.setBranchInfo("Drew " + oldWorldDeck.get(i).getName());
            nextStates.add(nextState);
            nextState.drawOldWorldCard(i);
        }
        calculateProbabilities();
    }

    public void drawOldWorldCard(int cardIndex) {
        OldWorldCard owc = oldWorldDeck.remove(cardIndex);
        oldWorldDrawn++;

        // Place on track, if able
        if(!owc.discardImmediately()) {
            if(oldWorldTrack[0] != null) {
                oldWorldTrack[1] = oldWorldTrack[0];
            }
            oldWorldTrack[0] = owc;
        }

        // Execute old world card
        if(owc.targetsNeeded() == 0) {
            owc.immediate(this);
            ongoingOldWorlds();
        } else {
            // Branch condition
            List<Integer> validTargets = owc.validTargets(this);
            List<List<Integer>> branches = NTakeM(validTargets, owc.targetsNeeded());

            branchType = DECISION;
            decidingPlayer = getMinThreat();
            for(List<Integer> branch : branches) {
                GameState nextGameState = new GameState(this);
                nextGameState.setBranchInfo("Responding to " + owc.getName() + " by choosing " + branch.toString());
                nextStates.add(nextGameState);
                owc.immediate(nextGameState, branch);
                nextGameState.ongoingOldWorlds();
            }
            makeDecision();
        }
    }

    public void ongoingOldWorlds() {
        if(oldWorldTrack[0] != null && !oldWorldOngoing[0]) {
            oldWorldOngoing[0] = true;
            oldWorldTrack[0].ongoing(this);
        }
        if(oldWorldTrack[1] != null && !oldWorldOngoing[1]) {
            oldWorldOngoing[1] = true;
            oldWorldTrack[1].ongoing(this);
        }
        drawPhase();
    }

    public void drawPhase() {
        // FIXME TODO

        activePlayer = KHORNE;
        summonPhase();
    }

    public void summonPhase() {
        Player player = players[activePlayer];

        // Generate all possible moves
        branchType = DECISION;
        decidingPlayer = activePlayer;

        for(int dstRegionIndex = 0; dstRegionIndex < NUM_REGIONS; dstRegionIndex++) {
            Region dstRegion = regions[dstRegionIndex];

            // Place plastic if adjacent
            if(playerAdjacentTo(activePlayer, dstRegionIndex)) {

                // Place from reserve
                for(int i = 0; i < player.reserve.size(); i++) {
                    Plastic p = player.reserve.get(i);
                    if(player.pp >= p.getCost(this, dstRegionIndex)) {
                        GameState nextGameState = new GameState(this);
                        nextStates.add(nextGameState);
                        nextGameState.setBranchInfo(getPlayerName(activePlayer) + " SUMMON " + p.getName()
                                + " TO " + dstRegion.getName() + " FROM RESERVE");
                        nextGameState.executeMove(activePlayer, i, RESERVE, dstRegionIndex);
                    }
                }

                // Or from another region
                for(int srcRegionIndex = 0; srcRegionIndex < NUM_REGIONS; srcRegionIndex++) {
                    Region srcRegion = regions[srcRegionIndex];
                    if(srcRegionIndex != dstRegionIndex && srcRegion.canSummonOut()) {
                        for(int i = 0; i < srcRegion.plastic.size(); i++) {
                            Plastic p = srcRegion.plastic.get(i);
                            if(player.pp >= p.getCost(this, dstRegionIndex)) {
                                GameState nextGameState = new GameState(this);
                                nextStates.add(nextGameState);
                                nextGameState.setBranchInfo(getPlayerName(activePlayer) + " SUMMON " + p.getName()
                                        + " TO " + dstRegion.getName() + " FROM " + srcRegion.getName());
                                nextGameState.executeMove(activePlayer, i, RESERVE, dstRegionIndex);
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
                    if (dstRegion.canPlayCard(activePlayer, slot) && player.getPP() >= card.getCost(player, dstRegion)) {
                        GameState nextGameState = new GameState(this);
                        nextStates.add(nextGameState);
                        nextGameState.setBranchInfo(getPlayerName(activePlayer) + " PLAYS " + card.getName()
                                + " TO " + dstRegion.getName() + " SLOT " + slot);
                        nextGameState.playCard(activePlayer, cardIndex, dstRegionIndex, slot));
                    }
                }
            }
        }

        // Can also pass.
        GameState nextGameState = new GameState(this);
        nextStates.add(nextGameState);
        nextGameState.setBranchInfo(getPlayerName(activePlayer) + "PASSES");
        nextGameState.players[activePlayer].pp = 0;
        nextGameState.advanceSummonPhase();

        makeDecision();
    }

    public void executeMove(int playerIndex, int plasticIndex, int srcRegionIndex, int dstRegionIndex) {
        Plastic plastic;
        Player player = players[playerIndex];

        if(srcRegionIndex == RESERVE) {
            plastic = player.reserve.remove(plasticIndex);
        } else {
            plastic = regions[srcRegionIndex].plastic.remove(plasticIndex);
        }
        player.pp -= plastic.getCost(this, dstRegionIndex);
        regions[dstRegionIndex].plastic.add(plastic);

        advanceSummonPhase();
    }

    public void playCard(int playerIndex, int cardIndex, int dstRegionIndex, int slot) {
        Player player = players[playerIndex];
        ChaosCard card = player.hand.get(cardIndex);
        Region region = regions[dstRegionIndex];

        // Place card in region
        player.pp -= card.getCost(player, region);
        player.hand.remove(cardIndex);
        if(region.cards.get(slot) != null) {
            // Discard the card under it
            ChaosCard dCard = region.cards.get(slot);
            players[dCard.belongsTo()].discardPile.add(dCard);
        }
        region.cards.set(slot, card);

        // Resolve effects
        // FIXME TODO need to handle targeted cards
        card.resolve(this, dstRegionIndex);

        advanceSummonPhase();
    }

    public void advanceSummonPhase() {
        int initialPlayer = activePlayer;
        activePlayer = (activePlayer + 1) % NUM_PLAYERS;

        while(players[activePlayer].pp == 0 && activePlayer != initialPlayer) {
            activePlayer = (activePlayer + 1) % NUM_PLAYERS;
        }

        if(activePlayer == initialPlayer && players[activePlayer].pp == 0) {
            battlePhase();
        } else {
            summonPhase();
        }
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
				break;
		}

		return actions;
	}

	// generateSuccessors
	// apply an action, then generate all possible successor gamestates
	// 	until the next player action;
	public List<GameState> generateSuccessors(int player, Action action) {
	    List<GameState> successors = new ArrayList<GameState>();

		GameState nextGameState;

		switch(action.getActionType()) {
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
					p.discardPile.add(p.hand.remove(i));
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
	    List<GameState> list = new ArrayList<GameState>();

		// discard any must discards immediately

        switch(gamePhase) {
            case OLDWORLD:
                // Draw a card
                for(int i = 0; i < oldWorldDeck.size(); i++) {
                    GameState nextState = new GameState(this);
                    nextState.setProbability(1.0/oldWorldDeck.size());
                    list.addAll(nextState.drawOldWorldCard(i));
                }
                // FIXME TODO
                break;
            case DRAW:
                // FIXME TODO
                break;
            case SUMMON:
                // FIXME TODO
                break;

            case BATTLE:
                // FIXME TODO
                break;
            case DOMINATE:
                // FIXME TODO
                break;
            case CORRUPTION:
                // FIXME TODO
                break;
            case END:
                switch(endGamePhase) {
                    case HERO:
                        // FIXME TODO
                        break;
                    case OLDWORLD:
                        // FIXME TODO
                        break;
                    case DIALS:
                        // FIXME TODO
                        break;
                }
                break;
        }
        return list;
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
			// a.setWinProbability(totalProb);
			if(totalProb > bestProb) {
				bestProb = totalProb;
				bestAction = a;
			}
		}

		/*
		if(print) {
			// Print actions in order of win probability.
			actions.sort(Comparator.comparing(Action::getWinProbability));
			Collections.reverse(actions);
			for (Action a: actions) {
				System.out.println(a.info());
			}
		}
		*/

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
	
	public boolean gameOver() {
		// FIXME TODO
        return false;
	}

	public int winner() {
		// FIXME TODO
        return BOARD;
	}

	//================================================================
	// Helper functions

    public boolean playerAdjacentTo(int playerIndex, int regionIndex) {
        // FIXME TODO
        return true;
    }

    public List<GameState> listify() {
        List<GameState> list = new ArrayList<GameState>();
        list.add(this);
        return list;
    }

    public int getMinThreat() {
        int min_threat = players[KHORNE].getThreat();
        int min_player = KHORNE;
        for(int i = 1; i < NUM_PLAYERS; i++) {
            if(players[i].getThreat() < min_threat) {
                min_threat = players[i].getThreat();
                min_player = i;
            }
        }
        return min_player;
    }

    private List<List<Integer>> NTakeM(List<Integer> choices, int needed) {
        List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();

        for(int i = 0; i < choices.size(); i++) {
            if(needed == 1) {
                List<Integer> thisChoice = new ArrayList<Integer>();
                thisChoice.add(choices.get(i));
                listOfLists.add(thisChoice);
            } else if(needed > 1) {
                List<List<Integer>> laterChoices = NTakeM(choices.subList(i+1, choices.size()), needed-1);
                for(List<Integer> laterChoice : laterChoices) {
                    laterChoice.add(choices.get(i));
                    listOfLists.add(laterChoice);
                }
            }
        }
        return listOfLists;
    }

    public int whoseTurn() {
        return activePlayer;
    }
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
	    this.probability = probability;
	}
	public String getBranchInfo() { return branchInfo; }
	public void setBranchInfo(String str) {
	    branchInfo = str;
        System.out.println("Exploring branch: " + branchInfo);
	}
}
