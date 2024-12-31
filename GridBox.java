/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.game2;

/**
 *
 * @author arahm
 */

// do we have to make it all protected
public class GridBox {
    
    /**
     * The top left corner of the Rectangle.
     */
    protected Coordinate position;
    /**
     * Width of the Rectangle.
     */
    protected int width;
    /**
     * Height of the Rectangle.
     */
    protected int height;

    /**
     * Creates the new Rectangle with provided properties.
     *
     * @param position The top left corner of the Rectangle.
     * @param width Width of the Rectangle.
     * @param height Height of the Rectangle.
     */
    public GridBox(Coordinate position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    /**
     * @param x X coordinate of the top left corner.
     * @param y Y coordinate of the top left corner.
     * @param width Width of the rectangle.
     * @param height Height of the rectangle.
     */
    
    public GridBox(int x, int y, int width, int height) {
        this(new Coordinate(x,y),width,height);
    }

    /**
     * Getters
     *
     */
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Gets the top left corner of the Rectangle.
     *
     * @return Top left corner of the Rectangle.
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Tests if the targetPosition is inside the Rectangle.
     *
     * @param targetPosition Position to test if it is inside the Rectangle.
     * @return True if the targetPosition is inside this Rectangle.
     */
    public boolean isPositionInside(Coordinate targetPosition) {
        return targetPosition.x >= position.x && targetPosition.y >= position.y
                && targetPosition.x < position.x + width && targetPosition.y < position.y + height;
    } /////// ????
    
}
