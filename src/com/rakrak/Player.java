package com.rakrak;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Player {
	public PlayerIndex index;
	public String name;
	public int pp;
	public int vp;
	public ArrayList<ChaosCard> hand;
	public int handSize;
	public boolean handKnown;
	public ArrayList<ChaosCard> deck;
	public ArrayList<Plastic> reserve;
	
	public ArrayList<Tick> dial;
	public int dial_position;
	public int dacs;
	
	public ArrayList<Upgrades> upgrades_possible;
	public ArrayList<Upgrades> upgrades_taken;
	
	private Rules rules;
	
	Player(Rules rules, PlayerIndex index) {
		name = rules.getPlayerName(idx);
		deck = rules.generateDeck(idx);
		knownHand = new ArrayList<ChaosCard> ();
		handSize = 0;
		handKnown = false;
		pp = rules.startingPP(idx);
		vp = 0;
		reserve = rules.generateReserve(idx);
		
		dial = rules.generateDial(idx);
		dial_position = 0;
		dacs = 0;
		
		upgrades_possible = rules.generateUpgrades(idx);
		upgrades_taken = new ArrayList<Upgrades>();
		
		this.rules = rules;
		this.index = index;
	}
	
	public int getVP() {
		return vp;
	}
	public boolean dialWin() {
		return (dial_position == dial.size() - 1);
	}
}
