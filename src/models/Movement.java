package models;

import views.GamePlay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Movement implements KeyListener {
    private GamePlay gamePlay;
    private Timer movementCooldown;

    public Movement(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
        gamePlay.addKeyListener(this);
        movementCooldown = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePlay.getGameViewModel().getPlayer().setVelocityX(0);
            }
        });
        movementCooldown.setRepeats(false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(gamePlay);
                if (gameFrame != null) {
                    gameFrame.dispose();
                }
                break;
            case KeyEvent.VK_UP:
                gamePlay.getGameViewModel().getPlayer().setVelocityY(-10);
                break;
            case KeyEvent.VK_LEFT:
                gamePlay.getGameViewModel().getPlayer().setVelocityX(-4);
                movementCooldown.restart();
                break;
            case KeyEvent.VK_RIGHT:
                gamePlay.getGameViewModel().getPlayer().setVelocityX(4);
                movementCooldown.restart();
                break;
            case KeyEvent.VK_DOWN:
                gamePlay.getGameViewModel().getPlayer().setVelocityY(gamePlay.getGameViewModel().getPlayer().getVelocityY() + 1);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
