package dogfight_Z.dogLog.dao;

import java.sql.SQLException;
import java.util.List;

import dogfight_Z.dogLog.model.PlayerProfile;
import dogfight_Z.dogLog.utils.ArgsList;
import dogfight_Z.dogLog.utils.JDBCConnection;
import dogfight_Z.dogLog.utils.JDBCFactory;

public class PlayerProfileDAOImp implements PlayerProfileDAO {

    private static PlayerProfileDAOImp playerProfileDAO;

    private PlayerProfileDAOImp() {
    }
    
    static {
        playerProfileDAO = new PlayerProfileDAOImp();
    }
    
    public static PlayerProfileDAO getPlayerProfileDAO() {
        return playerProfileDAO;
    }
    
    @Override
    public int registPlayer(PlayerProfile p) {
        if(p == null) return 0;
        Integer uid = null;
        JDBCConnection conn = null;
        try {
            conn = JDBCFactory.takeJDBC();
                uid = (Integer) conn.updateForGeneratedKey(
                        "insert into `PlayerProfile` ("
                           + "`userID`, `userName`, `userPass`, `userNick`, "
                           + "`userBank`, `userExp`, `gameRecordShare`, `createTime`, "
                           + "`resolutionX`, `resolutionY`, `fontSize`, `fontIndx`"
                      + ") values ("
                           + "null, ?, ?, ?, 0, 0, ?, (datetime('now', 'localtime')), ?, ?, ?, ?"
                      + ");", 
                      p.getUserName(),
                      p.getUserPass(),
                      p.getUserNick(),
                      p.getGameRecordShare(),
                      p.getResolutionX(),
                      p.getResolutionY(),
                      p.getFontSize(),
                      p.getFontIndx()
                );
        } catch (SQLException e) {
        } finally {
            if(conn != null) JDBCFactory.returnBack(conn);
        }
        
        return uid == null? -1: uid;
    }

    @Override
    public int removePlayers(PlayerProfile p) {
        if(p == null) return 0;
        String sql = "delete from `PlayerProfile` where 1=1";
        
        StringBuilder conditionStr = new StringBuilder();
        ArgsList<Object> args = new ArgsList<>();
        if(p.getUserID() != null) {
            conditionStr.append(" and `userID` = ?");
            args.add(p.getUserID());
        }
        if(p.getUserName() != null) {
            conditionStr.append(" and `userName` = ?");
            args.add(p.getUserName());
        }
        if(p.getUserPass() != null) {
            conditionStr.append(" and `userPass` = ?");
            args.add(p.getUserPass());
        }
        if(p.getUserNick() != null) {
            conditionStr.append(" and `userNick` = ?");
            args.add(p.getUserNick());
        }
        if(p.getUserBank() != null) {
            conditionStr.append(" and `userBank` = ?");
            args.add(p.getUserBank());
        }
        int updated = 0;
        JDBCConnection conn = null;
        try {
            conn = JDBCFactory.takeJDBC();
            updated = conn.update(args, sql + conditionStr.toString());
        } catch (SQLException e) {
            return 0;
        } finally {
            if(conn != null) JDBCFactory.returnBack(conn);
        }
        return updated;
    }

    @Override
    public List<PlayerProfile> queryPlayerProfiles(PlayerProfile p) {
        if(p == null) return null;
        String sql = "select "
                       + "`userID`, `userName`, `userPass`, "
                       + "`userNick`, `userBank`, `userExp`, `gameRecordShare`, `createTime`, "
                       + "`resolutionX`, `resolutionY`, `fontSize`, `fontIndx` " +
                     "from `PlayerProfile` where 1=1";
        
        StringBuilder conditionStr = new StringBuilder();
        ArgsList<Object> args = new ArgsList<>();
        if(p.getUserID() != null) {
            conditionStr.append(" and `userID` = ?");
            args.add(p.getUserID());
        }
        if(p.getUserName() != null) {
            conditionStr.append(" and `userName` = ?");
            args.add(p.getUserName());
        }
        if(p.getUserPass() != null) {
            conditionStr.append(" and `userPass` = ?");
            args.add(p.getUserPass());
        }
        if(p.getUserNick() != null) {
            conditionStr.append(" and `userNick` = ?");
            args.add(p.getUserNick());
        }
        if(p.getUserBank() != null) {
            conditionStr.append(" and `userBank` = ?");
            args.add(p.getUserBank());
        }
        
        List<PlayerProfile> resultList = null;
        JDBCConnection conn = null;
        try {
            conn = JDBCFactory.takeJDBC();
            resultList = conn.queryEntities(PlayerProfile.class, args, sql + conditionStr.toString());
        } catch (SQLException | NoSuchMethodException e) {
            return null;
        } finally {
            if(conn != null) JDBCFactory.returnBack(conn);
        }
        
        return resultList;
    }

    @Override
    public int updatePlayerProfiles(PlayerProfile p) {
        if(p == null) return 0;
        String where = null;
        
        if(p.getUserID() != null) where = " where `userID` = ?";
        else if(p.getUserName() != null) where = " where `userName` = ?";
        else return 0;

        String sql = "update `PlayerProfile` set ";

        StringBuilder settingStr = new StringBuilder();
        ArgsList<Object> args = new ArgsList<>();
        boolean notFirst = false;
        if(p.getUserID() != null  &&  p.getUserName() != null) {
            settingStr.append("`userName` = ?");
            args.add(p.getUserName());
            notFirst = true;
        }
        if(p.getUserPass() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`userPass` = ?");
            args.add(p.getUserPass());
            notFirst = true;
        }
        if(p.getUserNick() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`userNick` = ?");
            args.add(p.getUserNick());
            notFirst = true;
        }
        if(p.getUserBank() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`userBank` = ?");
            args.add(p.getUserBank());
            notFirst = true;
        }
        if(p.getUserExp() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`userExp` = ?");
            args.add(p.getUserExp());
            notFirst = true;
        }
        if(p.getGameRecordShare() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`gameRecordShare` = ?");
            args.add(p.getGameRecordShare());
            notFirst = true;
        }
        if(p.getResolutionX() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`resolutionX` = ?");
            args.add(p.getResolutionX());
            notFirst = true;
        }
        if(p.getResolutionY() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`resolutionY` = ?");
            args.add(p.getResolutionY());
            notFirst = true;
        }
        if(p.getFontSize() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`fontSize` = ?");
            args.add(p.getFontSize());
            notFirst = true;
        }
        if(p.getFontIndx() != null) {
            if(notFirst) settingStr.append(',');
            settingStr.append("`fontIndx` = ?");
            args.add(p.getFontIndx());
            notFirst = true;
        }
        
        int updated = 0;
        JDBCConnection conn = null;
        try {
            conn = JDBCFactory.takeJDBC();
            updated = conn.update(args, sql + settingStr.toString() + where);
        } catch (SQLException e) {
            return 0;
        } finally {
            if(conn != null) JDBCFactory.returnBack(conn);
        }
        return updated;
    }
}

/*
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
*/