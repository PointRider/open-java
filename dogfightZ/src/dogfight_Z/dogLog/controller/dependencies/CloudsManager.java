package dogfight_Z.dogLog.controller.dependencies;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dogfight_Z.RandomCloud;
import graphic_Z.Cameras.TDCamera;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.HzController;

public class CloudsManager implements Runnable
{
	private int maxCloudsCount = 320;
	private int	currentCloudsCount;
	private RandomCloud aCloud;
	private ArrayList<ThreeDs> clouds;
	private float visibility;
	private TDCamera<CharWorld> playerCamera;
	private HzController rateController;
	private Thread rateSynThread;
	private ExecutorService epool;
	
	public CloudsManager(ArrayList<ThreeDs> clouds, HzController rateController, TDCamera<CharWorld> playerCamera, float visibility) {
	    epool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        float random1, random2, random3;
		for(currentCloudsCount=0 ; currentCloudsCount < maxCloudsCount ; ++currentCloudsCount)
		{
			random1 = GraphicUtils.random();
			if((GraphicUtils.fastRanodmInt() & 1) == 0)
				random1 = -random1;
			random2 = GraphicUtils.random();
			if((GraphicUtils.fastRanodmInt() & 1) == 0)
				random2 = -random2;
			random3 = GraphicUtils.random();
			if((GraphicUtils.fastRanodmInt() & 1) == 0)
				random3 = -random3;
			
			clouds.add
			(
				new RandomCloud
				(playerCamera.location, visibility, -2250, 0.1F)
			);
		}
		this.clouds         = clouds;
		this.rateController = rateController;
		this.playerCamera   = playerCamera;
		this.visibility     = visibility;
	}
	
	

	@Override
	public void run()
	{
		try {
			while(true) {
				rateSynThread = new Thread(rateController);
				rateSynThread.setPriority(Thread.MAX_PRIORITY);
				rateSynThread.start();
				for(int i=0 ; i<currentCloudsCount ; ++i) {
					aCloud = (RandomCloud) clouds.get(i);
					
					if(range_YZ(aCloud.location, playerCamera.location) > visibility * 1.10F)
					    epool.execute(aCloud);
				}
				rateSynThread.join();
			}
		} catch(InterruptedException e) {e.printStackTrace();}
	}

	private static float range_YZ (float p1[], float p2[]) {
		float d1 = p2[1]-p1[1];
		float d2 = p2[2]-p1[2];
		
		return GraphicUtils.sqrt(d1*d1 + d2*d2);
	}
}
