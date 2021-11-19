package dogfight_Z;

public class GameRun
{

	public static void main(String[] args)
	{
		
		Game game = new Game
		(
			args[0] ,args[1] , args[2], args[3], args[4], args[5] ,
			args[6] , args[7], args[8], args[9], args[10], args[11], 
			args[12], args[13], args[14], args[15], 
			Integer.parseInt(args[16]), 
			Integer.parseInt(args[17]), 
			Integer.parseInt(args[18]),
			Integer.parseInt(args[19])
		);
		
		game.getIntoGameWorld();
		game.run();	
		
	}

}
