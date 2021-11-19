package dogfight_Z.dogLog.view.menus;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import graphic_Z.HUDs.CharLabel;

public class PilotLog extends MainMenu {

    static final String indices[] = {
        "PILOT  LOGIN",
        "PILOT REGIST"
    };
    
    private CharLabel logo;
    
    public PilotLog(int width, int height) {
        super(indices, width, height);
        logo = new CharLabel(
            screenBuffer, 
            0, 
            resolution, 
            logoString, 
            (resolution[0] >> 1) - 31, 
            (resolution[1] >> 1) - 11,
            true
        );
    }
    

    @Override
    public void getPrintNew(JTextArea screen) {
        
        clearScreenBuffer();
        
        logo.printNew();
        
        for(int cur = 0, length = 0; cur < indices.length; ++cur) {
            
            length = indices[cur].length();
            new CharLabel(
                screenBuffer, 
                0, 
                resolution, 
                cur == getSelectedIndex() ? 
                    " -" + loopChar('-', length) + "- \n" +
                    "/ " + loopChar(' ', length) + " \\\n" +
                    "> " + indices[cur] + " <\n" +
                    "\\ " + loopChar(' ', length) + " /\n"+
                    " -" + loopChar('-', length) + "- "
                  :
                    "\n\n  " + indices[cur] + "\n\n", 
                (resolution[0] >> 1) - ((length + 6) >> 1), 
                (resolution[1] >> 1) + (cur * 5),
                true
           ).printNew();
        
        }
        
        setScreen(screen);
    }

    @Override
    public Operating putKeyHit(int keyCode) {
        
        Operating opt = null;
        switch(keyCode) {
        case KeyEvent.VK_W:
            indexUp();
            break;
        case KeyEvent.VK_S:
            indexDown();
            break;
        case KeyEvent.VK_SPACE:
            opt = new Operating(false, null, new Color(128, 96, 64));
            break;
        }
        
        return opt;
    }

}
