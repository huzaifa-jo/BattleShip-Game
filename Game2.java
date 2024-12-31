/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.game2;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 *
 * @author arahm
 */
public class Game2 implements KeyListener {

    public static void main(String[] args) {
        new Game2();
    }
    
     /**
     * Creates the JFrame with a GamePanel inside it, attaches a key listener,
     * and makes everything visible.
     */
    
    private ControlPanel controlPanel;
    
    public Game2() {
        // Choose the AI Difficulty
     
        
        String[] options = new String[] {"Commander", "Admiral"};
        String message = "Commander is easy,\nAdmiral is hard";
        int difficultyChoice = JOptionPane.showOptionDialog(null, message, "Choose difficulty:", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        JFrame frame = new JFrame("Battleship");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        controlPanel = new ControlPanel(difficultyChoice);
        frame.getContentPane().add(controlPanel);

        frame.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        controlPanel.handleInput(e.getKeyCode());
    }

    
    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void keyReleased(KeyEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
