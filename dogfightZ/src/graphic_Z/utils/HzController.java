package graphic_Z.utils;

public class HzController implements Runnable
{
	int refresh_delay;
	public HzController(int refresh_rate) {
		this.refresh_delay = 1000 / refresh_rate;
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

}
