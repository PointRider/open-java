package graphic_Z.demo;

import graphic_Z.Objects.CharMessObject;

public class CubeA extends CharMessObject {

    public CubeA(String ModelFile) {
        super(ModelFile, 1.0F, DrawingMethod.drawTriangleSurface);
        surfaceChar = new char[] {'-', '-', '-', '-', '+', '+', '+', '+', '$', '$', '$', '$'};
    }

}
