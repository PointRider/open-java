package graphic_Z.HUDs;

public class CharLabel extends CharHUD
{
	protected short location[];
	protected String text;
	//protected char fraps_buffer[][];	//帧缓冲区(引用，实体在CharVisualManager中)
	
	//public boolean transparentAtSpace;
	//String HUDImgFile, char frapsBuffer[][], short HUDLayer, short scrResolution[], boolean transparent_at_space
	public CharLabel
	(
		char frapsBuffer[][], 
		short HUDLayer, 
		short scrResolution[],
		boolean transparentAtSpace
	)
	{
		super(null, frapsBuffer, HUDLayer, scrResolution, transparentAtSpace);
		resolution = scrResolution;
		layer		 = HUDLayer;
		fraps_buffer = frapsBuffer;
		location = new short[2];
		text = new String("");
		location[0] = 0;
		location[1] = 0;
		this.transparentAtSpace = transparentAtSpace;
		visible = true;
	}
	
	public CharLabel
	(
		char frapsBuffer[][], 
		short HUDLayer, 
		short scrResolution[], 
		String Text, 
		short location_X, 
		short location_Y,
		boolean transparentAtSpace
	)
	{
		this(frapsBuffer, HUDLayer, scrResolution, transparentAtSpace);
		if(Text != null)
			text = new String(Text);
		location[0] = location_X;
		location[1] = location_Y;
	}
	
	public CharLabel
	(
		char frapsBuffer[][], 
		short HUDLayer, 
		short scrResolution[], 
		String Text, 
		short location_X, 
		short location_Y
	)
	{
		this(frapsBuffer, HUDLayer, scrResolution, Text, location_X, location_Y, true);
	}
	
	public void printNew()
	{
		if(visible)
		{
			char tmp;
			for
			(
				short x=location[0], i=0, j=0; 
				x<resolution[0] && i<text.length(); 
				++i, ++x
			)	if ((tmp=(char)text.charAt(i)) != ' ' || transparentAtSpace == false)
			{
				if(tmp == '\n')
				{
					++j; x=(short) (location[0]-1);
				}
				else fraps_buffer[location[1] + j][x] = tmp;
			}
		}
	}
	
	public CharLabel
	(
		char frapsBuffer[][], 
		short HUDLayer, 
		short scrResolution[], 
		short X, short Y
	)
	{
		this(frapsBuffer, HUDLayer, scrResolution, true);
		location[0] = X;
		location[1] = Y;
	}
	
	public final void setText(String Text)
	{
		text = new String(Text);
	}
	
	public void setLocation(short X, short Y)
	{
		location[0] = X;
		location[1] = Y;
	}
}
