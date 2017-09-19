package com.rakrak;

import java.util.ArrayList;
import static com.rakrak.PlayerIndex.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Player {
	public int index;
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
	
	public ArrayList<Upgrade> upgrades_possible;
	public ArrayList<Upgrade> upgrades_taken;
	
	private Rules rules;
	
	Player(Rules rules, int index) {
		name = PlayerIndex.getPlayerName(index);
		deck = rules.generateDeck(index);
		hand = new ArrayList<ChaosCard> ();
		handSize = 0;
		handKnown = false;
		pp = rules.startingPP(index);
		vp = 0;
		reserve = rules.generateReserve(index);
		
		dial = rules.generateDial(index);
		dial_position = 0;
		dacs = 0;
		
		upgrades_possible = rules.generateUpgrades(index);
		upgrades_taken = new ArrayList<Upgrade>();
		
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
