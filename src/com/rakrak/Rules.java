package com.rakrak;

import java.util.*;

import static com.rakrak.Rules.RegionName.*;
import static com.rakrak.PlayerIndex.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Rules {

    public static class RegionName {
        static final int NORSCA = 0, TROLLCOUNTRY = 1, KISLEV = 2, EMPIRE = 3, BRETONNIA = 4, ESTALIA = 5, TILEA = 6, BORDERPRINCES = 7, BADLANDS = 8, RESERVE = -1;
        static final int NUM_REGIONS = 9;
    }

    Region[] defineBoard() {
        Region[] regions = new Region[NUM_REGIONS];

        // create regions: pass in rules, index, "Name", value, [adjacency list])
        regions[NORSCA]         = new Region(this, NORSCA,          "Norsca",        1, new int[]{TROLLCOUNTRY} );
        regions[TROLLCOUNTRY]   = new Region(this, TROLLCOUNTRY,    "Troll Country", 1, new int[]{NORSCA, KISLEV});
        regions[KISLEV]         = new Region(this, KISLEV,          "Kislev",        3, new int[]{TROLLCOUNTRY, EMPIRE});
        regions[EMPIRE]         = new Region(this, EMPIRE,          "The Empire",    5, new int[]{KISLEV, BRETONNIA, BORDERPRINCES});
        regions[BRETONNIA]      = new Region(this, BRETONNIA,       "Bretonnia",     3, new int[]{EMPIRE, ESTALIA, TILEA, BORDERPRINCES});
        regions[ESTALIA]        = new Region(this, ESTALIA,         "Estalia",       4, new int[]{BRETONNIA, TILEA});
        regions[TILEA]          = new Region(this, TILEA,           "Tilea",         2, new int[]{BRETONNIA, ESTALIA, BORDERPRINCES});
        regions[BORDERPRINCES]  = new Region(this, BORDERPRINCES,   "The Border Princes", 1, new int[]{EMPIRE, BRETONNIA, TILEA, BADLANDS});
        regions[BADLANDS]       = new Region(this, BADLANDS,        "The Badlands",  1, new int[]{BORDERPRINCES});

        return regions;
    }

    public Set<ChaosCard> generateDeck(int player) {
        Set<ChaosCard> deck = new HashSet<ChaosCard>();
        // FIXME TODO;
        return deck;
    }

    public int startingPP(int index) {
        // FIXME TODO
        return 0;
    }

    public Set<Plastic> generateReserve(int index) {
        Set<Plastic> list = new HashSet<Plastic>();
        // FIXME TODO
        return list;
    }

    public List<Tick> generateDial(int index) {
        List<Tick> dial = new ArrayList<Tick>();
        // FIXME TODO (populate from xml)
        return dial;
    }

    public Set<Upgrade> generateUpgrades(int index) {
        Set<Upgrade> upgrades = new HashSet<Upgrade>();
        // FIXME TODO (populate from xml?)
        return upgrades;
    }

}
