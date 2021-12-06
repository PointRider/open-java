package dogfight_Z;

import java.util.ArrayList;
import java.util.ListIterator;

import dogfight_Z.Ammo.Decoy;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;

public class GameManager implements GameManagement {

    private final Game game;
    
    public GameManager(Game game) {
        this.game = game;
    }
    
    @Override
    public final boolean isAircraftOfPlayer(Aircraft a) {
        return a == game.getMyJet();
    }
    
    @Override
    public final void addKillTip(Aircraft killer, Aircraft deader, String WeaponName) {
        game.addKillTip(killer, deader, WeaponName);
    }
    
    @Override
    public final int getRespawnTime() {
        return game.getRespawnTime();
    }
    
    @Override
    public void colorFlash(
        int R_Fore, int G_Fore, int B_Fore, 
        int R_Back, int G_Back, int B_Back, 
        int time
    ) { game.colorFlash(R_Fore, G_Fore, B_Fore, R_Back, G_Back, B_Back, time); }
    
    @Override
    public void addGBlack(float v) {
        game.addGBlack(v);
    }
    
    @Override
    public final float getWeaponMaxSearchingRange() {
        return game.getWeaponMaxSearchingRange();
    }
    
    @Override
    public final void fireAmmo(Dynamic ammo) {
        game.fireAmmo(ammo);
    }

    @Override
    public float[] getPlayerCameraLocation() {
        return game.getPlayerCameraLocation();
    }
    
    @Override
    public void throwDecoy(Decoy decoy) {
        game.throwDecoy(decoy);
    }

    @Override
    public final void decoyDisable(ListIterator<ThreeDs> decoyID) {
        game.decoyDisable(decoyID);
    }

    @Override
    public final float[] getPlayerLocation() {
        return game.getPlayerLocation();
    }
    
    @Override
    public final void newEffect(Dynamic effect) {
        game.newEffect(effect);
    }

    @Override
    public int[] getResolution() {
        return game.getResolution();
    }

    @Override
    public void execute(Runnable task) {
        game.execute(task);
    }

    @Override
    public ArrayList<ThreeDs> getClouds() {
        return game.getClouds();
    }
}
