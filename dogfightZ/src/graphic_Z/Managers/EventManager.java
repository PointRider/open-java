package graphic_Z.Managers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import graphic_Z.Common.SinglePoint;
import graphic_Z.Keyboard_Mouse.KeyboardResponse;
import graphic_Z.Keyboard_Mouse.MouseResponse;

public class EventManager extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1459883095946993721L;
	
	private final int PCScreenCenter_X;
	private final int PCScreenCenter_Y;
	
	//public final static String FONTFAMILY = "Consolas";
	public final static String FONTFAMILY = "DejaVu Sans Mono";
	
	public JTextArea		   mainScr;
	
	protected KeyboardResponse keyResponse;
	protected MouseResponse	   mouseResponse;
	protected int			   maxKeyBufferSize = 6;
	
	protected char fraps_buffer[][];					//帧缓冲(引用)
	
	protected	LinkedList<SinglePoint>	EventFrapsQueue_mouse;	//鼠标事件队列，存储鼠标位移坐标(x,y)
	public		LinkedList<Integer> EventFrapsQueue_keyboard;	//键盘事件队列，存储键盘扫描码
	
	public EventManager(int maxKeyBuffer_size)
	{
	    
		EventFrapsQueue_mouse	 = new LinkedList<SinglePoint>();
		EventFrapsQueue_keyboard = new LinkedList<Integer>();
		
		maxKeyBufferSize = maxKeyBuffer_size;
		
		keyResponse		= new KeyboardResponse(EventFrapsQueue_keyboard, maxKeyBufferSize);
		mouseResponse	= new MouseResponse(EventFrapsQueue_mouse);
		
		PCScreenCenter_X = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width >> 1);
		PCScreenCenter_Y = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height >> 1);
		
		addKeyListener(keyResponse);
		
		setSize(PCScreenCenter_X << 1, PCScreenCenter_Y << 1);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//---------------------------Setup GUI----------------------------------
		setLocation(0, 0);
		setUndecorated(true);
		//setOpacity(0.9f);
		
		mainScr = new JTextArea();
		mainScr.setLocation(0, 0);
		mainScr.setSize(PCScreenCenter_X << 1, PCScreenCenter_Y << 1);
		mainScr.setEditable(false);
		mainScr.setFocusable(false);
		mainScr.setText("Welcome to the game world !");
		mainScr.setFont(new Font(FONTFAMILY, Font.PLAIN, 8));
		mainScr.setBackground(new Color(0, 0, 0));
		mainScr.setForeground(new Color(255, 255, 255));
		//mainScr.setBackground(new Color(255, 200, 64));
		//mainScr.setForeground(new Color(0, 0, 0));
		
		mainScr.addMouseMotionListener(mouseResponse);
		
		this.add(mainScr);
		//----------------------------------------------------------------------
		
		//--------------------------Hide Cursor---------------------------------
		URL classUrl = this.getClass().getResource("");  
		Image imageCursor = Toolkit.getDefaultToolkit().getImage(classUrl);  
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
		                    imageCursor,  new Point(0, 0), "cursor")); 
		//----------------------------------------------------------------------
		
		//setVisible(true);
	}
	
	public EventManager()
	{
		this(6);
	}
	
	public void setScrZoom(int size)
	{
		mainScr.setFont(new Font(FONTFAMILY, Font.PLAIN, size));
	}

	public int popAKeyOpreation()
	{
		int keyCode = -1;
		if(EventFrapsQueue_keyboard.size() > 0)
		{
			keyCode = EventFrapsQueue_keyboard.poll();
			if(keyCode == KeyEvent.VK_ESCAPE)
				System.exit(0);
		}
		
		return keyCode;
	}
	
	public SinglePoint popAMouseOpreation()
	{
		if(EventFrapsQueue_mouse.size() > 0)
		{
			SinglePoint xy = new SinglePoint(EventFrapsQueue_mouse.poll());
			xy.x -= PCScreenCenter_X;
			xy.y -= PCScreenCenter_Y;
			
			return xy;
		}
		
		return new SinglePoint(0, 0);
	}
}
