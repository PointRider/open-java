package dogfight_Z.dogLog.view;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.controller.TipsConfirmMenu;
import dogfight_Z.dogLog.controller.TipsMenu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharSingleLineTextEdit;
import graphic_Z.HUDs.Operable;

public abstract class Menu implements DogMenu {
    
    protected String        args[];
    protected int           resolution[];
    protected char          screenBuffer[][];
    protected JTextArea     screen;
    private   Menu          currentDialog;
    private   Object        inBox;
    private   StringBuilder screenBuilder;
    private   Integer       selectedIndex;
    private   Object        mutex;
    private   char          emptyLine[];
    private   int           selectableCount;
    private   Runnable      overridedBeforeRefreshEvent;
    private   Runnable      overridedAfterRefreshNotification;
    
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
        currentDialog      = null;
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

        setOverridedBeforeRefreshEvent(null);
        setOverridedAfterRefreshNotification(null);
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

    public void setFocus(int id) {
        selectedIndex = id;
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

    protected void showConfirmDialog(String tip, String confirm, String cancel, Runnable confirmed, Runnable canceled) {
        currentDialog = new TipsConfirmMenu(
            args, 
            screenBuffer,
            tip, 
            confirm, 
            cancel, 
            screen, 
            resolution[0], 
            resolution[1], 
            confirmed, 
            canceled,
            new Runnable() {
                @Override
                public void run() {
                    closeDialog();
                }
            }
        );
    }
    
    protected void showConfirmDialog(String tip, String confirm, String cancel, Runnable confirmed) {
        Runnable cancelCall = new Runnable() {
            @Override
            public void run() {
                closeDialog();
            }
        };
        
        currentDialog = new TipsConfirmMenu(
            args, 
            screenBuffer,
            tip, 
            confirm, 
            cancel, 
            screen, 
            resolution[0], 
            resolution[1], 
            confirmed, 
            cancelCall,
            cancelCall
        );
    }
    
    protected void showTipsDialog(String tip, Runnable confirmed) {
        currentDialog = new TipsMenu(
            args, 
            screenBuffer,
            tip, 
            screen, 
            resolution[0], 
            resolution[1], 
            confirmed
        );
    }
    
    protected void showTipsDialog(String tip) {
        currentDialog = new TipsMenu(
            args, 
            screenBuffer,
            tip, 
            screen, 
            resolution[0], 
            resolution[1], 
            new Runnable() {
                @Override
                public void run() {
                    closeDialog();
                }
            }
        );
    }
    
    protected void closeDialog() {
        currentDialog = null;
    }
    
    public abstract void getRefresh();
    protected abstract void beforeRefreshEvent();
    protected abstract void afterRefreshEvent();
    
    public CharButton newCharButton(String text, int localtionX, int locationY, int size, Operable operator) {
        return new CharButton(screenBuffer, resolution, text, localtionX, locationY, size, operator);
    }
    
    public CharButton newCharButton(String text, int localtionX, int locationY, Operable operator) {
        return new CharButton(screenBuffer, resolution, text, localtionX, locationY, operator);
    }
    
    public CharLabel newCharLabel(String text, int localtionX, int locationY) {
        return new CharLabel(screenBuffer, 0, resolution, text, localtionX, locationY, true);
    }
    
    public CharLabel newCharLabel(String text, int localtionX, int locationY, boolean emptyAtSpace) {
        return new CharLabel(screenBuffer, 0, resolution, text, localtionX, locationY, emptyAtSpace);
    }
    
    public CharSingleLineTextEdit newCharSingleLineTextEdit(int localtionX, int locationY, int width) {
        return new CharSingleLineTextEdit(screenBuffer, resolution, localtionX, locationY, width);
    }
    
    @Override
    public synchronized void refresh() {
        if(overridedBeforeRefreshEvent == null) beforeRefreshEvent();
        else overridedBeforeRefreshEvent.run();
        
        clearScreenBuffer();
        if(currentDialog == null) getRefresh();
        else currentDialog.getRefresh();
        setScreen(screen);
        
        if(overridedAfterRefreshNotification == null) afterRefreshEvent();
        else overridedAfterRefreshNotification.run();
    }
    
    @Override
    public DogMenu getCurrentDialog() {
        return currentDialog;
    }
    
    @Override
    public Runnable getOverridedBeforeRefreshEvent() {
        return overridedBeforeRefreshEvent;
    }

    @Override
    public void setOverridedBeforeRefreshEvent(Runnable overridedBeforeRefreshEvent) {
        this.overridedBeforeRefreshEvent = overridedBeforeRefreshEvent;
    }

    @Override
    public Runnable getOverridedAfterRefreshNotification() {
        return overridedAfterRefreshNotification;
    }

    @Override
    public void setOverridedAfterRefreshNotification(Runnable overridedAfterRefreshNotification) {
        this.overridedAfterRefreshNotification = overridedAfterRefreshNotification;
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
