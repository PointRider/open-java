package graphic_Z.Keyboard_Mouse;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class KeyboardResponse implements KeyListener
{
	protected int maxKeyCodeBufferSize;
	protected LinkedList<Integer> FrapsEventQueue_keyboard;	//键盘事件队列(引用，实体在EventManager中)
	
	public KeyboardResponse(LinkedList<Integer> FrapsEventQueue, int MaxKeyCodeBufferSize)
	{
		FrapsEventQueue_keyboard = FrapsEventQueue;
		maxKeyCodeBufferSize = MaxKeyCodeBufferSize;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	    switch(e.getKeyCode()) {
        case KeyEvent.VK_E: case KeyEvent.VK_R:
            return;
        }
	    
		if(FrapsEventQueue_keyboard.size() < maxKeyCodeBufferSize)
			FrapsEventQueue_keyboard.addLast(e.getKeyCode());
		
		if(FrapsEventQueue_keyboard.size() < maxKeyCodeBufferSize)
			if(e.isShiftDown())FrapsEventQueue_keyboard.addLast(KeyEvent.VK_SHIFT);
		
		if(FrapsEventQueue_keyboard.size() < maxKeyCodeBufferSize)
			if(e.isControlDown())FrapsEventQueue_keyboard.addLast(KeyEvent.VK_CONTROL);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	    switch(e.getKeyCode()) {
	    case KeyEvent.VK_E: case KeyEvent.VK_R:
	        if(FrapsEventQueue_keyboard.size() < maxKeyCodeBufferSize)
	            FrapsEventQueue_keyboard.addLast(e.getKeyCode());
	        break;
	    }
	    /*
		if(FrapsEventQueue_keyboard.size() < maxKeyCodeBufferSize)
			FrapsEventQueue_keyboard.addLast(-e.getKeyCode());
		
		if(FrapsEventQueue_keyboard.size() < maxKeyCodeBufferSize)
			if(e.isShiftDown())FrapsEventQueue_keyboard.addLast(-KeyEvent.VK_SHIFT);
		
		if(FrapsEventQueue_keyboard.size() < maxKeyCodeBufferSize)
			if(e.isControlDown())FrapsEventQueue_keyboard.addLast(-KeyEvent.VK_CONTROL);
		*/
	}

}
