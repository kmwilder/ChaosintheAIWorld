package com.rakrak;

public class Action {
	// Simple container class?
	public int player;

	enum ActionType {
		MOVE_PLASTIC,
		MOVE_TOKEN,
		KILL_PLASTIC,
		KILL_PEASANT,
		DISCARD_PLASTIC,
		DISCARD_TOKEN,
		PLACE_CARD,
		DISCARD_CARD,
		DRAW_CARD,
		PICK_UPGRADE,
		PASS
	}

	public Plastic target_plastic = null;
	public ChaosCard target_card = null;
	public ActionType actionType = null;
	public Upgrade upgrade = null;
	public int srcRegion;
	public int dstRegion;
	public int slot;

	// Used for PASS, DRAW_CARD, DISCARD_CARD, KILL_PEASANT
	Action(int player, ActionType actionType) {
		this.player = player;
		this.actionType = actionType;
	}

	// Used for MOVE_PLASTIC
	Action(int player, ActionType actionType, Plastic target, int srcRegion, int dstRegion) {
		this.player = player;
		this.actionType = actionType;
		this.target_plastic = target;
		this.srcRegion = srcRegion;
		this.dstRegion = dstRegion;
	}

	// Used for PLACE_CARD
	Action(int player, ActionType actionType, ChaosCard card, int dstRegion, int slot) {
		this.player = player;
		this.actionType = actionType;
		this.target_card = card;
		this.dstRegion = dstRegion;
		this.slot = slot;
	}

	// Used for KILL_PLASTIC
	Action(int player, ActionType actionType, Plastic plastic, int region) {
		this.player = player;
		this.actionType = actionType;
		this.target_plastic = plastic;
		this.srcRegion = region;
		this.dstRegion = region;
	}

	// Used for PICK_UPGRADE
	Action(int player, ActionType actionType, Upgrade upgrade) {
		this.player = player;
		this.actionType = actionType;
		this.upgrade = upgrade;
	}

	private double winProbability;

	public void printInfo() {
		// FIXME TODO
		System.out.println("Action info: TODO");
	}

	public void setWinProbability(double prob) {
		winProbability = prob;
	}
	public double getWinProbability() {
		return winProbability;
	}
}
