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
        refreshWaitNanoTime = HzController.nanoOfHz(refreshRate);
        //this.rateController = new HzController(refreshRate);
        this.visibility     = range;
        this.playerCameraLocation = gameManager.getPlayerCameraLocation();
        
        float randomHight;
		for(currentCloudsCount=0 ; currentCloudsCount < maxCloudsCount ; ++currentCloudsCount)
		{
			/*random1 = GraphicUtils.random();
			if((GraphicUtils.fastRanodmInt() & 1) == 0)
				random1 = -random1;
			random2 = GraphicUtils.random();
			if((GraphicUtils.fastRanodmInt() & 1) == 0)
				random2 = -random2;
			random3 = GraphicUtils.random();
			if((GraphicUtils.fastRanodmInt() & 1) == 0)
				random3 = -random3;
			*/
		    randomHight = GraphicUtils.fastRanodmInt() & 2047;
			clouds.add (
				new RandomCloud
				(playerCameraLocation, visibility, -4096 - randomHight, 0.0125F)
			);
		}
	}

	@Override
	public void run()
	{
		try {
			while(gameManager.isRunning()) {
		        nextRefreshTime = System.nanoTime() + refreshWaitNanoTime;
				
				for(int i=0 ; i<currentCloudsCount ; ++i) {
					aCloud = (RandomCloud) clouds.get(i);
					
					if(GraphicUtils.range_YZ(aCloud.location, playerCameraLocation) > visibility * 1.10F)
						gameManager.execute(aCloud);
				}
				
				long now = nextRefreshTime - System.nanoTime();
		        if(now > 0) {
		            synchronized(this) {wait(now / 1000000, (int) (now % 1000000));}
		        }
			}
		} catch(InterruptedException e) {e.printStackTrace();}
	}

}
