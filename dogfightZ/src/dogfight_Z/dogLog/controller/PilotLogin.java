package dogfight_Z.dogLog.controller;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharPasswordEdit;
import graphic_Z.HUDs.CharSingleLineTextEdit;

public class PilotLogin extends Menu {

    private CharLabel lblTiltle;
    
    private CharLabel lblUsername;
    private CharSingleLineTextEdit tbUsername;
    
    private CharLabel lblUserPass;
    private CharPasswordEdit pasUserPass;
    
    private CharButton btnLogin;
    private CharButton btnCancel;

    public PilotLogin(String[] args, JTextArea screen, int selectableCount, int resolutionX, int resolutionY) {
        super(args, screen, selectableCount, resolutionX, resolutionY);
        lblTiltle = new CharLabel(
            screenBuffer, 
            1, 
            resolution, 
            "DOGFIGHT Z - LOGIN", 
            (resolution[0] >> 1) - 12, 
            6,
            false
        );
        
        lblUsername = new CharLabel(
            screenBuffer, 
            2, 
            resolution, 
            "Username:", 
            8, 
            12,
            false
        );
        
        tbUsername = new CharSingleLineTextEdit(
            screenBuffer, 
            resolution, 
            20, 
            12, 
            36
        );
        
        lblUserPass = new CharLabel(
            screenBuffer, 
            3, 
            resolution, 
            "Password:", 
            8, 
            15,
            false
        );
        
        pasUserPass = new CharPasswordEdit(
            screenBuffer, 
            resolution, 
            20, 
            15, 
            36
        );
    }

    @Override
    public void getPrintNew() {
        // TODO 自动生成的方法存根

    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        // TODO 自动生成的方法存根
        return null;
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
