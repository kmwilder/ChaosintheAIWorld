package com.rakrak;

/**
 * Created by Wilder on 9/19/2017.
 */
public class PlayerIndex {
    public static final int KHORNE = 0, NURGLE = 1, TZEENTCH = 2, SLAANESH = 3;
    public static final int NUM_PLAYERS = 4;
    public static String getPlayerName(int index) {
        switch(index) {
            case KHORNE: return "Khorne";
            case NURGLE: return "Nurgle";
            case TZEENTCH: return "Tzeentch";
            case SLAANESH: return "Slaanesh";
            default: return "Invalid God!";
        }
    }
}
