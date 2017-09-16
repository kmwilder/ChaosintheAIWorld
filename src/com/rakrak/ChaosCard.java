package com.rakrak;

/**
 * Created by Wilder on 9/6/2017.
 */
public class ChaosCard {
	private int cost;
	private String name;
	private String description;
	private boolean inPlay;
	private Region region;
	
	public int getCost(Region region) {
		// check for cost modifiers in region
		
		return cost;
	}
	
	public boolean play(Player player, Region region) {
		int cost = getCost(region);
		player.power -= cost;
		region.playCard(this);
		this.region = region;
		inPlay = true;
		return true;
		// FIXME TODO make sure this works well w/ other features
	}
	
	public boolean discard() {
		region.discard(this);
		inPlay = false;
		
	}
}
