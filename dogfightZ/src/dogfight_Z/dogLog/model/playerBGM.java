package dogfight_Z.dogLog.model;

public class playerBGM {
    
    private Integer userID;
    private Integer index;
    private String  title;
    private String  filePathName;
    
    public playerBGM() {
        // TODO 自动生成的构造函数存根
    }

    public playerBGM(int userID, int index, String title, String filePathName) {
        super();
        this.userID = userID;
        this.index = index;
        this.title = title;
        this.filePathName = filePathName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePathName() {
        return filePathName;
    }

    public void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }

}
