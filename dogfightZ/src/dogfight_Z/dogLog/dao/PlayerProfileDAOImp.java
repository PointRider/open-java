package dogfight_Z.dogLog.dao;

import java.sql.SQLException;

import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.utils.JDBCConnection;
import dogfight_Z.dogLog.utils.JDBCFactory;

public class PlayerProfileDAOImp implements PlayerProfileDAO {

    private static PlayerProfileDAOImp playerProfileDAO;
    
    static {
        playerProfileDAO = new PlayerProfileDAOImp();
    }
    
    private PlayerProfileDAOImp() {
    }
    
    @Override
    public int registPlayer(PlayerProfile p) {
        Integer uid = null;
        JDBCConnection conn = null;
        try {
            conn = JDBCFactory.takeJDBC();
                uid = (Integer) conn.updateForGeneratedKey(
                        "insert into `PlayerProfile` ("
                           + "`userID`, `userName`, `userPass`, "
                           + "`userNick`, `userBank`, `gameRecordShare`, `createTime`, "
                           + "`resolutionX`, `resolutionY`, `fontSize`"
                      + ") values ("
                           + "null, ?, ?, ?, 0, ?, (datetime('now', 'localtime')), ?, ?, ?"
                      + ");", 
                      p.getUserName(),
                      p.getUserPass(),
                      p.getUserNick(),
                      p.getGameRecordShare(),
                      p.getResolutionX(),
                      p.getResolutionY(),
                      p.getFontSize()
                );
        } catch (SQLException e) {
        } finally {
            if(conn != null) JDBCFactory.returnBack(conn);
        }
        
        return uid == null? -1: uid;
    }

    @Override
    public PlayerProfile login(PlayerProfile p) {

        PlayerProfile result = null;
        JDBCConnection conn  = null;
        try {
            conn = JDBCFactory.takeJDBC();
            result = conn.queryEntity(
                    PlayerProfile.class, 
                    "select "
                      + "`userID`, `userName`, `userPass`, "
                      + "`userNick`, `userBank`, `gameRecordShare`, `createTime`, "
                      + "`resolutionX`, `resolutionY`, `fontSize` " +
                    "from `PlayerProfile` where `userName` = ? and `userPass` = ?", 
                    p.getUserName(), p.getUserPass()
            );
            JDBCFactory.returnBack(conn);
            if(result != null) result.setUserPass(null);
        } catch (NoSuchMethodException | SQLException e) {
        } finally {
            if(conn != null) JDBCFactory.returnBack(conn);
        }
        
        return result;
    }
    
    public static PlayerProfileDAO getPlayerProfileDAO() {
        return playerProfileDAO;
    }
}
