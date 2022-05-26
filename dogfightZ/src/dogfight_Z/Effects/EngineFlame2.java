package dogfight_Z.Effects;

import java.util.ArrayList;

public class EngineFlame2 extends EngineFlame
{
	private static ArrayList<float[]> missileModelData;
	static {
		missileModelData = new ArrayList<float[]>();
		
		float newPonit[];
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = 20;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = -20;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 20;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = -20;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 20;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = -20;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
	}
	
	public EngineFlame2(float Location[], long lifeTime) {
		super(Location, lifeTime);
		points = missileModelData;
		points_count = missileModelData.size();
	}
	
	public EngineFlame2(float Location[], long lifeTime, char specialDisplayChar) {
		this(Location, lifeTime);
		specialDisplay = specialDisplayChar;
	}
}
