package dogfight_Z.dogLog.dao;

import java.sql.SQLException;
import java.util.Scanner;

import dogfight_Z.GameRun;
import dogfight_Z.dogLog.utils.JDBCConnection;
import dogfight_Z.dogLog.utils.JDBCFactory;

public class DBInit {

    static final String [] ddl = {
        "CREATE TABLE `PlayerProfile` (" +
        "  `userID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
        "  `userName` varchar(64) NOT NULL," +
        "  `userPass` varchar(64) NOT NULL," +
        "  `userNick` varchar(64)," +
        "  `userBank` decimal(12,2) DEFAULT 0.00," +
        "  `userExp` decimal(12,2) DEFAULT 0.00," +
        "  `gameRecordShare` integer NOT NULL," +
        "  `createTime` datetime NOT NULL DEFAULT (datetime('now', 'localtime'))," +
        "  `resolutionX` integer DEFAULT 192," +
        "  `resolutionY` integer DEFAULT 108," +
        "  `fontSize` integer DEFAULT 8," +
        "  `fontIndx` integer DEFAULT 1," +
        "  CONSTRAINT `uidKey` UNIQUE (`userID` COLLATE BINARY ASC)," +
        "  CONSTRAINT `unameKey` UNIQUE (`userName` COLLATE BINARY ASC)" +
        ")",
        
        "CREATE UNIQUE INDEX `unameIdx` ON `PlayerProfile` (" +
        "  `userName` COLLATE BINARY ASC" +
        ")",
        
        "CREATE UNIQUE INDEX `uidIdx` ON `PlayerProfile` (" +
        "  `userID` COLLATE BINARY ASC" +
        ")",
        //-------------------------------------------------------------------------------------
        "CREATE TABLE `bgmLists` (" + 
        "  `listID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + 
        "  `userID` INTEGER NOT NULL," + 
        "  `listName` varchar(64)," + 
        "  CONSTRAINT `fk_bgmLists_PlayerProfile_1` FOREIGN KEY (`userID`) REFERENCES `PlayerProfile` (`userName`) ON DELETE CASCADE," + 
        "  CONSTRAINT `uniqListID` UNIQUE (`listID` COLLATE BINARY ASC) ON CONFLICT ABORT" + 
        ")",
        
        "CREATE UNIQUE INDEX `listIdOnBgmLists` ON `bgmLists` (" + 
        "  `listID` COLLATE BINARY ASC" + 
        ")",
        
        "CREATE INDEX `userIdOnBgmLists` ON `bgmLists` (" + 
        "  `userID` COLLATE BINARY ASC" + 
        ")",
        //-------------------------------------------------------------------------------------
        "CREATE TABLE `playerBGM` (" + 
        "  `listID` INTEGER NOT NULL," + 
        "  `index` integer NOT NULL," + 
        "  `title` varchar(64)," + 
        "  `filePathName` varchar(256) NOT NULL," + 
        "  CONSTRAINT `fk_playerBGM_bgmLists_1` FOREIGN KEY (`listID`) REFERENCES `bgmLists` (`listID`) ON DELETE CASCADE" + 
        ")",
        
         "CREATE INDEX `listIdOnBGM` ON `playerBGM` (" + 
         "  `listID` COLLATE BINARY ASC" + 
         ")",
        //-------------------------------------------------------------------------------------
        "CREATE TABLE `playerBattleGroundConfig` (" + 
        "  `userID` INTEGER NOT NULL," + 
        "  `configName` varchar(64) NOT NULL," + 
        "  `battleTime` integer NOT NULL," + 
        "  `bgmList` integer," + 
        "  `npcs` TEXT," + 
        "  CONSTRAINT `fk_playerBattleGroundConfig_bgmLists_1` FOREIGN KEY (`userID`) REFERENCES `bgmLists` (`listID`) ON DELETE SET NULL" + 
        ");" + 
        
        "CREATE INDEX `userIdOnPlayerBattleGroundConfig` ON `playerBattleGroundConfig` (" + 
        "  `userID` COLLATE BINARY ASC" + 
        ");",
        //-------------------------------------------------------------------------------------
        "CREATE TABLE `playerGameRecord` (" +
        "  `userID` integer NOT NULL," +
        "  `gameTime` long," +
        "  `enemyCount` integer," +
        "  `friendCount` integer," +
        "  `enemyAvgDiff` double," +
        "  `friendAvgDiff` double," +
        "  `killed` integer," +
        "  `dead` integer," +
        "  `getBank` decimal(12,2)," +
        "  `getExp` decimal(12,2)," +
        "  `gameVersion` varchar(32)," +
        "  `resolutionX` integer," +
        "  `resolutionY` integer," +
        "  `recDateTime` datetime DEFAULT (datetime('now', 'localtime'))," +
        "  CONSTRAINT `fk_playerGameRecord_PlayerProfile_1` FOREIGN KEY (`userID`) REFERENCES `PlayerProfile` (`userID`) ON DELETE CASCADE" +
        ")",
        
        "CREATE INDEX `userIdOnPlayerGameRecord` ON `playerGameRecord` (" + 
        "  `userID` COLLATE BINARY ASC" + 
        ")"
    };
    
    //static {}
    
    public static void main(String [] args) {
        if(!GameRun.fileExists("dogfightZ.db")) {
            initDB();
            System.out.println("OK.");
            return;
        }
        
        Scanner input = new Scanner(System.in);
        System.err.print("数据库已存在，是否重置？(y/N)> ");
        if(input.next().toLowerCase().contentEquals("y")) {
            GameRun.deleteFile("dogfightZ.db");
            initDB();
        }
        input.close();
        System.out.println("OK.");
    }

    public static void initDB() {
        try {
            JDBCConnection conn = JDBCFactory.takeJDBC();
            conn.start();
                for(String sql : ddl) {
                    conn.update(sql);
                }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
