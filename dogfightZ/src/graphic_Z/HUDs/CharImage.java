package graphic_Z.HUDs;

import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;

public class CharImage extends CharHUD
{
	public          int location[];
	protected       int size[];
	public    final int centerX;
	public    final int centerY;
	
	public CharImage(
		String HUDImgFile, 
		char[][] frapsBuffer,
		int sizeX,
		int sizeY,
		int locationX,
		int locationY, 
		int HUDLayer, 
		int[] scrResolution,
		boolean transparent_at_space, 
		boolean needImgBuffer
	) {
		super(null, frapsBuffer, HUDLayer, scrResolution, transparent_at_space);

		location = new int[2];
		location[0] = locationX;
		location[1] = locationY;

		size = new int[2];
		size[0] = sizeX;
		size[1] = sizeY;
		
		centerX = size[0] >> 1;
		centerY = size[1] >> 1;

		if(needImgBuffer) HUDImg = new char[sizeY][sizeX];
		
		if(HUDImgFile != null) try(FileReader data = new FileReader(HUDImgFile)) {
			for(int i=0 ; i<sizeY ; ++i) {
				for(int j=0 ; j<sizeX ; ++j) {
					if(j != 0) data.read();
					HUDImg[i][j] = (char) data.read();
				}
				//\r\n
				data.read();data.read();
			}
		} catch(EOFException exc) {} catch(IOException exc) {
			System.out.println("HUD load fault.");
		} else for(int i=0 ; i<sizeY ; ++i) {
			for(int j=0 ; j<sizeX ; ++j) HUDImg[i][j] = ' ';
		}
	}
	
	public CharImage(
        String HUDImgFile, 
        char[][] frapsBuffer,
        int sizeX,
        int sizeY,
        int locationX,
        int locationY, 
        int HUDLayer, 
        int[] scrResolution,
        boolean transparent_at_space
    ) {
        this(HUDImgFile, frapsBuffer, sizeX, sizeY, locationX, locationY, HUDLayer, scrResolution, transparent_at_space, true);
    }
	
	private int x, y;
	public void printNew() {
		if(visible) for(int i=0 ; i<size[1] ; ++i) for(int j=0 ; j<size[0] ; ++j) {
			if(HUDImg[i][j] != ' ' || !transparentAtSpace) {
				y = i + location[1];
				x = j + location[0];
				if(x >= 0 && y >= 0 && x < resolution[0] && y < resolution[1]) 
					fraps_buffer[y][x] = HUDImg[i][j];
			}
		}
	}
	
    public void setLocation(int X, int Y)
    {
        location[0] = X;
        location[1] = Y;
    }

    public final int[] getSize() {
        return size;
    }
    
}
