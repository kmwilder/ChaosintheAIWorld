package com.rakrak;

import java.util.ArrayList;
import static com.rakrak.Rules.PlayerIndex.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Region {
    public int index;
    public String name;
    public int value;
    public int[] corruption;
    public int[] cultists;
    public int[] warriors;
    public int[] daemons;
    public ArrayList<ChaosCard> cards;
    public int num_cards = 0;
    private int[] adjacent;

    public int warpstones;
    public int peasants;
    public int nobles;
    public int heroes;
    public int skaven;
    public int events;

    private Rules rules;

    Region (Rules rules, int index, String name, int value, int[] adjacencies) {
        this.index = index;
        this.rules = rules;
        this.name = name;
        this.value = value;
        this.adjacent = adjacencies;

        corruption = new int[NUM_PLAYERS];
        cultists = new int[NUM_PLAYERS];
        warriors = new int[NUM_PLAYERS];
        daemons = new int[NUM_PLAYERS];
        cards = new ArrayList<ChaosCard>();
        num_cards = 0;

        warpstones = 0;
        peasants = 0;
        nobles = 0;
        heroes = 0;
        skaven = 0;
        events = 0;
    }
}
