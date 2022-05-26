package graphic_Z.GRecZ.player.view;

import graphic_Z.GRecZ.player.controller.PController;

public interface GRZPlayer {
    PController getController();
    void setVisible(boolean b);
    void dispose();
    void decSize();
    void addSize();
    void privFont();
    void nextFont();
    void switchFont(int idx);
    void setScrZoom(int size);
}
