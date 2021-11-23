package dogfight_Z.dogLog.service;

import java.sql.SQLException;

import dogfight_Z.dogLog.dao.PlayerProfileDAO;
import dogfight_Z.dogLog.model.PlayerProfile;

public class PlayerProfileService {

    private static PlayerProfileService playerProfileService;
    
    static {
        playerProfileService = new PlayerProfileService();
    }
    
    private PlayerProfileService() {
    }

    public int regist(PlayerProfile p) throws SQLException {
        return PlayerProfileDAO.getPlayerProfileDAO().registPlayer(p);
    }
    
    public static PlayerProfileService getPlayerProfileService() {
        return playerProfileService;
    }
}
