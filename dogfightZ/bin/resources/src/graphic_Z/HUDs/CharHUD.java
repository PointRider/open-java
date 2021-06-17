package graphic_Z.HUDs;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;

public class CharHUD extends HUD
{
	public		char HUDImg[][];
	protected	char fraps_buffer[][];	//帧缓冲区(引用，实体在CharVisualManager中)
	protected	boolean transparentAtSpace;
	public CharHUD(String HUDImgFile, char frapsBuffer[][], short HUDLayer, short scrResolution[],boolean transparent_at_space)
	{
		visible = true;
		resolution = scrResolution;
		transparentAtSpace = transparent_at_space;
		
		if(HUDImgFile != null)
		{
			HUDImg = new char[resolution[1]][resolution[0]];
			
			try(FileReader data = new FileReader(HUDImgFile))
			{
				for(int i=0 ; i<resolution[1] ; ++i)
				{
					for(int j=0 ; j<resolution[0] ; ++j)
					{
						if(j != 0)
							data.read();
						HUDImg[i][j] = (char) data.read();
					}
					data.read();data.read();
				}
			}
			catch(EOFException exc)
			{
			}
			catch(IOException exc)
			{
				System.out.println("HUD load fault.");
			}
		}
		
		layer		 = HUDLayer;
		fraps_buffer = frapsBuffer;
	}
	
	public void printNew()
	{
		if(visible) for(int i=0 ; i<resolution[1] ; ++i) for(int j=0 ; j<resolution[0] ; ++j)
		{
			if(HUDImg[i][j] != ' ' || !transparentAtSpace)
				fraps_buffer[i][j] = HUDImg[i][j];
		}
	}
	
	public void printNew(short reslution_[], char fraps[][])
	{
		if(visible) for(int i=0 ; i<reslution_[1] ; ++i) for(int j=0 ; j<reslution_[0] ; ++j)
		{
			if(HUDImg[i][j] != ' ' || !transparentAtSpace)
				fraps[i][j] = HUDImg[i][j];
		}
	}
}
