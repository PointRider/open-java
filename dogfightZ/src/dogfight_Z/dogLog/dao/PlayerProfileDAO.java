package dogfight_Z.dogLog.dao;

import java.util.List;

import dogfight_Z.dogLog.model.PlayerProfile;

public interface PlayerProfileDAO {
    int registPlayer(PlayerProfile p);
    int removePlayers(PlayerProfile p);
    List<PlayerProfile> queryPlayerProfiles(PlayerProfile p);
    int updatePlayerProfiles(PlayerProfile p);
}
