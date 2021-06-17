package dogfight_Z;

import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharLabel;

public class ScoreList extends CharDynamicHUD
{
	public Iterable<Aircraft> list;
	private CharLabel temp;
	private short camp;
	public ScoreList
	(
		String backGroundImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y,
		short Location_X,
		short Location_Y,
		boolean transparent_at_space,
		Iterable<Aircraft> thelist,
		short players_camp
	)
	{
		super(backGroundImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, Location_X, Location_Y, 0.0, transparent_at_space);
		list = thelist;
		temp = new CharLabel(frapsBuffer, (short)0, scrResolution, true);
		camp = players_camp;
	}
	
	@Override
	public void printNew()
	{
		if(visible)
		{
			super.printNew();
			int line = 0;
			
			temp.setText("Name");
			temp.setLocation((short)(location[0] + 3 - center_X), (short)(location[1] + 2 + line - center_Y));
			temp.printNew();
			temp.setText("Camp");
			temp.setLocation((short)(location[0] - 3), (short)(location[1] + 2 + line - center_Y));
			temp.printNew();
			temp.setText("Killed");
			temp.setLocation((short)(location[0] + 8), (short)(location[1] + 2 + line - center_Y));
			temp.printNew();
			temp.setText("Dead");
			temp.setLocation((short)(location[0] + 16), (short)(location[1] + 2 + line - center_Y));
			temp.printNew();
			
			for(Aircraft a : list)
			{
				temp.setText(a.ID);
				temp.setLocation((short)(location[0] + 3 - center_X), (short)(location[1] + 4 + line - center_Y));
				temp.printNew();
				
				temp.setText(a.camp + ":" + (a.camp == camp? "Friend" : "Enemy"));
				temp.setLocation((short)(location[0] - 3), (short)(location[1] + 4 + line - center_Y));
				temp.printNew();
				
				temp.setText(Integer.toString(a.killed));
				temp.setLocation((short)(location[0] + 8), (short)(location[1] + 4 + line - center_Y));
				temp.printNew();
				
				temp.setText(Integer.toString(a.dead));
				temp.setLocation((short)(location[0] + 16), (short)(location[1] + 4 + line - center_Y));
				temp.printNew();
				
				++line;
			}
		}
		
	}
}
