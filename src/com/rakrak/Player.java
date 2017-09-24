package com.rakrak;

import java.util.*;

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
	public List<Upgrade> upgrades_possible;
	public List<Upgrade> upgrades_taken;

	// Turn-wide vars
	public int pp;
	public List<ChaosCard> deck;
	public List<ChaosCard> hand;
	public int handSize;
	public List<ChaosCard> discard;
	public List<Plastic> reserve;
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
		maxDraws = (index == TZEENTCH) ? 5 : 2;
		handKnown = false;
		handSize = 0;
		deck = rules.generateDeck(index);
		hand = new ArrayList<ChaosCard>();
		drawnThisTurn = 0;
		discardedThisTurn = 0;
		pp = rules.startingPP(index);
		vp = 0;

		reserve = rules.generateReserve(index);
		
		dial = rules.generateDial(index);
		dial_position = 0;
		dacs = 0;
		
		upgrades_possible = rules.generateUpgrades(index);
		upgrades_taken = new ArrayList<Upgrade>();

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

	public int hasUpgrade(Upgrade.UpgradeType type) {
		int count = 0;
		for(Upgrade u: upgrades_taken) {
			if(u.type.equals(type)) {
				count++;
			}
		}
		return count;
	}

	public Tick getDialTick() {
		return dial.get(dial_position);
	}

}
