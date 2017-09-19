package com.rakrak;

import java.util.ArrayList;

/**
 * Created by Wilder on 9/19/2017.
 */
public class Tick {
    // Represents one tick on the dial
    private int threat;
    private int vp;
    private boolean upgrade;
    private int tokens;
    private int drawCards;
    public enum TokenAction { PLACE, MOVE, REMOVE };
    public enum TokenType { WARPSTONE, CORRUPTION, NOBLES, OLDWORLD };
    TokenAction tokenAction;
    TokenType tokenType;
    private boolean victory;

    Tick() {
        // default constructor... try not to use it?
        threat = 0;
        vp = 0;
        upgrade = false;
        tokens = 0;
        victory = false;
        drawCards = 0;
    }

    Tick(int threat) {
        // default constructor... try not to use it?
        this.threat = threat;
        vp = 0;
        upgrade = false;
        tokens = 0;
        victory = false;
        drawCards = 0;
    }

    public void setVictory() {
        victory = true;
    }
    public void setVp(int vp) {
        this.vp = vp;
    }
    public void setUpgrade() {
        this.upgrade = true;
    }
    public void setDrawCards(int cards) {
        this.drawCards = cards;
    }
    public void setTokenAction(TokenAction tokenAction, int tokens, TokenType tokenType) {
        this.tokenAction = tokenAction;
        this.tokens = tokens;
        this.tokenType = tokenType;
    }

    // FIXME TODO getters?

}
