package dogfight_Z;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ContinueListener implements KeyListener
{
	protected Game myGame;
	protected Thread gameThread;
	public    boolean paused;
	private	  long pauseTime;
	
	public ContinueListener(Game gme, Thread game_thread)
	{
		pauseTime = 0;
		myGame = gme;
		gameThread = game_thread;
		paused = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO 自动生成的方法存根
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void keyPressed(KeyEvent e)
	{
		// TODO 自动生成的方法存根
		if(e.getKeyCode() == KeyEvent.VK_P)
		{
			paused = !paused;
			
			if(paused)
			{
				pauseTime = System.currentTimeMillis() / 1000;
				gameThread.suspend();
				myGame.bgmThread.suspend();
			}
			else
			{
				pauseTime = System.currentTimeMillis() / 1000 - pauseTime;
				gameThread.resume();
				myGame.gameStopTime += pauseTime;
				myGame.bgmThread.resume();
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO 自动生成的方法存根

	}

}
