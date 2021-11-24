package dogfight_Z.dogLog.controller;

import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharLabel;

public class ScreenResize extends Menu {
    
    private CharLabel tip;
    
    private int resolutionSetting[];

    public ScreenResize(String args[], JTextArea screen, int resolutionX, int resolutionY, int resolutionSetting[]) {
        super(args, screen, 1, resolutionX, resolutionY);
        this.resolutionSetting = resolutionSetting;
        tip = new CharLabel(
            screenBuffer, 
            1, 
            resolution, 
            "  使用 '[' 和 ']' 键设置屏幕缩放比例，\n\n使用 'J'、'I'、'L'、'K' 键调整分辨率，\n\n      校准完成后按回车键返回。\n\n      按回车键开始屏幕校准...", 
            (resolution[0] >> 1) - 13, 
            (resolution[1] >> 1) - 1, 
            false
        );
    }

    @Override
    public void getPrintNew() {
        clearScreenBuffer();
        tip.printNew();
        setScreen(screen);
    }

    @Override
    public Operation putKeyReleaseEvent(int keyCode) {
        switch(keyCode) {
        case KeyEvent.VK_ENTER: 
            return new Operation(false, new ScreenResizer(args, screen, resolutionSetting), null, null, null, null);
        }
        return null;
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
    public Operation beforePrintNewEvent() {
        Object sizeInfo = pollMail();
        if(sizeInfo == null) return null;
        screen.setFont(new Font("新宋体", Font.PLAIN, 20));
        return new Operation(true, null, null, null, sizeInfo, null);
    }

    @Override
    public Operation afterPrintNewEvent() {
        return null;
    }

}
