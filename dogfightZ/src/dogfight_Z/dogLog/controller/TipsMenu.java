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
    
    public TipsMenu(String[] args, String tip, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 1, resolutionX, resolutionY);
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
                    return new Operation(true, null, null, null, null, null);
                }
                
            }
        );
    }

    @Override
    protected void getRefresh() {
        lblTip.printNew();
        btnOK.setSelected(true);
        btnOK.printNew();
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        Operation opt = null;
        switch(keyCode) {
        case KeyEvent.VK_ENTER:
            btnOK.setSelected(true);
            opt = btnOK.call();
            break;
        }
        return opt;
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public Operation putKeyPressEvent(int keyCode) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public Operation beforeRefreshNotification() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public Operation afterRefreshNotification() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected void beforeRefreshEvent() {
        
    }

    @Override
    protected void afterRefreshEvent() {
        
    }

}
