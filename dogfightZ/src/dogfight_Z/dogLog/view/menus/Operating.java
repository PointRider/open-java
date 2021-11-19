package dogfight_Z.dogLog.view.menus;

import java.awt.Color;

public class Operating {
    private boolean goBack;
    private DogMenu getInto;
    private Color   flashColor;
    
    public Operating() {
        super();
        this.goBack     = false;
        this.getInto    = null;
        this.flashColor = null;
    }
    
    public Operating(boolean goBack, DogMenu getInto, Color flashColor) {
        super();
        this.goBack     = goBack;
        this.getInto    = getInto;
        this.flashColor = flashColor;
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
}
