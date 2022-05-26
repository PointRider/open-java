package dogfight_Z.dogLog.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlayerGameRecord {
    
    private Integer       userID;
    private Long          gameTime;
    private Integer       enemyCount;
    private Integer       friendCount;
    private Double        enemyAvgDiff;
    private Double        friendAvgDiff;
    private Integer       killed;
    private Integer       dead;
    private BigDecimal    getBank;
    private String        gameVersion;
    private Integer       resolutionX;
    private Integer       resolutionY;
    private LocalDateTime recDateTime;
    
    public PlayerGameRecord() {
        // TODO 自动生成的构造函数存根
    }

    public PlayerGameRecord(int userID, long gameTime, int enemyCount, int friendCount, double enemyAvgDiff,
            double friendAvgDiff, int killed, int dead, BigDecimal getBank, String gameVersion, int resolutionX,
            int resolutionY, LocalDateTime recDateTime) {
        super();
        this.userID = userID;
        this.gameTime = gameTime;
        this.enemyCount = enemyCount;
        this.friendCount = friendCount;
        this.enemyAvgDiff = enemyAvgDiff;
        this.friendAvgDiff = friendAvgDiff;
        this.killed = killed;
        this.dead = dead;
        this.getBank = getBank;
        this.gameVersion = gameVersion;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.recDateTime = recDateTime;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public void setEnemyCount(int enemyCount) {
        this.enemyCount = enemyCount;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }

    public double getEnemyAvgDiff() {
        return enemyAvgDiff;
    }

    public void setEnemyAvgDiff(double enemyAvgDiff) {
        this.enemyAvgDiff = enemyAvgDiff;
    }

    public double getFriendAvgDiff() {
        return friendAvgDiff;
    }

    public void setFriendAvgDiff(double friendAvgDiff) {
        this.friendAvgDiff = friendAvgDiff;
    }

    public int getKilled() {
        return killed;
    }

    public void setKilled(int killed) {
        this.killed = killed;
    }

    public int getDead() {
        return dead;
    }

    public void setDead(int dead) {
        this.dead = dead;
    }

    public BigDecimal getGetBank() {
        return getBank;
    }

    public void setGetBank(BigDecimal getBank) {
        this.getBank = getBank;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public int getResolutionX() {
        return resolutionX;
    }

    public void setResolutionX(int resolutionX) {
        this.resolutionX = resolutionX;
    }

    public int getResolutionY() {
        return resolutionY;
    }

    public void setResolutionY(int resolutionY) {
        this.resolutionY = resolutionY;
    }

    public LocalDateTime getRecDateTime() {
        return recDateTime;
    }

    public void setRecDateTime(LocalDateTime recDateTime) {
        this.recDateTime = recDateTime;
    }
}
