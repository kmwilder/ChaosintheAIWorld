package com.rakrak;

import static com.rakrak.Tick.TokenType.*;
import static com.rakrak.Tick.TokenAction.*;


/**
 * Created by Wilder on 9/19/2017.
 * This is a simple immutable wrapper around some standard dial tick information.
 * All information must be passed through the constructor.
 * getters (always valid):
 *      int getThreat();
 *      DialType getDialType(); // enum: START, VICTORY, VP, UPGRADE, DRAW_CARDS, TOKEN
 *      int getNum(); // context sensitive based off of getDialType
 *  getters (context sensitive based on DialType):
 *      TokenAction getDialAction(); // enum: PLACE, MOVE, REMOVE;
 *      TokenType getTokenType(); // enum: WARPSTONE, CORRUPTION, NOBLES, OLDWORLD
 *  FIXME TODO: maybe remove TokenAction MOVE?
 */
public final class Tick {
    // Represents one tick on the dial
    public enum DialType { START, VICTORY, VP, UPGRADE, DRAW_CARDS, TOKEN };
    public enum TokenAction { PLACE, MOVE, REMOVE };
    public enum TokenType { WARPSTONE, CORRUPTION, NOBLES, OLDWORLD };

    // Context-insensitive
    private final int threat;
    private final DialType dialType;

    // Context-sensitive
    private final int num;
    private final TokenAction tokenAction;
    private final TokenType tokenType;

    // Used for VICTORY, UPGRADE, START types
    Tick(int threat, DialType type) {
        this.threat = threat;
        this.dialType = type;

        // These aren't used.
        num = 0;
        tokenAction = PLACE;
        tokenType = WARPSTONE;
    }

    // Used for VP, DRAW_CARDS
    Tick(int threat, DialType type, int num) {
        this.threat = threat;
        this.dialType = type;
        this.num = num;

        // These aren't used.
        tokenAction = PLACE;
        tokenType = WARPSTONE;
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
    public int getNum() { return num; }
    public DialType getDialType() {
        return dialType;
    }
    public TokenAction getTokenAction() { return tokenAction; }
    public TokenType getTokenType() { return tokenType; }
}
