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
    private Runnable   exitCall;
    //private Runnable   callBack;
    
    public TipsConfirmMenu(String[] args, String tip, JTextArea screen, int resolutionX, int resolutionY, Runnable callBackConfirm, Runnable callBackCancel, Runnable exitCall) {
        this(args, null, tip, "O K", "Cancel", screen, resolutionX, resolutionY, callBackConfirm, callBackCancel, exitCall);
    }
    
    public TipsConfirmMenu(String[] args, char scrBuffer[][], String tip, String selectionConfirm, String selectionCancel, JTextArea screen, int resolutionX, int resolutionY, Runnable callBackConfirm, Runnable callBackCancel, Runnable exitCall) {
        super(args, screen, 2, resolutionX, resolutionY);
        if(scrBuffer != null) super.screenBuffer = scrBuffer;
        //this.callBack = callBack;
        this.exitCall = exitCall;
        int tipsLength = tip.length();
        for(int i = 0, j = tipsLength; i < j; ++i) {
            if(tip.charAt(i) > 127) ++tipsLength;
        }
        
        lblTip = new CharLabel(
            screenBuffer, 
            1, 
            resolution, 
            tip, 
            (resolutionX >> 1) - (tipsLength >> 1), 
            resolutionY >> 1,
            false
        );
        
        btnOK = new CharButton(
            screenBuffer, 
            resolution, 
            selectionConfirm, 
            (resolutionX >> 1) + 1, 
            resolutionY - 6,
            20,
            new Operable() {
                @Override
                public Operation call() {
                    if(callBackConfirm != null) callBackConfirm.run();
                    return null;
                }
                
            }
        );
        
        btnCancel = new CharButton(
                screenBuffer, 
                resolution, 
                selectionCancel, 
                (resolutionX >> 1) - 25, 
                resolutionY - 6,
                20,
                new Operable() {
                    @Override
                    public Operation call() {
                        if(callBackCancel != null) callBackCancel.run();
                        return null;
                    }
                    
                }
            );
    }

    @Override
    public void getRefresh() {
        switch(getSelectedIndex()) {
        case 0: btnCancel.setSelected(true); break;
        case 1: btnOK.setSelected(true); break;
        }
        lblTip.printNew();
        btnCancel.printNew();
        btnOK.printNew();
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
        case KeyEvent.VK_ESCAPE:
            if(exitCall == null) {
                btnCancel.setSelected(true);
                opt = btnCancel.call();
            } else exitCall.run();
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
