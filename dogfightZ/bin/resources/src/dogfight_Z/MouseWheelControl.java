package dogfight_Z;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

public class MouseWheelControl implements MouseWheelListener, MouseListener
{
	protected List<Integer> FrapsEventQueue_keyboard;
	public MouseWheelControl(List<Integer> keyboardEventQueue)
	{
		FrapsEventQueue_keyboard = keyboardEventQueue;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		FrapsEventQueue_keyboard.add(e.getWheelRotation()<0? (Integer)(4096) : (Integer)(8192));
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO 自动生成的方法存根
		
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO 自动生成的方法存根
		int button = e.getButton();
		if(button != 0)
			FrapsEventQueue_keyboard.add(16384 + button);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO 自动生成的方法存根
		int button = e.getButton();
		if(button != 0)
			FrapsEventQueue_keyboard.add(-16384 - button);
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO 自动生成的方法存根
		
	}
}
