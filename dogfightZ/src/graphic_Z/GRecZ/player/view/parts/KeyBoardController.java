package graphic_Z.GRecZ.player.view.parts;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import graphic_Z.GRecZ.player.view.GRecZPlayer;

public class KeyBoardController implements KeyListener {

    public GRecZPlayer player;
    
    public KeyBoardController(GRecZPlayer player) {
        this.player = player;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO 自动生成的方法存根

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO 自动生成的方法存根

    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        switch(e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            player.setVisible(false);
            player.dispose();
            break;
        case KeyEvent.VK_SPACE: case KeyEvent.VK_ENTER:
            
            break;
        }
    }

}