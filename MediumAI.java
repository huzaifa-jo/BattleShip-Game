
package com.mycompany.game2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediumAI extends AI{
    
     /**
     * A list of positions where ships were hit, that are not yet destroyed.
     */
    private List<Coordinate> shipHits;

    /**
     * When true the adjacent moves are evaluated for forming a line with existing ship positions.
     * When false the move is selected at random from valid adjacent moves.
     * preferMovesFormingLine
     */
    private boolean priorityMove;
    /**
     * When true the random selection of moves will find either the first random move with
     * four adjacent not attacked tiles, or the one with the highest number of not attacked tiles
     * When false it will just use the next random selection
     */
    private boolean maxSides;

    /**
     * Creates the basic setup for the AI by setting up references to the player's grid,
     * and creates a list of all valid moves.
     *
     * @param playerGrid A reference to the grid controlled by the player for testing attacks.
     * @param preferMovesFormingLine True will enable the smartest version of the AI to try and form lines when attacking ships.
     * @param maximiseAdjacentRandomisation True makes the randomized attacks prefer grid positions that have more not attacked points around them.
     */
    public MediumAI(Grid playerGrid, boolean preferMovesFormingLine, boolean maximiseAdjacentRandomisation) {
        super(playerGrid);
        shipHits = new ArrayList<>();
        this.priorityMove = preferMovesFormingLine;
        this.maxSides = maximiseAdjacentRandomisation;
        Collections.shuffle(validMoves);
    }

    /**
     * Resets the ships that have been hit and randomizes the move order.
     */
    @Override
    public void reset() {
        super.reset();
        
        shipHits.clear();
        Collections.shuffle(validMoves);
    }

    /**
     * Selects an appropriate move depending on whether any ships were currently hit and not yet destroyed.
     * The AI will choose an attack adjacent to known ship hit locations if a ship has been found, otherwise
     * it will select the next random move.
     *
     * @return The selected position to attack.
     */
    @Override
    public Coordinate selectMove() {
        Coordinate selectedMove;
        // If a ship has been hit, but not destroyed
        if(shipHits.size() > 0) {
            
            if(priorityMove) selectedMove = getSmarterAttack();
            else selectedMove = getSmartAttack();
            
        } 
        else {
            
            if(maxSides) selectedMove = findMostOpenPosition();
            else selectedMove = validMoves.get(0); // use a random move
            
        }
        
        updateShipHits(selectedMove);
        validMoves.remove(selectedMove);
        return selectedMove;
    }

    /**
     * Gets a list of moves adjacent to shipHits and chooses one at random.
     *
     * @return A random move that has a good chance of hitting a ship again.
     */
    private Coordinate getSmartAttack() {
        List<Coordinate> suggestedMoves = getAdjacentSmartMoves();
        Collections.shuffle(suggestedMoves);
        return suggestedMoves.get(0);
    }


    /**
     * Gets a list of moves adjacent to shipHits and chooses one based on
     * whether it forms a line of at least two elements with adjacent ship hits.
     * If no optimal guess is found a random adjacent move is selected.
     *
     * @return A valid move that is adjacent to shipHits preferring one that forms a line.
     */
    private Coordinate getSmarterAttack() {
        List<Coordinate> suggestedMoves = getAdjacentSmartMoves();
        for(Coordinate possibleOptimalMove : suggestedMoves) {
            if(atLeastTwoHitsInDirection(possibleOptimalMove, Coordinate.LEFT)) return possibleOptimalMove;
            else if(atLeastTwoHitsInDirection(possibleOptimalMove, Coordinate.RIGHT)) return possibleOptimalMove;
            else if(atLeastTwoHitsInDirection(possibleOptimalMove, Coordinate.DOWN)) return possibleOptimalMove;
            else if(atLeastTwoHitsInDirection(possibleOptimalMove,Coordinate.UP)) return possibleOptimalMove;
        }
        // else no optimal choice found, just randomise the move
        Collections.shuffle(suggestedMoves);
        return suggestedMoves.get(0);
    }

    /**
     * Searches for the valid move with the most adjacent cells that have not been attacked
     * Meaning, prioritizes cells with four sides, then three, then two
     * @return The first position with the highest score in the valid moves list.
     */
    private Coordinate findMostOpenPosition() {
        Coordinate position = validMoves.get(0);
        int highestNotAttacked = -1;
        for(int i = 0; i < validMoves.size(); i++) {
            int testCount = getAdjacentNotAttackedCount(validMoves.get(i));
            if(testCount == 4) { // Maximum found, just return immediately
                return validMoves.get(i);
            } else if(testCount > highestNotAttacked) {
                highestNotAttacked = testCount;
                position = validMoves.get(i);
            }
        }
        return position;
    }

    /**
     * Counts the number of adjacent cells that have not been marked around the specified position.
     *
     * @param position The position to count adjacent cells.
     * @return The number of adjacent cells that have not been marked around the position.
     */
    private int getAdjacentNotAttackedCount(Coordinate position) {
        List<Coordinate> adjacentCells = getAdjacentCells(position);
        int notAttackedCount = 0;
        for(Coordinate adjacentCell : adjacentCells) {
            if(!playerGrid.getMarkerAtPosition(adjacentCell).isMarked()) {
                notAttackedCount++;
            }
        }
        return notAttackedCount;
    }

    /**
     * Tests if there are two adjacent ship hits in a direction from a test start point.
     *
     * @param start Position to start from (but not test).
     * @param direction Direction to move from the start position.
     * @return True if there are two adjacent ship hits in the specified direction.
     */
    private boolean atLeastTwoHitsInDirection(Coordinate start, Coordinate direction) {
        Coordinate testPosition = new Coordinate(start);
        testPosition.add(direction);
        if(!shipHits.contains(testPosition)) return false;
        testPosition.add(direction);
        if(!shipHits.contains(testPosition)) return false;
       
        return true;
    }

    /**
     * Gets the adjacent cells around every shipHit and creates a unique list of the
     * elements that are also still in the valid move list.
     *
     * @return A list of all valid moves that are adjacent cells to the current ship hits.
     */
    private List<Coordinate> getAdjacentSmartMoves() {
        List<Coordinate> result = new ArrayList<>();
        for(Coordinate shipHitPos : shipHits) {
            List<Coordinate> adjacentPositions = getAdjacentCells(shipHitPos);
            for(Coordinate adjacentPosition : adjacentPositions) {
                if(!result.contains(adjacentPosition) && validMoves.contains(adjacentPosition)) {
                    result.add(adjacentPosition);
                }
            }
        }
        
        return result;
    }

    /**
     * Debug method to print a list of Positions.
     *
     * @param messagePrefix Debug message to show before the data.
     * @param data A list of elements to show in the form [,,,]
     */
    private void printPositionList(String messagePrefix, List<Coordinate> data) {
        String result = "[";
        for(int i = 0; i < data.size(); i++) {
            result += data.get(i);
            if(i != data.size()-1) {
                result += ", ";
            }
        }
        result += "]";
        System.out.println(messagePrefix + " " + result);
    }

    /**
     * Creates a list of all adjacent cells around the position excluding any that
     * are off the grid.
     *
     * @param position Position to find adjacent cells around.
     * @return A list of all adjacent positions that are inside the grid space.
     */
    private List<Coordinate> getAdjacentCells(Coordinate position) {
        List<Coordinate> result = new ArrayList<>();
        if(position.x != 0) {
            Coordinate left = new Coordinate(position);
            left.add(Coordinate.LEFT);
            result.add(left);
        }
        if(position.x != Grid.GRID_WIDTH - 1) {
            Coordinate right = new Coordinate(position);
            right.add(Coordinate.RIGHT);
            result.add(right);
        }
        if(position.y != 0) {
            Coordinate up = new Coordinate(position);
            up.add(Coordinate.UP);
            result.add(up);
        }
        if(position.y != Grid.GRID_HEIGHT - 1) {
            Coordinate down = new Coordinate(position);
            down.add(Coordinate.DOWN);
            result.add(down);
        }
        return result;
    }

    /**
     * Tests if the position hits a ship. Then evaluates if the ship that is hit
     * would be destroyed. If it would be destroyed the data is all cleared for that
     * ship because it is no longer necessary to know about destroyed ships.
     *
     * @param testPosition The position that is being evaluated for hitting a ship.
     */
    private void updateShipHits(Coordinate testPosition) {
        Marker marker = playerGrid.getMarkerAtPosition(testPosition);
        if(marker.isShip()) {
            shipHits.add(testPosition);
            // Check to find if this was the last place to hit on the targeted ship
            List<Coordinate> allPositionsOfLastShip = marker.getAssociatedShip().getOccupiedCoordinates();

            boolean hitAllOfShip = containsAllPositions(allPositionsOfLastShip, shipHits);
            // If it was remove the ship data from history to now ignore it
            if(hitAllOfShip) {
                for(Coordinate shipPosition : allPositionsOfLastShip) {
                    for(int i = 0; i < shipHits.size(); i++) {
                        if(shipHits.get(i).equals(shipPosition)) {
                            shipHits.remove(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Tests if all the positions in positionsToSearch are in listToSearchIn.
     *
     * @param positionsToSearch List of positions to search all of.
     * @param listToSearchIn List of positions to search inside of.
     * @return True if all the positions in positionsToSearch are in listToSearchIn.
     */
    private boolean containsAllPositions(List<Coordinate> positionsToSearch, List<Coordinate> listToSearchIn) {
        for(Coordinate searchPosition : positionsToSearch) {
            boolean found = false;
            for(Coordinate searchInPosition : listToSearchIn) {
                if(searchInPosition.equals(searchPosition)) {
                    found = true;
                    break;
                }
            }
            if(!found) return false;
        }
        return true;
    }
}


