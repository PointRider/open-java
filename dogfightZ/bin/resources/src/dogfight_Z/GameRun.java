package dogfight_Z;

import java.awt.Font;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
//import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class GameRun
{

	public static void main(String[] args)
	{
		// TODO 设定各类游戏参数
		
		JFrame formMenu = new JFrame("Dog Fight Z -- Setting Menu");
		formMenu.setSize(400, 300);
		formMenu.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		JButton btnStart = new JButton("Start the Game !");
		btnStart.setActionCommand("Act_gameStart");
		
		btnStart.setFont(new Font("Times New Roman", 3, 32));
		
		formMenu.add(btnStart);
		
		
		/*Thread game = new Thread(new Game
				(
					args[0] ,args[1] , args[2], args[3], args[4], 
					args[5] ,args[6] , args[7], args[8], args[9], 
					args[10], args[11], 
					Short.parseShort(args[12]), 
					Short.parseShort(args[13]), 
					Short.parseShort(args[14])
				));*/
		Game game = new Game
		(
			args[0] ,args[1] , args[2], args[3], args[4], args[5] ,
			args[6] , args[7], args[8], args[9], args[10], args[11], 
			args[12], args[13], args[14], args[15], args[16], 
			Short.parseShort(args[17]), 
			Short.parseShort(args[18]), 
			Short.parseShort(args[19])
		);
		//game.start();
		game.run();	
		//formMenu.dispose();
		/*
		btnStart.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(e.getActionCommand() == "Act_gameStart")
					{
						JOptionPane.showMessageDialog(null, "Act_gameStart !");
						Thread game = new Thread(new Game
								(
									args[0] ,args[1] , args[2], args[3], args[4], 
									args[5] ,args[6] , args[7], args[8], args[9], 
									args[10], args[11], 
									Short.parseShort(args[12]), 
									Short.parseShort(args[13]), 
									Short.parseShort(args[14])
								));
						game.start();
								//game.run();	
						formMenu.dispose();
										
					}
				}
			}
		);
		*/
		//`formMenu.setVisible(true);
		
		//--------------------------------------
		
	}

}
