package com.mycompany.game2;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


public class Ship {
    
     /**
     * Used for placement color changing.
     * Valid: Indicates the ship could be placed at the current location shown as a Green ship.
     * Invalid: Indicates the ship can't be placed at the current location shown as a Red ship.
     * Placed: Used when the ship has been placed and will use default color settings.
     */
    public enum ShipPlacementColour {green, red, placed}

    /**
     * The position in grid coordinates for where the ship is located.
     */
    private Coordinate gridPosition; // where ship is
    /**
     * The position in pixels for drawing the ship.
     */
    private Coordinate drawPosition;
    /**
     * The number of segments in the ship to show how many cells it goes across.
     */
    private int segments;
    /**
     * True indicates the ship is horizontal, and false indicates the ship is vertical.
     */
    private boolean isSideways;
    /**
     * The number of destroyed sections to help determine if all of the ship has been destroyed when compared to segments.
     */
    private int destroyedSections;
    /**
     * Used to change the color during manual placement by the player to show Green or Red to show valid and invalid placement.
     */
    private ShipPlacementColour shipPlacementColour;

    /**
     * Creates the ship with default properties ready for use. Assumes it has already been placed when created.
     *
     * @param gridPosition The position where the ship is located in terms of grid coordinates.
     * @param drawPosition Top left corner of the cell to start drawing the ship in represented in pixels.
     * @param segments The number of segments in the ship to show how many cells it goes across.
     * @param isSideways True indicates the ship is horizontal, and false indicates the ship is vertical.
     */
    
    
    public Ship(Coordinate gridPosition, Coordinate drawPosition, int segments, boolean isSideways) {
        this.gridPosition = gridPosition;
        this.drawPosition = drawPosition;
        this.segments = segments;
        this.isSideways = isSideways;
        destroyedSections = 0;
        shipPlacementColour = ShipPlacementColour.placed; // why placed?
    }

    /**
     * Draws the ship by first selecting the color and then drawing the ship in the correct direction.
     * Color is selected to be: Green if currently placing and it is valid, red if it is placing and invalid.
     * If it has already been placed it will show as red if destroyed, or dark gray in any other case.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        if(shipPlacementColour == ShipPlacementColour.placed) {
            g.setColor(destroyedSections >= segments ? Color.RED : Color.DARK_GRAY); // if destroyed then red else dark gray
        } else {
            g.setColor(shipPlacementColour == ShipPlacementColour.green ? Color.GREEN : Color.RED);
        }
        if(isSideways) paintHorizontal(g);
        else paintVertical(g);
    }

    /**
     * Sets the placement color to indicate the state of the ship.
     *
     * @param shipPlacementColour Valid sets ship to show Green, Invalid sets ship to show Red, Placed sets to defaults.
     */
    public void setShipPlacementColour(ShipPlacementColour shipPlacementColour) {
        this.shipPlacementColour = shipPlacementColour;
    }

    /**
     * Toggles the current state between vertical and horizontal.
     */
    public void toggleSideways() {
        isSideways = !isSideways;
    }

    /**
     * Call when a section has been destroyed to let the ship keep track of how many sections have been destroyed.
     */
    public void destroySection() {
        destroyedSections++;
    }

    /**
     * Tests if the number of sections destroyed indicate all segments have been destroyed.
     *
     * @return True if all sections have been destroyed.
     */
    public boolean isDestroyed() { 
        return destroyedSections >= segments; 
    }

    /**
     * Updates the position to draw the ship at to the newPosition.
     *
     * @param gridPosition Position where the ship is now on the grid.
     * @param drawPosition Position to draw the Ship at in Pixels.
     * 
     * we need draw_position since we are using graphics
     */
    public void setDrawPosition(Coordinate gridPosition, Coordinate drawPosition) {
        this.drawPosition = drawPosition;
        this.gridPosition = gridPosition;
    }

    /**
     * Gets the current direction of the ship.
     *
     * @return True if the ship is currently horizontal, or false if vertical.
     */
    public boolean isSideways() {
        return isSideways;
    }

    /**
     * Gets the number of segments that make up the ship. (current ship)
     *
     * @return The number of cells the ship occupies.
     */
    public int getSegments() {
        return segments;
    }

    /**
     * Gets a list of all cells that this ship occupies to be used for validation in AI checks.
     *
     * @return A list of all cells that this ship occupies.
     */
    public List<Coordinate> getOccupiedCoordinates() {
        List<Coordinate> result = new ArrayList<>();
        if(isSideways) { // handle the case when horizontal
            for(int x = 0; x < segments; x++) {
                result.add(new Coordinate(gridPosition.x, gridPosition.y));
            }
        } else { // handle the case when vertical
            for(int y = 0; y < segments; y++) {
                result.add(new Coordinate(gridPosition.x, gridPosition.y+y));
            }
        }
        return result;
    }

    /**
     * Draws the vertical ship by first drawing a triangle for the first cell, and then a
     * rectangle to cover the remaining cells based on the number of segments.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paintVertical(Graphics g) {
        int boatWidth = (int)(30 * 0.8);
        int boatLeftX = drawPosition.x + 30 / 2 - boatWidth / 2;
        g.fillPolygon(new int[]{drawPosition.x+30/2,boatLeftX,boatLeftX+boatWidth},
                new int[]{drawPosition.y+30/4,drawPosition.y+30,drawPosition.y+30},3);
        g.fillRect(boatLeftX,drawPosition.y+30, boatWidth,
                (int)(30 * (segments-1.2)));
    }

    /**
     * Draws the horizontal ship by first drawing a triangle for the first cell, and then a
     * rectangle to cover the remaining cells based on the number of segments.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paintHorizontal(Graphics g) {
        int boatWidth = (int)(30 * 0.8);
        int boatTopY = drawPosition.y + 30 / 2 - boatWidth / 2;
        g.fillPolygon(new int[]{drawPosition.x+30/4,drawPosition.x+30,drawPosition.x+30},
                      new int[]{drawPosition.y+30/2,boatTopY,boatTopY+boatWidth},3);
        g.fillRect(drawPosition.x+30,boatTopY,
                (int)(30 * (segments-1.2)), boatWidth);
    }
    
    
}
