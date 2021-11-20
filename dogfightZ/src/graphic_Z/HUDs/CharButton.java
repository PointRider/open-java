package graphic_Z.HUDs;

import dogfight_Z.dogLog.view.menus.Operation;
import graphic_Z.utils.Common;

public class CharButton extends CharLabel implements Operable, Selectable {

    private CharLabel outerBoxSelected;
    private String    outerBoxSelectedText;
    private boolean   selected;
    private Operable  caller;
    
    public CharButton(
        char[][] frapsBuffer, 
        int[] scrResolution, 
        String Text, 
        int locationX, 
        int locationY,
        Operable caller
    ) { this(frapsBuffer, scrResolution, Text, locationX, locationY, Text.length(), caller); }
    
    public CharButton(
        char[][] frapsBuffer, 
        int[] scrResolution, 
        String Text, 
        int locationX, 
        int locationY, 
        int sizeX,
        Operable caller
    ) {
        super(
            frapsBuffer,
            (int)(Math.random() * 32767), 
            scrResolution, 
            Text, 
            sizeX > Text.length()? 
                    locationX + ((sizeX - Text.length()) >> 1)
                :
                    locationX, 
            locationY, 
            false
        );
        
        if(sizeX < Text.length()) sizeX = Text.length();
        
        outerBoxSelectedText = 
        " -" + Common.loopChar('-', sizeX) + "- \n" +
        "/ " + Common.loopChar(' ', sizeX) + " \\\n" +
        "> " + Common.loopChar(' ', sizeX) + " <\n" +
        "\\ " + Common.loopChar(' ', sizeX) + " /\n"+
        " -" + Common.loopChar('-', sizeX) + "- ";
        
        outerBoxSelected = new CharLabel(
            frapsBuffer,          layer,         scrResolution, 
            outerBoxSelectedText, locationX - 2, locationY - 2
        );
        selected         = false;
        this.caller      = caller;
    }
    
    @Override
    public void printNew() {
        this.printNew(selected);
        super.printNew();
    }
    
    public void printNew(boolean selected) {
        if(selected) outerBoxSelected.printNew();
        selected = false;
    }
    
    @Override
    public Operation call() {
        if(!selected) return null;
        return caller.call();
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
