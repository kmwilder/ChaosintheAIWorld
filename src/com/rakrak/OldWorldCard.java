package com.rakrak;

import java.util.ArrayList;
import java.util.List;

import static com.rakrak.Region.RegionEffect.*;
import static com.rakrak.Rules.Defines.*;

/**
 * Created by Wilder on 9/30/2017.
 */
public abstract class OldWorldCard {
    protected String name = "DEFAULT_NAME";
    protected boolean discard = false;
    protected int num = 1;
    protected int targets = 0;

    // Helper functions
    protected List<Integer> allRegions() {
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < NUM_REGIONS; i++) {
            list.add(new Integer(i));
        }
        return list;
    }
    public String getName() { return name; }
    public boolean hasComet() {
        return false; // FIXME TODO need to look up rules
    }

    // This should be called when drawn.
    public int targetsNeeded() { return targets; }
    public List<Integer> validTargets(GameState gameState) { return null; }
    public void immediate(GameState gameState) { }
    public void immediate(GameState gameState, int target) { }
    public void immediate(GameState gameState, List<Integer> targets) { }
    public boolean discardImmediately() { return discard; }

    // This should be called at the start of each turn.
    public void ongoing(GameState gameState) { }

    // This should be called at the end phase
    public void resolve(GameState gameState) { }

}

class PlungedIntoChaos extends OldWorldCard {
    PlungedIntoChaos() {
        name = "Plunged Into Chaos"; discard = true; num = 4;
    }
    public void immediate(GameState gameState) {
        // Each player scores 1 VP for each peasant.
        int max_peasants = 0;
        Player player_max = null;
        for(int i = 0; i < NUM_PLAYERS; i++) {
            Player p = gameState.players[i];

            // Check to see who has the most peasants;
            if(p.peasants == max_peasants) {
                player_max = null; // ties go to nobody
            } else if(p.peasants > max_peasants) {
                max_peasants = p.peasants;
                player_max = p;
            }

            // Also give this player VP for each peasant
            p.vp += p.peasants;
        }

        // Give 3VP to the max (if there is one)
        if(player_max != null) {
            player_max.vp += 3;
        }
    }
}

class PeasantUprising extends OldWorldCard {
    PeasantUprising() {
        name = "Peasant Uprising"; discard = false; num = 2; targets = 0;
    }
    public void immediate(GameState gameState) {
        // Place one Peasant token in every region but the badlands
        for(int i = 0; i < NUM_REGIONS; i++) {
            Region r = gameState.regions[i];
            if(i != BADLANDS) {
                r.peasants++;
            }
        }
    }
    public void ongoing(GameState gameState) {
        // Grant peasant uprising effect to every region
        for(int i = 0; i < NUM_REGIONS; i++) {
            Region r = gameState.regions[i];
            r.addEffect(Region.RegionEffect.PEASANT_UPRISING);
        }
    }
}

class WarpstoneDiscovery extends OldWorldCard {
    WarpstoneDiscovery() {
        name = "Warpstone Discovery"; discard = false; num = 1; targets = 1;
    }
    public List<Integer> validTargets(GameState gameState) {
        return allRegions();
    }
    public void immediate(GameState gameState, int target) {
        gameState.regions[target].warpstones += 2;
    }
    public void resolve(GameState gameState) {
        // Any player with at least one follower in a region containing a warpstone...
        boolean[] playerArray = new boolean[NUM_PLAYERS];

        for(int i = 0; i < NUM_REGIONS; i++) {
            Region r = gameState.regions[i];
            if(r.warpstones > 0) {
                for(Plastic p : r.plastic) {
                   playerArray[p.controlledBy] = true;
                }
            }
        }

        // Draws a chaos card
        for(int i = 0; i < NUM_PLAYERS; i++) {
            if(playerArray[i]) {
                gameState.players[i].drawCard();
            }
        }
    }
}

class TheCrusadeIsCome extends OldWorldCard {
    TheCrusadeIsCome() {
        name = "The Crusade is Come"; discard = true; num = 1; targets = 2;
    }
    public List<Integer> validTargets(GameState gameState) {
        return allRegions();
    }
    public void immediate(GameState gameState, List<Integer> targets) {
        // FIXME TODO how the hell will this be done
    }
}

class UpFromSkavenblight extends OldWorldCard {
    UpFromSkavenblight() {
        name = "Up From Skavenblight"; discard = true; num = 2; targets = 1;
    }
    public List<Integer> validTargets(GameState gameState) {
        // any region w/ a warpstone, or adjacent to a warpstone
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < NUM_REGIONS; i++) {
            Region region = gameState.regions[i];

            // sum warpstones here and in adjacent regions
            int local_warpstones = region.warpstones;
            int[] adjacencies = region.getAdjacencies();
            for(int j = 0; j < adjacencies.length; j++) {
                local_warpstones += gameState.regions[adjacencies[j]].warpstones;
            }
            if(local_warpstones > 0) {
                list.add(new Integer(i));
            }
        }
        return list;
    }
    public void immediate(GameState gameState, int target) {
        // Place a skaven token
        gameState.regions[target].skaven++;
    }
}

class MeteorShowers extends OldWorldCard {
    MeteorShowers() {
        name = "Meteor Showers"; discard = true; num = 2; targets = 2;
    }
    public List<Integer> validTargets(GameState gameState) {
        return allRegions();
    }
    public void immediate(GameState gameState, List<Integer> targets) {
        for(Integer i : targets) {
            gameState.regions[i.intValue()].warpstones++;
        }
    }
}

class GreenskinsInvade extends OldWorldCard {
    GreenskinsInvade() {
        name = "Greenskins Invade"; discard = false; num = 1; targets = 0;
    }
    public void immediate(GameState gameState) {
        // Remove all event tokens from board
        for(int i = 0; i < NUM_REGIONS; i++) {
            gameState.regions[i].events = 0;
        }

        // Remove all old world cards bearing twin tailed comet
        if(gameState.oldWorldTrack[0].hasComet() && gameState.oldWorldTrack[0] != this) {
            gameState.oldWorldTrack[0] = null;
        }
        if(gameState.oldWorldTrack[1].hasComet()) {
            gameState.oldWorldTrack[1] = null;
        }

        // Place an event in badlands, empire, tilea
        gameState.regions[BADLANDS].events++;
        gameState.regions[EMPIRE].events++;
        gameState.regions[TILEA].events++;
    }
    public void ongoing(GameState gameState) {
        for(int i = 0; i < NUM_REGIONS; i++) {
            Region region = gameState.regions[i];
            if(region.events > 0) {
                region.addEffect(GREENSKINS_INVADE);
            }
        }
    }
}

// We don't play w/ electors or the grail is found

class DwarfTrollslayers extends OldWorldCard {
    DwarfTrollslayers() {
        name = "Dwarf Trollslayers"; discard = false; num = 1; targets = 1;
    }
    public List<Integer> validTargets(GameState gameState) {
        List<Integer> list = new ArrayList<Integer> ();
        list.add(new Integer(NORSCA));
        list.add(new Integer(TROLLCOUNTRY));
        return list;
    }
    public void immediate(GameState gameState, int target) {
        if(target == NORSCA) {
            gameState.regions[NORSCA].heroes++;
            gameState.regions[KISLEV].heroes++;
        } else {
            gameState.regions[TROLLCOUNTRY].heroes++;
            gameState.regions[EMPIRE].heroes++;
        }
    }

    public void resolve(GameState gameState) {
        // need a way to give targets here
        // FIXME TODO
    }
}

// FIXME TODO none of these are implemented from here on out
class TheHornedOnesDue extends OldWorldCard {
    TheHornedOnesDue() {
        name = "The Horned One's Due"; discard = false; num = 1; // targets = 1;
    }
}

class TeclisAidsTheEmpire extends OldWorldCard {

}

class DarkElfCorsairs extends OldWorldCard {

}

class NorseReavers extends OldWorldCard {

}

class FranzsDecree extends OldWorldCard {

}

class Skavengers extends OldWorldCard {

}

class MassExodus extends OldWorldCard {

}

class ElectorsArise extends OldWorldCard {

}

class WitchHunters extends OldWorldCard {

}

class DivineInspiration extends OldWorldCard {

}

class BretonnianKnights extends OldWorldCard {
    BretonnianKnights() {
        name = "BretonnianKnights"; num = 1; discard = false; targets = 1;
    }
    public List<Integer> validTargets(GameState gameState) {
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < NUM_REGIONS; i++) {
            if(i != BRETONNIA) {
                list.add(new Integer(i));
            }
        }
        return list;
    }
    public void immediate(GameState gameState, int target) {
        gameState.regions[target].heroes++;
        gameState.regions[BRETONNIA].heroes++;
    }
    public void resolve(GameState gameState) {
        boolean[] playerArray = new boolean[NUM_PLAYERS];
        for(Plastic p : gameState.regions[BRETONNIA].plastic) {
            playerArray[p.belongsTo] = true; // belongsTo? or ownedBy? FIXME TODO rules clarification
        }
        for(int i = 0; i < NUM_PLAYERS; i++) {
            if(playerArray[i]) {
                gameState.players[i].discard();
            }
        }
    }

}

class RoadWardens extends OldWorldCard {

}
