package graphic_Z.GRecZ.player.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import graphic_Z.GRecZ.GameVideoRecording;
import graphic_Z.GRecZ.player.controller.PController;
import graphic_Z.GRecZ.player.controller.PlayerController;
import graphic_Z.GRecZ.player.view.parts.UserController;
import graphic_Z.GRecZ.player.view.parts.WaitingDialog;

public class GRecZPlayer extends JFrame implements GRZPlayer {

    /**
     * 
     */
    private static final long serialVersionUID = 243097494485654204L;
    private JTextArea mainScr;
    private final int PCScreenCenter_X;
    private final int PCScreenCenter_Y;
    private int fontSize;
    private int currentFontIdx;
    private static Font supportedFonts[]; static {
        supportedFonts = new Font[3];
        try {
            supportedFonts[0] = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("consola.ttf"));
            supportedFonts[1] = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("DejaVuSansMono_0.ttf"));
            supportedFonts[2] = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream("simsun.ttc"));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    };
    
    private PlayerController     controller;

    public GRecZPlayer(String vRecFile, String bgm_info_file) throws HeadlessException {
        super("dogfight Z - Recording Player");
        
        
        PCScreenCenter_X = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width >> 1);
        PCScreenCenter_Y = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height >> 1);
        
        setSize(PCScreenCenter_X << 1, PCScreenCenter_Y << 1);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        //---------------------------Setup GUI----------------------------------
        setLocation(0, 0);
        setUndecorated(true);
        //setOpacity(0.9f);
        fontSize = 8;
        mainScr = new JTextArea();
        mainScr.setLocation(0, 0);
        mainScr.setSize(PCScreenCenter_X << 1, PCScreenCenter_Y << 1);
        mainScr.setEditable(false);
        mainScr.setFocusable(false);
        mainScr.setText("Welcome to the game world !");
        currentFontIdx = 1;
        mainScr.setFont(supportedFonts[1].deriveFont(Font.PLAIN, fontSize));
        mainScr.setBackground(new Color(0, 0, 0));
        mainScr.setForeground(new Color(255, 255, 255));
        //mainScr.setBackground(new Color(255, 200, 64));
        //mainScr.setForeground(new Color(0, 0, 0));
        
        //mainScr.addMouseMotionListener(mouseResponse);
        
        this.add(mainScr);
        //----------------------------------------------------------------------
        
        //--------------------------Hide Cursor---------------------------------
        URL classUrl = this.getClass().getResource("");  
        Image imageCursor = Toolkit.getDefaultToolkit().getImage(classUrl);  
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                            imageCursor,  new Point(0, 0), "cursor")); 
        //----------------------------------------------------------------------
        controller = null;
        
        try(DataInputStream recFileStream = new DataInputStream(new BufferedInputStream(new FileInputStream(vRecFile)))) {
            GameVideoRecording recording = new GameVideoRecording(recFileStream);
            controller = new PlayerController(mainScr, recording, bgm_info_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(controller != null) {
            (new Thread(controller)).start();
        }
        
        UserController controller = new UserController(this);
        addKeyListener(controller);
        mainScr.addMouseListener(controller);
        mainScr.addMouseWheelListener(controller);
        mainScr.addMouseMotionListener(controller);
    }
    
    public final void setScrZoom(int size) {
        fontSize = size;
        mainScr.setFont(supportedFonts[currentFontIdx].deriveFont(Font.PLAIN, fontSize));
    }

    public final void switchFont(int idx) {
        if(idx < 0 || idx > supportedFonts.length) return;
        mainScr.setFont(supportedFonts[idx].deriveFont(Font.PLAIN, fontSize));
        currentFontIdx = idx;
    }

    public static void main(String args[]) {
        WaitingDialog dia = new WaitingDialog("文件已选择。正在预缓冲解码，请稍候");
        dia.setVisible(true);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GRecZPlayer player = new GRecZPlayer(args[0], args[1]);
                    dia.setVisible(false);
                    dia.dispose();
                    player.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public final PController getController() {
        return controller;
    }
}
