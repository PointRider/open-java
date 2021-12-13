package dogfight_Z.dogLog.service;

import dogfight_Z.dogLog.model.PlayerProfile;

public interface PlayerProfileService {
    int regist(PlayerProfile p);
    PlayerProfile login(PlayerProfile p);
    boolean editProfile(PlayerProfile newProfileInfo);
}
