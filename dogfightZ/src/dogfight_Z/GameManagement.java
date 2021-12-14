package dogfight_Z;

import java.util.ArrayList;
import java.util.ListIterator;

import dogfight_Z.Ammo.Decoy;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;

public interface GameManagement {

    boolean isAircraftOfPlayer(Aircraft a);

    void addKillTip(Aircraft killer, Aircraft deader, String WeaponName);

    int getRespawnTime();

    float getWeaponMaxSearchingRange();

    void colorFlash(int R_Fore, int G_Fore, int B_Fore, int R_Back, int G_Back, int B_Back, int time);

    void addGBlack(float v);

    void fireAmmo(Dynamic ammo);

    float[] getPlayerCameraLocation();

    void throwDecoy(Decoy decoy);

    void decoyDisable(ListIterator<ThreeDs> decoyID);

    void newEffect(Dynamic effect);

    float[] getPlayerLocation();
    
    int[] getResolution();
    
    void execute(Runnable task);
    
    ArrayList<ThreeDs> getClouds();
    
    boolean isRunning();
}
