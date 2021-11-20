package dogfight_Z.dogLog.view.menus;

import javax.swing.JTextArea;

public abstract class Menu implements DogMenu {
    
    //protected final String  menuIndices[];
    protected int           resolution[];
    protected char          screenBuffer[][];
    private   StringBuilder screenBuilder;
    private   Integer       selectedIndex;
    private   Object        mutex;
    private   char          emptyLine[];
    private   int           selectableCount;
    
    protected static final  String logoString = 
              "ooooo   oooo   ooooo oooooo oooo  ooooo oo  oo oooooo   @@@@@@\n"
            + "oo  oo oo  oo oo     oo      oo  oo     oo  oo   oo     @  @@ \n"
            + "oo  oo oo  oo oo ooo oooo    oo  oo ooo oooooo   oo       @@  \n"
            + "oo  oo oo  oo oo  oo oo      oo  oo  oo oo  oo   oo      @@  @\n"
            + "ooooo   oooo   ooooo oo     oooo  ooooo oo  oo   oo     @@@@@@\n\n"
            + "==============================================================\n"
            + "=============================================================="; 
    
    public Menu(int selectableCount, int resolutionX, int resolutionY) {
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

    @Override
    public abstract void getPrintNew(JTextArea screen);
    
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
    
    public final void clearScreenBuffer() {
        for(int i=0 ; i<resolution[1] ; ++i)
            System.arraycopy(emptyLine, 0, screenBuffer[i], 0, resolution[0]);
    }
    
    public final void setScreen(JTextArea screen) {
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
    
    @Override
    public abstract Operation putKeyHit(int keyCode);
    @Override
    public abstract Operation putKeyType(int keyChar);
}
