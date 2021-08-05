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
	public static void main(String args[]) {
		
		double angle = 1.23;
		System.out.println("sin: " + Math.sin(angle) + ", " + sin(angle));
		System.out.println("cos: " + Math.cos(angle) + ", " + cos(angle));
		System.out.println("tan: " + Math.tan(angle) + ", " + tan(angle));
	}
}
