package graphic_Z.Objects;

public class CharMessObject extends CharObject
{
	protected float mess;
	protected float velocity[];
	public float velocity_roll[];
	/*
	public CharMessObject(CharMessObject another)
	{
		super(another);

		mess = another.mess;
		
		velocity = new float[3];
		velocity_roll =  new float[3];
		
		velocity[0] = another.velocity[0];
		velocity[1] = another.velocity[1];
		velocity[2] = another.velocity[2];
		
		velocity_roll[0] = another.velocity_roll[0];
		velocity_roll[1] = another.velocity_roll[1];
		velocity_roll[2] = another.velocity_roll[2];
	}
	*/
	public CharMessObject(String ModelFile, float Mess, boolean lineConstruct)
	{
		super(ModelFile, lineConstruct);

		mess = Mess;
		
		velocity = new float[3];
		velocity_roll =  new float[3];
		
		velocity[0] = 0.0F;
		velocity[1] = 0.0F;
		velocity[2] = 0.0F;
		
		velocity_roll[0] = 0.0F;
		velocity_roll[1] = 0.0F;
		velocity_roll[2] = 0.0F;
	}
	
	public CharMessObject(String ModelFile, float Mess) {
		this(ModelFile, Mess, false);
	}

	@Override
	public void go()
	{
		location[0] += velocity[0];
		location[1] += velocity[1];
		location[2] += velocity[2];
		
		roll_angle[0]+= velocity_roll[0];
		roll_angle[1]+= velocity_roll[1];
		roll_angle[2]+= velocity_roll[2];
	}
	
	@Override
	public void run()
	{
		go();
	}
}
