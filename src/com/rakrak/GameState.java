package com.rakrak;

import java.util.EnumMap;

import static com.rakrak.Rules.RegionName.*;

/**
 * Created by Wilder on 9/6/2017.
 * Represents full game state, used by:
 *      Game object, to capture actual state of game.
 *      Agents, to reason about the game.
 */



public class GameState {

    enum GamePhase { OLDWORLD, DRAW, SUMMON, BATTLE, CORRUPTION, END };

    private Rules rules;
    private Region[] regions;
    private Player[] players;

    private GamePhase gamePhase;


    GameState(Rules rules) {
        this.rules = rules;
        this.regions = rules.defineBoard();
    }


    // Should store:
    // gameStateData (p much all counters, token placements, phase of game, etc)

    // Will need to implement:
    // loadStateFromFile( file )
    // getLegalActions( agentIndex ) where agent = Khorne, Nurgle, Tzeentch, Slaanesh
    // generateSuccessor( agentIndex, action ) returns new gamestate
    // getScore( agentIndex ) returns expected score (given successive gamestates?)
    // maybe a bunch of getter functions
    // win/loss conditions

}
