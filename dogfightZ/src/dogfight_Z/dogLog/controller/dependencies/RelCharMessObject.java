package dogfight_Z.dogLog.controller.dependencies;

import graphic_Z.Objects.CharMessObject;

public class RelCharMessObject extends CharMessObject {

    public RelCharMessObject(String ModelFile, float Mess, DrawingMethod drawingMethod) {
        super(ModelFile, Mess, drawingMethod);
    }

    public RelCharMessObject(String ModelFile, float Mess) {
        super(ModelFile, Mess);
    }
    
    @Override
    public final PointType getPointType() {
        return PointType.rel;
    }

}
