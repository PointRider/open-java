package startTheWorld;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dogfight_Z.GameRun;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;

import javax.swing.JButton;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.Color;

public class Start_GUI extends JFrame {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4221527046600513L;
	private JPanel contentPane;
	private File filewrite;
	private RecordUI gamerecord;
	//private static String beready="java dogFight_Z.GameRun \"%~dp0Jet.dat\" \"%~dp0Crosshair2.hud\" \"%~dp0LoopingScrollBar1.hud\" \"%~dp0LoopingScrollBar3.hud\" \"%~dp0HUD2.hud\" \"%~dp0MyJetHUD_Friend.hud\" \"%~dp0MyJetHUD_Enemy.hud\" \"%~dp0MyJetHUD_Locking.hud\" \"%~dp0MyJetHUD_Locked.hud\" \"%~dp0MissileWarning.hud\" \"%~dp0RadarHUD.hud\" \"%~dp0RaderPainter.hud\" \"%~dp0ScoreHUD.hud\" \"%~dp0GameOver.hud\" 107 57 120 ";
	//private Icon iconstart = new ImageIcon("resources\\icon.jpg");
	//private Icon iconrecord = new ImageIcon("resources\\icon2.jpg");
	
	static { 
	    GameRun.initEnviroment();
	    System.out.println("欢迎来到dogfightZ，\n\t一起来守护广袤的夜空吧！");
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Start_GUI frame = new Start_GUI(args);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	//.txt文件实例化
	public void createFile(){
        filewrite=new File("resources/StartTheWorld.txt");
        if (!filewrite.exists()) {//判断文件是否存在
            try {
                filewrite.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    //用以实现导入数据
    public JButton writeDataButton(JTextArea jt){
        JButton write=new JButton("加载");
        write.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                try {
                    FileWriter fw=new FileWriter(filewrite,true);
                    BufferedWriter bw=new BufferedWriter(fw);//新建缓存
                    jt.append("\n");//增加换行符
                    bw.write(jt.getText());//将文本框内容全部写入文件
                    bw.close();
                    System.out.println("成功加载数据");//在控制台端输出提示
                    
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        return write;
    }
	/**
	 * Create the frame.
	 */
	public Start_GUI(String[] Args) {
		super("Welcome to dogfight_Z !");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 800, 450);
		contentPane = new JPanel();
		//contentPane.setToolTipText("\u6218\u6B4C");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//背景图片
		File picture = new File("resources/GuiBackground.jpg");
		BufferedImage sourceImg = null;
		try {
				sourceImg = ImageIO.read(new FileInputStream(picture));
		} catch (FileNotFoundException e2) {
					// TODO 自动生成的 catch 块
					e2.printStackTrace();
		} catch (IOException e2) {
					// TODO 自动生成的 catch 块
					e2.printStackTrace();
				} 
		BackgroundPanel bgp=new BackgroundPanel(sourceImg);
		bgp.setBounds(0,0,800,450);
		
		//contentPane.add(comboBox);
		
		//标题
		JLabel lblNewLabel_1 = new JLabel("dogfight_Z");
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setFont(new Font("宋体", Font.BOLD | Font.ITALIC, 50));
		lblNewLabel_1.setBounds(24, 20, 295, 107);
		contentPane.add(lblNewLabel_1);
		createFile();
		
		
		//命令行运行beready，启动
		contentPane.add(bgp);
		bgp.setLayout(null);
		
		//添加游戏参与者
		JButton btnNewButton0 = new JButton("游戏设置");
		btnNewButton0.setBounds(24, 150, 200, 40);
		bgp.add(btnNewButton0);
		btnNewButton0.setFont(new Font("宋体", Font.BOLD, 22));
		
		//调出游戏记录，查看
		JButton btnNewButton = new JButton("游戏记录");
		btnNewButton.setBounds(24, 200, 200, 40);
		bgp.add(btnNewButton);
		btnNewButton.setFont(new Font("宋体", Font.BOLD, 22));
		//设置图标
		//btnNewButton.setIcon(iconrecord);

		JButton btnNewButton2 = new JButton("清除记录");
		btnNewButton2.setBounds(24, 250, 200, 40);
		bgp.add(btnNewButton2);
		btnNewButton2.setFont(new Font("宋体", Font.BOLD, 22));
		
		//添加按钮Start！
		JButton btnNewButton1 = new JButton("Start\uFF01");
		btnNewButton1.setBounds(24, 350, 200, 40);
		bgp.add(btnNewButton1);
		btnNewButton1.setFont(new Font("宋体", Font.BOLD, 26));
		
		//播放列表窗体启动
		JButton btnNewButton3 = new JButton("播放列表");
		btnNewButton3.setBounds(24, 300, 200, 40);
		bgp.add(btnNewButton3);
		btnNewButton3.setFont(new Font("宋体", Font.BOLD, 22));
		
		//监听Start按钮，当被点击时，
		//将选择的难度添加到 beready中，
		//将选中的歌曲，加载（未写）
		//ClassLoader.getSystemResource(name)
		btnNewButton1.addActionListener
		( 
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
				    String args[] = {
			            "resources/Jet.dat",
                        "resources/Crosshair2.hud",
                        "resources/LoopingScrollBar1.hud",
                        "resources/LoopingScrollBar3.hud",
                        "resources/HUD3.hud",
                        "resources/MyJetHUD_Friend.hud",
                        "resources/MyJetHUD_Enemy.hud",
                        "resources/MyJetHUD_Locking.hud",
                        "resources/MyJetHUD_Locked.hud",
                        "resources/MissileWarning.hud",
                        "resources/RadarHUD.hud",
                        "resources/RaderPainter.hud",
                        "resources/ScoreHUD.hud",
                        "resources/config_NPC.cfg",
                        "resources/gameRecord.rec",
                        "resources/config_OST.cfg",
                        "192", "108", "64", "8"
				    };
				    
				    GameRun.main(args);
				}
			}
		);
		
		btnNewButton0.addActionListener( 
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e) {
					//将选择的难度加入到需要的命令行String中
						SwingUtilities.invokeLater
						(
							new Runnable()
							{
								@Override
								public void run()
								{
									new GameStartGUI("resources/config_NPC.cfg");
								}
							}
						);
					}
				}
		);
		
		btnNewButton2.addActionListener( 
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e) {
				//将选择的难度加入到需要的命令行String中
					File f = new File("resources/gameRecord.rec");
					f.delete();
					JOptionPane.showMessageDialog(null, "所有游戏记录已成功清除。");
				}
			}
		);
		
		btnNewButton.addActionListener( 
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e) {
					try {
						gamerecord=new RecordUI("resources/gameRecord.rec");
					} catch (FileNotFoundException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}
					gamerecord.setVisible(true);
				}
			} 	
		);
		
		btnNewButton3.addActionListener( 
    		new ActionListener()
    		{
    			public void actionPerformed(ActionEvent e) {
    				SwingUtilities.invokeLater
    				(
    					new Runnable()
    					{
    						@Override
    						public void run()
    						{
    							new SoundTrackSelector("resources/config_OST.cfg");
    						}
    					}
    				);
    			}
    		} 	
    	);
	}
}



/*
ProcessBuilder pb = new ProcessBuilder
(
    "java", "dogfight_Z.Game",
    "resources/Jet.dat",
    "resources/Crosshair2.hud",
    "resources/LoopingScrollBar1.hud",
    "resources/LoopingScrollBar3.hud",
    "resources/HUD3.hud",
    "resources/MyJetHUD_Friend.hud",
    "resources/MyJetHUD_Enemy.hud",
    "resources/MyJetHUD_Locking.hud",
    "resources/MyJetHUD_Locked.hud",
    "resources/MissileWarning.hud",
    "resources/RadarHUD.hud",
    "resources/RaderPainter.hud",
    "resources/ScoreHUD.hud",
    "resources/config_NPC.cfg",
    "resources/gameRecord.rec",
    "resources/config_OST.cfg",
    "192", "108", "64", "8"
);
try
{
    pb.start();
} catch (IOException e1)
{
    // TODO 自动生成的 catch 块
    e1.printStackTrace();
}*/