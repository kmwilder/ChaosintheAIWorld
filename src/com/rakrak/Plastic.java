package com.rakrak;

import static com.rakrak.Plastic.PlasticType.*;
import static com.rakrak.Rules.Defines.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public abstract class Plastic {
    // make this a base class / interface instead?
    protected int belongsTo;
    protected int controlledBy;

    public enum PlasticType{ CULTIST, WARRIOR, DAEMON };
    protected PlasticType type;
    protected String name;

    protected int cost;
    protected int dice;
    protected int defense;
    protected boolean warpShielded;

    Plastic() { }

    public String getName() { return name; }
    public PlasticType getType() { return type; }
    public boolean isWarpShielded() { return warpShielded; }
    public void setWarpShielded(boolean ws) { warpShielded = ws; }

    public int getCost(GameState gameState, int region) {
        return cost;
    }
    public int getDefense(GameState gameState, int region) {
        return defense;
    }

    public int getAttacks(GameState gameState, int region) { return dice; }
    public int getAttacks(Region region) { return dice; } // fixme should only be this or the above

    public void newTurn() {
        controlledBy = belongsTo;
        warpShielded = false;
    }
}

class Bloodsworn extends Plastic {
    Bloodsworn() {
        belongsTo = KHORNE;
        controlledBy = KHORNE;
        cost = 1;
        dice = 0;
        defense = 1;
        type = CULTIST;
        name = "Bloodsworn";
    }

    public int getAttacks(GameState gameState, int region) {
        int attacks = super.getAttacks(gameState, region);
        if (gameState.players[KHORNE].hasUpgrade(Upgrade.UpgradeType.CULTIST) > 0) {
            attacks++;
        }
        return attacks;
    }
}

class Bloodletter extends Plastic {
    Bloodletter() {
        belongsTo = KHORNE; controlledBy = KHORNE;
        cost = 2; dice = 2; defense = 1; type = WARRIOR;
        name = "Bloodletter";
    }
}

class Bloodthirster extends Plastic {
    Bloodthirster() {
        belongsTo = KHORNE; controlledBy = KHORNE;
        cost = 3; dice = 4; defense = 3; type = DAEMON;
        name = "Bloodthirster";
    }
}

class Leper extends Plastic {
    private boolean upgrade;
    Leper() {
        belongsTo = NURGLE; controlledBy = NURGLE;
        cost = 1; dice = 0; defense = 1; type = CULTIST;
        name = "Leper";
        upgrade = false;
    }
    public void newTurn() {
        upgrade = false;
        super.newTurn();
    }
    public int getCost(GameState gameState, int region) {
        int cost = super.getCost(gameState, region);

        // Reduce cost by one if no figures present
        if(gameState.players[NURGLE].hasUpgrade(Upgrade.UpgradeType.CULTIST) > 0) {
            for(Plastic p: gameState.regions[region].plastic) {
                if(p.belongsTo == NURGLE) {
                    return cost;
                }
            }
            return cost-1;
        }

        return cost;
    }
}

class Plaguebearer extends Plastic {
    Plaguebearer() {
        belongsTo = NURGLE; controlledBy = NURGLE;
        cost = 1; dice = 1; defense = 1; type = WARRIOR;
        name = "Plaguebearer";
    }
}

class GreatUncleanOne extends Plastic {
    GreatUncleanOne() {
        belongsTo = NURGLE; controlledBy = NURGLE;
        cost = 3; dice = 3; defense = 3; type = DAEMON;
        name = "Great Unclean One";
    }
}

class Acolyte extends Plastic {
    Acolyte() {
        belongsTo = TZEENTCH; controlledBy = TZEENTCH;
        cost = 1; dice = 0; defense = 1; type = CULTIST;
        name = "Acolyte";
    }
}

class Horror extends Plastic {
    Horror() {
        belongsTo = TZEENTCH; controlledBy = TZEENTCH;
        cost = 2; dice = 1; defense = 2; type = WARRIOR;
        name = "Horror";
    }
}

class LordOfChange extends Plastic {
    LordOfChange() {
        belongsTo = TZEENTCH; controlledBy = TZEENTCH;
        cost = 3; dice = 3; defense = 2; type = DAEMON;
        name = "Lord of Change";
    }
}

class Seductress extends Plastic {
    Seductress() {
        belongsTo = SLAANESH; controlledBy = SLAANESH;
        cost = 1; dice = 0; defense = 1; type = CULTIST;
        name = "Seductress";
    }
    public int getDefense(GameState gameState, int region) {
        int defense = super.getDefense(gameState, region);
        if(gameState.players[SLAANESH].hasUpgrade(Upgrade.UpgradeType.CULTIST) > 0) {
            defense++;
        }
        return defense;
    }
}

class Daemonette extends Plastic {
    Daemonette() {
        belongsTo = SLAANESH; controlledBy = SLAANESH;
        cost = 2; dice = 1; defense = 2; type = WARRIOR;
        name = "Daemonette";
    }
}

class KeeperOfSecrets extends Plastic {
    KeeperOfSecrets() {
        belongsTo = SLAANESH; controlledBy = SLAANESH;
        cost = 3; dice = 2; defense = 4; type = DAEMON;
        name = "Keeper of Secrets";
    }
}