package startTheWorld;

public class NPCInformation implements Comparable<NPCInformation>
{
	String ID;
	double difficulty;
	short camp;
	
	public NPCInformation
	(
		String id,
		double difficultyRate,
		short campTo
	)
	{
		ID			= id;
		difficulty	= difficultyRate;
		camp		= campTo;
	}
	
	public NPCInformation
	(
		String id
	)
	{
		this(id, 0.0, (short)0);
	}

	@Override
	public int compareTo(NPCInformation o)
	{
		return ID.compareTo(o.ID);
	}
	
	@Override
	public String toString()
	{
		return ID;
	}
}
