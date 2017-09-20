package com.rakrak;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.rakrak.PlayerIndex.*;
import static com.rakrak.Rules.RegionName.NUM_REGIONS;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Player {
	// Constant variables
	public int index;
	public String name;
	public boolean handKnown;

	// Game-wide vars
	private Rules rules;
	public int vp;
	public List<Tick> dial;
	public int dial_position;
	public Set<Upgrade> upgrades_possible;
	public Set<Upgrade> upgrades_taken;

	// Turn-wide vars
	public int pp;
	public Set<ChaosCard> deck;
	public Set<ChaosCard> hand;
	public Set<Plastic> reserve;
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

	Player(Rules rules, int index) {
		name = PlayerIndex.getPlayerName(index);
		deck = rules.generateDeck(index);
		hand = new HashSet<ChaosCard>();
		maxDraws = (index == TZEENTCH) ? 5 : 2;
		handKnown = false;
		drawnThisTurn = 0;
		discardedThisTurn = 0;
		pp = rules.startingPP(index);
		vp = 0;

		reserve = rules.generateReserve(index);
		
		dial = rules.generateDial(index);
		dial_position = 0;
		dacs = 0;
		
		upgrades_possible = rules.generateUpgrades(index);
		upgrades_taken = new HashSet<Upgrade>();

		num_hits = new int[NUM_REGIONS];
		num_kills = new int[NUM_REGIONS];
		
		this.rules = rules;
		this.index = index;
	}
	
	public int getVP() {
		return vp;
	}
	public boolean dialWin() {
		return (dial_position == dial.size() - 1);
	}
	public int getPP() { return pp; }

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

	public Tick getDialTick() {
		return dial.get(dial_position);
	}

	public boolean adjacent(Region region) {
		// FIXME TODO
		return true;
	}
}
