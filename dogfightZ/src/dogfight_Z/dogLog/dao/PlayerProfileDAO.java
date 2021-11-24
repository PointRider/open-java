package dogfight_Z.dogLog.dao;

import java.sql.SQLException;

import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.utils.JDBCConnection;
import dogfight_Z.dogLog.utils.JDBCFactory;

public class PlayerProfileDAO {

    private static PlayerProfileDAO playerProfileDAO;
    
    static {
        playerProfileDAO = new PlayerProfileDAO();
    }
    
    private PlayerProfileDAO() {
        // TODO 自动生成的构造函数存根
    }
    
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
