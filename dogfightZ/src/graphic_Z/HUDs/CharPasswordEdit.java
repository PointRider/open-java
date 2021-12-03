package graphic_Z.HUDs;

public class CharPasswordEdit extends CharSingleLineTextEdit {

    public CharPasswordEdit(char[][] frapsBuffer, int[] scrResolution, int locationX, int locationY, int sizeX) {
        super(frapsBuffer, scrResolution, locationX, locationY, sizeX);
    }
    
    public CharPasswordEdit(char[][] frapsBuffer, int[] scrResolution, int locationX, int locationY, int sizeX, String placeholder) {
        super(frapsBuffer, scrResolution, locationX, locationY, sizeX, placeholder);
    }
    
    @Override
    public void printNew() {
        super.printNew(super.selected);
        if(placeholder == null  ||  !text.isEmpty()) super.printChar('*');
        else super.printNew(placeholder);
    }
}
