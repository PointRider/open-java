package dogfight_Z.dogLog.view;

import graphic_Z.Common.Operation;

public interface DogMenu {

    Operation beforePrintNewEvent();
    Operation afterPrintNewEvent();
    Operation putKeyReleaseEvent(int keyCode);
    Operation putKeyPressEvent(int keyCode);
    Operation putKeyTypeEvent(int keyChar);
    void sendMail(Object o);
    void getPrintNew();
}
