package dogfight_Z.dogLog.controller;

import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.Operable;

public class TipsConfirmMenu extends Menu {

    private CharLabel  lblTip;
    private CharButton btnOK;
    private CharButton btnCancel;
    
    public TipsConfirmMenu(String[] args, String tip, JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 2, resolutionX, resolutionY);
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
            34, 
            29,
            20,
            new Operable() {
                @Override
                public Operation call() {
                    return new Operation(true, null, null, null, true);
                }
                
            }
        );
        
        btnCancel = new CharButton(
                screenBuffer, 
                resolution, 
                "Cancel", 
                10, 
                29,
                20,
                new Operable() {
                    @Override
                    public Operation call() {
                        return new Operation(true, null, null, null, false);
                    }
                    
                }
            );
    }

    @Override
    public void getPrintNew() {
        clearScreenBuffer();
        
        switch(getSelectedIndex()) {
        case 0: btnCancel.setSelected(true); break;
        case 1: btnOK.setSelected(true); break;
        }
        lblTip.printNew();
        btnCancel.printNew();
        btnOK.printNew();
        
        setScreen(screen);
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        Operation opt = null;
        switch(keyCode) {
        case KeyEvent.VK_LEFT:
            indexUp();
            break;
        case KeyEvent.VK_RIGHT:
            indexDown();
            break;
        case KeyEvent.VK_ENTER:
            switch(getSelectedIndex()) {
            case 0: 
                btnCancel.setSelected(true);
                opt = btnCancel.call();
                break;
            case 1: 
                btnOK.setSelected(true);
                opt = btnOK.call();
                break;
            }   break;
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
    public Operation beforePrintNewEvent() {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public Operation afterPrintNewEvent() {
        // TODO 自动生成的方法存根
        return null;
    }

}
