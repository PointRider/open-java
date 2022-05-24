package graphic_Z.GRecZ.player.view;

import graphic_Z.GRecZ.player.controller.PController;

public interface GRZPlayer {
    PController getController();
    void setVisible(boolean b);
    void dispose();
}
