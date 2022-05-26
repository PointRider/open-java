
CREATE TABLE `sqlite_sequence` (
  `name`,
  `seq`
);

CREATE TABLE `PlayerProfile` (
  `userID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `userName` varchar(64) NOT NULL,
  `userPass` varchar(64) NOT NULL,
  `userNick` varchar(64),
  `userBank` decimal(12,2) DEFAULT 0.00,
  `gameRecordShare` integer NOT NULL,
  `createTime` datetime NOT NULL DEFAULT (datetime('now', 'localtime')),
  `resolutionX` integer DEFAULT 160,
  `resolutionY` integer DEFAULT 83,
  `fontSize` integer DEFAULT 10,
  CONSTRAINT `uidKey` UNIQUE (`userID` COLLATE BINARY ASC),
  CONSTRAINT `unameKey` UNIQUE (`userName` COLLATE BINARY ASC)
);

CREATE UNIQUE INDEX `uidIdx` ON `PlayerProfile` (
  `userID` COLLATE BINARY ASC
);

CREATE UNIQUE INDEX `unameIdx` ON `PlayerProfile` (
  `userName` COLLATE BINARY ASC
);

CREATE TABLE `bgmLists` (
  `listID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `userID` INTEGER NOT NULL,
  `listName` varchar(64),
  CONSTRAINT `fk_bgmLists_PlayerProfile_1` FOREIGN KEY (`userID`) REFERENCES `PlayerProfile` (`userName`) ON DELETE CASCADE,
  CONSTRAINT `uniqListID` UNIQUE (`listID` COLLATE BINARY ASC) ON CONFLICT ABORT
);
CREATE UNIQUE INDEX `listIdOnBgmLists` ON `bgmLists` (
  `listID` COLLATE BINARY ASC
);

CREATE INDEX `userIdOnBgmLists` ON `bgmLists` (
  `userID` COLLATE BINARY ASC
);

CREATE TABLE `playerBGM` (
  `listID` INTEGER NOT NULL,
  `index` integer NOT NULL,
  `title` varchar(64),
  `filePathName` varchar(256) NOT NULL,
  CONSTRAINT `fk_playerBGM_bgmLists_1` FOREIGN KEY (`listID`) REFERENCES `bgmLists` (`listID`) ON DELETE CASCADE
);

CREATE INDEX `listIdOnBGM` ON `playerBGM` (
  `listID` COLLATE BINARY ASC
);

CREATE TABLE `playerBattleGroundConfig` (
  `userID` INTEGER NOT NULL,
  `configName` varchar(64) NOT NULL,
  `battleTime` integer NOT NULL,
  `bgmList` integer,
  `npcs` TEXT,
  CONSTRAINT `fk_playerBattleGroundConfig_bgmLists_1` FOREIGN KEY (`userID`) REFERENCES `bgmLists` (`listID`) ON DELETE SET NULL
);
CREATE INDEX `userIdOnPlayerBattleGroundConfig` ON `playerBattleGroundConfig` (
  `userID` COLLATE BINARY ASC
);

CREATE TABLE `playerGameRecord` (
  `userID` integer NOT NULL,
  `gameTime` long,
  `enemyCount` integer,
  `friendCount` integer,
  `enemyAvgDiff` double,
  `friendAvgDiff` double,
  `killed` integer,
  `dead` integer,
  `getBank` decimal(12,2),
  `gameVersion` varchar(32),
  `resolutionX` integer,
  `resolutionY` integer,
  `recDateTime` datetime DEFAULT (datetime('now', 'localtime')),
  CONSTRAINT `fk_playerGameRecord_PlayerProfile_1` FOREIGN KEY (`userID`) REFERENCES `PlayerProfile` (`userID`) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE INDEX `userIdOnPlayerGameRecord` ON `playerGameRecord` (
  `userID` COLLATE BINARY ASC
);
