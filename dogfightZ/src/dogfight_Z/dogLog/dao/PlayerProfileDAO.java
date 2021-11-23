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
    
    public int registPlayer(PlayerProfile p) throws SQLException {
        
        JDBCConnection conn = JDBCFactory.takeJDBC();
        Integer uid = (Integer) conn.updateForGeneratedKey(
                "insert into `PlayerProfile` ("
                   + "`userID`, `userName`, `userPass`, "
                   + "`userNick`, `userBank`, `createTime`, "
                   + "`resolutionX`, `resolutionY`, `fontSize`"
              + ") values ("
                   + "null, ?, ?, ?, 0, (datetime('now', 'localtime')), ?, ?, ?"
              + ");", 
              p.getUserName(),
              p.getUserPass(),
              p.getUserNick(),
              p.getResolutionX(),
              p.getResolutionY(),
              p.getFontSize()
        );
        
        JDBCFactory.returnBack(conn);
        
        
        return uid == null? -1: uid;
    }
    
    public static PlayerProfileDAO getPlayerProfileDAO() {
        return playerProfileDAO;
    }
}
