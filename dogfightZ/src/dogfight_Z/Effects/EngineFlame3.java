package dogfight_Z.Effects;

import java.util.ArrayList;

public class EngineFlame3 extends EngineFlame
{
	public EngineFlame3(float Location1[], float Location0[], long lifeTime) {
		super(Location1, lifeTime, DrawingMethod.drawLine);
		points = null;
		points_abs = new ArrayList<float[]>();
		points_abs.add(new float [] {Location1[0], Location1[1], Location1[2], Location0[0], Location0[1], Location0[2]});
		points_count = 1;
	}
	
	public EngineFlame3(float Location1[], float Location0[], long lifeTime, char specialDisplayChar) {
		this(Location1, Location0, lifeTime);
		specialDisplay = specialDisplayChar;
	}

    @Override
    public final PointType getPointType() {
        return PointType.abs;
    }
}
