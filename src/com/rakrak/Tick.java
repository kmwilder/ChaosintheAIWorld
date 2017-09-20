package com.rakrak;

import java.util.ArrayList;

/**
 * Created by Wilder on 9/19/2017.
 */
public class Tick {
    // Represents one tick on the dial
    public enum DialType { VICTORY, VP, UPGRADE, DRAW_CARDS, TOKEN };
    public enum TokenAction { PLACE, MOVE, REMOVE };
    public enum TokenType { WARPSTONE, CORRUPTION, NOBLES, OLDWORLD };

    // Context-insensitive
    private int threat;
    DialType dialType;

    // Context-sensitive
    private int num;
    private TokenAction tokenAction;
    private TokenType tokenType;

    Tick() {
        // default constructor... try not to use it?
    }

    // Used for VICTORY, UPGRADE types
    Tick(int threat, DialType type) {
        this.threat = threat;
        this.dialType = type;
    }

    // Used for VP, DRAW_CARDS
    Tick(int threat, DialType type, int num) {
        this.threat = threat;
        this.dialType = type;
        this.num = num;
    }

    // Used for TOKEN
    Tick(int threat, DialType dialType, TokenAction tokenAction, int num, TokenType tokenType) {
        this.threat = threat;
        this.dialType = dialType;
        this.tokenAction = tokenAction;
        this.num = num;
        this.tokenType = tokenType;
    }

    public int getThreat() {
        return threat;
    }

    public DialType getDialType() {
        return dialType;
    }

}
