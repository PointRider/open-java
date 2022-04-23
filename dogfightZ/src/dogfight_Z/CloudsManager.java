package dogfight_Z;

import java.util.ArrayList;

import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.utils.GraphicUtils;
import graphic_Z.utils.HzController;
//import graphic_Z.utils.HzController;

public class CloudsManager implements Runnable
{
	private int maxCloudsCount = 320;
	private int	currentCloudsCount;
	private RandomCloud aCloud;
	private ArrayList<ThreeDs> clouds;
	private float visibility;
	private float playerCameraLocation[];
	//private HzController rateController;
	//private Thread rateSynThread;
	private GameManagement gameManager;
    private long refreshWaitNanoTime;
    private long nextRefreshTime;
	
	public CloudsManager(GameManagement gameManager, int refreshRate, float range) {

        this.gameManager = gameManager;
        
        this.clouds         = gameManager.getClouds();
        refreshWaitNanoTime = HzController.msOfHz(refreshRate);
        //this.rateController = new HzController(refreshRate);
        this.visibility     = range;
        this.playerCameraLocation = gameManager.getPlayerCameraLocation();
        
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
			
			clouds.add (
				new RandomCloud
				(playerCameraLocation, visibility, -2250, 0.1F)
			);
		}
	}

	@Override
	public void run()
	{
		try {
			while(gameManager.isRunning()) {
		        nextRefreshTime = System.nanoTime() + refreshWaitNanoTime;
				/*rateSynThread = new Thread(rateController);
				rateSynThread.setPriority(Thread.MAX_PRIORITY);
				rateSynThread.start();*/
				for(int i=0 ; i<currentCloudsCount ; ++i) {
					aCloud = (RandomCloud) clouds.get(i);
					
					if(GraphicUtils.range_YZ(aCloud.location, playerCameraLocation) > visibility * 1.10F)
						gameManager.execute(aCloud);
				}
				//rateSynThread.join();
				long now = nextRefreshTime - System.nanoTime();
		        
		        if(now > 0) {
		            synchronized(this) {wait(now / 1000000, (int) (now % 1000000));}
		        }
			}
		} catch(InterruptedException e) {e.printStackTrace();}
	}

}
