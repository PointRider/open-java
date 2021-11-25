package graphic_Z.HUDs;

public interface Widget extends KeyInputGetter, Selectable, Operable {
    void setText(String string);
    void resetScreenBuffer(char[][] screenBuffer);
}
