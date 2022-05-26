package startTheWorld;

import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.TreeSet;

public class GameStartGUI
{
	
	private class MyIDChangedListener implements FocusListener
	{
		private GameStartGUI gui;
		private String currentName;
		
		public MyIDChangedListener(GameStartGUI Gui)
		{
			gui = Gui;
		}
		
		@Override
		public void focusGained(FocusEvent e)
		{
			currentName = gui.textMyID.getText();
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			String newName = gui.textMyID.getText();
			if(!newName.equals(""))
			{
				if(!newName.equals(currentName))
				{
					NPCInformation newMe = new NPCInformation
					(
						newName, 0.8, (short) 0
					);
					
					if(!gui.setIDs.contains(newMe))
					{
						gui.setIDs.remove(new NPCInformation(currentName));
						gui.setIDs.add(newMe);
						
						gui.listPrintNew();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "The player named \"" + newName + "\" has been existed.");
						gui.textMyID.setText(currentName);
					}
				}
			}
			else 
			{
				JOptionPane.showMessageDialog(null, "The name is not allowed to set as empty.");
				gui.textMyID.setText(currentName);
			}
			gui.listCamp.setSelectedIndex(0);
			gui.listPlayer.setSelectedIndex(0);
		}
	};
	
	private class TimeChangedListener implements FocusListener
	{
		private GameStartGUI gui;
		private int currentTime;
		
		public TimeChangedListener(GameStartGUI Gui)
		{
			gui = Gui;
		}
		
		@Override
		public void focusGained(FocusEvent e)
		{
			try
			{
				currentTime = Integer.parseInt(gui.textTime.getText());
			}
			catch(ClassCastException exc)
			{
				currentTime = 300;
				gui.textTime.setText("300");
			}
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			int tmp = 300;
			try
			{
				tmp = Integer.parseInt(gui.textTime.getText());
				currentTime = tmp;
			}
			catch(ClassCastException exc)
			{
				gui.textTime.setText(Integer.toString(currentTime));
				return;
			}
			catch(NumberFormatException exc)
			{
				gui.textTime.setText(Integer.toString(currentTime));
				return;
			}
		}
	};
	
	private class rollDiffChangeListener implements AdjustmentListener
	{
		private GameStartGUI gui;
		
		public rollDiffChangeListener(GameStartGUI Gui)
		{
			gui = Gui;
		}

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e)
		{
			// TODO 自动生成的方法存根
			gui.lblDiff.setText("Difficulty (" + e.getValue() + "):");
		}
		
	}
	
	private class ListPlayerSelectListener implements ListSelectionListener
	{
		private GameStartGUI gui;
		
		public ListPlayerSelectListener(GameStartGUI Gui)
		{
			gui = Gui;
		}
		
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			gui.listCamp.setSelectedIndex(gui.listPlayer.getSelectedIndex());
		}
	}
	
	private class ListCampSelectListener implements ListSelectionListener
	{
		private GameStartGUI gui;
		
		public ListCampSelectListener(GameStartGUI Gui)
		{
			gui = Gui;
		}
		
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			gui.listPlayer.setSelectedIndex(gui.listCamp.getSelectedIndex());
		}
	}
	
	private class ListPlayerMouseListener implements MouseListener
	{
		private GameStartGUI gui;
		
		public ListPlayerMouseListener(GameStartGUI Gui)
		{
			gui = Gui;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() == 2)
			{
				if(gui.listPlayer.getSelectedIndex() != -1)
				{
					gui.listCamp.setSelectedIndex(gui.listPlayer.getSelectedIndex());
					if(!gui.listPlayer.getSelectedValue().equals(gui.textMyID.getText()))
					{
						gui.setIDs.remove(new NPCInformation(gui.listPlayer.getSelectedValue()));
						gui.listPrintNew();
					}
					else JOptionPane.showMessageDialog(null, "You can not remove yourself.");
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e){}
		@Override
		public void mouseReleased(MouseEvent e){}
		@Override
		public void mouseEntered(MouseEvent e){}
		@Override
		public void mouseExited(MouseEvent e){}
	}
	
	private class ListCampMouseListener implements MouseListener
	{
		private GameStartGUI gui;
		
		public ListCampMouseListener(GameStartGUI Gui)
		{
			gui = Gui;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() == 2)
			{
				if(gui.listCamp.getSelectedIndex() != -1)
				{
					gui.listPlayer.setSelectedIndex(gui.listCamp.getSelectedIndex());
					if(!gui.listPlayer.getSelectedValue().equals(gui.textMyID.getText()))
					{
						gui.setIDs.remove(new NPCInformation(gui.listPlayer.getSelectedValue()));
						gui.listPrintNew();
					}
					else JOptionPane.showMessageDialog(null, "You can not remove yourself.");
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e){}
		@Override
		public void mouseReleased(MouseEvent e){}
		@Override
		public void mouseEntered(MouseEvent e){}
		@Override
		public void mouseExited(MouseEvent e){}
	}
	
	private class AddNPCListener implements ActionListener
	{
		private GameStartGUI gui;
		
		public AddNPCListener(GameStartGUI Gui)
		{
			gui = Gui;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int camp;
			String name = gui.textNPCID.getText();
			if(!gui.textCamp.getText().equals("") && !name.equals(""))
			{
				try
				{
					camp = Integer.parseInt(gui.textCamp.getText());
					if(camp < 32768 && camp >= 0)
					{
						if(!gui.setIDs.contains(new NPCInformation(name)))
						{
							gui.setIDs.add(new NPCInformation(name, (double)(gui.rollDiff.getValue()) / 100.0, (short) camp));
							gui.listPrintNew();
						}	else JOptionPane.showMessageDialog(null, "The player named \"" + name + "\" has been existed.");
					}	else JOptionPane.showMessageDialog(null, "The camp must between 0 to 32767.");
				}
				catch(ClassCastException exc)
				{
					return;
				}
				catch(NumberFormatException exc)
				{
					return;
				}
			}
		}
	}
	
	private class GoNextListener implements ActionListener
	{
		private GameStartGUI gui;
		
		public GoNextListener(GameStartGUI Gui)
		{
			gui = Gui;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String myID = gui.textMyID.getText();
			try(FileWriter writer = new FileWriter(new File(configFilePath)))
			{
				writer.write(myID);
				writer.write("\n");
				writer.write(gui.textTime.getText());
				
				for(NPCInformation i:gui.setIDs)
				{
					if(!i.ID.equals(myID))
					{
						writer.write("\n");
						writer.write(i.ID);
						writer.write("\n");
						writer.write(Double.toString(i.difficulty));
						writer.write("\n");
						writer.write(Integer.toString(i.camp));
					}
					
					gui.formMain.dispose();
				}
			}	catch(IOException exc){}
		}
	}
	private String						configFilePath;
	
	private TreeSet<NPCInformation>		setIDs;
	
	private JFrame						formMain;
	private JList<String>				listPlayer;
	private JList<Integer>				listCamp;
	private DefaultListModel<String>	dlm_NPC;
	private DefaultListModel<Integer>	dlm_CMP;
	private MyIDChangedListener			actMyIDConfirm_Focus;
	private TimeChangedListener			actTimeConfirm_Focus;
	private JTextField					textMyID;
	private JTextField					textTime;
	private JTextField					textNPCID;
	private JTextField					textCamp;
	private JScrollBar					rollDiff;
	private JLabel						lblDiff;
	private JButton						butAdd;
	private JButton						butGoNext;
	
	public void listPrintNew()
	{
		dlm_NPC.clear();
		dlm_CMP.clear();
		
		for(NPCInformation i:setIDs)
		{
			dlm_NPC.addElement(i.ID);
			dlm_CMP.addElement((int) i.camp);
		}
		
		listPlayer.repaint();
	}
	
	public void initGUI()
	{
		formMain = new JFrame("dogfight_Z Game Settings");
		formMain.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		formMain.setBackground(new Color(255, 255, 255));
		formMain.setResizable(false);
		formMain.setLayout(null);
		formMain.setSize(500, 400);
		
		dlm_NPC = new DefaultListModel<String>();
		dlm_CMP = new DefaultListModel<Integer>();
		
		listPlayer = new JList<String>();
		formMain.add(listPlayer);
		listPlayer.setSize(100, 300);
		listPlayer.setLocation(10, 30);
		listPlayer.setModel(dlm_NPC);
		listCamp = new JList<Integer>();
		formMain.add(listCamp);
		listCamp.setSize(40, 300);
		listCamp.setLocation(110, 30);
		listCamp.setModel(dlm_CMP);
		
		JLabel lblRef = new JLabel("My name:");
		formMain.add(lblRef);
		lblRef.setSize(310, 20);
		lblRef.setLocation(160, 10);
		lblRef = new JLabel("Game Time (seconds, -1 means no limit):");
		formMain.add(lblRef);
		lblRef.setSize(310, 20);
		lblRef.setLocation(160, 50);
		lblRef = new JLabel("-----------------------------------NPCs----------------------------------");
		formMain.add(lblRef);
		lblRef.setSize(310, 20);
		lblRef.setLocation(160, 100);
		lblRef = new JLabel("NPC Name:");
		formMain.add(lblRef);
		lblRef.setSize(310, 20);
		lblRef.setLocation(160, 120);
		lblRef = new JLabel("Double click to remove.");
		formMain.add(lblRef);
		lblRef.setSize(200, 20);
		lblRef.setLocation(10, 335);
		lblRef = new JLabel("Name");
		formMain.add(lblRef);
		lblRef.setSize(200, 20);
		lblRef.setLocation(10, 10);
		lblRef = new JLabel("Camp");
		formMain.add(lblRef);
		lblRef.setSize(200, 20);
		lblRef.setLocation(110, 10);
		lblRef = new JLabel("Camp (An integer >= 0):");
		formMain.add(lblRef);
		lblRef.setSize(310, 20);
		lblRef.setLocation(160, 160);
		
		textMyID = new JTextField();
		formMain.add(textMyID);
		textMyID.setSize(310, 20);
		textMyID.setLocation(160, 30);

		textTime = new JTextField();
		formMain.add(textTime);
		textTime.setSize(310, 20);
		textTime.setLocation(160, 70);
		
		textNPCID = new JTextField();
		formMain.add(textNPCID);
		textNPCID.setSize(310, 20);
		textNPCID.setLocation(160, 140);
		
		textCamp = new JTextField();
		formMain.add(textCamp);
		textCamp.setSize(310, 20);
		textCamp.setLocation(160, 180);
		
		lblDiff = new JLabel("Difficulty (80):");
		formMain.add(lblDiff);
		lblDiff.setSize(310, 20);
		lblDiff.setLocation(160, 200);
		
		rollDiff = new JScrollBar(JScrollBar.HORIZONTAL);
		rollDiff.setMinimum(10);
		rollDiff.setMaximum(110);
		formMain.add(rollDiff);
		rollDiff.setSize(310, 20);
		rollDiff.setLocation(160, 220);
		rollDiff.setValue(80);
		
		butAdd = new JButton("Add NPC");
		formMain.add(butAdd);
		butAdd.setSize(310, 20);
		butAdd.setLocation(160, 250);
		
		butGoNext = new JButton("OK");
		formMain.add(butGoNext);
		butGoNext.setSize(100, 20);
		butGoNext.setLocation(370, 335);
		
		try(FileReader reader = new FileReader(new File(configFilePath)))
		{
			String tmp;
			int c;
			tmp = "";
			while((c = reader.read()) != '\n')
				tmp = tmp + (char)c;

			setIDs.add
			(
				new NPCInformation
				(
					tmp, 0.8, (short) 0
				)
			);
			textMyID.setText(tmp);
			
			tmp = "";
			while((c = reader.read()) != '\n' && c != -1)
				tmp = tmp + (char)c;
			
			textTime.setText(tmp);
			
			String id, diff, cmp;
			if(c != -1) while(true)
			{
				tmp = "";
				while((c = reader.read()) != -1 && c != '\n')
					tmp = tmp + (char)c;
				
				id = new String(tmp);
				
				tmp = "";
				while((c = reader.read()) != '\n')
					tmp = tmp + (char)c;
				diff = new String(tmp);
				
				tmp = "";
				while((c = reader.read()) != '\n'  && c != -1)
					tmp = tmp + (char)c;
				
				cmp = new String(tmp);
				setIDs.add
				(
					new NPCInformation
					(
						id, Double.parseDouble(diff), Short.parseShort(cmp)
					)
				);
				
				if(c == -1)
					break;
			}
		}	catch(IOException exc){}
		
		listPrintNew();
		
		actMyIDConfirm_Focus = new MyIDChangedListener(this);
		actTimeConfirm_Focus = new TimeChangedListener(this);
		
		textMyID.addFocusListener(actMyIDConfirm_Focus);
		textTime.addFocusListener(actTimeConfirm_Focus);
		rollDiff.addAdjustmentListener(new rollDiffChangeListener(this));
		butAdd.addActionListener(new AddNPCListener(this));
		butGoNext.addActionListener(new GoNextListener(this));
		listPlayer.addListSelectionListener(new ListPlayerSelectListener(this));
		listCamp.addListSelectionListener(new ListCampSelectListener(this));
		formMain.setVisible(true);
		
		listCamp.setSelectedIndex(0);
		listPlayer.setSelectedIndex(0);
		listPlayer.addMouseListener(new ListPlayerMouseListener(this));
		listCamp.addMouseListener(new ListCampMouseListener(this));
	}
	
	public GameStartGUI(String configFile)
	{
		setIDs = new TreeSet<NPCInformation>();
		configFilePath = configFile;
		initGUI();
	}
	/*
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					new GameStartGUI(configFilePath);
				}
			}
		);
	}*/
}
