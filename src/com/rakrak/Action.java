package com.rakrak;

import static com.rakrak.Action.ActionType.*;

public final class Action {
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

    private final int player;
	private final int target_plastic;
	private final int target_card;
	private final ActionType actionType;
	private final int srcRegion;
	private final int dstRegion;
	private final int slot;
    private final Upgrade upgrade;

    // Getters... though perhaps instead, this should execute on gameState?
    public int getPlayer()          { return player; }
    public int getTargetPlastic()   { return target_plastic; }
    public int getTargetCard()      { return target_card; }
    public ActionType getActionType() { return actionType; }
    public int getSrcRegion()       { return srcRegion; }
    public int getDstRegion()       { return dstRegion; }
    public int getSlot()            { return slot; }
    public Upgrade getUpgrade()     { return upgrade; }

	// Used for PASS, DRAW_CARD, DISCARD_CARD, KILL_PEASANT
	Action(int player, ActionType actionType) {
		this.player = player;
		this.actionType = actionType;

		// not used
		target_plastic = 0;
		target_card = 0;
		srcRegion = 0;
		dstRegion = 0;
		slot = 0;
		upgrade = null;
	}

	// Blood God's Call needs a custom action :/
	Action(int player, ActionType actionType, int dstRegion) {
		this.player = player;
		this.actionType = actionType;
		this.dstRegion = dstRegion;

		// not used
		target_plastic = 0;
		target_card = 0;
		srcRegion = 0;
		slot = 0;
		upgrade = null;
	}

	// Used for MOVE_PLASTIC or PLACE_CARD
	Action(int player, ActionType actionType, int target, int srcRegionOrSlot, int dstRegion) {
		this.player = player;
		this.actionType = actionType;

		this.target_plastic = target; // MOVE_PLASTIC
		this.target_card = target; // PLACE_CARD
		this.srcRegion = srcRegionOrSlot; // MOVE_PLASTIC
		this.slot = srcRegionOrSlot; // PLACE_CARD
		this.dstRegion = dstRegion; // both

		this.upgrade = null;
	}

	// Used for KILL_PLASTIC, DISCARD_PLASTIC
	Action(int player, ActionType actionType, int region, int plastic) {
		this.player = player;
		this.actionType = actionType;
		this.target_plastic = plastic;
		this.srcRegion = region;
		this.dstRegion = region;

		// not used
		target_card = 0;
		slot = 0;
		upgrade = null;
	}

	// Used for PICK_UPGRADE
    Action(int player, ActionType actionType, Upgrade upgrade) {
        this.player = player;
        this.actionType = actionType;
        this.upgrade = upgrade;

		// not used
		target_plastic = 0;
		target_card = 0;
		srcRegion = 0;
		dstRegion = 0;
		slot = 0;
    }

	public String info() {
		// Used to generate info text about this action.
        String str = Rules.Defines.getPlayerName(player);
		switch(actionType) {
            case MOVE_PLASTIC:
                str += " MOVE_PLASTIC " + target_plastic + " FROM " +
                        Rules.Defines.getRegionName(srcRegion) + " TO " +
                        Rules.Defines.getRegionName(dstRegion);
                break;
            case MOVE_TOKEN:
                str += " FIXME TODO NOT IMPLEMENTED";
                // FIXME TODO
                break;
            case KILL_PLASTIC:
                str += " KILL_PLASTIC " + target_plastic + " FROM " +
                        Rules.Defines.getRegionName(dstRegion);
                break;
            case KILL_PEASANT:
                str += " KILL PEASANT";
                break;
            case DISCARD_PLASTIC:
                str += " DISCARD_PLASTIC " + target_plastic + " FROM " +
                        Rules.Defines.getRegionName(dstRegion);
                break;
            case DISCARD_TOKEN:
                str += " FIXME TODO NOT IMPLEMENTED";
                // FIXME TODO
                break;
            case PLACE_CARD:
                str += " PLACE_CARD " + target_card + " IN " +
                        Rules.Defines.getRegionName(dstRegion) +
                        " SLOT " + slot;
                break;
            case DISCARD_CARD:
                str += " DISCARD CARD";
                // FIXME TODO do we need a target card here?
                break;
            case DRAW_CARD:
                str += " DRAW CARD";
                break;
            case PICK_UPGRADE:
                str += " PICK_UPGRADE " + upgrade.getType();
                break;
            case PASS:
                str += " PASS";
                break;
            case BGC:
                str += " BLOOD GOD'S CALL to " + Rules.Defines.getRegionName(dstRegion);
                break;
		}
		return str;
	}
}
