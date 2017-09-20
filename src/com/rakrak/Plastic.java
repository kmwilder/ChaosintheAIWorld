package com.rakrak;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Plastic {
    // make this a base class / interface instead?
    public int belongsTo;
    public int controlledBy;

    public enum PlasticType{ CULTIST, WARRIOR, DAEMON };
    public PlasticType type;

    public static int cost; // can't be static across plastic
    public static int dice;
    public static int defense;

    public int getCost() {
        return cost;
    }

    public int getDefense() {
        return defense;
    }
}
