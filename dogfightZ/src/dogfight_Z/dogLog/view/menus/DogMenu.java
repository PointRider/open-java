package dogfight_Z.dogLog.view.menus;

import javax.swing.JTextArea;

public interface DogMenu {
    Operation putKeyHit(int keyCode);
    Operation putKeyType(int keyChar);
    void getPrintNew(JTextArea screen);
}
