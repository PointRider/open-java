package graphic_Z.GRecZ.player.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
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

import graphic_Z.GRecZ.GameVideoRecordingFast;
import graphic_Z.GRecZ.player.controller.PController;
import graphic_Z.GRecZ.player.controller.PlayerControllerFast;
import graphic_Z.GRecZ.player.view.parts.UserController;
import graphic_Z.GRecZ.player.view.parts.WaitingDialog;
import graphic_Z.Managers.EventManager;

public class GRecZPlayerFast extends JFrame implements GRZPlayer {

    /**
     * 
     */
    private static final long serialVersionUID = 243097494485654204L;
    private JTextArea mainScr;
    private final int PCScreenCenter_X;
    private final int PCScreenCenter_Y;
    private int fontSize;
    private int currentFontIdx;
    
    private PlayerControllerFast controller;

    public GRecZPlayerFast(String vRecFile, String bgm_info_file, int fontSize, int fontIdx) throws HeadlessException {
        super("dogfight Z - Recording Player");
        
        PCScreenCenter_X = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width >> 1);
        PCScreenCenter_Y = (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height >> 1);
        
        setSize(PCScreenCenter_X << 1, PCScreenCenter_Y << 1);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        //---------------------------Setup GUI----------------------------------
        setLocation(0, 0);
        setUndecorated(true);
        //setOpacity(0.9f);
        this.fontSize  = fontSize;
        currentFontIdx = fontIdx;
        mainScr = new JTextArea();
        mainScr.setLocation(0, 0);
        mainScr.setSize(PCScreenCenter_X << 1, PCScreenCenter_Y << 1);
        mainScr.setEditable(false);
        mainScr.setFocusable(false);
        mainScr.setText("Welcome to the game world !");
        mainScr.setFont(EventManager.getSupportedFonts(1).deriveFont(Font.PLAIN, fontSize));
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
            GameVideoRecordingFast recording = new GameVideoRecordingFast(recFileStream);
            controller = new PlayerControllerFast(mainScr, recording, bgm_info_file);
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

    @Override
    public final void setScrZoom(int size) {
        fontSize = size;
        mainScr.setFont(EventManager.getSupportedFonts(currentFontIdx).deriveFont(Font.PLAIN, fontSize));
    }

    @Override
    public final void switchFont(int idx) {
        if(idx < 0 || idx > EventManager.getSupportedFontsCount()) return;
        mainScr.setFont(EventManager.getSupportedFonts(idx).deriveFont(Font.PLAIN, fontSize));
        currentFontIdx = idx;
    }

    @Override
    public final void nextFont() {
        if(++currentFontIdx == EventManager.getSupportedFontsCount()) currentFontIdx = 0;
        mainScr.setFont(EventManager.getSupportedFonts(currentFontIdx).deriveFont(Font.PLAIN, fontSize));
    }

    @Override
    public final void privFont() {
        if(currentFontIdx-- == 0) currentFontIdx = EventManager.getSupportedFontsCount() - 1;
        mainScr.setFont(EventManager.getSupportedFonts(currentFontIdx).deriveFont(Font.PLAIN, fontSize));
    }

    @Override
    public final void addSize() {
        fontSize += 1;
        setScrZoom(fontSize);
    }

    @Override
    public final void decSize() {
        if(fontSize > 1) fontSize -= 1;
        setScrZoom(fontSize);
    }
    
    public static void main(String args[]) {
        WaitingDialog dia = new WaitingDialog("文件已选择。正在预缓冲解码，请稍候");
        dia.setVisible(true);
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    GRecZPlayerFast player = new GRecZPlayerFast(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    dia.setVisible(false);
                    dia.dispose();
                    player.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public final PController getController() {
        return controller;
    }
}
