package graphic_Z.GRecZ.player.view.parts;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import graphic_Z.GRecZ.player.view.GRZPlayer;

public class UserController implements KeyListener, MouseWheelListener, MouseListener, MouseMotionListener {

    private Robot rbt;
    private int flag = 0;
    private final int PCScreenCenter_X;
    private final int PCScreenCenter_Y;
    private GRZPlayer player;
    
    public UserController(GRZPlayer player) {
        this.player = player;
        try
        {
            rbt = new Robot();
        } catch (AWTException e)
        {e.printStackTrace();}

        PCScreenCenter_X = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width >> 1);
        PCScreenCenter_Y = ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height >> 1);

        rbt.mouseMove(PCScreenCenter_X, PCScreenCenter_Y);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            player.getController().goAround();
            break;
        case KeyEvent.VK_RIGHT:
            player.getController().goAhead();
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        switch(e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            player.getController().resume();
            player.getController().finish();
            player.setVisible(false);
            player.dispose();
            break;
        case KeyEvent.VK_SPACE: case KeyEvent.VK_ENTER:
            pauseOrResume();
            break;
        case KeyEvent.VK_BACK_SPACE:
            player.getController().reset();
            break;
        case KeyEvent.VK_UP:
            player.getController().goAround(1);
            break;
        case KeyEvent.VK_DOWN:
            player.getController().goAhead(1);
            break;
        case KeyEvent.VK_E:
            player.getController().previousBgm();
            break;
        case KeyEvent.VK_R:
            player.getController().nextBgm();
            break;
        }
    }

    private void pauseOrResume() {
        if(player.getController().isPaused()) {
            player.getController().resume();
        } else {
            player.getController().pause();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        pauseOrResume();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation() < 0) {
            player.getController().goAround(1);
        } else {
            player.getController().goAhead(1);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        ++flag;
        
        int f = e.getX() - PCScreenCenter_X;
        if(f >= 0) {
            player.getController().goAhead(f << 1);
        } else {
            player.getController().goAround((-f) << 1);
        }
        
        if(flag == 2) 
        {
            rbt.mouseMove(PCScreenCenter_X, PCScreenCenter_Y);
            flag = 0;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
