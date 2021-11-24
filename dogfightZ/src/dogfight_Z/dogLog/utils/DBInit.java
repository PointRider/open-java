package dogfight_Z.dogLog.utils;

import java.sql.SQLException;

public class DBInit {
    
    static {
        try {
            Class.forName("dogfight_Z.dogLog.utils.JDBCFactory");
        } catch (ClassNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
    
    public static void main(String [] args) {
        initDB();
    }

    public static void initDB() {
        
        String [] ddl = new String[6];
        
        ddl[0] = "CREATE TABLE `PlayerProfile` (" +
                "  `userID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "  `userName` varchar(64) NOT NULL," +
                "  `userPass` varchar(64) NOT NULL," +
                "  `userNick` varchar(64)," +
                "  `userBank` decimal(12,2) DEFAULT 0.00," +
                "  `gameRecordShare` integer NOT NULL," +
                "  `createTime` datetime NOT NULL DEFAULT (datetime('now', 'localtime'))," +
                "  `resolutionX` integer DEFAULT 160," +
                "  `resolutionY` integer DEFAULT 83," +
                "  `fontSize` integer DEFAULT 10," +
                "  CONSTRAINT `uidKey` UNIQUE (`userID` COLLATE BINARY ASC)," +
                "  CONSTRAINT `unameKey` UNIQUE (`userName` COLLATE BINARY ASC)" +
                ")";
        
        ddl[1] = "CREATE UNIQUE INDEX `unameIdx`" +
                "ON `PlayerProfile` (" +
                "  `userName` COLLATE BINARY ASC" +
                ")";
        
        ddl[2] = "CREATE UNIQUE INDEX `uidIdx`" +
                "ON `PlayerProfile` (" +
                "  `userID` COLLATE BINARY ASC" +
                ")";
        
        ddl[3] = "CREATE TABLE `playerBGM` (" +
                "  `userID` INTEGER NOT NULL," +
                "  `index` integer NOT NULL," +
                "  `title` varchar(64)," +
                "  `filePathName` varchar(256) NOT NULL," +
                "  CONSTRAINT `fk_playerBGM_PlayerProfile_1` FOREIGN KEY (`userID`) REFERENCES `PlayerProfile` (`userID`) ON DELETE CASCADE" +
                ")";
        
        ddl[4] = "CREATE INDEX `uidIdxOnBGM`" +
                "ON `playerBGM` (" +
                "  `userID` COLLATE BINARY ASC" +
                ")";
        
        ddl[5] = "CREATE TABLE `playerGameRecord` (" +
                "  `userID` integer NOT NULL," +
                "  `gameTime` long," +
                "  `enemyCount` integer," +
                "  `friendCount` integer," +
                "  `enemyAvgDiff` double," +
                "  `friendAvgDiff` double," +
                "  `killed` integer," +
                "  `dead` integer," +
                "  `getBank` decimal(12,2)," +
                "  `gameVersion` varchar(32)," +
                "  `resolutionX` integer," +
                "  `resolutionY` integer," +
                "  `recDateTime` datetime DEFAULT (datetime('now', 'localtime'))," +
                "  CONSTRAINT `fk_playerGameRecord_PlayerProfile_1` FOREIGN KEY (`userID`) REFERENCES `PlayerProfile` (`userID`) ON DELETE CASCADE" +
                ")";
        
        JDBCConnection conn = JDBCFactory.takeJDBC();
        conn.start();
        try {
            for(String sql : ddl) {
                conn.update(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn.commit();
    }
}
