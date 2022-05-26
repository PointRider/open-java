package dogfight_Z;

import graphic_Z.HUDs.CharImage;
import graphic_Z.HUDs.CharLabel;

public class ScoreList extends CharImage
{
	public Iterable<Aircraft> list;
	private CharLabel temp;
	private int camp;
	private int myLocation[];
	public ScoreList
	(
		String backGroundImgFile, 
		char[][] frapsBuffer, 
		int HUDLayer, 
		int[] scrResolution,
		int size_X,
		int size_Y,
		int Location_X,
		int Location_Y,
		boolean transparent_at_space,
		Iterable<Aircraft> thelist,
		int players_camp
	)
	{
		super(backGroundImgFile, frapsBuffer, size_X, size_Y, Location_X - (size_X >> 1), Location_Y - (size_Y >> 1), HUDLayer, scrResolution, transparent_at_space);
		myLocation = new int[] {Location_X, Location_Y};
		list = thelist;
		temp = new CharLabel(frapsBuffer, 0, scrResolution, true);
		camp = players_camp;
	}
	
	
    @Override
    public void setLocation(int x, int y)
    {
        super.setLocation(x - (size[0] >> 1), y - (size[1] >> 1));
        myLocation[0] = x;
        myLocation[1] = y;
    }

    @Override
	public void reSizeScreen(int resolution[], char fraps_buffer[][]) {
		super.reSizeScreen(resolution, fraps_buffer);
		temp.reSizeScreen(resolution, fraps_buffer);
	}
	
	@Override
	public void printNew()
	{
		if(visible)
		{
			super.printNew();
			int line = 0;
			
			temp.setText("Name");
			temp.setLocation(myLocation[0] + 3 - centerX, myLocation[1] + 2 + line - centerY);
			temp.printNew();
			temp.setText("Camp");
			temp.setLocation(myLocation[0] - 3, myLocation[1] + 2 + line - centerY);
			temp.printNew();
			temp.setText("Killed");
			temp.setLocation(myLocation[0] + 8, myLocation[1] + 2 + line - centerY);
			temp.printNew();
			temp.setText("Dead");
			temp.setLocation(myLocation[0] + 16, myLocation[1] + 2 + line - centerY);
			temp.printNew();
			
			for(Aircraft a : list)
			{
				temp.setText(a.getID());
				temp.setLocation(myLocation[0] + 3 - centerX, myLocation[1] + 4 + line - centerY);
				temp.printNew();
				
				temp.setText(a.getCamp() + ":" + (a.getCamp() == camp? "Friend" : "Enemy"));
				temp.setLocation(myLocation[0] - 3, myLocation[1] + 4 + line - centerY);
				temp.printNew();
				
				temp.setText(Integer.toString(a.killed));
				temp.setLocation(myLocation[0] + 8, myLocation[1] + 4 + line - centerY);
				temp.printNew();
				
				temp.setText(Integer.toString(a.dead));
				temp.setLocation(myLocation[0] + 16, myLocation[1] + 4 + line - centerY);
				temp.printNew();
				
				++line;
			}
		}
		
	}
}
