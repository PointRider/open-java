package graphic_Z.utils;

public class HzController implements Runnable
{
	int refresh_delay;
	private HzController(int Hz) {
		this.refresh_delay = msOfHz(Hz);
	}

	@Override
	public void run()
	{
		try {
			Thread.sleep(refresh_delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static int msOfHz(int Hz) {
	    return 1000 / Hz;
	}
	
    public static int nanoOfHz(int Hz) {
        return 1000000000 / Hz;
    }
}
