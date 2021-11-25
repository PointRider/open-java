package dogfight_Z.dogLog.dao;

import dogfight_Z.dogLog.model.PlayerProfile;

public interface PlayerProfileDAO {
    int registPlayer(PlayerProfile p);
    PlayerProfile login(PlayerProfile p);
}
