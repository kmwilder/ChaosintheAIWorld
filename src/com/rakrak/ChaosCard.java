package com.rakrak;

import java.util.ArrayList;
import java.util.List;

import static com.rakrak.Action.ActionType.*;
import static com.rakrak.Plastic.PlasticType.*;
import static com.rakrak.Rules.Defines.*;
import static com.rakrak.Region.RegionEffect.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public abstract class ChaosCard {
	protected int cost;
	protected String name;
	protected boolean magic;
	protected boolean needstarget = false;
	protected boolean needsdestination = false;
	protected int target;
	protected int destination;
	
	public int getCost(Player player, Region region) {
		if(region.hasEffect(TEMP_STASIS) && player.index != TZEENTCH) {
			return cost+2;
		}
		return cost;
	}
	public int getPower(Player player, Region region) {
		// check for cost modifiers in region
		return cost;
	}
	
	public boolean play(Player player, Region region) {
		int cost = getCost(player, region);
		player.pp -= cost;
		region.playCard(this);
		return true;
		// FIXME TODO make sure this works well w/ other features
	}

	public String getName() { return name; }
	public boolean isMagic() { return magic; }
	public boolean leaveNextTurn(GameState gameState, int region) { return false; }
	public void resolve(GameState gameState, int region) { /* do nothing by default */ }

	public boolean needsTarget() { return needstarget; }
	public List<Integer> validTargets(GameState gameState, int region) { return new ArrayList<Integer>(); }
	public void setTarget(int target) { this.target = target; }

	public boolean needsDestination() { return needsdestination; }
	public List<Integer> validDestinations() { return new ArrayList<Integer>(); }
	public void setDestination(int destination) { this.destination = destination; }
}

// Tzeentch
class DrainPower extends ChaosCard {
	DrainPower() {
		cost = 0; magic = false; name = "Drain Power";
	}
	public void resolve(GameState gameState, int region) {
		Region r = gameState.regions[region];
		boolean player_presence[] = new boolean[NUM_PLAYERS];
		for(Plastic p : r.plastic) {
			if(p.type == CULTIST) {
				player_presence[p.belongsTo] = true;
			}
		}
		for(int i = 0; i < NUM_PLAYERS; i++) {
			if(player_presence[i] && i != TZEENTCH) {
				gameState.players[i].pp -= 1;
			}
		}
		gameState.players[TZEENTCH].pp += 1;
	}
}

class ChangerOfWays extends ChaosCard {
	ChangerOfWays() {
		cost = 0; magic = true; name = "Changer of Ways"; needstarget = false;
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(CHANGER_OF_WAYS);
        // FIXME TODO are there follow-on actions here? (Cancel other cards?)
	}
}

class WarpShield extends ChaosCard {
	WarpShield() {
		cost = 0; magic = false; name = "Warp Shield"; needstarget = true;
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].plastic.get(target).warpShielded = true;
	}
	public List<Integer> validTargets(GameState gameState, int region) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < gameState.regions[region].plastic.size(); i++) {
			if(gameState.regions[region].plastic.get(i).belongsTo == TZEENTCH) {
				list.add(new Integer(i));
			}
		}
		return list;
	}
}

class TemporalStasis extends ChaosCard {
	TemporalStasis() {
		cost = 1; magic = true; name = "Temporal Stasis"; needstarget = false;
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(TEMP_STASIS);
	}
}

class MeddlingOfSkaven extends ChaosCard {
	MeddlingOfSkaven() {
		cost = 1; magic = false; name = "The Meddling of Skaven"; needstarget = true;
	}
	public void resolve(GameState gameState, int region) {
		gameState.queue(new Action(target, DISCARD_CARD));
		gameState.queue(new Action(target, DISCARD_CARD));
	}
	public List<Integer> validTargets(GameState gameState, int region) {
		List<Integer> list = new ArrayList<Integer>();
		boolean present[] = new boolean[NUM_PLAYERS];
		for(Plastic p: gameState.regions[region].plastic) {
			present[p.belongsTo] = true;
		}
		for(int i = 0; i < NUM_PLAYERS; i++) {
			if(present[i] && i != TZEENTCH) {
				list.add(new Integer(i));
			}
		}
		return list;
	}
}

class Teleport extends ChaosCard {
	Teleport() {
		cost = 1; magic = false; name = "Teleport"; needstarget = true; needsdestination = true;
	}
	public void resolve(GameState gameState, int region) {
		if( !gameState.regions[region].hasEffect(FIELD_OF_CARNAGE) || gameState.regions[region].plastic.get(target).belongsTo == KHORNE)  {
			gameState.queue(new Action(BOARD, MOVE_PLASTIC, target, region, destination));
		}
	}
	public List<Integer> validTargets(GameState gameState, int region) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < gameState.regions[region].plastic.size(); i++) {
			Plastic.PlasticType t = gameState.regions[region].plastic.get(i).type;
			if (t == CULTIST || t == WARRIOR) {
				list.add(new Integer(i));
			}
		}
		return list;
	}
}

class PersistenceOfChange extends ChaosCard {
	PersistenceOfChange() {
		cost = 1; magic = true; name = "The Persistence of Change";
	}
	public void resolve(GameState gameState, int region) {
		// No effect... but make sure this stays around if conditions are correct.
	}
	public boolean leaveNextTurn(GameState gameState, int region) {
		return (gameState.players[TZEENTCH].num_kills[region] > 0);
	}
}

class Dazzle extends ChaosCard {
	Dazzle() {
		cost = 2; magic = true; name = "Dazzle";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(DAZZLE);
	}
}

class PlagueTouch extends ChaosCard {
	PlagueTouch() {
		cost = 0; magic = false; name = "Plague Touch";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(PLAGUE_TOUCH);
	}
}

class PlagueAura extends ChaosCard {
	PlagueAura() {
		cost = 0; magic = true; name = "Plague Aura";
	}
	public void resolve(GameState gameState, int region) {
		// no effect via resolve
	}
	public int getPower(Player player, Region region) {
		int power = super.getPower(player, region);
		if(!region.hasEffect(CHANGER_OF_WAYS)) {
			power += region.corruption[NURGLE];
		}
		return power;
	}
}

class RainOfPus extends ChaosCard {
	RainOfPus() {
		cost = 1; magic = false; name = "Rain Of Pus";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(RAIN_OF_PUS);
	}
}

class Influenza extends ChaosCard {
	Influenza() {
		cost = 1; magic = false; name = "Influenza";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(INFLUENZA);
	}
}

class UltimatePlague extends ChaosCard {
	UltimatePlague() {
		cost = 3; magic = false; name = "Ultimate Plague";
	}
	public void resolve(GameState gameState, int region) {
		// no effect
	}
	public boolean leaveNextTurn(GameState gameState, int region) {
		return (gameState.regions[region].corruption[NURGLE]
			- gameState.lastRound.regions[region].corruption[NURGLE] > 0);
	}
}

class QuickenDecay extends ChaosCard {
	QuickenDecay() {
		cost = 1; magic = false; name = "Quicken Decay";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(QUICKEN_DECAY);
	}
}

class TheFinalRotting extends ChaosCard {
	TheFinalRotting() {
		cost = 2; magic = false; name = "The Final Rotting";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(FINAL_ROTTING);
	}
}

class TheStenchOfDeath extends ChaosCard {
	TheStenchOfDeath() {
		cost = 3; magic = false; name = "The Stench of Death";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(STENCH_OF_DEATH);
	}
}

class PerverseInfiltration extends ChaosCard {
	PerverseInfiltration() {
		cost = 0; magic = false; name = "Perverse Infiltration";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].corruption[SLAANESH] += 1;
	}
}

class AbyssalPact extends ChaosCard {
	AbyssalPact() {
		cost = 0; magic = true; name = "Abyssal Pact";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(ABYSSAL_PACT);
	}
}

class InsidiousLies extends ChaosCard {
	InsidiousLies() {
		cost = 1; magic = false; name = "Insidious Lies";
	}
	public void resolve(GameState gameState, int region) {
		// no effect
	}
	public int getPower(Player player, Region region) {
		int power = super.getPower(player, region);
		if(!region.hasEffect(CHANGER_OF_WAYS)) {
			power += region.heroes * 2;
			power += region.nobles * 2;
		}
		return power;
	}
}

class DarkInfluence extends ChaosCard {
	DarkInfluence() {
		cost = 1; magic = true; name = "Dark Influence"; needstarget = true; needsdestination=true;
	}
	public void resolve(GameState gameState, int region) {
		// mapping: peasants, heroes, nobles
		Region srcRegion = gameState.regions[region];
		if(target < srcRegion.peasants) {
			srcRegion.peasants--;
			gameState.regions[destination].peasants++;
		} else if(target < (srcRegion.peasants + srcRegion.heroes)) {
			srcRegion.heroes--;
			gameState.regions[destination].heroes++;
		} else if (target < (srcRegion.peasants + srcRegion.heroes + srcRegion.nobles)) {
			srcRegion.nobles--;
			gameState.regions[destination].nobles++;
		}
	}
	public List<Integer> validTargets(GameState gameState, int region) {
		List<Integer> list = new ArrayList<Integer>();
		Region srcRegion = gameState.regions[region];
		for(int i = 0; i < srcRegion.peasants + srcRegion.heroes + srcRegion.nobles; i++) {
			list.add(new Integer(i));
		}
		return list;
	}
}

class SoporificMusk extends ChaosCard {
	SoporificMusk() {
		cost = 2; magic = true; name = "Soporific Musk"; needstarget = true;
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].plastic.get(target).controlledBy = SLAANESH;
	}
	public List<Integer> validTargets(GameState gameState, int region) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < gameState.regions[region].plastic.size(); i++) {
			if(gameState.regions[region].plastic.get(i).controlledBy != SLAANESH) {
				list.add(new Integer(i));
			}
		}
		return list;
	}
}

class FieldOfEcstasy extends ChaosCard {
	FieldOfEcstasy() {
		cost = 2; magic = false; name = "Field of Ecstasy";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(FIELD_OF_ECSTASY);
	}
}

class DegenerateRoyalty extends ChaosCard {
	DegenerateRoyalty() {
		cost = 3; magic = false; name = "Degenerate Royalty";
	}
	public void resolve(GameState gameState, int region) {
		// Nothing Special
	}
	public int getCost(Player player, Region region) {
		int cost = super.getCost(player, region);
		int num_corruption = 0;
		for(int i = 0; i < NUM_PLAYERS; i++) {
			num_corruption += region.corruption[i];
		}
		if(num_corruption >= 3) {
			cost -= 3;
		}
		return cost;
	}
}

class BloodFrenzy extends ChaosCard {
	BloodFrenzy() {
		cost = 0; magic = false; name = "Blood Frenzy";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(BLOOD_FRENZY);
	}
}

class RitualSlaying extends ChaosCard {
	RitualSlaying() {
		cost = 1; magic = false; name = "Ritual Slaying";
	}
	public void resolve(GameState gameState, int region) {
	    // FIXME TODO
    }
	// FIXME TODO
}

class FieldOfCarnage extends ChaosCard {
	FieldOfCarnage() {
		cost = 1; magic = false; name = "Field of Carnage";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(FIELD_OF_CARNAGE);
	}
}

class BattleCry extends ChaosCard {
	BattleCry() {
		cost = 1; magic = false; name = "BattleCry";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(BATTLE_CRY);
	}
}

class TheSkullThrone extends ChaosCard {
	TheSkullThrone() {
		cost = 1; magic = false; name = "The Skull Throne";
	}
	public int getPower(Player player, Region region) {
		int power = super.getPower(player, region);

		// only add extra power to first skullthrone in region
		if(region.cards.size() >= 2 && region.cards.get(0).name.equals(this.name) && region.cards.get(1).name.equals(this.name) && region.cards.get(1) == this) {
			return power;
		}

		for(Plastic p : region.plastic) {
			if(p.controlledBy == KHORNE) {
				power += p.getAttacks(region) - 1;
			}
		}
		return power;
	}
	public void resolve(GameState gameState, int region) {
	    // no resolution needed
    }
}

class RebornInBlood extends ChaosCard {
	RebornInBlood() {
		cost = 2; magic = false; name = "Reborn In Blood";
	}
	public void resolve(GameState gameState, int region) {
		gameState.regions[region].addEffect(REBORN_IN_BLOOD);
	}
}

class TheBloodGodsCall extends ChaosCard {
	TheBloodGodsCall() {
		cost = 2; magic = false; name = "The Blood God's Call";
	}
	public void resolve(GameState gameState, int region) {
		gameState.queue(new Action(KHORNE, BGC, region));
	}
}