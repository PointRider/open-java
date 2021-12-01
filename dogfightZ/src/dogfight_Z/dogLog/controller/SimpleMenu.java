package dogfight_Z.dogLog.controller;

import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

import dogfight_Z.dogLog.view.Menu;
import graphic_Z.Common.Operation;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.Widget;

public class SimpleMenu extends Menu {
    
    public CharLabel labels[];
    public Widget    widgets[];
    
    public SimpleMenu(String[] args, JTextArea screen, CharLabel labels[], Widget widgets[], int resolutionX, int resolutionY) {
        super(args, screen, 0, resolutionX, resolutionY);
        if(labels == null  ||  widgets == null) throw new NullPointerException("必须传入界面上的CharLabel、Widget数组（哪怕是空数组）。");
        this.labels  = labels;
        this.widgets = widgets;
        setSelectableCount(widgets.length);
        
        for(CharLabel l : labels) {
            l.resetScreenBuffer(screenBuffer);
        }
        for(Widget w : widgets) {
            w.resetScreenBuffer(screenBuffer);
        }
    }

    @Override
    public void getRefresh() {
        for(CharLabel l : labels) {
            l.printNew();
        }
        for(Widget w : widgets) {
            w.printNew();
        }
    }

    @Override
    protected void beforeRefreshEvent() {
        widgets[getSelectedIndex()].setSelected(true);
    }

    @Override
    protected void afterRefreshEvent() {
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
            widgets[getSelectedIndex()].setSelected(true);
            opt = widgets[getSelectedIndex()].call();
            break;
        case KeyEvent.VK_ESCAPE:
            opt = new Operation(true, null, null, null, null, null);
        default:
            widgets[getSelectedIndex()].setSelected(true);
            widgets[getSelectedIndex()].getControl(keyCode);
        }
        return opt;
    }

    @Override
    public Operation putKeyTypeEvent(int keyChar) {
        widgets[getSelectedIndex()].setSelected(true);
        widgets[getSelectedIndex()].getInput(keyChar);
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

}
