package graphic_Z.Worlds;

import graphic_Z.Managers.EventManager;
import graphic_Z.Managers.TDObjectsManager;
import graphic_Z.Managers.VisualManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class TDWorld<WorldType, ObjectType, HUDType> {
    
	public TDObjectsManager         objectsManager;
	public VisualManager<WorldType> visualManager;
	public EventManager             eventManager;

	public int refreshHz;
    private ExecutorService epool;
    
	public TDWorld(int refresh_rate) {
		refreshHz = refresh_rate;
        epool     = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	public void getIntoGameWorld() {
		eventManager.setVisible(true);
	}

	public void exitTheWorld() {
		eventManager.setVisible(false);
	}

    public void execute(Runnable r) {
        epool.execute(r);
    }
    
    public Future<?> submit(Runnable r) {
        return epool.submit(r);
    }

    @Override
    protected void finalize() {
        shutdown();
    }
    
    protected void shutdown() {
        epool.shutdownNow();
    }
    
	public abstract void setRefreshRate(int refresh_rate);
	public abstract void printNew();
}
