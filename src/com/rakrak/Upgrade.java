package com.rakrak;

/**
 * Created by Wilder on 9/19/2017.
 */
public class Upgrade {

    public enum UpgradeType { CULTIST, WARRIOR, DEMON, POWER, CARDS, SPECIAL};
    public UpgradeType type;

    Upgrade(UpgradeType type) {
        this.type = type;
    }
}
