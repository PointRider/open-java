package graphic_Z.utils;

public class GraphicUtils
{
	private static final  int boot = 65536;
	private static double bootTmp;
	private static double sint[];
	private static double cost[];
	private static double tant[];
	
	static {
		
		sint = new double[boot];
		cost = new double[boot];
		tant = new double[boot];
		
		bootTmp = (double)boot / (2.0 * Math.PI);
		
		double tmp = 0.0, each = 2 * Math.PI / boot;
		for(int i = 0; i < boot; ++i) {
			tmp = each * i;
			sint[i] = Math.sin(tmp);
			cost[i] = Math.cos(tmp);
			tant[i] = Math.tan(tmp);
		}
	}
	
	public static double sin(double i) {
		i %= 2.0 * Math.PI;
		if(i < 0) return - sint[(int)(bootTmp * -i)];
		return sint[(int)(bootTmp * i)];
	}
	
	public static double cos(double i) {
		i %= 2.0 * Math.PI;
		return cost[(int)(bootTmp * Math.abs(i))];
	}
	
	public static double tan(double i) {
		i %= Math.PI;
		if(i < 0) return - tant[(int)(bootTmp * -i)];
		return tant[(int)(bootTmp * i)];
	}
	
	public static int absI(int x) {
		return x < 0? -x: x;
	}
	
	public static void drawLine(char fraps_buffer[][], int x1, int y1, int x2, int y2, char pixel) {
		//DDA
		if(fraps_buffer == null) return;
		
		int maxX = fraps_buffer[0].length;
		int maxY = fraps_buffer.length;
		
		int deltaX = x2 - x1;
		int deltaY = y2 - y1;
		
		int drX, drY;
		
		if(absI(deltaX) > absI(deltaY)) {
			
			float k = (float)deltaY / (float)deltaX;
			float y = y1;
			int   x = x1;
			
			if(x < x2) for(; x <= x2; y += k) {
				drX = x++;
				drY = (int)(y + 0.5);
				if(drY >= 0 && drY < maxY && drX >=0 && drX <= maxX)
				fraps_buffer[drY][drX] = pixel;
			} else for(; x >= x2; y-=k) {
				drX = x--;
				drY = (int)(y + 0.5);
				if(drY >= 0 && drY < maxY && drX >=0 && drX <= maxX)
				fraps_buffer[drY][drX] = pixel;
			}
			
		} else {
			
			float k = (float)deltaX / (float)deltaY;
			float x = x1;
			int   y = y1;
			
			if(y < y2) for(; y <= y2; x+=k) {
				drX = (int)(x + 0.5);
				drY = y++;
				if(drY >= 0 && drY < maxY && drX >=0 && drX <= maxX)
				fraps_buffer[drY][drX] = pixel;
			} else for(; y >= y2; x-=k) {
				drX = (int)(x + 0.5);
				drY = y--;
				if(drY >= 0 && drY < maxY && drX >=0 && drX <= maxX)
				fraps_buffer[drY][drX] = pixel;
			}
			
		}
	}
	/*
	public static double sin(double i) {
		return Math.sin(i);
	}
	
	public static double cos(double i) {
		return Math.cos(i);
	}
	
	public static double tan(double i) {
		return Math.tan(i);
	}
	*/
	
	public static void drawCircle(char fraps_buffer[][], int x0, int y0, int r, char pixel) {
		
		int   x = 0, y = r;
		float d = 1.25f - r;
		
		circlePoints(fraps_buffer, x0, y0, x, y, pixel);
		
		while(x <= y) {
			if(d < 0) d += (x<<1) + 3;
			else {
				d += ((x-y)<<1) + 5; --y;
			}
			++x;
			circlePoints(fraps_buffer, x0, y0, x, y, pixel);
		}
	}
	
	//8对称标点
	private static void circlePoints(char fraps_buffer[][], int x0, int y0, int x, int y, char pc) {

		int maxX = fraps_buffer[0].length;
		int maxY = fraps_buffer.length;
		int ty, tx;
		
		ty = y + y0;
		tx = x + x0;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
		ty = x + y0;
		tx = y + x0;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
		ty = y + y0;
		tx = x0 - x;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - x;
		tx = y + x0;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - y;
		tx = x + x0;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
		ty = x + y0;
		tx = x0 - y;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - y;
		tx = x0 - x;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - x;
		tx = x0 - y;
		if(tx >= 0  &&  tx <= maxX  &&  ty >= 0  &&  ty <= maxY) fraps_buffer[ty][tx] = pc;
	}
	
	public static void main(String args[]) {
		
		double angle = 1.23;
		System.out.println("sin: " + Math.sin(angle) + ", " + sin(angle));
		System.out.println("cos: " + Math.cos(angle) + ", " + cos(angle));
		System.out.println("tan: " + Math.tan(angle) + ", " + tan(angle));
	}
}
