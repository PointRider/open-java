package graphic_Z.GRecZ;

public class Pixel {

    byte repeat;
    byte pixel;
    
    public Pixel(int repeat, int pixel) {
        this.repeat = Frame.storeByte(repeat);
        this.pixel  = Frame.storeByte(pixel);
    }

    public final int getRepeat() {
        return Frame.loadByte(repeat);
    }

    public final void setRepeat(int repeat) {
        this.repeat = Frame.storeByte(repeat);
    }

    public final int getPixel() {
        return Frame.loadByte(pixel);
    }

    public final void setPixel(int pixel) {
        this.pixel  = Frame.storeByte(pixel);
    }
}
