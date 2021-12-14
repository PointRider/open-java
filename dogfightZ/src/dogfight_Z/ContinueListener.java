package dogfight_Z;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ContinueListener implements KeyListener
{
	protected Game myGame;
	public    boolean paused;
	private	  long pauseTime;
	
	public ContinueListener(Game gme)
	{
		pauseTime = 0;
		myGame = gme;
		paused = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// TODO 自动生成的方法存根
		if(e.getKeyCode() == KeyEvent.VK_P)
		{
			paused = !paused;
			
			if(paused) {
				pauseTime = System.currentTimeMillis() / 1000;
				myGame.pause();
			} else {
				pauseTime = System.currentTimeMillis() / 1000 - pauseTime;
				myGame.gameStopTime += pauseTime;
				myGame.resume();
				synchronized(myGame) {
				    myGame.notifyAll();
				}
			}
		}
		/*else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
		    myGame.exit();
		}*/
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO 自动生成的方法存根

	}

}
