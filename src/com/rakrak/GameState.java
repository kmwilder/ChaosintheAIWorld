package com.rakrak;

import java.util.*;

import static com.rakrak.GameState.BranchType.*;
import static com.rakrak.Rules.Defines.*;
import static com.rakrak.GameState.Defines.*;

/**
 * Created by Wilder on 9/6/2017.
 * Represents full game state, used by:
 *      Game object, to capture actual state of game.
 *      Agents, to reason about the game.
 */
public class GameState {
    public static class Defines {
        public static final int MINMAX = 0, MAX = 1;
        public static final int DECISION_POLICY = MAX;
        public static final boolean VERBOSE = true;
        public static final boolean EARLY_END = true; // shrinks numbers for debugging
        public static void dbgPrint(String str) {
            if(VERBOSE) {
                System.out.println(str);
            }
        }
    }
    enum BranchType { NONE, PROBABILITY, DECISION }

    public Region[] regions;
    public Player[] players;

	// phases of the game
    private int activePlayer;
	private int currentRegion;

    // Old world track
    private List<OldWorldCard> oldWorldDeck;
    private int oldWorldDrawn;
    private int ruinedCount;
    public OldWorldCard[] oldWorldTrack;
    private boolean[] oldWorldOngoing;
    private boolean[] oldWorldResolved;

	// Probability of this gameState existing
    private double probability;
    private int decidingPlayer;
    private List<GameState> nextStates;
    private BranchType branchType;
    private String branchInfo;

    private double[] winProbabilities;

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
        ruinedCount = 0;
        oldWorldTrack = new OldWorldCard[2];
        oldWorldOngoing = new boolean[2];
        oldWorldResolved = new boolean[2];
		
		activePlayer = KHORNE;
        decidingPlayer = KHORNE;
        nextStates = new ArrayList<GameState>();
        branchInfo = "";
        branchType = NONE;
        winProbabilities = new double[NUM_PLAYERS];

		probability = 1.0;

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
        this.oldWorldDrawn = source.oldWorldDrawn;
        this.ruinedCount = source.ruinedCount;
        this.oldWorldTrack = new OldWorldCard[2];
        this.oldWorldOngoing = new boolean[2];
        this.oldWorldResolved = new boolean[2];
        for(int i = 0; i < 2; i++) {
            this.oldWorldTrack[i] = source.oldWorldTrack[i];
            this.oldWorldOngoing[i] = source.oldWorldOngoing[i];
            this.oldWorldResolved[i] = source.oldWorldResolved[i];
        }

		this.activePlayer = source.activePlayer;
        this.decidingPlayer = source.decidingPlayer;
        nextStates = new ArrayList<GameState>();
        branchInfo = "";
        branchType = NONE;
        winProbabilities = new double[NUM_PLAYERS];

		this.currentRegion = source.currentRegion;
		this.probability = source.probability;
		lastRound = source.lastRound;
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

        for(OldWorldCard owc : oldWorldDeck) {
            if(owc.getName().equals("Bretonnian Knights")) {
                oldWorldDeck.remove(owc);
                oldWorldTrack[0] = owc;
            }
        }
        oldWorldDrawn++;
        regions[BRETONNIA].heroes++;
        regions[BORDERPRINCES].heroes++;

        for(int i = 0; i < 5; i++) {
            players[KHORNE].drawCard();
            players[NURGLE].drawCard();
            players[TZEENTCH].drawCard();
            players[SLAANESH].drawCard();
        }

        activePlayer = KHORNE;
        lastRound = this;

        // First round of moves
        // K: BT to Empire
        regions[EMPIRE].plastic.add(players[KHORNE].reserve.remove(10));
        players[KHORNE].pp -= 3;

        // N, T, S play cards:
        // FIXME TODO
        players[NURGLE].pp--;
        players[TZEENTCH].pp--;
        players[SLAANESH].pp--;

        // K: Bloodsworn to TBP
        regions[BORDERPRINCES].plastic.add(players[KHORNE].reserve.remove(0));
        players[KHORNE].pp--;

        // N: Leper to Estalia
        regions[ESTALIA].plastic.add(players[NURGLE].reserve.remove(0));
        players[NURGLE].pp--;

        // T: Acolyte to Bret
        regions[BRETONNIA].plastic.add(players[TZEENTCH].reserve.remove(0));
        players[TZEENTCH].pp--;

        // S: Seductress to Tilea
        regions[TILEA].plastic.add(players[SLAANESH].reserve.remove(0));
        players[SLAANESH].pp--;

        summonPhase();
    }

    private void newTurn() {
        // Reset per-turn counters throughout the gameState
        for(Region region : regions) {
            region.newTurn();
        }
        for(Player player : players) {
            player.newTurn();
        }

        activePlayer = KHORNE;
        currentRegion = NORSCA;
        for(int i = 0; i < 2; i++) {
            oldWorldOngoing[i] = false;
            oldWorldResolved[i] = false;
        }

        // And also set this as our startpoint for comparison reasons.
        lastRound = this;
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
        collapse();
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
            collapse();
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
        /*
            if (player.canDiscard()) {
                actions.add(new Action(playerIndex, DISCARD_CARD));
            }
            if (player.canDraw()) {
                actions.add(new Action(playerIndex, DRAW_CARD));
            }

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

            break;
         */

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

                // Place from reserve; shave this down to only try one of each type
                EnumSet<Plastic.PlasticType> tried = EnumSet.noneOf(Plastic.PlasticType.class);
                for(int i = 0; i < player.reserve.size(); i++) {
                    Plastic p = player.reserve.get(i);
                    if(!tried.contains(p.type) && player.pp >= p.getCost(this, dstRegionIndex)) {
                        tried.add(p.type);
                        GameState nextGameState = new GameState(this);
                        nextStates.add(nextGameState);
                        nextGameState.setBranchInfo(getPlayerName(activePlayer)
                                + "(" + player.pp + "pp) SUMMON " + p.getName()
                                + "(" + p.getCost(this, dstRegionIndex) + "pp) "
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
                            if(p.controlledBy == activePlayer && player.pp >= p.getCost(this, dstRegionIndex)) {
                                GameState nextGameState = new GameState(this);
                                nextStates.add(nextGameState);
                                nextGameState.setBranchInfo(getPlayerName(activePlayer)
                                        + "(" + player.pp + "pp) SUMMON " + p.getName()
                                        + "(" + p.getCost(this, dstRegionIndex) + "pp)"
                                        + " TO " + dstRegion.getName() + " FROM " + srcRegion.getName());
                                nextGameState.executeMove(activePlayer, i, srcRegionIndex, dstRegionIndex);
                            }
                        }
                    }
                }
            }

            // Play a card to available slots
            List<ChaosCard> hand = player.hand;
            for(int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
                ChaosCard card = hand.get(cardIndex);
                for (int slot = 0; slot < dstRegion.getCardSlots(); slot++) {
                    if (dstRegion.canPlayCard(activePlayer, slot) && player.pp >= card.getCost(player, dstRegion)) {
                        GameState nextGameState = new GameState(this);
                        nextStates.add(nextGameState);
                        nextGameState.setBranchInfo(getPlayerName(activePlayer) + " PLAYS " + card.getName()
                                + " TO " + dstRegion.getName() + " SLOT " + slot);
                        nextGameState.playCard(activePlayer, cardIndex, dstRegionIndex, slot);
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

        collapse();
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

        while(players[activePlayer].pp <= 0 && activePlayer != initialPlayer) {
            activePlayer = (activePlayer + 1) % NUM_PLAYERS;
        }

        if(activePlayer == initialPlayer && players[activePlayer].pp <= 0) {
            currentRegion = NORSCA;
            battlePhase();
        } else {
            summonPhase();
        }
    }

    public void battlePhase() {
        // FIXME TODO D: this will be interesting
        /*
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
         */

        corruptionPhase();
    }

    public void corruptionPhase() {
        // FIXME TODO check for corruption modifiers, place corruption

        endPhase();
    }

    public void endPhase() {
        // Remove Chaos Cards
        for (int r = 0; r < NUM_REGIONS; r++) {
            Region region = regions[r];
            for (int c = 0; c < region.cards.size(); c++) {
                ChaosCard card = region.cards.get(c);
                if (!card.leaveNextTurn(this, r)) {
                    region.cards.set(c, null);
                    players[card.belongsTo()].discardPile.add(card);
                }
            }
        }

        resolveHeroes();
    }

    public void resolveHeroes() {
        // Resolve Hero tokens
        // FIXME TODO

        scoreRuinedRegions();
    }

    public void scoreRuinedRegions() {
        // Score Ruined Regions
        for(int rIndex = 0; rIndex < NUM_REGIONS; rIndex++) {
            Region region = regions[rIndex];
            Region regionl = lastRound.regions[rIndex];

            List<Player> firsts = null, seconds = null;
            int corruption_first = 0, corruption_second = 0;

            if(region.ruined && !regionl.ruined) {
                // It ruined this round. Score it.

                for(int pIndex = 0; pIndex < NUM_PLAYERS; pIndex++) {
                    int corruption = region.corruption[pIndex];
                    if (corruption > regionl.corruption[pIndex]) {
                        players[pIndex].vp += Rules.RuinerBonus(ruinedCount);
                    }

                    // Rank by corruption
                    if (corruption > corruption_first) {
                        corruption_second = corruption_first;
                        seconds = firsts;
                        firsts = new ArrayList<Player>();
                        firsts.add(players[pIndex]);
                        corruption_first = corruption;
                    } else if (corruption == corruption_first) {
                        firsts.add(players[pIndex]);
                    } else if (corruption > corruption_second) {
                        seconds = new ArrayList<Player>();
                        seconds.add(players[pIndex]);
                        corruption_second = corruption;
                    } else if (corruption == corruption_second) {
                        seconds.add(players[pIndex]);
                    }
                }

                // Score by corruption
                if(firsts.size() == 1) {
                    // 1st takes 1st
                    firsts.get(0).vp += Rules.RuinerFirst(ruinedCount, rIndex);

                    if(seconds.size() == 1) {
                        // 2nd takes 2nd
                        seconds.get(0).vp += Rules.RuinerSecond(ruinedCount, rIndex);
                    } else {
                        // 2nds split 2nd
                        int bonus = Rules.RuinerSecond(ruinedCount, rIndex) / seconds.size();
                        for(Player player : seconds) {
                            player.vp += bonus;
                        }
                    }
                } else {
                    // Everyone in first splits; 2nds get nothing
                    int bonus = (Rules.RuinerFirst(ruinedCount, rIndex) +
                                Rules.RuinerSecond(ruinedCount, rIndex)) / firsts.size();

                    for(Player player : firsts) {
                        player.vp += bonus;
                    }
                }
                ruinedCount++;
            }
        }

        advanceThreatDials();
    }

    public void advanceThreatDials() {
        // Advance Threat Dials
        // FIXME TODO

        /*
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

        case PICK_UPGRADE:
				for(int i = 0; i < players[player].upgrades_possible.size(); i++) {
					nextGameState = new GameState(this);
					Player p = nextGameState.players[player];
					p.upgrades_taken.add(p.upgrades_possible.remove(i));
					// FIXME amend rules here?
					successors.addAll(nextGameState.resolve());
				}
				break;
        */

        checkForGameEnd();
    }

    public void checkForGameEnd() {
        // Check for Game End

        Player winner = null;

        // Ticks win first
        for(int p = 0; p < NUM_PLAYERS; p++) {
            Player player = players[p];
            if(player.dialWin()) {
                if(winner == null || player.vp > winner.vp) {
                    winner = player;
                }
            }
        }

        // Otherwise, check for VP victory
        if(winner == null) {
            for(int p = 0; p < NUM_PLAYERS; p++) {
                Player player = players[p];
                if(player.vp >= 50 && (winner == null || player.vp > winner.vp
                        || (player.vp == winner.vp && player.getThreat() > winner.getThreat()))) {
                    winner = player;
                }
            }
        }

        if(winner != null) {
            // set winner probs, end of tree
            for(int p = 0; p < NUM_PLAYERS; p++) {
                winProbabilities[p] = (p == winner.index) ? 1.0 : 0.0;
            }
        } else if(oldWorldDrawn >= 1 || ruinedCount >= 6) {
            // Board victory if run out of old world or ruins
            //System.out.println("Board victory!");
            for(int p = 0; p < NUM_PLAYERS; p++) {
                winProbabilities[p] = 0.0;
            }
        } else {
            // The game continues.
            oldWorldPhase();
        }
    }

    //========================================================
    // Tree traversal functions
    public void collapse() {
        if(branchType == PROBABILITY) {
            // Condense them all down to a single win/loss probability
            GameState maxState = null; double maxProb = 0; // used to track most likely scenario for review
            for(GameState nextState : nextStates) {
                double thisProb = nextState.getProbability();
                for(int pIndex = 0; pIndex < NUM_PLAYERS; pIndex++) {
                    winProbabilities[pIndex] += thisProb * nextState.winProbabilities[pIndex];
                }
                if(maxState == null || thisProb > maxProb) {
                    maxProb = thisProb;
                    maxState = nextState;
                }
            }
            branchInfo = branchInfo + "\n" + "Expected P(" + maxProb + "): " + maxState.getBranchInfo();
        } else if(branchType == DECISION) {
            // Choose based on our decision policy
            if(DECISION_POLICY == MAX) {
                // Choose the best outcome for decidingPlayer;
                GameState best = null;
                for(GameState trial : nextStates) {
                    if(best == null) {
                        best = trial;
                    } else if(trial.winProbabilities[decidingPlayer] > best.winProbabilities[decidingPlayer]) {
                        best = trial;
                    }
                }
                winProbabilities = best.winProbabilities;
                branchInfo = branchInfo + "\n" + best.getBranchInfo();
            } else {
                // FIXME TODO MINMAX
            }
        } else {
            dbgPrint("Error: collapsing a non-branch");
        }
        // Can prune branches below afterwards.
        nextStates = null;
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
        Player player = players[playerIndex];
        if(player.reserve.size() == player.maxReserve) {
            return true; // no plastic on the board
        }

        // check this region
        for(Plastic p : regions[regionIndex].plastic) {
            if(p.controlledBy == playerIndex) {
                return true;
            }
        }

        // check adjacent regions
        for(int r : regions[regionIndex].getAdjacencies()) {
            for(Plastic p : regions[r].plastic) {
                if(p.controlledBy == playerIndex) {
                    return true;
                }
            }
        }
        return false;
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
	}
}
