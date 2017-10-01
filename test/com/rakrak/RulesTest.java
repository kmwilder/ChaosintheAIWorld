package com.rakrak;

import org.junit.Test;

import java.util.List;

import static com.rakrak.Rules.Defines.*;
import static org.junit.Assert.*;

/**
 * Created by Wilder on 9/30/2017.
 */
public class RulesTest {

    @Test
    public void defineBoardTests() {
        Region[] regions = Rules.defineBoard();

        assertEquals("Checking regions length = " + NUM_REGIONS, NUM_REGIONS, regions.length);
        assertEquals("Checking region array: Norsca", "Norsca", regions[NORSCA].getName());
        assertEquals("Checking region array: TC", "Troll Country", regions[TROLLCOUNTRY].getName());
        assertEquals("Checking region array: Kislev", "Kislev", regions[KISLEV].getName());
        assertEquals("Checking region array: Empire", "The Empire", regions[EMPIRE].getName());
        assertEquals("Checking region array: Bretonnia", "Bretonnia", regions[BRETONNIA].getName());
        assertEquals("Checking region array: Estalia", "Estalia", regions[ESTALIA].getName());
        assertEquals("Checking region array: Tilea", "Tilea", regions[TILEA].getName());
        assertEquals("Checking region array: TBP", "The Border Princes", regions[BORDERPRINCES].getName());
        assertEquals("Checking region array: Badlands", "The Badlands", regions[BADLANDS].getName());
    }

    @Test
    public void generateKhorneDeck() {
        List<ChaosCard> deck = Rules.generateDeck(KHORNE);

        ChaosCard[] types = new ChaosCard[7];
        int[] count = new int[7];
        types[0] = new BloodFrenzy();
        count[0] = 4;
        types[1] = new BattleCry();
        count[1] = 4;
        types[2] = new TheSkullThrone();
        count[2] = 4;
        types[3] = new RitualSlaying();
        count[3] = 3;
        types[4] = new FieldOfCarnage();
        count[4] = 3;
        types[5] = new RebornInBlood();
        count[5] = 3;
        types[6] = new TheBloodGodsCall();
        count[6] = 3;

        assertEquals("Deck should have 24 cards", 24, deck.size());
        for(int i = 0; i < 7; i++) {
            assertEquals("Deck should have " + count[i] + " of " + types[i].name,
                    count[i], deckContains(deck, types[i]));
        }
    }

    @Test
    public void generateDecks() {
        List<ChaosCard> deck;

        for(int i = 0; i < NUM_PLAYERS; i++) {
            deck = Rules.generateDeck(i);
            assertEquals("Deck should have 24 cards", 24, deck.size());
        }
    }

    @Test
    public void generateUpgrades() {
        List<Upgrade> upgrades;

        for(int i = 0; i < NUM_PLAYERS; i++) {
            upgrades = Rules.generateUpgrades(i);
            assertEquals("Player " + i + " should have 5 upgrades", 5, upgrades.size());
            assertEquals("Upgrade[0] should be for cultists", Upgrade.UpgradeType.CULTIST, upgrades.get(0).getType());
            assertEquals("Upgrade[1] should be for warriors", Upgrade.UpgradeType.WARRIOR, upgrades.get(1).getType());
            assertEquals("Upgrade[2] should be for daemons", Upgrade.UpgradeType.DAEMON, upgrades.get(2).getType());
        }
    }

    @Test
    public void generateDials() {
        List<Tick> dial;

        dial = Rules.generateDial(KHORNE);
        assertEquals("Khorne dial length 10", 10, dial.size());
        sanityCheckDial(dial);

        dial = Rules.generateDial(NURGLE);
        assertEquals("Nurgle dial length 10", 10, dial.size());
        sanityCheckDial(dial);

        dial = Rules.generateDial(TZEENTCH);
        assertEquals("Tzeentch dial length 9", 9, dial.size());
        sanityCheckDial(dial);

        dial = Rules.generateDial(SLAANESH);
        assertEquals("Slaanesh dial length: 8", 8, dial.size());
        sanityCheckDial(dial);


    }
    private void sanityCheckDial(List<Tick> dial) {
        assertEquals("Dial starts with start.", Tick.DialType.START, dial.get(0).getDialType());
        assertEquals("Dial ends with victory.", Tick.DialType.VICTORY, dial.get(dial.size()-1).getDialType());


        int count = 0;
        for(Tick t : dial) {
            if(t.getDialType().equals(Tick.DialType.UPGRADE)) {
                count++;
            }
        }
        assertEquals("Should have 3 upgrade ticks", 3, count);
    }

    @Test
    public void generateReserves() {
        for(int i = 0; i < NUM_PLAYERS; i++) {
            List<Plastic> reserve = Rules.generateReserve(i);
            int size = 0;
            int cultists = 0;
            int warriors = 0;
            switch (i) {
                case KHORNE: size = 11; cultists = 4; warriors = 6; break;
                case NURGLE: size = 12; cultists = 6; warriors = 5; break;
                case TZEENTCH: size = 12; cultists = 8; warriors = 3; break;
                case SLAANESH: size = 10; cultists = 6; warriors = 3; break;
            }
            assertEquals("Reserve size should be " + size + " for " + i, size, reserve.size());
            assertEquals("One demon at the end for " + i, Plastic.PlasticType.DAEMON, reserve.get(size-1).type);
            // count down pieces expected
            for(Plastic p : reserve) {
                if(p.type == Plastic.PlasticType.CULTIST) {
                    cultists--;
                } else if(p.type == Plastic.PlasticType.WARRIOR) {
                    warriors--;
                }
            }
            assertEquals("Cultists for " + i, 0, cultists);
            assertEquals("Warriors for " + i, 0, warriors);
        }
    }

    private int deckContains(List<ChaosCard> deck, ChaosCard card) {
        int contains = 0;
        for(ChaosCard deckCard : deck) {
            if(deckCard.getName().equals(card.getName())) {
                contains++;
            }
        }
        return contains;
    }
}