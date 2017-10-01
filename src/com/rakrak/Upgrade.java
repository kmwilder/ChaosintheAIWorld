package com.rakrak;

/**
 * Created by Wilder on 9/19/2017.
 * Simple wrapper around UpgradeType enum, single getter.
 * FIXME TODO: simply replace this with the enum (no class)?
 */
public final class Upgrade {

    public enum UpgradeType { CULTIST, WARRIOR, DAEMON, POWER, CARDS, SPECIAL };

    private final UpgradeType type;

    Upgrade(UpgradeType type) {
        this.type = type;
    }

    public UpgradeType getType() {
        return type;
    }

    boolean equals(Upgrade other) {
        return other.getType().equals(type);
    }
}
