package graphic_Z.Worlds;

public class CharTimeSpace extends CharWorld
{
	public static double  g;			//重力加速度
	//public static double valRate;	//帧速率
	
	public CharTimeSpace(int resolution_X, int resolution_Y)
	{
		super(resolution_X, resolution_Y);
	}
	
	public CharTimeSpace(int resolution_X, int resolution_Y, int refresh_rate, double g)
	{
		super(resolution_X, resolution_Y, refresh_rate);
		CharTimeSpace.g = g;
		//CharTimeSpace.valRate = frapsValRate / refresh_rate;
	}
	/*
	public CharTimeSpace(short resolution_X, short resolution_Y, int refresh_rate, double g)
	{
		this(resolution_X, resolution_Y, refresh_rate, g, 36);
	}
	*/
	public CharTimeSpace(int resolution_X, int resolution_Y, int refresh_rate)
	{
		this(resolution_X, resolution_Y, refresh_rate, 9.8);
	}
	
	public void buffStatic() {
		visualManager.buff();
	}
	
	@Override
	public void printNew()
	{
		//visualManager.printNew();
		//Thread visualPrintNew = new Thread(visualManager);
		visualManager.printNew();
		//visualManager.printNew();
		/*
		try
		{
			Thread.sleep(refreshDelay);
		} catch (InterruptedException e)
		{ e.printStackTrace();}
		*/
		//Thread objectsPrintNew = new Thread(objectsManager);
		//objectsPrintNew.start();
		objectsManager.printNew();
		/*
		try
		{
			visualPrintNew.join();
			objectsPrintNew.join();
		} catch (InterruptedException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}*/
	}
	
}
