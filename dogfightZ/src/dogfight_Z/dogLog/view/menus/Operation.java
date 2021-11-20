package dogfight_Z.dogLog.view.menus;

import java.awt.Color;

public class Operation {
    private boolean goBack;
    private DogMenu getInto;
    private Color   flashColor;
    private Color   doubleFlashColor;
    
    public Operation() {
        super();
        this.goBack           = false;
        this.getInto          = null;
        this.flashColor       = null;
        this.doubleFlashColor = null;
    }
    
    public Operation(boolean goBack, DogMenu getInto, Color flashColor, Color doubleFlashColor) {
        super();
        this.goBack           = goBack;
        this.getInto          = getInto;
        this.flashColor       = flashColor;
        this.doubleFlashColor = doubleFlashColor;
    }

    public boolean isGoBack() {
        return goBack;
    }

    public void setGoBack(boolean goBack) {
        this.goBack = goBack;
    }

    public DogMenu getGetInto() {
        return getInto;
    }

    public void setGetInto(DogMenu getInto) {
        this.getInto = getInto;
    }

    public Color getFlashColor() {
        return flashColor;
    }

    public void setFlashColor(Color flashColor) {
        this.flashColor = flashColor;
    }

    public Color getDoubleFlashColor() {
        return doubleFlashColor;
    }

    public void setDoubleFlashColor(Color doubleFlashColor) {
        this.doubleFlashColor = doubleFlashColor;
    }
}
