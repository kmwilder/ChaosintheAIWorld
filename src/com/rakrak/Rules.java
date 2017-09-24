package com.rakrak;

import java.util.*;

import static com.rakrak.Plastic.PlasticType.*;
import static com.rakrak.Rules.RegionName.*;
import static com.rakrak.PlayerIndex.*;
import static com.rakrak.Tick.DialType.*;
import static com.rakrak.Tick.TokenAction.*;
import static com.rakrak.Tick.TokenType.*;

/**
 * Created by Wilder on 9/6/2017.
 */
public class Rules {

    public static class RegionName {
        static final int NORSCA = 0, TROLLCOUNTRY = 1, KISLEV = 2, EMPIRE = 3, BRETONNIA = 4, ESTALIA = 5, TILEA = 6, BORDERPRINCES = 7, BADLANDS = 8, RESERVE = -1;
        static final int NUM_REGIONS = 9;
    }

    Region[] defineBoard() {
        Region[] regions = new Region[NUM_REGIONS];

        // create regions: pass in rules, index, "Name", value, [adjacency list], populous)
        regions[NORSCA]         = new Region(this, NORSCA,          "Norsca",        1, new int[]{TROLLCOUNTRY}, false );
        regions[TROLLCOUNTRY]   = new Region(this, TROLLCOUNTRY,    "Troll Country", 1, new int[]{NORSCA, KISLEV}, false);
        regions[KISLEV]         = new Region(this, KISLEV,          "Kislev",        3, new int[]{TROLLCOUNTRY, EMPIRE}, true);
        regions[EMPIRE]         = new Region(this, EMPIRE,          "The Empire",    5, new int[]{KISLEV, BRETONNIA, BORDERPRINCES}, true);
        regions[BRETONNIA]      = new Region(this, BRETONNIA,       "Bretonnia",     3, new int[]{EMPIRE, ESTALIA, TILEA, BORDERPRINCES}, true);
        regions[ESTALIA]        = new Region(this, ESTALIA,         "Estalia",       4, new int[]{BRETONNIA, TILEA}, true);
        regions[TILEA]          = new Region(this, TILEA,           "Tilea",         2, new int[]{BRETONNIA, ESTALIA, BORDERPRINCES}, false);
        regions[BORDERPRINCES]  = new Region(this, BORDERPRINCES,   "The Border Princes", 1, new int[]{EMPIRE, BRETONNIA, TILEA, BADLANDS}, false);
        regions[BADLANDS]       = new Region(this, BADLANDS,        "The Badlands",  1, new int[]{BORDERPRINCES}, false);

        return regions;
    }

    public List<ChaosCard> generateDeck(int player) {
        List<ChaosCard> deck = new ArrayList<ChaosCard>();
        switch(player) {
            case KHORNE:
                for(int i = 0; i < 4; i++) {
                    // Add 4 of these
                    deck.add(new BloodFrenzy());
                    deck.add(new BattleCry());
                    deck.add(new TheSkullThrone());
                }
                for(int i = 0; i < 3; i++) {
                    // Add 3 of these
                    deck.add(new RitualSlaying());
                    deck.add(new FieldOfCarnage());
                    deck.add(new RebornInBlood());
                    deck.add(new TheBloodGodsCall());
                }
                break;
            case NURGLE:
                for(int i = 0; i < 4; i++) {
                    deck.add(new PlagueAura());
                    deck.add(new Influenza());
                }
                for(int i = 0; i < 3; i++) {
                    deck.add(new PlagueTouch());
                    deck.add(new RainOfPus());
                    deck.add(new QuickenDecay());
                    deck.add(new TheFinalRotting());
                }
                for(int i = 0; i < 2; i++) {
                    deck.add(new UltimatePlague());
                    deck.add(new TheStenchOfDeath());
                }
                break;

            case TZEENTCH:
                for(int i = 0; i < 4; i++) {
                    deck.add(new Teleport());
                }
                for(int i = 0; i < 3; i++) {
                    deck.add(new ChangerOfWays());
                    deck.add(new WarpShield());
                    deck.add(new TemporalStasis());
                    deck.add(new MeddlingOfSkaven());
                    deck.add(new PersistenceOfChange());
                    deck.add(new Dazzle());
                }
                for(int i = 0; i < 2; i++) {
                    deck.add(new DrainPower());
                }
                break;

            case SLAANESH:
                for(int i = 0; i < 4; i++) {
                    deck.add(new PerverseInfiltration());
                    deck.add(new InsidiousLies());
                    deck.add(new DarkInfluence());
                    deck.add(new SoporificMusk());
                }
                for(int i = 0; i < 3; i++) {
                    deck.add(new FieldOfEcstasy());
                    deck.add(new DegenerateRoyalty());
                }
                for(int i = 0; i < 2; i++) {
                    deck.add(new AbyssalPact());
                }
                break;

            default:
                System.out.println("Error in generateDeck, unknown god" + player);
        }

        return deck;
    }

    public int startingPP(int index) {
        if(index == KHORNE) {
            return 7;
        } else {
            return 6;
        }
    }

    public List<Plastic> generateReserve(int index) {
        List<Plastic> list = new ArrayList<Plastic>();
        switch(index) {
            case KHORNE:
                for(int i = 0; i < 4; i++) {
                    list.add(new Bloodsworn());
                }
                for(int i = 0; i < 6; i++) {
                    list.add(new Bloodletter());
                }
                list.add(new Bloodthirster());
                break;
            case NURGLE:
                for(int i = 0; i < 6; i++) {
                    list.add(new Leper());
                }
                for(int i = 0; i < 5; i++) {
                    list.add(new Plaguebearer());
                }
                list.add(new GreatUncleanOne());
                break;
            case TZEENTCH:
                for(int i = 0; i < 8; i++) {
                    list.add(new Acolyte());
                }
                for(int i = 3; i < 3; i++) {
                    list.add(new Horror());
                }
                list.add(new LordOfChange());
                break;
            case SLAANESH:
                for(int i=0; i < 6; i++) {
                    list.add(new Seductress());
                }
                for(int i = 0; i < 3; i++) {
                    list.add(new Daemonette());
                }
                list.add(new KeeperOfSecrets());
                break;
            default:
                System.out.println("Error @ generateReserve for " + index);
        }
        // FIXME TODO
        return list;
    }

    public List<Tick> generateDial(int index) {
        List<Tick> dial = new ArrayList<Tick>();
        if(index == KHORNE) {
            dial.add(new Tick(3, START));
            dial.add(new Tick(6, VP, 4));
            dial.add(new Tick(12, UPGRADE));
            dial.add(new Tick(14, DRAW_CARDS, 2));
            dial.add(new Tick(17, VP, 4));
            dial.add(new Tick(23, UPGRADE));
            dial.add(new Tick(28, UPGRADE));
            dial.add(new Tick(31, DRAW_CARDS, 2));
            dial.add(new Tick(32, DRAW_CARDS, 2));
            dial.add(new Tick(0, VICTORY));
        } else if(index == NURGLE) {
            dial.add(new Tick(1, START));
            dial.add(new Tick(5, VP, 3));
            // dial.add(new Tick()); hybrid nurgle
            dial.add(new Tick(10, UPGRADE));
            dial.add(new Tick(13, VP, 3));
            dial.add(new Tick(18, UPGRADE));
            dial.add(new Tick(22, TOKEN, REMOVE, 2, CORRUPTION));
            dial.add(new Tick(26, UPGRADE));
            dial.add(new Tick(30, VP, 3));
            dial.add(new Tick(33, TOKEN, REMOVE, 2, CORRUPTION));
        } else if(index == TZEENTCH) {
            dial.add(new Tick(0, START));
            dial.add(new Tick(7, TOKEN, PLACE, 1, WARPSTONE));
            dial.add(new Tick(9, UPGRADE));
            dial.add(new Tick(15, VP, 3));
            dial.add(new Tick(20, UPGRADE));
            dial.add(new Tick(21, TOKEN, PLACE, 2, WARPSTONE));
            dial.add(new Tick(25, UPGRADE));
            dial.add(new Tick(29, VP, 5));
            dial.add(new Tick(0, VICTORY));
        } else if(index == SLAANESH) {
            dial.add(new Tick(4, START));
            dial.add(new Tick(8, VP, 3));
            dial.add(new Tick(11, UPGRADE));
            dial.add(new Tick(16, TOKEN, PLACE, 2, NOBLES));
            dial.add(new Tick(19, UPGRADE));
            dial.add(new Tick(24, UPGRADE));
            dial.add(new Tick(27, TOKEN, REMOVE, 2, OLDWORLD));
            dial.add(new Tick(0, VICTORY));
        }

        return dial;
    }

    public List<Upgrade> generateUpgrades(int index) {
        List<Upgrade> upgrades = new ArrayList<Upgrade>();
        upgrades.add(new Upgrade(Upgrade.UpgradeType.CULTIST)); // TODO: t
        upgrades.add(new Upgrade(Upgrade.UpgradeType.WARRIOR)); // TODO: knts
        upgrades.add(new Upgrade(Upgrade.UpgradeType.DEMON)); // TODO: knts
        switch(index) {
            case KHORNE:
                upgrades.add(new Upgrade(Upgrade.UpgradeType.CARDS)); // todo
                upgrades.add(new Upgrade(Upgrade.UpgradeType.POWER)); // todo
                break;
            case NURGLE:
                upgrades.add(new Upgrade(Upgrade.UpgradeType.SPECIAL)); // todo
                upgrades.add(new Upgrade(Upgrade.UpgradeType.POWER)); // todo
                break;
            case TZEENTCH:
                upgrades.add(new Upgrade(Upgrade.UpgradeType.POWER));// todo
                upgrades.add(new Upgrade(Upgrade.UpgradeType.CARDS));// todo
                break;
            case SLAANESH:
                upgrades.add(new Upgrade(Upgrade.UpgradeType.POWER));// todo
                upgrades.add(new Upgrade(Upgrade.UpgradeType.POWER));// todo
                break;
        }
        return upgrades;
    }

}
