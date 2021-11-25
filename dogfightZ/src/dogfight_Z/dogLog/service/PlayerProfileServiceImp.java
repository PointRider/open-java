package dogfight_Z.dogLog.service;

import dogfight_Z.dogLog.dao.PlayerProfileDAOImp;
import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.view.DogLog;

public class PlayerProfileServiceImp implements PlayerProfileService {

    private static PlayerProfileServiceImp playerProfileService;
    
    static {
        playerProfileService = new PlayerProfileServiceImp();
    }
    
    private PlayerProfileServiceImp() {
    }

    @Override
    public int regist(PlayerProfile p) {
        p.setUserPass(DogLog.getPasswordencoder().encrypt(p.getUserPass()));
        return PlayerProfileDAOImp.getPlayerProfileDAO().registPlayer(p);
    }

    @Override
    public PlayerProfile login(PlayerProfile p) {
        p.setUserPass(DogLog.getPasswordencoder().encrypt(p.getUserPass()));
        return PlayerProfileDAOImp.getPlayerProfileDAO().login(p);
    }
    
    public static PlayerProfileServiceImp getPlayerProfileService() {
        return playerProfileService;
    }
}
