
package com.mycompany.game2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;


public class ControlPanel extends JPanel implements MouseListener, MouseMotionListener{

    public enum GameState { ShipMode, GameMode, FinishedMode }
   
    private MessageBoard messageBoard;
    private Grid player;
    private Grid computer;
    private AI aiLevel;

    private Ship temporaryShip; // shipmodes temporary ship
    
    private Coordinate temporaryCoordinate;
   
    private int shipNum; //Reference to which ship should be placed next during the PlacingShips state.
    
    private GameState gameState; // what state the game is in
    
   
    public ControlPanel(int choice) {
        // create 2 grids
        computer = new Grid(500,0);
        player = new Grid(0, 0);
        
        setBackground(new Color(30, 100, 200)); // add cyan color      
       
       // setPreferredSize(new Dimension(computer.getWidth() * 3 , player.getPosition().y + player.getHeight() + 20));
        setPreferredSize(new Dimension(computer.getWidth() * 3 , player.getPosition().y + player.getHeight() + 20));
        addMouseListener(this);
        addMouseMotionListener(this); // what do these 2 do
        if(choice == 0) aiLevel = new EasyAI(player);
        else aiLevel = new MediumAI(player,choice == 2,choice == 2);
        
        messageBoard = new MessageBoard(new Coordinate(computer.getWidth() * 2 + 50, 0), 250, 49);
        restart(); 
        
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void restart() {
        
        computer.reset();
        player.reset();
        aiLevel.reset();
        
        // Player can see their own ships by default
        player.setShowShips(true);
        
        temporaryCoordinate = Coordinate.ZERO;
        temporaryShip = new Ship(Coordinate.ZERO,
                               new Coordinate(player.getPosition().x, player.getPosition().y), // u can add coordinate 0
                               Grid.BOAT_SIZES[0], true);
        shipNum = 0; // out of the five ships
        updateShipPlacement(temporaryCoordinate); // ?
        
        computer.populateShips();
        
        messageBoard.reset();
        gameState = GameState.ShipMode;     
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
    private void tryPlaceShip(Coordinate mousePosition) {
        
        Coordinate targetPosition = player.getCoordinateInGrid(mousePosition.x, mousePosition.y);
        
        updateShipPlacement(targetPosition); // ??
        if(player.canPlaceShipAt(targetPosition.x, targetPosition.y,
                Grid.BOAT_SIZES[shipNum],temporaryShip.isSideways())) {
            placeShip(targetPosition);
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    private void placeShip(Coordinate targetPosition) {

        player.placeShip(temporaryShip,temporaryCoordinate.x,temporaryCoordinate.y);
        temporaryShip.setShipPlacementColour(Ship.ShipPlacementColour.placed);   // color change not necessary
        
        // then place the ship when mouse click and increment ship num
        
        shipNum++;
        // If there are still ships to place
        if(shipNum < Grid.BOAT_SIZES.length) {
            temporaryShip = new Ship(new Coordinate(targetPosition.x, targetPosition.y),
                          new Coordinate(player.getPosition().x + targetPosition.x * Grid.CELL_SIZE,
                       player.getPosition().y + targetPosition.y * Grid.CELL_SIZE),
                          Grid.BOAT_SIZES[shipNum], true);
            updateShipPlacement(temporaryCoordinate);
        } 
        else {
            gameState = GameState.GameMode;
            messageBoard.setTopLine("Begin Attacking!");
            messageBoard.setBottomLine("Destroy all enemy ships to win!");
        }
    }
    
    
    /**
     * Constrains the ship to fit inside the grid. Updates the drawn position of the ship,
     * and changes the color of the ship based on whether it is a valid or invalid placement.
     *
     * @param targetPos The grid coordinate where the ship being placed should change to.
     */
    private void updateShipPlacement(Coordinate targetPos) {
        // Constrain to fit inside the grid
        // have to understand this math !!
        if(temporaryShip.isSideways()) {
            targetPos.x = Math.min(targetPos.x, Grid.GRID_WIDTH - Grid.BOAT_SIZES[shipNum]);
        } 
        else {
            targetPos.y = Math.min(targetPos.y, Grid.GRID_HEIGHT - Grid.BOAT_SIZES[shipNum]);
        }
        
        // Update drawing position to use the new target position
        temporaryShip.setDrawPosition(new Coordinate(targetPos),
                                    new Coordinate(player.getPosition().x + targetPos.x * Grid.CELL_SIZE,
                                 player.getPosition().y + targetPos.y * Grid.CELL_SIZE));
        
        // Store the grid position for other testing cases (what does this mean bruh
        
        temporaryCoordinate = targetPos;
        
        
        // Change the colour of the ship based on whether it could be placed at the current location.
        if(player.canPlaceShipAt(temporaryCoordinate.x, temporaryCoordinate.y,
                Grid.BOAT_SIZES[shipNum], temporaryShip.isSideways())) {
            temporaryShip.setShipPlacementColour(Ship.ShipPlacementColour.green);
        } else {
            temporaryShip.setShipPlacementColour(Ship.ShipPlacementColour.red);
        }
    }
    
    
    
      /**
     * Updates location of the ship being placed if the mouse is inside the grid.
     *
     * @param mousePosition Mouse coordinates inside the panel.
     */
    private void tryMovePlacingShip(Coordinate mousePosition) {
        if(player.isPositionInside(mousePosition)) {
            Coordinate targetPos = player.getCoordinateInGrid(mousePosition.x, mousePosition.y);
            updateShipPlacement(targetPos);
        }
    }
    

    /**
     * Attempts to fire at a position on the computer's board.
     * The player is notified if they hit/missed, or nothing if they
     * have clicked the same place again. After the player's turn,
     * the AI is given a turn if the game is not already ended.
     *
     * @param mousePosition Mouse coordinates inside the panel.
     */
    private void tryFireAtComputer(Coordinate mousePosition) {
        Coordinate targetPosition = computer.getCoordinateInGrid(mousePosition.x,mousePosition.y);
        // Ignore if position was already clicked
        if(!computer.isPositionMarked(targetPosition)) {
            playerTurn(targetPosition);
            // Only do the AI turn if the game didn't end from the player's turn.
            if(!computer.areAllShipsDestroyed()) {
                AITurn();
            }
        }
    }

    /**
     * Processes the player's turn based on where they selected to attack.
     * Based on the result of the attack a message is displayed to the player,
     * and if they destroyed the last ship the game updates to a won state.
     *
     * @param targetPosition The grid position clicked on by the player.
     */
    private void playerTurn(Coordinate targetPosition) {
        boolean hit = computer.markPosition(targetPosition);
        String hitMiss = hit ? "Hit" : "Missed";
        String destroyed = "";
        if(hit && computer.getMarkerAtPosition(targetPosition).getAssociatedShip().isDestroyed()) {
            destroyed = "(Destroyed)";
        } // ??
        
        messageBoard.setTopLine("Player " + hitMiss + " " + targetPosition + destroyed); // we can change this
        
        if(computer.areAllShipsDestroyed()) {
            // Player wins!
            gameState = GameState.FinishedMode;
            messageBoard.showGameOver(true); // or directly print game is over
        }
    }

    /**
     * Processes the AI turn by using the AI Controller to select a move.
     * Then processes the result to display it to the player. If the AI
     * destroyed the last ship the game will end with AI winning.
     */
    private void AITurn() {
        Coordinate aiMove = aiLevel.selectMove(); // select it randomly
        // same as playerTurn, just that in the former, a coordinate is passed as a parameter
        boolean hit = player.markPosition(aiMove);
        String hitMiss = hit ? "Hit" : "Missed";
        String destroyed = "";
        if(hit && player.getMarkerAtPosition(aiMove).getAssociatedShip().isDestroyed()) {
            destroyed = "(Destroyed)";
        }
        // mssgboard needs to be changed as grid should be sideways, and maybe bigger
        messageBoard.setBottomLine("Computer " + hitMiss + " " + aiMove + destroyed);
        
        if(player.areAllShipsDestroyed()) {
            // Computer wins!
            gameState = GameState.FinishedMode;
            messageBoard.showGameOver(false); // can directly print that game is over
        }
    }

    
     @Override
    public void paint(Graphics g) {
        super.paint(g);
        computer.paint(g); // check selectiongrid paint
        player.paint(g);
        if(gameState == GameState.ShipMode) temporaryShip.paint(g);
       
        messageBoard.paint(g); // finally draw the status panel, but I want to draw this on the right side
    }

    /**
     * Triggered when the mouse button is released. If in the PlacingShips state and the
     * cursor is inside the player's grid it will try to place the ship.
     * Otherwise if in the FiringShots state and the cursor is in the computer's grid,
     * it will try to fire at the computer.
     *
     * @param e Details about where the mouse event occurred.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        Coordinate mousePosition = new Coordinate(e.getX(), e.getY());
        if(gameState == GameState.ShipMode && player.isPositionInside(mousePosition)) {
            tryPlaceShip(mousePosition);
        } else if(gameState == GameState.GameMode && computer.isPositionInside(mousePosition)) {
            tryFireAtComputer(mousePosition);
        }
        repaint(); // why repaint
    }

    /**
     * Triggered when the mouse moves inside the panel. Does nothing if not in the PlacingShips state.
     * Will try and move the ship that is currently being placed based on the mouse coordinates.
     *
     * @param e Details about where the mouse event occurred.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        
        if(gameState != GameState.ShipMode) return;
        tryMovePlacingShip(new Coordinate(e.getX(), e.getY()));
        
        repaint();
    }

    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mouseClicked(MouseEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mousePressed(MouseEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mouseEntered(MouseEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mouseExited(MouseEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mouseDragged(MouseEvent e) {}
    
    
    
    public void handleInput(int keyCode) { 
        
        if(keyCode == KeyEvent.VK_ESCAPE) System.exit(1);
         
        else if(gameState == GameState.ShipMode && keyCode == KeyEvent.VK_Z) {
            temporaryShip.toggleSideways();
            updateShipPlacement(temporaryCoordinate); // explain this in detail
        } 
        repaint(); // continuously update it
    }
  
    
}
