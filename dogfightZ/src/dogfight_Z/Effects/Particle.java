package dogfight_Z.Effects;

import dogfight_Z.GameManagement;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;

public class Particle extends EngineFlame
{
	public float  velocity;
	public float  resistanceRate;
	
	public Particle(float[] Location, float [] roll_Angle, long lifeTime, float Velocity, float resistance_rate) {
		super(Location, lifeTime);

		roll_angle[0] = roll_Angle[0];
		roll_angle[1] = roll_Angle[1];
		roll_angle[2] = roll_Angle[2];
		
		velocity = Velocity * GraphicUtils.random() * 2;
		resistanceRate = resistance_rate;
		resistanceRate *= GraphicUtils.random();
	}
	
	@Override
	public void go() {
		if(lifeLeft <= 0) {
			visible = false;
			end = true;
		} else {
			float x, y, z, t = GraphicUtils.cos(roll_angle[1]) * velocity;
			
			x = GraphicUtils.tan(roll_angle[1]) * t;
			y = GraphicUtils.sin(roll_angle[0]) * t;
			z = GraphicUtils.cos(roll_angle[0]) * t;
			
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;
			
			velocity -= velocity * resistanceRate * 1.5;
			location[0] += CharTimeSpace.g * (life - lifeLeft) * 0.0275 - (life-lifeLeft) * resistanceRate * 2.4;
			
			--lifeLeft;
		}
	}
	
	public static void makeExplosion(GameManagement gameManager, float location[], float velocity, long lifeTime, float density, float resistanceRate) {
        float roll[] = new float[3];
        roll[2] = 0;
        
        for(float x = 0, dx = GraphicUtils.RAD1/density ; x<GraphicUtils.RAD360 ; x += dx) {
            for(float y=-0.5235987755982989F, dy = GraphicUtils.RAD1/density/2/*-30 degrees*/ ; y<GraphicUtils.RAD90 ; y += dy) {
                roll[0] = x;
                roll[1] = y*2;
                gameManager.newEffect(new Particle(location, roll, lifeTime + (int)(y * GraphicUtils.random()), velocity, resistanceRate));
            }
        }
    }
}
