package com.mycompany.game2;

import java.util.ArrayList;
import java.util.List;

public class AI {
    
    protected Grid playerGrid;
    protected List<Coordinate> validMoves; // represents all possible coordinates that are valid

    public AI(Grid playerGrid) {
        this.playerGrid = playerGrid;
        createValidMoveList(); // how?
    }

    public Coordinate selectMove() { // override this in child classes
        return Coordinate.ZERO;
    }
    
    public void reset() { // why not just call that instead of reset?
        createValidMoveList();
    }
 
    private void createValidMoveList() { // for every grid coordinate
        validMoves = new ArrayList<>();
        for(int x = 0; x < Grid.GRID_WIDTH; x++) {
            for(int y = 0; y < Grid.GRID_HEIGHT; y++) {
                validMoves.add(new Coordinate(x,y)); 
            }
        }
    }
}
