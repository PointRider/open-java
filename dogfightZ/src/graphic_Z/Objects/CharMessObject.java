package graphic_Z.Objects;

import graphic_Z.Worlds.CharTimeSpace;

public class CharMessObject extends CharObject
{
	protected double mess;
	protected double velocity[];
	public double velocity_roll[];
	/*
	public CharMessObject(CharMessObject another)
	{
		super(another);

		mess = another.mess;
		
		velocity = new double[3];
		velocity_roll =  new double[3];
		
		velocity[0] = another.velocity[0];
		velocity[1] = another.velocity[1];
		velocity[2] = another.velocity[2];
		
		velocity_roll[0] = another.velocity_roll[0];
		velocity_roll[1] = another.velocity_roll[1];
		velocity_roll[2] = another.velocity_roll[2];
	}
	*/
	public CharMessObject(String ModelFile, double Mess, boolean lineConstruct)
	{
		super(ModelFile, lineConstruct);

		mess = Mess;
		
		velocity = new double[3];
		velocity_roll =  new double[3];
		
		velocity[0] = 0.0;
		velocity[1] = 0.0;
		velocity[2] = 0.0;
		
		velocity_roll[0] = 0.0;
		velocity_roll[1] = 0.0;
		velocity_roll[2] = 0.0;
	}
	
	public CharMessObject(String ModelFile, double Mess) {
		this(ModelFile, Mess, false);
	}

	@Override
	public void go()
	{
		location[0] += velocity[0];
		location[1] += velocity[1];
		location[2] += velocity[2];
		
		roll_angle[0]+= velocity_roll[0] + CharTimeSpace.g;
		roll_angle[1]+= velocity_roll[1];
		roll_angle[2]+= velocity_roll[2];
	}
	
	@Override
	public void run()
	{
		go();
	}
}
