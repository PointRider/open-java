package dogfight_Z.dogLog.service;

import dogfight_Z.dogLog.dao.PlayerProfileDAO;
import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.view.DogLog;

public class PlayerProfileService {

    private static PlayerProfileService playerProfileService;
    
    static {
        playerProfileService = new PlayerProfileService();
    }
    
    private PlayerProfileService() {
    }

    public int regist(PlayerProfile p) {
        p.setUserPass(DogLog.getPasswordencoder().encrypt(p.getUserPass()));
        return PlayerProfileDAO.getPlayerProfileDAO().registPlayer(p);
    }
    
    public PlayerProfile login(PlayerProfile p) {
        p.setUserPass(DogLog.getPasswordencoder().encrypt(p.getUserPass()));
        return PlayerProfileDAO.getPlayerProfileDAO().login(p);
    }    
    public static PlayerProfileService getPlayerProfileService() {
        return playerProfileService;
    }
}
