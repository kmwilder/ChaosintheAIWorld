package com.rakrak;

import java.util.*;

import static com.rakrak.Rules.Defines.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Region {
    private final int index;
    private final int value;
    private final int[] adjacent;
    private final boolean populous;

    public List<Plastic> plastic;
    public int[] corruption;
    public List<ChaosCard> cards;
    public int num_cards = 0; // FIXME TODO is this needed?

    public int warpstones;
    public int peasants;
    public int nobles;
    public int heroes;
    public int skaven;
    public int events;

    public enum RegionEffect { TEMP_STASIS, CHANGER_OF_WAYS, FIELD_OF_CARNAGE, DAZZLE, PLAGUE_TOUCH,
        RAIN_OF_PUS, INFLUENZA, QUICKEN_DECAY, FINAL_ROTTING, STENCH_OF_DEATH, ABYSSAL_PACT, FIELD_OF_ECSTASY,
        BLOOD_FRENZY, BATTLE_CRY, REBORN_IN_BLOOD,
        PEASANT_UPRISING, GREENSKINS_INVADE
    };
    private EnumSet<RegionEffect> effects;

    Region (int index, int value, int[] adjacencies, boolean populous) {
        this.index = index;
        this.value = value;
        this.adjacent = adjacencies;
        this.populous = populous;

        corruption = new int[NUM_PLAYERS];
        plastic = new ArrayList<Plastic>();
        cards = new ArrayList<ChaosCard>();
        num_cards = 0;

        warpstones = 0;
        peasants = 0;
        nobles = 0;
        heroes = 0;
        skaven = 0;
        events = 0;

        effects = EnumSet.noneOf(RegionEffect.class);
    }

    Region (Region source) {
        this.index = source.index;
        this.value = source.value;
        this.adjacent = source.adjacent;
        this.populous = source.populous;

        this.corruption = new int[NUM_PLAYERS];
        for(int i = 0; i < NUM_PLAYERS; i++) {
            corruption[i] = source.corruption[i];
        }
        this.plastic = new ArrayList<Plastic>(source.plastic);
        this.cards = new ArrayList<ChaosCard>(source.cards);
        this.num_cards = source.num_cards;

        this.warpstones = source.warpstones;
        this.peasants = source.peasants;
        this.nobles = source.nobles;
        this.heroes = source.heroes;
        this.skaven = source.skaven;
        this.events = source.events;

        effects = source.effects.clone();
    }

    public String getName() {
        return Rules.Defines.getRegionName(index);
    }
    public int[] getAdjacencies() {
        return adjacent;
    }

    public void playCard(ChaosCard card) {
        // FIXME TODO
    }

    public void discard(ChaosCard card) {
        // FIXME TODO
        // is this even needed? Or simply discard everything in a region at once, calling each card's discard?
    }

    public int getCardSlots() {
        return 2; // HORNED_RAT will change this
    }

    public boolean canPlayCard(int playerIndex, int slot) {
        // FIXME TODO
        return true;
    }
    public int cardCost(Player player, ChaosCard card) {
        // FIXME TODO
        return card.getCost(player, this);
    }

    public int cardPower(Player player, ChaosCard card) {
        // FIXME TODO
        return card.getPower(player, this);
    }
/*
    public int plasticCost(GameState gameState, Plastic plastic) {
        // FIXME TODO
        return plastic.getCost();
    }

    public int getDefense(int playerIndex, Plastic plastic) {
        // FIXME TODO
        return plastic.getDefense();
    }
    */
    public Collection<Plastic> getPlasticControlledBy(int playerIndex) {
        HashSet<Plastic> set = new HashSet<Plastic>();
        for(Plastic p: plastic) {
            if(p.controlledBy == playerIndex) {
                set.add(p);
            }
        }
        return set;
    }

    public Collection<Plastic> getPlasticNotControlledBy(int playerIndex) {
        HashSet<Plastic> set = new HashSet<Plastic>();
        for(Plastic p: plastic) {
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

    public void addEffect(RegionEffect effect) {
        effects.add(effect);
    }
    public void removeEffect(RegionEffect effect) {
        effects.remove(effect);

    }
    public boolean hasEffect(RegionEffect effect) {
        return effects.contains(effect);
    }
}
