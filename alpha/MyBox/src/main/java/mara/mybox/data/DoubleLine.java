package mara.mybox.data;

/**
 * @Author Mara
 * @CreateDate 2023-7-6
 * @License Apache License Version 2.0
 */
public class DoubleLine implements DoubleShape {

    private double startX, startY, endX, endY;

    public DoubleLine() {

    }

    public DoubleLine(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public DoubleLine cloneValues() {
        return new DoubleLine(startX, startY, endX, endY);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean contains(double x, double y) {
        if (startX == x && startY == y //  same points
                || endX == x && endY == y) {
            return true;

        } else if (startX == endX) {           // veriical line
            if (x == startX) {
                return startY > y && y > endY
                        || startY < y && y < endY;
            } else {
                return false;
            }

        } else if (startY == endY) {       // horizontal line
            if (y == startY) {
                return startX > x && x > endX
                        || startX < x && x < endX;
            } else {
                return false;
            }
        } else if (startX == x || startY == y // oblique line
                || endX == x || endY == y) {
            return false;

        } else {                               // slop
            double s1 = (endX - startX) / (endY - startY);
            double s2 = (endX - x) / (endY - y);
            return Math.abs(s1 - s2) < 1e-6;
        }
    }

    @Override
    public DoubleLine move(double offset) {
        return move(offset, offset);
    }

    @Override
    public DoubleLine move(double offsetX, double offsetY) {
        DoubleLine nline = new DoubleLine(
                startX + offsetX, startY + offsetY,
                endX + offsetX, endY + offsetY);
        return nline;
    }

    @Override
    public DoubleRectangle getBound() {
        return new DoubleRectangle(startX, startY, endX, endY);
    }

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

}