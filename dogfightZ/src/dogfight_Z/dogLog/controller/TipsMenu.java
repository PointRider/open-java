package dogfight_Z.dogLog.controller;

import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.Operable;

public class TipsMenu extends Menu {

    private CharLabel  lblTip;
    private CharButton btnOK;
    
    public TipsMenu(String[] args, char scrBuffer[][], String tip, JTextArea screen, int resolutionX, int resolutionY) {
        this(args, scrBuffer, tip, screen, resolutionX, resolutionY, null);
    }
    
    public TipsMenu(String[] args, char scrBuffer[][], String tip, JTextArea screen, int resolutionX, int resolutionY, Runnable caller) {
        super(args, screen, 1, resolutionX, resolutionY);
        if(scrBuffer != null) super.screenBuffer = scrBuffer;
        lblTip = new CharLabel(
            screenBuffer, 
            1, 
            resolution, 
            tip, 
            (resolutionX >> 1) - (tip.length() >> 1), 
            resolutionY >> 1,
            false
        );
        
        btnOK = new CharButton(
            screenBuffer, 
            resolution, 
            "O K", 
            (resolutionX >> 1) - 12, 
            resolutionY - 6,
            20,
            new Operable() {
                @Override
                public Operation call() {
                    if(caller != null) caller.run();
                    return new Operation(false, null, null, null, null, null);
                }
            }
        );
    }

    @Override
    public void getRefresh() {
        lblTip.printNew();
        btnOK.setSelected(true);
        btnOK.printNew();
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        Operation opt = null;
        switch(keyCode) {
        case KeyEvent.VK_ENTER: case KeyEvent.VK_ESCAPE:
            btnOK.setSelected(true);
            opt = btnOK.call();
            break;
        }
        return opt;
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
        return null;
    }

    @Override
    public Operation putKeyPressEvent(int keyCode) {
        return null;
    }

    @Override
    public Operation beforeRefreshNotification() {
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        return null;
    }

    @Override
    protected void beforeRefreshEvent() {
        
    }

    @Override
    protected void afterRefreshEvent() {
        
    }

}
