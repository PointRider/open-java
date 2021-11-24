package dogfight_Z.dogLog.controller;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharButton;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.Operable;
//import graphic_Z.HUDs.CharSingleLineTextEdit;

public class PilotLog extends Menu {

    private static final String indices[] = {
        "PILOT  LOGIN",
        "PILOT REGIST"
    };
    
    private final Operable buttonEvents[] = {
        new Operable() {

            @Override
            public Operation call() {
                return new Operation(false, new PilotLogin(args, screen, resolution[0], resolution[1]), null, new Color(64, 96, 128), null, null);
            }
            
        },
        new Operable() {

            @Override
            public Operation call() {
                return new Operation(false, new DogRegist(args, screen, resolution[0], resolution[1]), null, null, null, null);
            }
            
        },
    };
    
    private CharLabel  logo;
    private CharButton entries[];
    
    public PilotLog(String args[], JTextArea screen, int width, int height) {
        super(args, screen, indices.length, width, height);
        logo = new CharLabel(
            screenBuffer, 
            0, 
            resolution, 
            logoString, 
            (resolution[0] >> 1) - 31, 
            4,
            true
        );
        
        //txtbox = new CharSingleLineTextEdit(screenBuffer, resolution, (resolution[0] >> 1) - 20, (resolution[1] >> 1) + 12, 40);
        
        entries = new CharButton[indices.length];
        
        for(int cur = 0, length = 0; cur < indices.length; ++cur) {
            length = indices[cur].length();
            entries[cur] = new CharButton(
                screenBuffer, 
                resolution, 
                indices[cur],
                (resolution[0] >> 1) - ((length + 4) >> 1), 
                18 + (cur * 5),
                buttonEvents[cur]
            );
        }
    }
    
    @Override
    public void getPrintNew() {
        
        clearScreenBuffer();
        
        logo.printNew();
        
        for(int i = 0; i < entries.length; ++i) {
            entries[i].setSelected(i == getSelectedIndex());
            entries[i].printNew();
        }
        //txtbox.printNew();
        setScreen(screen);
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        
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
            entries[getSelectedIndex()].setSelected(true);
            opt = entries[getSelectedIndex()].call();
            break;
        case KeyEvent.VK_ESCAPE:
            opt = new Operation(true, null, null, null, null, null);
            break;
        default:
            //txtbox.getControl(keyCode);
            break;
        }
        
        return opt;
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
        //txtbox.getInput(keyChar);
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
