package dogfight_Z.dogLog.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlayerProfile extends SimplePrimaryKey {
    //-----------[profile]------------
    private Integer       userID;
    private String        userName;
    private String        userPass;
    private String        userNick;
    private BigDecimal    userBank;
    private Integer       gameRecordShare;
    private LocalDateTime createTime;
    //-----------[config]-------------
    private Integer       resolutionX;
    private Integer       resolutionY;
    private Integer       fontSize;
    //--------------------------------
    
    public PlayerProfile() {
    }
    
    public PlayerProfile(int userID, String userName, String userPass, String userNick, BigDecimal userBank, int gameRecordShared,
            LocalDateTime createTime, int resolutionX, int resolutionY, int fontSize) {
        super();
        this.userID = userID;
        this.userName = userName;
        this.userPass = userPass;
        this.userNick = userNick;
        this.userBank = userBank;
        this.gameRecordShare = gameRecordShared;
        this.createTime = createTime;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.fontSize = fontSize;
    }

    public PlayerProfile(ResultSet rs) throws SQLException  {
        super();
        this.userID          = rs.getInt("userID");
        this.userName        = rs.getString("userName");
        this.userPass        = rs.getString("userPass");
        this.userNick        = rs.getString("userNick");
        this.userBank        = rs.getBigDecimal("userBank");
        this.gameRecordShare = rs.getInt("gameRecordShare");
        this.createTime      = LocalDateTime.parse(rs.getString("createTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.resolutionX     = rs.getInt("resolutionX");
        this.resolutionY     = rs.getInt("resolutionY");
        this.fontSize        = rs.getInt("fontSize");
    }

    @Override
    public Object getPrimaryKey() {
        return getUserID();
    }
    
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public BigDecimal getUserBank() {
        return userBank;
    }

    public void setUserBank(BigDecimal userBank) {
        this.userBank = userBank;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getResolutionX() {
        return resolutionX;
    }

    public void setResolutionX(int resolutionX) {
        this.resolutionX = resolutionX;
    }

    public Integer getResolutionY() {
        return resolutionY;
    }

    public void setResolutionY(int resolutionY) {
        this.resolutionY = resolutionY;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Integer getGameRecordShare() {
        return gameRecordShare;
    }

    public void setGameRecordShare(int gameRecordShare) {
        this.gameRecordShare = gameRecordShare;
    }

    @Override
    public String toString() {
        return "PlayerProfile [userID=" + userID + ", userName=" + userName + ", userPass=" + userPass + ", userNick="
                + userNick + ", userBank=" + userBank + ", gameRecordShared=" + gameRecordShare + ", createTime="
                + createTime + ", resolutionX=" + resolutionX + ", resolutionY=" + resolutionY + ", fontSize="
                + fontSize + "]";
    }
    
}
