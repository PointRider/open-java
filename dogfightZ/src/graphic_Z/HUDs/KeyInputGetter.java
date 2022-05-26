package graphic_Z.HUDs;

public interface KeyInputGetter extends TwoDs {
    boolean getInput(int keyChar);
    boolean getControl(int keyCode);
}
