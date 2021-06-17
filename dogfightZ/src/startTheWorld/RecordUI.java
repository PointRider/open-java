package startTheWorld;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class RecordUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2270027895493015765L;
	private JPanel contentPane;
	private File fileread;
	
	
	public void createFile(){
        fileread=new File("resources/gameRecord.rec");
        if (!fileread.exists()) {//判断文件是否存在
            try {
                fileread.createNewFile();
            } catch (IOException e1) {
            
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
	
	/**
	 * Create the frame.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	
	public RecordUI(String recFile) throws FileNotFoundException, IOException {
		super("Game Records");
		setResizable(false);
		//创建容器
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		BackgroundPanel bgp=new BackgroundPanel(null);
		bgp.setBounds(0,0,400,300);
		contentPane.add(bgp);
		bgp.setLayout(null);
		

		JLabel lbl = new JLabel(String.format("%-25s %-10s %-6s %-6s", "Player", "Time", "Killed", "Dead"));
		lbl.setFont(new Font("Consolas", Font.PLAIN, 12));
		bgp.add(lbl);
		lbl.setBounds(0,0,375,30);
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		JScrollPane scrollPane = new JScrollPane();
		bgp.add(scrollPane);
		scrollPane.setBounds(0,30,375,192);
		
		scrollPane.setViewportView(textArea);
		
		try(FileReader reader = new FileReader(new File(recFile)))
		{
			String tmp;
			int ch;
			String id, time, k, dead;
			boolean firstLn = true;
			while(true)
			{
				tmp = "";
				while((ch = reader.read()) != '\n' && ch != -1)
					tmp = tmp + (char)ch;
				
				if(ch == -1)
					break;
				
				id = new String(tmp);
				
				tmp = "";
				while((ch = reader.read()) != '\n')
					tmp = tmp + (char)ch;
				time = new String(tmp);
				
				tmp = "";
				while((ch = reader.read()) != '\n')
					tmp = tmp + (char)ch;
				k = new String(tmp);
				
				tmp = "";
				while((ch = reader.read()) != '\n')
					tmp = tmp + (char)ch;
				dead = new String(tmp);
				
				if(!firstLn)
					textArea.append("\n");
				
				textArea.append(String.format("%-25s %-10s %-6s %-6s", id, time, k, dead));
				firstLn = false;
			}
			setVisible(true);
		}	catch(IOException exc){}
		/*
		JScrollPane scrollPane = new JScrollPane();
		bgp.add(scrollPane);
		scrollPane.setBounds(64, 30, 300, 192);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		textArea.setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		//显示记录
		try(BufferedReader br=new BufferedReader(new FileReader(fileread))) {
			int times=0;
		    String s=null;
		    String ss=null;
		    while ((s=br.readLine())!=null) {
		    	if(times==0) {
		    	textArea.append(s);//设置文本框内容
		        textArea.append("\n");//增加换行符
		        times=1;
		    	}	
		    	else {
		    		ss="  "+s;
		    		while(ss.length()<22) ss+=" ";
			    	textArea.append(ss);//设置文本框内容
			    	times++;
			    	if(times==5) {
			    		textArea.append("\n");//增加换行符
			    		times=1;
			    	}
		    	}
		    }
		} catch (Exception e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();}
		*/
	}

}


