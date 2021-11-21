package graphic_Z.HUDs;

public class CharLabel extends CharHUD
{
	protected int location[];
	protected String text;
	//protected char fraps_buffer[][];	//帧缓冲区(引用，实体在CharVisualManager中)
	
	//public boolean transparentAtSpace;
	//String HUDImgFile, char frapsBuffer[][], short HUDLayer, short scrResolution[], boolean transparent_at_space
	public CharLabel
	(
		char frapsBuffer[][], 
		int HUDLayer, 
		int scrResolution[],
		boolean transparentAtSpace
	)
	{
		super(null, frapsBuffer, HUDLayer, scrResolution, transparentAtSpace);
		resolution = scrResolution;
		layer		 = HUDLayer;
		fraps_buffer = frapsBuffer;
		location = new int[2];
		text = new String("");
		location[0] = 0;
		location[1] = 0;
		this.transparentAtSpace = transparentAtSpace;
		visible = true;
	}
	
	public CharLabel
	(
		char frapsBuffer[][], 
		int HUDLayer, 
		int scrResolution[], 
		String Text, 
		int location_X, 
		int location_Y,
		boolean transparentAtSpace
	)
	{
		this(frapsBuffer, HUDLayer, scrResolution, transparentAtSpace);
		//if(Text != null)
		//	text = new String(Text);
		text = Text;
		location[0] = location_X;
		location[1] = location_Y;
	}
	
	public CharLabel
	(
		char frapsBuffer[][], 
		int HUDLayer, 
		int scrResolution[], 
		String Text, 
		int location_X, 
		int location_Y
	)
	{
		this(frapsBuffer, HUDLayer, scrResolution, Text, location_X, location_Y, true);
	}
	
	@Override
	public void printNew()
	{
		if(visible)
		{
			char tmp;
			for (
				int x=location[0], y=0, i=0, j=0; 
			    (y = location[1] + j) < resolution[1] && y >= 0 && x >= 0 && i<text.length(); 
				++i, ++x
			)	if ((tmp=(char)text.charAt(i)) != ' ' || transparentAtSpace == false) {
				if(tmp == '\n') {
					++j; x = location[0] - 1;
				} else if(x < resolution[0]) fraps_buffer[y][x] = tmp;
			}
		}
	}
	
    public void printChar(char c)
    {
        if(visible)
        {
            char tmp;
            for (
                int x=location[0], y=0, i=0, j=0; 
                (y = location[1] + j) < resolution[1] && x<resolution[0] && i<text.length(); 
                ++i, ++x
            )   if ((tmp=(char)text.charAt(i)) != ' ' || transparentAtSpace == false) {
                if(tmp == '\n') {
                    ++j; x = location[0] - 1;
                } else fraps_buffer[y][x] = c;
            }
        }
    }
	
	public CharLabel
	(
		char frapsBuffer[][], 
		int HUDLayer, 
		int scrResolution[], 
		int locationX, int locationY
	)
	{
		this(frapsBuffer, HUDLayer, scrResolution, true);
		location[0] = locationX;
		location[1] = locationY;
	}
	
	public final void setText(String Text)
	{
		text = new String(Text);
	}
	
	public void setLocation(int locationX, int locationY)
	{
		location[0] = locationX;
		location[1] = locationY;
	}
}
