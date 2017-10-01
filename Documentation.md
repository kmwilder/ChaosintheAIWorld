//=====================================================
// Need to clone these while searching through moves

Class GameState
    Contains:
        Regions
        Players
        gamePhase
        endGamePhase
        activePlayer;
        currentRegion;
        probability
        actionQueue
        GameState lastRound;
    Majority of turn logic

Class Plastic -- Abstract class, implemented by various gods' units
    getters/setters:
        int belongsTo
        int controlledBy
        PlasticType { CULTIST, WARRIOR, DAEMON } getType
        String getName
        int getCost(GameState, regionIndex);
        int getDefense(GameState, regionIndex);
        int getAttacks(GameState, regionIndex);
    newTurn // resets temporary states

Class Player
    newTurn // resets temp states

Class Region
    Local data:
        static info: index, value, adjacencies, populous
        dynamic info:
            List<Plastic> plastic
            int[god] corruption
            List<ChaosCard> cards
        token counters: warpstones, peasants, nobles, heroes, skaven, events
        EnumSet<RegionEffect> effects;
    Turn logic & helper functions:
        newTurn // resets temp states
        some helper functions

Class ChaosCard -- Abstract class, implemented by various game cards
    // FIXME TODO try to make this immutable? (Need to pass targets into resolve)
    Used to assess playability:
        int getCost(player, region)
        boolean needsTarget()
        List<Integer> validTargets(GameState, region)
        boolean needsDestination();
        public List<Integer> validDestinations(GameState, region)
    Used to play:
        setTarget
        setDestination
        play(player, region);
        resolve(gameState, regionIndex); // lots of card logic shoved into here
    Domination/DACs:
        int getPower(player, region);
        boolean isMagic();
        // FIXME TODO lots of unimplemented / untested stuff in here
    Cleanup:
        boolean leaveNextTurn(gameState, regionIndex);

//=====================================================
// Immutables below -- no need to clone while searching

Class Action -- IMMUTABLE
    enum ActionType { MOVE_PLASTIC, MOVE_TOKEN,	KILL_PLASTIC, KILL_PEASANT, DISCARD_PLASTIC, DISCARD_TOKEN,
            PLACE_CARD, DISCARD_CARD, DRAW_CARD, PICK_UPGRADE, PASS, BGC }
    getters for player, targetPlastic, targetCard, ActionType, srcRegion, dstRegion, slot, upgrade

Class Rules -- STATIC FINAL
    Helper functions to generate initial board states
    defines static class RegionName (defines names, NUM_REGIONS, NUM_PLAYERS)
    Region[] defineBoard();
    List<ChaosCard> generateDeck(playerIndex);
    int startingPP(playerIndex);
    List<Plastic> generateReserve(playerIndex);
    List<Tick> generateDial(playerIndex);
    List<Upgrade> generateUpgrades(playerIndex);
        // FIXME TODO tracking -- some of these are unimplemented

Class Tick -- IMMUTABLE
    getters:
        int getThreat();
        int getNum(); // context sensitive based off of getDialType
        DialType getDialType(); enum: START, VICTORY, VP, UPGRADE, DRAW_CARDS, TOKEN
        TokenAction getDialAction(); enum: PLACE, MOVE, REMOVE;
        TokenType getTokenType(); enum: WARPSTONE, CORRUPTION, NOBLES, OLDWORLD

Class Upgrade -- IMMUTABLE
    Simple wrapper around type UpgradeType enum: CULTIST, WARRIOR, DAEMON, POWER, CARDS, SPECIAL
    getters: getType()
    // FIXME TODO: Should this simply be replaced by the enum?