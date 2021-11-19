package dogfight_Z.dogLog.view.menus;

import javax.swing.JTextArea;

public interface DogMenu {
    Operating putKeyHit(int keyCode);
    void getPrintNew(JTextArea screen);
}
