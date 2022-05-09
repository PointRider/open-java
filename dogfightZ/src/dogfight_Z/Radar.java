package dogfight_Z;

import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharImage;
import graphic_Z.utils.GraphicUtils;

public class Radar extends CharImage
{
	private Aircraft myself;
	private float maxSearchRange;
	private char  front[][];
	private char  back[][];
	private char  emptyLine[];
	private float nowAngle;
	private CharDynamicHUD painter;
	private CharImage sideEdge;
    private int x, y, r;
    
	@Override
	public void reSizeScreen(int resolution[], char fraps_buffer[][]) {
		super.reSizeScreen(resolution, fraps_buffer);
		sideEdge.reSizeScreen(resolution, fraps_buffer);
		painter.reSizeScreen(resolution, fraps_buffer);
	}
	
	public Radar
	(
		String HUDImgFile, 
		String HUDPainterImg,
		char[][] frapsBuffer, 
		int HUDLayer, 
		int[] scrResolution,
		int size,
		int Location_X,
		int Location_Y,
		Aircraft myJet,
		float maxSearch_range
	) {
	    super(null, frapsBuffer, size, size, Location_X - (size>>1), Location_Y - (size>>1), HUDLayer, scrResolution, true);
		myself			= myJet;
		maxSearchRange	= maxSearch_range;
		front			= new char[size][size];
		back			= new char[size][size];
		emptyLine       = new char[size];
		nowAngle		= GraphicUtils.RAD270;
		r               = size >> 1;
		sideEdge = new CharImage(HUDImgFile, frapsBuffer, size, size, Location_X - r, Location_Y - r, HUDLayer, scrResolution, true);
		painter = new CharDynamicHUD(HUDPainterImg, frapsBuffer, HUDLayer, scrResolution, size, size, Location_X, Location_Y , 0.0F, true);

		for(int i = 0, j = size; i < j; ++i) emptyLine[i] = ' ';
	}
	
	@Override
	public final void printNew()
	{
		if(!visible) return;
		nowAngle += 0.1047197551196598F;
		painter.angle = nowAngle;
        painter.printNew();
        sideEdge.printNew();

        back[centerX][centerY] = '+';
		for(int i=0 ; i<r ; ++i)
		{
			x = (int)(GraphicUtils.cos(nowAngle) * i);
			y = (int)(GraphicUtils.sin(nowAngle) * i);
			
			x += centerX;
			y += centerY;

			HUDImg[y][x] = back[y][x];
		}

		nowAngle %= GraphicUtils.RAD360;
        
		super.printNew();
	}
	
	@Override
    public final void setLocation(int X, int Y)
    {
        super.setLocation(X - r, Y - r);
        sideEdge.setLocation(X - r, Y - r);
        painter.setLocation(X, Y);
    }
    
    public final void clear() {
        for(int i = 0, j = size[0]; i < j; ++i) {
            System.arraycopy(emptyLine, 0, front[i], 0, size[0]);
            System.arraycopy(emptyLine, 0, back[i], 0, size[0]);
        }
    }
    
    public final void report(float Y, float Z, boolean friendly) {
        int x, y;

        float R = GraphicUtils.rangeXY(Y, Z, 0, 0);
        
        if(GraphicUtils.abs(R) <= maxSearchRange)
        {
            x = (int) (Y * r / maxSearchRange);
            y = (int) (Z * r / maxSearchRange);
            
            x += centerX;
            y += centerY + 1;
            
            if(GraphicUtils.abs(myself.roll_angle[1]) < GraphicUtils.RAD90)
                 y = size[1] - y;
            else x = size[0] - x;
            
            x = x < size[0]? x : size[0]-1;
            y = y < size[1]? y : size[1]-1;
            
            if(x < 0) x = 0;
            if(y < 0) y = 0;
            
            back[y][x] = (friendly? 'o' : 'X');
        }
    }
}
