package dogfight_Z.dogLog.view;

import graphic_Z.Common.Operation;

public interface DogMenu {

    Operation beforeRefreshNotification();
    Operation afterRefreshNotification();
    Operation putKeyReleaseEvent(int keyCode);
    Operation putKeyPressEvent(int keyCode);
    Operation putKeyTypeEvent(int keyChar);
    void sendMail(Object o);
    void refresh();
    DogMenu getCurrentDialog();
    Runnable getOverridedBeforeRefreshEvent();
    void setOverridedBeforeRefreshEvent(Runnable overridedBeforeRefreshEvent);
    Runnable getOverridedAfterRefreshNotification();
    void setOverridedAfterRefreshNotification(Runnable overridedAfterRefreshNotification);
}
