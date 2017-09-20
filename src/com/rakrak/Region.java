package com.rakrak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static com.rakrak.PlayerIndex.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Region {
    public int index;
    public String name;
    public int value;
    public Collection<Plastic> plasticSet;
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
        plasticSet = new HashSet<Plastic>(); // FIXME TODO replace the above stuff with this?
        cards = new ArrayList<ChaosCard>();
        num_cards = 0;

        warpstones = 0;
        peasants = 0;
        nobles = 0;
        heroes = 0;
        skaven = 0;
        events = 0;
    }

    public void playCard(ChaosCard card) {
        // FIXME TODO
    }

    public void discard(ChaosCard card) {
        // FIXME TODO
        // is this even needed? Or simply discard everything in a region at once, calling each card's discard?
    }

    private boolean contains(Plastic plastic) {
        return plasticSet.contains(plastic);
    }

    public int getCardSlots() {
        return 2; // HORNED_RAT will change this
    }
    public boolean canPlayCard(int playerIndex, int slot) {
        // FIXME TODO
        return true;
    }
    public int cardCost(int playerIndex, ChaosCard card) {
        // FIXME TODO
        return card.getCost(this);
    }

    public int cardPower(int playerIndex, ChaosCard card) {
        // FIXME TODO
        return card.getPower(this);
    }

    public int plasticCost(int playerIndex, Plastic plastic) {
        // FIXME TODO
        return plastic.getCost();
    }
    public int getDefense(int playerIndex, Plastic plastic) {
        // FIXME TODO
        return plastic.getDefense();
    }
    public Collection<Plastic> getPlasticControlledBy(int playerIndex) {
        HashSet<Plastic> set = new HashSet<Plastic>();
        for(Plastic p: plasticSet) {
            if(p.controlledBy == playerIndex) {
                set.add(p);
            }
        }
        return set;
    }

    public Collection<Plastic> getPlasticNotControlledBy(int playerIndex) {
        HashSet<Plastic> set = new HashSet<Plastic>();
        for(Plastic p: plasticSet) {
            if(p.controlledBy != playerIndex) {
                set.add(p);
            }
        }
        return set;
    }

    public boolean canSummonOut() {
        // FIXME TODO
        return true;
    }
}
