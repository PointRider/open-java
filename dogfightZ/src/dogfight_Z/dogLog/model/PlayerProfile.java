package dogfight_Z.dogLog.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PlayerProfile extends SimplePrimaryKey {
    //-----------[profile]------------
    private int           userID;
    private String        userName;
    private String        userPass;
    private String        userNick;
    private BigDecimal    userBank;
    private LocalDateTime createTime;
    //-----------[config]-------------
    private int           resolutionX;
    private int           resolutionY;
    private int           fontSize;
    //--------------------------------
    
    public PlayerProfile() {
    }

    public PlayerProfile(int userID, String userName, String userPass, String userNick, BigDecimal userBank,
            LocalDateTime createTime, int resolutionX, int resolutionY, int fontSize) {
        super();
        this.userID = userID;
        this.userName = userName;
        this.userPass = userPass;
        this.userNick = userNick;
        this.userBank = userBank;
        this.createTime = createTime;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.fontSize = fontSize;
    }

    @Override
    public Object getPrimaryKey() {
        return getUserID();
    }
    
    public int getUserID() {
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

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
