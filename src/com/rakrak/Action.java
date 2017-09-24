package com.rakrak;

import static com.rakrak.Action.ActionType.MOVE_PLASTIC;

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
		PASS,
		BGC
	}

	public int target_plastic;
	public int target_card;
	public ActionType actionType = null;
	public int srcRegion;
	public int dstRegion;
	public int slot;

	// Used for PASS, DRAW_CARD, DISCARD_CARD, KILL_PEASANT, PICK_UPGRADE
	Action(int player, ActionType actionType) {
		this.player = player;
		this.actionType = actionType;
	}

	// Blood God's Call needs a custom action :/
	Action(int player, ActionType actionType, int dstRegion) {
		this.player = player;
		this.actionType = actionType;
		this.dstRegion = dstRegion;
	}

	// Used for MOVE_PLASTIC
	Action(int player, ActionType actionType, int target, int srcRegion, int dstRegion) {
		this.player = player;
		this.actionType = actionType;
		if(actionType == MOVE_PLASTIC) {
			this.target_plastic = target;
			this.srcRegion = srcRegion;
			this.dstRegion = dstRegion;
		} else {
			// PLACE_CARD
			this.target_card = target;
			this.slot = srcRegion;
			this.dstRegion = dstRegion;
		}
	}

	// Used for KILL_PLASTIC
	Action(int player, ActionType actionType, int region, int plastic) {
		this.player = player;
		this.actionType = actionType;
		this.target_plastic = plastic;
		this.srcRegion = region;
		this.dstRegion = region;
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
