package graphic_Z.HUDs;

public class CharPasswordEdit extends CharSingleLineTextEdit {

    public CharPasswordEdit(char[][] frapsBuffer, int[] scrResolution, int locationX, int locationY, int sizeX) {
        super(frapsBuffer, scrResolution, locationX, locationY, sizeX);
        // TODO 自动生成的构造函数存根
    }
    
    @Override
    public void printNew() {
        super.printNew(super.selected);
        super.printChar('*');
    }
}
