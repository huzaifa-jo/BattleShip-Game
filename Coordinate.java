package com.mycompany.game2;

public class Coordinate {
    
    public int x, y;
    public static final Coordinate ZERO = new Coordinate(0,0);
    
    public static final Coordinate DOWN = new Coordinate(0,1);
    public static final Coordinate UP = new Coordinate(0,-1);
    public static final Coordinate LEFT = new Coordinate(-1,0);
    public static final Coordinate RIGHT = new Coordinate(1,0);
 

    public Coordinate(int x, int y) { // set coordinate
        this.x = x;
        this.y = y;
    }

    public Coordinate(Coordinate position) { // overriding constructor, just in the coordinate form
        this.x = position.x;
        this.y = position.y;
    }

    //getter  
    public Coordinate getCoordinate() {
        return new Coordinate(x, y);
    }
    //setter
    public void setCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // add, sub, multiply
    
    public void add(Coordinate position) {
        this.x += position.x;
        this.y += position.y;
    }

    public void subtract(Coordinate position) {
        this.x -= position.x;
        this.y -= position.y;
    }
    
    public void multiply(int amount) {
        x *= amount;
        y *= amount;
    }

    // distance found using pythagoras theorem
    
    public double distanceFrom(Coordinate position) {
        return Math.sqrt(Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2));
    } // check this
    
    @Override
    public String toString() { // print in words
        return "(" + x + ", " + y + ")";
    }

  
    /**
     * Compares the Position object against another object.
     * Any non-Position object will return false. Otherwise compares x and y for equality.
     *
     * @param o Object to compare this Position against.
     * @return True if the object o is equal to this position for both x and y.
     */
    @Override
    public boolean equals(Object o) { // no clue what this does
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate position = (Coordinate) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        return hash;
    } 
    
}
