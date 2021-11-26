package dogfight_Z.dogLog.service;

import java.util.List;

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
        if(p.getUserName() == null  ||  p.getUserPass() == null) return null;
        
        p.setUserPass(DogLog.getPasswordencoder().encrypt(p.getUserPass()));
        List<PlayerProfile> players = null;
        players = PlayerProfileDAOImp.getPlayerProfileDAO().queryPlayerProfiles(p);
        if(players == null  ||  players.size() == 0) return null;
        
        return players.get(0);
    }
    
    public static PlayerProfileServiceImp getPlayerProfileService() {
        return playerProfileService;
    }
}
