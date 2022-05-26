package graphic_Z.GRecZ.player.controller;

public interface PController {
    void goAround();
    void goAhead();
    void goAround(int frames);
    void goAhead(int frames);
    void reset();

    void resume();
    boolean isPaused();
    void pause();
    void finish();
    
    void previousBgm();
    void nextBgm();
}
