package com.rakrak;

import java.util.*;

import static com.rakrak.Rules.Defines.*;
import static com.rakrak.Tick.DialType.*;
import static com.rakrak.Tick.TokenAction.*;
import static com.rakrak.Tick.TokenType.*;

/**
 * Created by Wilder on 9/6/2017.
 * Static, final class used to populate a bunch of starting state.
 * Also includes the Defines subclass, used to act like cpp #define values so I can abuse raw arrays.
 * Perhaps this project will teach me why that's a bad idea?
 */
public final class Rules {

    public static class Defines {
        public static final int NORSCA = 0, TROLLCOUNTRY = 1, KISLEV = 2, EMPIRE = 3, BRETONNIA = 4, ESTALIA = 5, TILEA = 6, BORDERPRINCES = 7, BADLANDS = 8, RESERVE = -1;
        public static final int NUM_REGIONS = 9;
        public static final int BOARD = -1, KHORNE = 0, NURGLE = 1, TZEENTCH = 2, SLAANESH = 3;
        public static final int NUM_PLAYERS = 4;
        public static String getPlayerName(int index) {
            switch(index) {
                case BOARD: return "Board";
                case KHORNE: return "Khorne";
                case NURGLE: return "Nurgle";
                case TZEENTCH: return "Tzeentch";
                case SLAANESH: return "Slaanesh";
                default: return "Invalid God!";
            }
        }
        public static String getRegionName(int index) {
            switch(index) {
                case NORSCA: return "Norsca";
                case TROLLCOUNTRY: return "Troll Country";
                case KISLEV: return "Kislev";
                case EMPIRE: return "The Empire";
                case BRETONNIA: return "Bretonnia";
                case ESTALIA: return "Estalia";
                case TILEA: return "Tilea";
                case BORDERPRINCES: return "The Border Princes";
                case BADLANDS: return "The Badlands";
                default: return "Invalid region!";
            }
        }
    }

    public static Region[] defineBoard() {
        Region[] regions = new Region[NUM_REGIONS];

        // create regions: pass in index, value, [adjacency list], populous)
        regions[NORSCA]         = new Region(NORSCA,        1, new int[]{TROLLCOUNTRY}, false );
        regions[TROLLCOUNTRY]   = new Region(TROLLCOUNTRY,  1, new int[]{NORSCA, KISLEV}, false);
        regions[KISLEV]         = new Region(KISLEV,        3, new int[]{TROLLCOUNTRY, EMPIRE}, true);
        regions[EMPIRE]         = new Region(EMPIRE,        5, new int[]{KISLEV, BRETONNIA, BORDERPRINCES}, true);
        regions[BRETONNIA]      = new Region(BRETONNIA,     3, new int[]{EMPIRE, ESTALIA, TILEA, BORDERPRINCES}, true);
        regions[ESTALIA]        = new Region(ESTALIA,       4, new int[]{BRETONNIA, TILEA}, true);
        regions[TILEA]          = new Region(TILEA,         2, new int[]{BRETONNIA, ESTALIA, BORDERPRINCES}, false);
        regions[BORDERPRINCES]  = new Region(BORDERPRINCES, 1, new int[]{EMPIRE, BRETONNIA, TILEA, BADLANDS}, false);
        regions[BADLANDS]       = new Region(BADLANDS,      1, new int[]{BORDERPRINCES}, false);

        return regions;
    }

    public static List<ChaosCard> generateDeck(int player) {
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

    public static int startingPP(int index) {
        if(index == KHORNE) {
            return 7;
        } else {
            return 6;
        }
    }

    public static List<Plastic> generateReserve(int index) {
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
                for(int i = 0; i < 3; i++) {
                    list.add(new Horror());
                }
                list.add(new LordOfChange());
                break;
            case SLAANESH:
                for(int i = 0; i < 6; i++) {
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
        return list;
    }

    public static List<Tick> generateDial(int index) {
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
            dial.add(new Tick(0, VICTORY));
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

    public static List<Upgrade> generateUpgrades(int index) {
        List<Upgrade> upgrades = new ArrayList<Upgrade>();
        upgrades.add(new Upgrade(Upgrade.UpgradeType.CULTIST)); // TODO: t
        upgrades.add(new Upgrade(Upgrade.UpgradeType.WARRIOR)); // TODO: knts
        upgrades.add(new Upgrade(Upgrade.UpgradeType.DAEMON)); // TODO: knts
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

    public static List<OldWorldCard> generateOldWorldDeck() {
        List<OldWorldCard> deck = new ArrayList<OldWorldCard>();
        deck.add(new PlungedIntoChaos());
        deck.add(new PlungedIntoChaos());
        deck.add(new PlungedIntoChaos());
        deck.add(new PlungedIntoChaos());

        deck.add(new PeasantUprising());
        deck.add(new PeasantUprising());

        deck.add(new UpFromSkavenblight());
        deck.add(new UpFromSkavenblight());

        deck.add(new MeteorShowers());
        deck.add(new MeteorShowers());

        // One-offs
        deck.add(new WarpstoneDiscovery());
        deck.add(new TheCrusadeIsCome());
        deck.add(new GreenskinsInvade());
        deck.add(new DwarfTrollslayers());
        deck.add(new TheHornedOnesDue());
        deck.add(new TeclisAidsTheEmpire());
        deck.add(new DarkElfCorsairs());
        deck.add(new NorseReavers());
        deck.add(new FranzsDecree());
        deck.add(new Skavengers());
        deck.add(new MassExodus());
        deck.add(new ElectorsArise());
        deck.add(new WitchHunters());
        deck.add(new DivineInspiration());
        deck.add(new BretonnianKnights());
        deck.add(new RoadWardens());

        return deck;
    }

}
