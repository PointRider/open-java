package dogfight_Z.dogLog.controller;

import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharPasswordEdit;
import graphic_Z.HUDs.CharSingleLineTextEdit;
import graphic_Z.HUDs.Operable;
import graphic_Z.HUDs.Widget;

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
    
    private Widget widget[];
    
    public DogRegist(String args[], JTextArea screen, int resolutionX, int resolutionY) {
        super(args, screen, 7, resolutionX, resolutionY);
        
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

        lblUserPassConfirm = new CharLabel(
            screenBuffer, 
            4, 
            resolution, 
            " Confirm:", 
            8, 
            18,
            false
        );
        
        pasUserPassConfirm = new CharPasswordEdit(
            screenBuffer, 
            resolution, 
            20, 
            18, 
            36
        );
        
        lblUserNick = new CharLabel(
            screenBuffer, 
            5, 
            resolution, 
            "Nickname:", 
            8, 
            21,
            false
        );
        
        tbUserNick = new CharSingleLineTextEdit(
            screenBuffer, 
            resolution, 
            20, 
            21, 
            36
        );
        
        btnUserScreenSetup = new CharButton(
            screenBuffer, 
            resolution, 
            "Setup Screen Size", 
            10, 
            26,
            44,
            new Operable() {

                @Override
                public Operation call() {
                    
                    return new Operation(false, new ScreenResize(args, screen, resolution[0], resolution[1]), null, null, null);
                }
                
            }
        );
        
        btnConfirm = new CharButton(
            screenBuffer, 
            resolution, 
            "O K", 
            10, 
            29,
            20,
            new Operable() {

                @Override
                public Operation call() {
                    
                    return null;
                }
                
            }
        );
        
        btnCancel = new CharButton(
            screenBuffer, 
            resolution, 
            "Cancel", 
            34, 
            29,
            20,
            new Operable() {
                @Override
                public Operation call() {
                    return new Operation(true, null, null, null, null);
                }
            }
        );
        
        //--------------------------------------------
        widget = new Widget[] {
            tbUsername,
            pasUserPass,
            pasUserPassConfirm,
            tbUserNick,
            btnUserScreenSetup,
            btnConfirm,
            btnCancel
        };
        
    }

    @Override
    public void getPrintNew() {

        clearScreenBuffer();
        
        for(int i = 0, j = widget.length; i < j; ++i) {
            if(i == getSelectedIndex()) widget[i].setSelected(true);
            widget[i].printNew();
        }
        
        btnUserScreenSetup.printNew();
        
        lblTiltle.printNew();
        lblUsername.printNew();
        lblUserPass.printNew();
        lblUserPassConfirm.printNew();
        lblUserNick.printNew();
        
        setScreen(screen);
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        
        int selected = getSelectedIndex();
        
        Operation opt = null;
        switch(keyCode) {
        case KeyEvent.VK_UP:
            indexUp();
            break;
        case KeyEvent.VK_DOWN:
            indexDown();
            break;
        case KeyEvent.VK_ENTER:
            if(selected >= 0  &&  selected < 4) {
                //文本框
                indexDown();
            } else {
                //按钮
                widget[selected].setSelected(true);
                opt = widget[selected].call();
            }
            break;
        default:
            if(selected >= 0  &&  selected < widget.length) {
                widget[selected].setSelected(true);
                widget[selected].getControl(keyCode);
            }
            break;
        }
        
        return opt;
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
        if(keyChar == '\n'  ||  keyChar == '\r') return null;
        int selected = getSelectedIndex();
        if(selected >= 0  &&  selected < widget.length) {
            widget[selected].setSelected(true);
            widget[selected].getInput(keyChar);
        }
        return null;
    }

    @Override
    public Operation putKeyPressEvent(int keyCode) {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    public Operation beforePrintNewEvent() {
        int sizeInfo[] = (int[]) pollMail();
        if(sizeInfo != null) {
            System.out.println(sizeInfo[0] + ", " + sizeInfo[1] + ", " + sizeInfo[2]);
        }
        return null;
    }

    @Override
    public Operation afterPrintNewEvent() {
        // TODO 自动生成的方法存根
        return null;
    }

}
