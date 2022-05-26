package startTheWorld;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

public class SoundTrackSelector extends JFrame
{
	ArrayList<String> soundTrack;
	/**
	 * 
	 */
	private String defaultPath;
	private static final long serialVersionUID = 1155593127399839038L;
	private boolean cancelled;
	
	private static String fileName(String path)
	{
		int startInd = path.length()-1;
		char ch;
		
		while(startInd >= 0 &&  (ch = path.charAt(startInd)) != '/' && ch != '\\')
			--startInd;
		
		if(startInd >= 0 && ((ch = path.charAt(startInd)) == '/' || ch == '\\'))
			++startInd;
		
		if(startInd >= 0)
			return path.substring(startInd);
		else return "";
	}
	
	private static String path(String path)
	{
		int startInd = path.length()-1;
		char ch;
		
		while(startInd >= 0 &&  (ch = path.charAt(startInd)) != '/' && ch != '\\')
			--startInd;
		
		if(startInd >= 0 && ((ch = path.charAt(startInd)) == '/' || ch == '\\'))
			++startInd;
		
		if(startInd >= 0)
			return path.substring(0, startInd);
		else return "";
	}
	
	public SoundTrackSelector(String ostCFGFile)
	{
		defaultPath = null;
		cancelled = false;
		soundTrack = new ArrayList<String>();
		soundTrack.clear();
		
		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(ostCFGFile), "GBK"))
		{
			String tmp;
			int ch;
			
			while(true)
			{
				tmp = "";
				while((ch = reader.read()) != '\n' && ch != -1)
					tmp = tmp + (char)ch;
				
				if(!tmp.equals("") && tmp.toLowerCase().endsWith(".mp3"))
					soundTrack.add(tmp);
				if(ch == -1) break;
			}
			
		}	catch(IOException exc){}
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(null);
		this.setBounds(200, 100, 600, 400);
		this.setTitle("Soundtrack Creating");
		this.setResizable(false);
		
		//------------list-------------
		DefaultListModel<String> listCmt = new DefaultListModel<String>();
		listCmt.clear();
		for(String each : soundTrack)
			listCmt.addElement(fileName(each));
		
		JList<String> list = new JList<String>();
		list.setModel(listCmt);
		this.add(list);
		list.setFont(new Font("宋体", Font.ITALIC, 12));
		list.setBounds(10, 50, 565, 300);
		
		if(soundTrack.size() > 0)
			defaultPath = path(soundTrack.get(soundTrack.size()-1));
		

		JScrollPane scrollPane = new JScrollPane();
		this.add(scrollPane);
		scrollPane.setBounds(10, 50, 565, 300);
		
		scrollPane.setViewportView(list);
		//----------list--end----------
		
		//-----------buttons-----------
		Font fntBtn = new Font("Consolas", Font.BOLD, 12);
		
		JButton btnAdd = new JButton("Add Mp3");
		
		btnAdd.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser jfc;
					if(defaultPath != null)
						jfc = new JFileChooser(defaultPath);
					else jfc = new JFileChooser(".");
					
					FileFilter filter = new FileFilter()
					{
						public String getDescription()
						{    
							return "*.mp3";    
						}    
						    
						public boolean accept(File file)
						{    
							String name = file.getName();    
						 	return file.isDirectory() || name.toLowerCase().endsWith(".mp3");
						}
					};
					
					jfc.addChoosableFileFilter(filter);
					jfc.setFileFilter(filter);
					
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
					jfc.setMultiSelectionEnabled(true);
					jfc.showDialog(new JLabel(), "Select");
					File files[]=jfc.getSelectedFiles();
					
					for(File each : files)
					{
						if(each.getName().toLowerCase().endsWith(".mp3"))
						{
							soundTrack.add(each.getPath());
							listCmt.addElement(each.getName());
						}
					}
					
					if(soundTrack.size() > 0)
						defaultPath = path(soundTrack.get(soundTrack.size()-1));
				}
			}
		);
		
		this.add(btnAdd);
		btnAdd.setFont(fntBtn);
		btnAdd.setBounds(10, 10, 100, 30);
		
		//-----------------------------
		
		JButton btnRemove = new JButton("Remove");
		
		btnRemove.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int selected[] = list.getSelectedIndices();
					if(selected.length <= 1)
					{
						int i = list.getSelectedIndex();
						if(i >= 0)
						{
							soundTrack.remove(i);
							listCmt.remove(i);
							list.repaint();
							if(i > 0)
								list.setSelectedIndex(i-1);
							else if(listCmt.getSize() > 0)
								list.setSelectedIndex(i);
						}
					}
					else
					{
						int offset = 0;
						for(int eachIndex : selected)
						{
							if(eachIndex != soundTrack.size()-1)
							{
								soundTrack.remove(eachIndex - offset);
								listCmt.remove(eachIndex - offset);
								++offset;
							}
						}
						list.repaint();
					}
				}
			}
		);
		
		this.add(btnRemove);
		btnRemove.setFont(fntBtn);
		btnRemove.setBounds(120, 10, 100, 30);
		
		list.addMouseListener
		(
			new MouseListener()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{
					if(e.getClickCount() == 2)
					{
						int i = list.getSelectedIndex();
						if(i >= 0)
						{
							soundTrack.remove(i);
							listCmt.remove(i);
							list.repaint();
							if(listCmt.getSize() > i)
								list.setSelectedIndex(i);
							else list.setSelectedIndex(listCmt.getSize()-1);
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
		);

		//-----------------------------
		
		JButton btnMoveUp = new JButton("Move Up");
		
		btnMoveUp.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int i = list.getSelectedIndex();
					if(i > 0)
					{
						String tmp;
						
						tmp = listCmt.get(i);
						listCmt.set(i, listCmt.get(i-1));
						listCmt.set(i-1, tmp);
						
						tmp = soundTrack.get(i);
						soundTrack.set(i, soundTrack.get(i-1));
						soundTrack.set(i-1, tmp);
						
						list.setSelectedIndex(i-1);
						list.repaint();
					}
				}
			}
		);
		
		this.add(btnMoveUp);
		btnMoveUp.setFont(fntBtn);
		btnMoveUp.setBounds(230, 10, 100, 30);
		//-----------------------------
		
		JButton btnMoveDown = new JButton("Move Down");
		
		btnMoveDown.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					int i = list.getSelectedIndex();
					if(i >= 0 && i < listCmt.getSize() - 1)
					{
						String tmp;
						
						tmp = listCmt.get(i);
						listCmt.set(i, listCmt.get(i+1));
						listCmt.set(i+1, tmp);
						
						tmp = soundTrack.get(i);
						soundTrack.set(i, soundTrack.get(i+1));
						soundTrack.set(i+1, tmp);
						
						list.setSelectedIndex(i+1);
						list.repaint();
					}
				}
			}
		);
		
		this.add(btnMoveDown);
		btnMoveDown.setFont(fntBtn);
		btnMoveDown.setBounds(340, 10, 100, 30);
		
		//-----------------------------
		
		JButton btnCancel = new JButton("OK");
		
		btnCancel.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if(!cancelled) try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(ostCFGFile), "GBK"))
					{
						for(int i=0, j=soundTrack.size() ; i<j ; ++i)
						{
							if(i != 0)
								writer.write('\n' + soundTrack.get(i));
							else writer.write(soundTrack.get(i));
						}
						dispose();
					}	catch(IOException exc){dispose();}
				}
			}
		);
				
		this.add(btnCancel);
		btnCancel.setFont(fntBtn);
		btnCancel.setBounds(470, 10, 100, 30);

		//--------buttons--end---------
		
		this.setVisible(true);
	}
}
