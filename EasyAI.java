package com.mycompany.game2;

import java.util.Collections;

/**
 *
 * @author arahm
 */
public class EasyAI extends AI{
     /**
     * Initializes the simple AI by randomizing the order of moves.
     *
     * @param playerGrid Reference to the player's grid to attack.
     */
    public EasyAI(Grid playerGrid) {
        super(playerGrid);
        Collections.shuffle(validMoves); // what does this do
    }

    /**
     * Resets the AI by resetting the parent class, and then
     * reshuffling the refreshed list of valid moves.
     */
    @Override
    public void reset() {
        super.reset();
        Collections.shuffle(validMoves);
    }
    
    /**
     * Takes the move from the top of the list and returns it.
     *
     * @return A position from the valid moves list.
     */
    @Override
    public Coordinate selectMove() {
        Coordinate nextMove = validMoves.get(0); // why not just make random coordinate
        validMoves.remove(0);
        return nextMove;
    }
}
