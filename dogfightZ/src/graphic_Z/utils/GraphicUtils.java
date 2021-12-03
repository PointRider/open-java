package graphic_Z.utils;

import java.util.Random;

public class GraphicUtils
{
    public  static final float PI = 3.141592653589793F;
    public  static final float halfAPI = PI / 2;
    public  static final float PIMUL2 = PI * 2;
    public  static final float negativeHalfAPI = -halfAPI;
	private static final int boot = 65536;
    private static final int boot_1 = boot - 1;
    private static final int halfABoot = boot >> 1;
    private static final int bootDiv256 = boot >> 8;
	private static float bootTmp;
	private static float sint[];
	private static float cost[];
	private static float tant[];
    private static float asint[];
    private static float acost[];
    private static float atant[];
    private static float rantF[]; 
    private static int   rantI[]; 
    private static char  curRandomIdx; /*char is unsigned between 0 ~ 65535*/
	
	
	public static final Random randomMaker;
	
	static {
	    randomMaker = new Random();
		sint  = new float[boot];
		cost  = new float[boot];
		asint = new float[boot];
		acost = new float[boot];
        atant = new float[boot];
        tant  = new float[boot];
        rantF = new float[boot];
        rantI = new int[boot];
		
		bootTmp = (float)boot / (2.0F * (float)PI);
		
		float tmp = 0.0F, each = 2 * (float)PI / boot;
		for(int i = 0; i < boot; ++i) {
			tmp = each * i;
			sint[i]  = (float) Math.sin(tmp);
			cost[i]  = (float) Math.cos(tmp);
			tant[i]  = (float) Math.tan(tmp);
			rantF[i] = (float) Math.random();
			rantI[i] = randomMaker.nextInt();
		}
		
		each = 2.0F / boot;
		for(int i = 0; i < boot; ++i) {
		    tmp = each * i;
            asint[i] = (float) Math.asin(tmp - 1.0);
            acost[i] = (float) Math.acos(tmp - 1.0);
		}
		
		each = 256.0F / boot;
        for(int i = 0; i < boot; ++i) {
            tmp = each * i;
            atant[i] = (float) Math.atan(tmp);
        }
        
        curRandomIdx = 0;
	}
	
	public static float sin(float i) {
		i %= PIMUL2;
		if(i < 0) return - sint[(int)(bootTmp * -i)];
		return sint[(int)(bootTmp * i)];
	}
	
	public static float cos(float i) {
		i %= PIMUL2;
		return cost[(int)(bootTmp * Math.abs(i))];
	}
	
	public static float tan(float i) {
		i %= PI;
		if(i < 0) return - tant[(int)(bootTmp * -i)];
		return tant[(int)(bootTmp * i)];
	}
	
    public static float acos(float theta) {
        int idx = (int)((theta + 1.0F) * halfABoot);
        if(idx >= boot) return Float.NaN;
        return acost[idx];
    }
    
    public static float asin(float theta) {
        int idx = (int)((theta + 1.0F) * halfABoot);
        if(idx >= boot) return Float.NaN;
        return asint[idx];
    }
    
    public static float atan(float theta) {
        int idx = (int)(theta * bootDiv256);
        if(idx < 0){
            int negative = -idx;
            if(negative < boot) return -atant[negative];
            else return negativeHalfAPI;
        } else {
            if(idx < boot) return atant[idx];
            else return halfAPI;
        }
    }
    
    public static float atan2(float y, float x) {
        if(x > 0) return atan(y/x);
        else if(x < 0) {
             if(y >= 0) return atan(y/x) + PI;
                   else return atan(y/x) - PI;
        } else {
            if(y > 0)      return  halfAPI;
            else if(y < 0) return -halfAPI;
            else return Float.NaN;
        }
    }
	
	public static int absI(int x) {
		return x < 0? -x: x;
	}

    public static float toDegrees(float rad) {
        return rad * 57.29577951308233F;
    }

    public static float toRadians(float deg) {
        return deg * 0.0174532925199433F;
    }
    
    public static float random() {
        ++curRandomIdx;
        return rantF[curRandomIdx &= boot_1];
    }
    
	public static void drawLine(char fraps_buffer[][], int x1, int y1, int x2, int y2, char pixel, boolean noRewrite) {
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
				if(drY >= 0 && drY < maxY && drX >=0 && drX < maxX && (!noRewrite  ||  fraps_buffer[drY][drX] == ' '))
					fraps_buffer[drY][drX] = pixel;
			} else for(; x >= x2; y-=k) {
				drX = x--;
				drY = (int)(y + 0.5);
				if(drY >= 0 && drY < maxY && drX >=0 && drX < maxX && (!noRewrite  ||  fraps_buffer[drY][drX] == ' '))
				fraps_buffer[drY][drX] = pixel;
			}
			
		} else {
			
			float k = (float)deltaX / (float)deltaY;
			float x = x1;
			int   y = y1;
			
			if(y < y2) for(; y <= y2; x+=k) {
				drX = (int)(x + 0.5);
				drY = y++;
				if(drY >= 0 && drY < maxY && drX >=0 && drX < maxX && (!noRewrite  ||  fraps_buffer[drY][drX] == ' '))
					fraps_buffer[drY][drX] = pixel;
			} else for(; y >= y2; x-=k) {
				drX = (int)(x + 0.5);
				drY = y--;
				if(drY >= 0 && drY < maxY && drX >=0 && drX < maxX && (!noRewrite  ||  fraps_buffer[drY][drX] == ' '))
					fraps_buffer[drY][drX] = pixel;
			}
			
		}
	}
	
	public static void drawLine(char fraps_buffer[][], int x1, int y1, int x2, int y2, char pixel) {
		drawLine(fraps_buffer, x1, y1, x2, y2, pixel, false);
	}

    public static float vectorLength(float xyz[]) {
        float x2 = xyz[0] * xyz[0];
        float y2 = xyz[1] * xyz[1];
        float z2 = xyz[2] * xyz[2];
        
        return GraphicUtils.sqrt(x2 + y2 + z2);
    }
    
    public static float[] getDirectionVector(float speedVector[]) {
        float x = speedVector[0], y = speedVector[1], z = speedVector[2];
        float length = vectorLength(speedVector);
        
        return new float[] {x / length, y / length, z / length};
    }
    
	
	public static void drawCircle(char fraps_buffer[][], int x0, int y0, int r, char pixel) {
		
		int   x = 0, y = r;
		float d = 1.25F - r;
		
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
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
		ty = x + y0;
		tx = y + x0;
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
		ty = y + y0;
		tx = x0 - x;
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - x;
		tx = y + x0;
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - y;
		tx = x + x0;
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
		ty = x + y0;
		tx = x0 - y;
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - y;
		tx = x0 - x;
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
		ty = y0 - x;
		tx = x0 - y;
		if(tx >= 0  &&  tx < maxX  &&  ty >= 0  &&  ty < maxY) fraps_buffer[ty][tx] = pc;
	}
	
	public static void main(String args[]) {
	    //float angle = -999.23F;
		//System.out.println("asin: " + Math.asin(angle) + ", " + asin(angle));
	    //System.out.println("acos: " + Math.acos(angle) + ", " + acos(angle));
		//System.out.println("atan: " + Math.atan(angle) + ", " + atan(angle));
	    //float x = -123, y = -4.56F;
	    //float d = (float) Math.atan2(y, x);
	    //System.out.println("atan2: " + d + ", " + atan2(y, x));
	}
    
    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }
    
    public static float max(float a, int b) {
        return a > b? a: b;
    }
    
    public static float max(float a, float b) {
        return a > b? a: b;
    }

    public static float min(float a, float b) {
        return a < b? a: b;
    }

    public static int min(int a, int b) {
        return a < b? a: b;
    }
    
    public static float abs(float f) {
        return Math.abs(f);
    }
    
    public static float log(float x) {
        return (float) Math.log(x);
    }
    
    public static float pow(float x, float y) {
        return (float) Math.pow(x, y);
    }
    
    public static int fastRanodmInt() {
        ++curRandomIdx;
        return rantI[curRandomIdx &= boot_1];
    }
    
    public static int randomInt(int min, int max) {
        return randomMaker.nextInt(max-min) + min;
    }
    
    public static int randomInt(int max) {
        return randomMaker.nextInt(max);
    }
}
