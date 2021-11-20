package dogfight_Z.dogLog.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.menus.DogMenu;
import dogfight_Z.dogLog.view.menus.Operation;
import dogfight_Z.dogLog.view.menus.PilotLog;

public class DogLog extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -4187237321637169638L;

    private   JTextArea      mainScr;
    private   int            pcScreenWidth;
    private   int            pcScreenHeight;

    private   Stack<DogMenu> menuStack; 
    private   DogMenu        baseMenu;
    
    private void initUI() {
        pcScreenWidth  = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        pcScreenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

        setSize(pcScreenWidth, pcScreenHeight);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setLocation(0, 0);
        setUndecorated(true);
        //setOpacity(0.9f);
        
        mainScr = new JTextArea();
        mainScr.setLocation(0, 0);
        mainScr.setSize(pcScreenWidth, pcScreenHeight);
        mainScr.setEditable(false);
        mainScr.setFocusable(false);
        mainScr.setText("Welcome to the game world !");
        mainScr.setFont(new Font("新宋体", Font.PLAIN, 24));
        mainScr.setBackground(new Color(0, 0, 0));
        mainScr.setForeground(new Color(255, 255, 255));
        //mainScr.setBackground(new Color(255, 200, 64));
        //mainScr.setForeground(new Color(0, 0, 0));
        
        this.add(mainScr);
        
        URL classUrl = this.getClass().getResource("");  
        Image imageCursor = Toolkit.getDefaultToolkit().getImage(classUrl);  
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                            imageCursor,  new Point(0, 0), "cursor"));
        
        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                menuStack.peek().putKeyType(e.getKeyChar());
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO 自动生成的方法存根
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO 自动生成的方法存根
                int keyCode = e.getKeyCode();
                if(keyCode == KeyEvent.VK_ESCAPE) System.exit(0);
                
                DogMenu menu = menuStack.peek(), tmp = null;
                Color c;
                Operation o  = menu.putKeyHit(keyCode);
                
                if(o != null) {
                    if(o.isGoBack() && menu != baseMenu) {
                        menuStack.pop();
                        menu = menuStack.peek();
                    }
                    if((tmp = o.getGetInto()) != null) {
                        menuStack.push(tmp);
                        menu = tmp;
                    }
                    if((c = o.getFlashColor()) != null) {
                        flashColor(c);
                    }
                    if((c = o.getDoubleFlashColor()) != null) {
                        doubleFlashColor(c);
                    }
                }
                
                menu.getPrintNew(mainScr);
            }
            
        });
    }
    
    private void flashColor(Color c) {
        Color oldBg = new Color(0, 0, 0);
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    mainScr.setBackground(c);
                    Thread.sleep(100);
                    mainScr.setBackground(oldBg);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            
        }).start();
    }

    private void doubleFlashColor(Color c) {
        Color oldBg = new Color(0, 0, 0);
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    mainScr.setBackground(c);
                    Thread.sleep(100);
                    mainScr.setBackground(oldBg);
                    Thread.sleep(100);
                    mainScr.setBackground(c);
                    Thread.sleep(100);
                    mainScr.setBackground(oldBg);
                } catch (InterruptedException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            
        }).start();
    }
    private void constructor() {
        baseMenu  = new PilotLog(64, 32);
        menuStack = new Stack<>();
        menuStack.push(baseMenu);
        
        initUI();
        
        menuStack.peek().getPrintNew(mainScr);
    }
    
    public DogLog() throws HeadlessException {
        constructor();
    }

    public DogLog(GraphicsConfiguration gc) {
        super(gc);
        constructor();
    }

    public DogLog(String title) throws HeadlessException {
        super(title);
        constructor();
    }

    public DogLog(String title, GraphicsConfiguration gc) {
        super(title, gc);
        constructor();
    }
    
    public void setScrZoom(int size)
    {
        mainScr.setFont(new Font("新宋体", Font.PLAIN, size));
    }
    
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DogLog dlog = new DogLog();
                    dlog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
