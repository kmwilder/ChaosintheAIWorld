package com.rakrak;


import java.util.ArrayList;
import static com.rakrak.Rules.PlayerIndex.*;

public class Action {
	// Simple container class?
	public PlayerIndex player;
	enum ActionType {
		MOVE_PLASTIC,
		MOVE_TOKEN,
		DISCARD_PLASTIC,
		DISCARD_TOKEN,
		PLACE_CARD,
		DISCARD_CARDS,
		PICK_UPGRADE,
		PASS
	} 
	public Plastic target_plastic = null;
	public ActionType actionType = null;
	public Region srcRegion = null;
	public Region dstRegion = null;
	
	public Action chainedAction = null;
}
