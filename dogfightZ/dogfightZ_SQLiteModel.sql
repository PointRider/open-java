CREATE TABLE "PlayerProfile" (
  "userID" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "userName" varchar(32) NOT NULL,
  "userPass" varchar(32) NOT NULL,
  "userNick" varchar(64),
  "createTime" datetime NOT NULL DEFAULT datetime('now', 'localtime'),
  "resolutionX" integer DEFAULT 160,
  "resolutionY" integer DEFAULT 83,
  "fontSize" integer DEFAULT 10,
  CONSTRAINT "uidKey" UNIQUE ("userID" COLLATE BINARY ASC),
  CONSTRAINT "unameKey" UNIQUE ("userName" COLLATE BINARY ASC)
);
CREATE UNIQUE INDEX "unameIdx"
ON "PlayerProfile" (
  "userName" COLLATE BINARY ASC
);
CREATE UNIQUE INDEX "uidIdx"
ON "PlayerProfile" (
  "userID" COLLATE BINARY ASC
);

CREATE TABLE "playerBGM" (
  "userID" INTEGER NOT NULL,
  "index" integer NOT NULL,
  "title" varchar(64),
  "filePathName" varchar(256) NOT NULL,
  CONSTRAINT "fk_playerBGM_PlayerProfile_1" FOREIGN KEY ("userID") REFERENCES "PlayerProfile" ("userID") ON DELETE CASCADE
);
CREATE INDEX "uidIdxOnBGM"
ON "playerBGM" (
  "userID" COLLATE BINARY ASC
);

CREATE TABLE "playerGameRecord" (
  "userID" integer NOT NULL,
  "gameTime" long,
  "enemyCount" integer,
  "friendCount" integer,
  "enemyAvgDiff" double,
  "friendAvgDiff" double,
  "killed" integer,
  "dead" integer,
  "gameVersion" varchar(32),
  "resolutionX" integer,
  "resolutionY" integer,
  "recDateTime" datetime DEFAULT datetime('now', 'localtime'),
  CONSTRAINT "fk_playerGameRecord_PlayerProfile_1" FOREIGN KEY ("userID") REFERENCES "PlayerProfile" ("userID") ON DELETE CASCADE
);
CREATE INDEX "uidIdxOnRec"
ON "playerGameRecord" (
  "userID" COLLATE BINARY ASC
);
