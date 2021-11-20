package dogfight_Z.dogLog.view.menus;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharPasswordEdit;
import graphic_Z.HUDs.CharSingleLineTextEdit;

public class DogRegist extends Menu {
    
    private CharLabel lblTiltle;

    private CharLabel lblUsername;
    private CharSingleLineTextEdit tbUsername;
    
    private CharLabel lblUserPass;
    private CharPasswordEdit pasUserPass;

    private CharLabel lblUserPassConfirm;
    private CharPasswordEdit pasUserPassConfirm;
    
    private CharLabel lblUserNick;
    private CharSingleLineTextEdit tbUserNick;
    
    private CharButton btnUserScreenSetup;
    
    private CharButton btnConfirm;
    private CharButton btnCancel;
    
    private CharSingleLineTextEdit textBoxes[];
    
    public DogRegist(int resolutionX, int resolutionY) {
        super(7, resolutionX, resolutionY);
        
        lblTiltle = new CharLabel(
            screenBuffer, 
            1, 
            resolution, 
            "DOGFIGHT Z - PILOT REGIST", 
            (resolution[0] >> 1) - 12, 
            6,
            false
        );
        
        lblUsername = new CharLabel(
            screenBuffer, 
            1, 
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
            32
        );
        
        lblUserPass = new CharLabel(
            screenBuffer, 
            1, 
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
            32
        );
        
        textBoxes = new CharSingleLineTextEdit[] {
            tbUsername,
            pasUserPass
        };
        
    }

    @Override
    public void getPrintNew(JTextArea screen) {
        
        switch(getSelectedIndex()) {
        case 0: 
            tbUsername.setSelected(true);
            break;
        case 1:
            pasUserPass.setSelected(true);
            break;
        }
        
        clearScreenBuffer();
        
        lblTiltle.printNew();
        
        tbUsername.printNew();
        lblUsername.printNew();
        
        lblUserPass.printNew();
        pasUserPass.printNew();
        
        setScreen(screen);
    }

    @Override
    public Operation putKeyHit(int keyCode) {
        // TODO 自动生成的方法存根
        int selected = getSelectedIndex();
        if(keyCode == KeyEvent.VK_END) return new Operation(true, null, null, null);
        
        Operation opt = null;
        switch(keyCode) {
        case KeyEvent.VK_UP:
            indexUp();
            break;
        case KeyEvent.VK_DOWN:
            indexDown();
            break;
        case KeyEvent.VK_ENTER:
            //opt = new Operation(false, null, new Color(64, 96, 128));
            //opt = entries[getSelectedIndex()].call();
            break;
        default:
            if(selected >= 0  &&  selected < textBoxes.length) {
                textBoxes[selected].setSelected(true);
                textBoxes[selected].getControl(keyCode);
            }
            break;
        }
        
        return opt;
    }

    @Override
    public Operation putKeyType(int keyChar) {
        int selected = getSelectedIndex();
        if(selected >= 0  &&  selected < textBoxes.length) {
            textBoxes[selected].setSelected(true);
            textBoxes[selected].getInput(keyChar);
        }
        return null;
    }

}
