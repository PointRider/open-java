package dogfight_Z.Menu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteTest {

    public static void main(String[] args) {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection sqliteConnection = null;
        try {
            sqliteConnection = DriverManager.getConnection("jdbc:sqlite:dogfightZ.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            sqliteConnection.setAutoCommit(false);
            //PreparedStatement setupDB =

            sqliteConnection.prepareStatement(
                    "CREATE TABLE `PlayerProfile` (" +
                            "  `userID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                            "  `userName` varchar(32) NOT NULL," +
                            "  `userPass` varchar(32) NOT NULL," +
                            "  `userNick` varchar(64)," +
                            "  `createTime` datetime NOT NULL DEFAULT (datetime('now', 'localtime'))," +
                            "  `resolutionX` integer DEFAULT 160," +
                            "  `resolutionY` integer DEFAULT 83," +
                            "  `fontSize` integer DEFAULT 10," +
                            "  CONSTRAINT \"uidKey\" UNIQUE (\"userID\" COLLATE BINARY ASC)," +
                            "  CONSTRAINT \"unameKey\" UNIQUE (\"userName\" COLLATE BINARY ASC)" +
                            ")"
            ).execute();

            sqliteConnection.prepareStatement(
                    "CREATE UNIQUE INDEX \"unameIdx\"\n" +
                            "ON \"PlayerProfile\" (\n" +
                            "  \"userName\" COLLATE BINARY ASC\n" +
                            ")"
            ).execute();

            sqliteConnection.prepareStatement(
                    "CREATE UNIQUE INDEX \"uidIdx\"\n" +
                            "ON \"PlayerProfile\" (\n" +
                            "  \"userID\" COLLATE BINARY ASC\n" +
                            ")"
            ).execute();

            sqliteConnection.prepareStatement(
                    "CREATE TABLE \"playerBGM\" (\n" +
                            "  \"userID\" INTEGER NOT NULL,\n" +
                            "  \"index\" integer NOT NULL,\n" +
                            "  \"title\" varchar(64),\n" +
                            "  \"filePathName\" varchar(256) NOT NULL,\n" +
                            "  CONSTRAINT \"fk_playerBGM_PlayerProfile_1\" FOREIGN KEY (\"userID\") REFERENCES \"PlayerProfile\" (\"userID\") ON DELETE CASCADE\n" +
                            ")"
            ).execute();

            sqliteConnection.prepareStatement(
                    "CREATE INDEX `uidIdxOnBGM`" +
                            "ON `playerBGM` (" +
                            "  `userID` COLLATE BINARY ASC" +
                            ")"
            ).execute();

            sqliteConnection.prepareStatement(
                    "CREATE TABLE `playerGameRecord` (" +
                            "  `userID` integer NOT NULL," +
                            "  `gameTime` long," +
                            "  `enemyCount` integer," +
                            "  `friendCount` integer," +
                            "  `enemyAvgDiff` double," +
                            "  `friendAvgDiff` double," +
                            "  `killed` integer," +
                            "  `dead` integer," +
                            "  `gameVersion` varchar(32)," +
                            "  `resolutionX` integer," +
                            "  `resolutionY` integer," +
                            "  `recDateTime` datetime DEFAULT (datetime('now', 'localtime'))," +
                            "  CONSTRAINT `fk_playerGameRecord_PlayerProfile_1` FOREIGN KEY (`userID`) REFERENCES `PlayerProfile` (`userID`) ON DELETE CASCADE" +
                            ")"
            ).execute();

            sqliteConnection.prepareStatement(
                    "CREATE INDEX `uidIdxOnRec`" +
                            "ON `playerGameRecord` (" +
                            "  `userID` COLLATE BINARY ASC" +
                            ")"
            ).execute();

            sqliteConnection.commit();
            sqliteConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
