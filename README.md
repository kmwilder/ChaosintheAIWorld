# ChaosintheAIWorld
Suggestion machine for Chaos in the Old World

Use case: store current game state in \<text file>.
Rules stored in \<other text file> not included in public repo to avoid copyright issues.

Invoke w/ argument \<agent> to get a selection of best moves for that old god.

Might also specify probabilities, main factors in that being the right choice (ie enemy decisions.)

Thanks to the Stanford CS221 pacman minmax agent project for guidance on how to do this.
 
Possible problems:
* Can't copy gamestate if two different array/sets point to the same object?
 
 TODOs:
 * Implement Upgrade class
 * Implement Action class
 * Implement Rules.startingPP, generateReserve, generateDeck, ...
 * Implement GameState.loadStateFromFile();
 * Implement Region.playCard()
 * Implement Tick getters
 
 Program Flow:
 * Main instances singlet Rules, initial GameState
 * Main asks gameState to update itself from file.
 * Main finds out whose turn it is, asks for the best move
   * Need to extend this to generate states of possible unknown hands, find best moves over those states
 * Main prints out details about that move.
 

 GameState.getBestMove(player)
 * explore all possible moves
 * get all possible successor states from possible moves (and win probs)
 * calc probability of each move
 * print all move probabilities (if print)
 * return best move
 
 GameState.getLegalActions(player)
 * Assume gameState is waiting for player's input at current gamestate, region, etc.