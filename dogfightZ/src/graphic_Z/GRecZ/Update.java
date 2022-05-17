package graphic_Z.GRecZ;

import java.io.Serializable;

public class Update implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9042029515299889702L;
    short index;
    byte  pixel;
    
    public Update(short index, byte pixel) {
        this.index = index;
        this.pixel = pixel;
    }

}
