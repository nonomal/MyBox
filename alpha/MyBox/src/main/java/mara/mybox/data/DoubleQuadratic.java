package mara.mybox.data;

import java.awt.geom.QuadCurve2D;

/**
 * @Author Mara
 * @CreateDate 2023-7-31
 * @License Apache License Version 2.0
 */
public class DoubleQuadratic implements DoubleShape {

    private double startX, startY, controlX, controlY, endX, endY;

    public DoubleQuadratic() {

    }

    public DoubleQuadratic(double startX, double startY,
            double controlX, double controlY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.controlX = controlX;
        this.controlY = controlY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public QuadCurve2D.Double getShape() {
        return new QuadCurve2D.Double(startX, startY, controlX, controlY, endX, endY);
    }

    @Override
    public DoubleQuadratic cloneValues() {
        return new DoubleQuadratic(startX, startY, controlX, controlY, endX, endY);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return !isValid();
    }

    @Override
    public boolean translateRel(double offsetX, double offsetY) {
        startX += offsetX;
        startY += offsetY;
        controlX += offsetX;
        controlY += offsetY;
        endX += offsetX;
        endY += offsetY;
        return true;
    }


    /*
        get
     */
    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public double getControlX() {
        return controlX;
    }

    public void setControlX(double controlX) {
        this.controlX = controlX;
    }

    public double getControlY() {
        return controlY;
    }

    public void setControlY(double controlY) {
        this.controlY = controlY;
    }

}