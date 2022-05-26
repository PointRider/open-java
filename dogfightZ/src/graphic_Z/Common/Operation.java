package graphic_Z.Common;

import java.awt.Color;

import dogfight_Z.dogLog.view.DogMenu;

public class Operation {
    private boolean  goBack;
    private DogMenu  getInto;
    private Color    flashColor;
    private Color    doubleFlashColor;
    private Object   returnValue;
    private Runnable callBack;
    
    public final static Operation EXIT = new Operation(true, null, null, null, null, null);
    
    public Operation() {
        super();
        this.goBack           = false;
        this.getInto          = null;
        this.flashColor       = null;
        this.doubleFlashColor = null;
        this.returnValue      = null;
        this.callBack         = null;
    }
    
    public Operation(boolean goBack, DogMenu getInto, Color flashColor, Color doubleFlashColor, Object returnValue, Runnable callBack) {
        super();
        this.goBack           = goBack;
        this.getInto          = getInto;
        this.flashColor       = flashColor;
        this.doubleFlashColor = doubleFlashColor;
        this.returnValue      = returnValue;
        this.callBack         = callBack;
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

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Runnable getCallBack() {
        return callBack;
    }

    public void setCallBack(Runnable callBack) {
        this.callBack = callBack;
    }
    
    public static Operation getIntoMenu(DogMenu m) {
        return new Operation(false, m, null, null, null, null);
    }
}
