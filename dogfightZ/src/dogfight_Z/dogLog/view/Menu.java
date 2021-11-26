package dogfight_Z.dogLog.view;

import javax.swing.JTextArea;

import graphic_Z.Common.Operation;

public abstract class Menu implements DogMenu {
    
    //protected final String  menuIndices[];
    protected String        args[];
    protected int           resolution[];
    protected char          screenBuffer[][];
    protected JTextArea     screen;
    private   Object        inBox;
    //protected Stack<Object> returnStack;
    private   StringBuilder screenBuilder;
    private   Integer       selectedIndex;
    private   Object        mutex;
    private   char          emptyLine[];
    private   int           selectableCount;
    
    protected static final  String logoString = 
              "ooooo   oooo   ooooo oooooo oooo  ooooo oo  oo oooooo   @@@@@@\n"
            + "oo  oo oo  oo oo     oo   o  oo  oo     oo  oo o oo o   @  @@ \n"
            + "oo  oo oo  oo oo ooo oooo    oo  oo ooo oooooo   oo       @@  \n"
            + "oo  oo oo  oo oo  oo oo      oo  oo  oo oo  oo   oo      @@  @\n"
            + "ooooo   oooo   ooooo ooo    oooo  ooooo oo  oo  oooo    @@@@@@\n\n"
            + "==============================================================\n"
            + "=============================================================="; 
    
    public Menu(String args[], JTextArea screen, int selectableCount, int resolutionX, int resolutionY) {
        this.args          = args;
        //this.returnStack   = returnStack;
        this.screen        = screen;
        inBox              = null;
        mutex              = new Object();
        resolution         = new int[2];
        //this.menuIndices   = menuIndices;
        this.selectableCount = selectableCount;
        this.resolution[0] = resolutionX;
        this.resolution[1] = resolutionY;
        selectedIndex      = 0;
        screenBuffer       = new char[resolutionY][resolutionX];
        screenBuilder      = new StringBuilder(resolutionY * resolutionX);
        emptyLine          = new char[resolutionX];

        for(int i=0 ; i<resolutionX ; ++i) emptyLine[i] = ' ';
        
    }
    
    public void setResolution(int x, int y) {
        synchronized(mutex) {
            resolution[0] = x;
            resolution[1] = y;
            screenBuffer  = new char[x][y];
            screenBuilder = new StringBuilder(x * y);
            emptyLine     = new char[x];
            for(int i=0 ; i<x ; ++i) emptyLine[i] = ' ';
        }
    }
    
    public void indexUp() {
        synchronized(selectedIndex) {
            if(selectedIndex-- == 0) selectedIndex = selectableCount - 1;
        }
    }
    
    public void indexDown() {
        synchronized(selectedIndex) {
            if(++selectedIndex >= selectableCount) selectedIndex = 0;
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    public void setSelectableCount(int count) {
        this.selectableCount = count;
        selectedIndex = 0;
    }
    
    private final void clearScreenBuffer() {
        for(int i=0 ; i<resolution[1] ; ++i)
            System.arraycopy(emptyLine, 0, screenBuffer[i], 0, resolution[0]);
    }
    
    private synchronized final void setScreen(JTextArea screen) {
        screenBuilder.delete(0, screenBuilder.length());
        
        boolean firstInLine, firstLine;
        firstLine = true;
        for(char y[]:screenBuffer) {
            firstInLine = true;
            if(!firstLine) screenBuilder.append('\n');
            
            for(char x:y) {
                if(!firstInLine) screenBuilder.append(' ');
                screenBuilder.append(x);
                firstInLine = false;
            }
            
            firstLine = false;
        }
        
        screen.setText(screenBuilder.toString());
    }

    protected void resizeScreen(int x, int y) {
        resolution[0] = x;
        resolution[1] = y;
        
        screenBuffer = new char[y][];
        emptyLine    = new char[x];
        
        for(int i=0 ; i<y ; ++i)
            screenBuffer[i] = new char[x];

        for(int i=0 ; i<x ; ++i)
            emptyLine[i] = ' ';
        
        screenBuilder = new StringBuilder(x * y);
    }
    
    public void sendMail(Object o) {
        inBox = o;
    }

    public Object pollMail() {
        Object o = inBox;
        inBox = null;
        return o;
    }

    public abstract void getRefresh();
    protected abstract void beforeRefreshEvent();
    protected abstract void afterRefreshEvent();
    
    @Override
    public synchronized void refresh() {
        beforeRefreshEvent();
        clearScreenBuffer();
        getRefresh();
        setScreen(screen);
        afterRefreshEvent();
    }
    
    @Override
    public abstract Operation putKeyReleaseEvent(int keyCode);
    @Override
    public abstract Operation putKeyTypeEvent(int keyChar);
    @Override
    public abstract Operation putKeyPressEvent(int keyCode);
    @Override
    public abstract Operation beforeRefreshNotification();
    @Override
    public abstract Operation afterRefreshNotification();
}
