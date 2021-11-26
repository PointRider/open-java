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

import dogfight_Z.dogLog.controller.PilotLog;
import dogfight_Z.dogLog.controller.TipsConfirmMenu;
import dogfight_Z.dogLog.security.Irreversible;
import dogfight_Z.dogLog.security.SHA256Hex;
import graphic_Z.Common.Operation;
import graphic_Z.utils.HzController;

public class DogLog extends JFrame {

    /**
     * 
     */
    private static final Irreversible<String, String> passwordEncoder = new SHA256Hex();
    
    private static final long serialVersionUID = -4187237321637169638L;

    private JTextArea      mainScr;
    private int            pcScreenWidth;
    private int            pcScreenHeight;
    private static int     resolution[] = {64, 36};

    //private Stack<Object>  menuReturnStack; 
    private Stack<DogMenu> menuStack; 
    private DogMenu        baseMenu;
    private DogMenu        makeSureMenu;
    private boolean        running;
    private HzController   secondRefresher;
    
    static {
        try {
            Class.forName("dogfight_Z.dogLog.utils.JDBCFactory");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    private void initUI() {
        pcScreenWidth  = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        pcScreenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

        setSize(pcScreenWidth, pcScreenHeight);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setLocation(0, 0);
        setUndecorated(true);
        //setOpacity(0.5f);
        
        mainScr = new JTextArea();
        mainScr.setLocation(0, 0);
        mainScr.setSize(pcScreenWidth, pcScreenHeight);
        mainScr.setEditable(false);
        mainScr.setFocusable(false);
        mainScr.setText("Welcome to the game world !");
        mainScr.setFont(new Font("新宋体", Font.PLAIN, 20));
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
                synchronized(menuStack) {
                    menuStack.peek().putKeyTypeEvent(e.getKeyChar());
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                synchronized(menuStack) {
                    DogMenu menu = menuStack.peek();
                    
                    Operation o  = menu.putKeyPressEvent(e.getKeyCode());
                    
                    DogMenu m = operationProcessor(menu, o);
                    
                    o = m.beforeRefreshNotification();
                    if(o != null) m = operationProcessor(m, o);
                    m.refresh();
                    m.afterRefreshNotification();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                synchronized(menuStack) {
                    DogMenu menu = menuStack.peek();
                    
                    Operation o  = menu.putKeyReleaseEvent(e.getKeyCode());
                    
                    DogMenu m = operationProcessor(menu, o);
                    
                    do {
                        o = m.beforeRefreshNotification();
                        menu = m;
                        if(o != null) m = operationProcessor(m, o);
                    } while(m != menu);
                    
                    m.refresh();
                    m.afterRefreshNotification();
                }
            }
            
            private synchronized DogMenu operationProcessor(DogMenu menu, Operation o) {
                Color c;
                Object returnValue;
                DogMenu tmp = menu;
                Runnable callback;
                if(o != null) {
                    if(o.isGoBack()) {
                        if(menu != baseMenu) {
                            menuStack.pop();//注1
                            menu = menuStack.peek();
                        } else {
                            menu = makeSureMenu;
                            menuStack.push(makeSureMenu);
                        }
                    }
                    if((returnValue = o.getReturnValue()) != null) {
                        menu.sendMail(returnValue);//注意此时的menu可能已经是 注1 处pop后的下一个menu
                    }
                    if((tmp = o.getGetInto()) != null) {
                        menuStack.push(tmp);
                        menu = tmp;
                    }
                    if((callback = o.getCallBack()) != null) {
                        callback.run();
                    }
                    if((c = o.getFlashColor()) != null) {
                        flashColor(c);
                    }
                    if((c = o.getDoubleFlashColor()) != null) {
                        doubleFlashColor(c);
                    }
                }
                return menu;
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
    
    private void constructor(String args[]) {
        //this.args = args;
        initUI();
        //menuReturnStack = new Stack<>();
        baseMenu = new PilotLog(args, mainScr, resolution[0], resolution[1]);
        
        menuStack = new Stack<>();
        menuStack.push(baseMenu);
        menuStack.peek().refresh();
        
        makeSureMenu = new TipsConfirmMenu(
            args, 
            null,
            "确定要退出游戏吗？", 
            "YES", 
            "CANCEL", 
            mainScr, 
            resolution[0], 
            resolution[1], 
            new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 
            null
        );
        
        running = true;
        secondRefresher = new HzController(2);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread syn = null;
                try {
                    while(running) {
                        syn = new Thread(secondRefresher);
                        synchronized(menuStack) {
                            menuStack.peek().refresh();
                        }
                        syn.join();
                    }
                } catch (InterruptedException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            
        }).start();
    }
    
    public DogLog(String args[]) throws HeadlessException {
        constructor(args);
    }

    public DogLog(String args[], GraphicsConfiguration gc) {
        super(gc);
        constructor(args);
    }

    public DogLog(String args[], String title) throws HeadlessException {
        super(title);
        constructor(args);
    }

    public DogLog(String args[], String title, GraphicsConfiguration gc) {
        super(title, gc);
        constructor(args);
    }
    
    public void setScrZoom(int size)
    {
        mainScr.setFont(new Font("新宋体", Font.PLAIN, size));
    }
    
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DogLog dlog = new DogLog(args);
                    dlog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Irreversible<String, String> getPasswordencoder() {
        return passwordEncoder;
    }
}
