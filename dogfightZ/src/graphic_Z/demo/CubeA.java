package graphic_Z.demo;

import graphic_Z.Objects.CharMessObject;

public class CubeA extends CharMessObject {
    //这是立方体的类，加载的是绘制的模型文件
    public CubeA(String ModelFile) {
        super(ModelFile, 1.0F, DrawingMethod.drawTriangleSurface);
        surfaceChar = new char[] {'-', '-', '-', '-', '+', '+', '+', '+', '$', '$', '$', '$'};
    }

    @Override
    public final PointType getPointType() {
        return PointType.rel;
    }
}
