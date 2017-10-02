package com.rakrak;

import java.util.*;

import static com.rakrak.Rules.Defines.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Player {
	// Constant variables
	public int index;
	public String name;
	public boolean handKnown;

	// Game-wide vars
	public int vp;
	public List<Tick> dial;
	public int dial_position;
	public List<Upgrade> upgrades_possible;
	public List<Upgrade> upgrades_taken;
    public int peasants;

	// Turn-wide vars
	public int pp;
	public List<ChaosCard> deck;
	public List<ChaosCard> hand;
	public int handSize;
	public List<ChaosCard> discardPile;
	public List<Plastic> reserve;
    public int maxReserve;
	public int dacs;

	// Draw phase
	public int drawnThisTurn;
	public int discardedThisTurn;
	public int maxDraws;

	// Battle phase
	public int[] num_hits;
	public int[] num_kills;

	// End phase
	public int ticks;

	Player(int index) {
		name = getPlayerName(index);
		maxDraws = (index == TZEENTCH) ? 5 : 2;
		handKnown = false;
		handSize = 0;
		deck = Rules.generateDeck(index);
		hand = new ArrayList<ChaosCard>();
		drawnThisTurn = 0;
		discardedThisTurn = 0;
		pp = Rules.startingPP(index);
		vp = 0;
        peasants = 0;

		reserve = Rules.generateReserve(index);
        maxReserve = reserve.size();
		
		dial = Rules.generateDial(index);
		dial_position = 0;
		dacs = 0;
		
		upgrades_possible = Rules.generateUpgrades(index);
		upgrades_taken = new ArrayList<Upgrade>();

		num_hits = new int[NUM_REGIONS];
		num_kills = new int[NUM_REGIONS];

		this.index = index;
	}

	Player(Player source) {
	    this.index = source.index;
	    this.name = source.name;
        this.maxDraws = source.maxDraws;
        this.handKnown = source.handKnown;
        this.handSize = source.handSize;
        this.deck = new ArrayList<ChaosCard>(source.deck);
        this.hand = new ArrayList<ChaosCard>(source.hand);
        this.drawnThisTurn = source.drawnThisTurn;
        this.discardedThisTurn = source.discardedThisTurn;
        this.pp = source.pp;
        this.vp = source.vp;
        this.peasants = source.peasants;
        this.reserve = new ArrayList<Plastic>(source.reserve);
        this.maxReserve = source.maxReserve;
        this.dial = source.dial;
        this.dial_position = source.dial_position;
        this.dacs = source.dacs;

        this.upgrades_possible = new ArrayList<Upgrade>(source.upgrades_possible);
        this.upgrades_taken = new ArrayList<Upgrade>(source.upgrades_taken);

        num_hits = new int[NUM_REGIONS];
        num_kills = new int[NUM_REGIONS];
        for(int i = 0; i < NUM_REGIONS; i++) {
            num_hits[i] = source.num_hits[i];
            num_kills[i] = source.num_kills[i];
        }
    }

    public void newTurn() {
        drawnThisTurn = 0;
        discardedThisTurn = 0;
        pp = Rules.startingPP(index);
        // FIXME TODO upgrades here
        dacs = 0;
        num_hits = new int[NUM_REGIONS];
        num_kills = new int[NUM_REGIONS];
    }

	public boolean dialWin() {
		return (dial_position == dial.size() - 1);
	}

	// Logic used to determine possible actions
	public boolean canDraw() {
		if(index == TZEENTCH) {
			return hand.size() < maxDraws;
		} else {
			return drawnThisTurn < maxDraws;
		}
	}
	public boolean canDiscard() {
		if(index == TZEENTCH) {
			return (discardedThisTurn == 0 && drawnThisTurn == 0);
		} else {
			return false;
		}
	}

	public int hasUpgrade(Upgrade.UpgradeType type) {
		int count = 0;
		for(Upgrade u: upgrades_taken) {
			if(u.getType().equals(type)) {
				count++;
			}
		}
		return count;
	}

	public Tick getDialTick() {
		return dial.get(dial_position);
	}
	public int getThreat() { return dial.get(dial_position).getThreat(); }

	public void drawCard() {
	    // FIXME TODO
    }
    public void discard() {
        // FIXME TODO
    }
}
