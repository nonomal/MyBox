package mara.mybox.db.migration;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.image.Image;
import mara.mybox.data.DoubleCircle;
import mara.mybox.data.DoubleEllipse;
import mara.mybox.data.DoublePolygon;
import mara.mybox.data.DoubleRectangle;
import mara.mybox.data.IntPoint;
import mara.mybox.db.data.BaseData;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.value.Colors;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2018-8-1 16:22:41
 * @License Apache License Version 2.0
 */
public class OldImageScope extends BaseData {

    public static String ValueSeparator = ",";

    protected String file, name, areaData, colorData, outlineName;
    protected ScopeType scopeType;
    protected ColorScopeType colorScopeType;
    protected List<Color> colors;
    protected List<IntPoint> points;
    protected DoubleRectangle rectangle;
    protected DoubleCircle circle;
    protected DoubleEllipse ellipse;
    protected DoublePolygon polygon;
    protected int colorDistance, colorDistanceSquare;
    protected float hsbDistance;
    protected boolean areaExcluded, colorExcluded, eightNeighbor, distanceSquareRoot;
    protected Image image, clip;
    protected Color maskColor;
    protected float maskOpacity;
    protected Date createTime, modifyTime;
    protected BufferedImage outlineSource, outline;

    public static enum ScopeType {
        Whole, Matting, Rectangle, Circle, Ellipse, Polygon, Colors, Outline
    }

    public static enum ColorScopeType {
        AllColor, Color, Red, Green, Blue, Brightness, Saturation, Hue
    }

    public OldImageScope() {
        init();
    }

    public OldImageScope(Image image) {
        this.image = image;
        init();
    }

    public OldImageScope(Image image, ScopeType type) {
        this.image = image;
        init();
        scopeType = type;
    }

    private void init() {
        scopeType = ScopeType.Whole;
        colorScopeType = ColorScopeType.AllColor;
        clearValues();
    }

    @Override
    public boolean valid() {
        return valid(this);
    }

    @Override
    public boolean setValue(String column, Object value) {
        return setValue(this, column, value);
    }

    @Override
    public Object getValue(String column) {
        return getValue(this, column);
    }

    public final void clearValues() {
        colors = new ArrayList<>();
        points = new ArrayList<>();
        colorDistance = 50;
        colorDistanceSquare = colorDistance * colorDistance;
        hsbDistance = 0.5f;
        maskColor = Colors.TRANSPARENT;
        maskOpacity = 0.5f;
        areaExcluded = colorExcluded = distanceSquareRoot = false;
        eightNeighbor = true;
        rectangle = null;
        circle = null;
        ellipse = null;
        polygon = null;
        areaData = null;
        colorData = null;
        outlineName = null;
        outlineSource = null;
        outline = null;
    }

    public OldImageScope cloneValues() {
        return OldImageScopeTools.cloneAll(this);
    }

    public boolean isWhole() {
        return scopeType == null || scopeType == ScopeType.Whole;
    }

    public void decode(FxTask task) {
        if (colorData != null) {
            OldImageScopeTools.decodeColorData(this);
        }
        if (areaData != null) {
            OldImageScopeTools.decodeAreaData(this);
        }
        if (outlineName != null) {
            OldImageScopeTools.decodeOutline(task, this);
        }
    }

    public void encode(FxTask task) {
        OldImageScopeTools.encodeColorData(this);
        OldImageScopeTools.encodeAreaData(this);
        OldImageScopeTools.encodeOutline(task, this);
    }

    public void addPoint(IntPoint point) {
        if (point == null) {
            return;
        }
        if (points == null) {
            points = new ArrayList<>();
        }
        if (!points.contains(point)) {
            points.add(point);
        }
    }

    public void addPoint(int x, int y) {
        if (x < 0 || y < 0) {
            return;
        }
        IntPoint point = new IntPoint(x, y);
        addPoint(point);
    }

    public void setPoint(int index, int x, int y) {
        if (x < 0 || y < 0 || points == null || index < 0 || index >= points.size()) {
            return;
        }
        IntPoint point = new IntPoint(x, y);
        points.set(index, point);
    }

    public void deletePoint(int index) {
        if (points == null || index < 0 || index >= points.size()) {
            return;
        }
        points.remove(index);
    }

    public void clearPoints() {
        points = new ArrayList<>();
    }

    public boolean addColor(Color color) {
        if (color == null) {
            return false;
        }
        if (colors == null) {
            colors = new ArrayList<>();
        }
        if (!colors.contains(color)) {
            colors.add(color);
            return true;
        } else {
            return false;
        }
    }

    public void clearColors() {
        colors = new ArrayList<>();
    }

    /*
        SubClass should implement this
     */
    protected boolean inScope(int x, int y, Color color) {
        return true;
    }

    public boolean inColorMatch(Color color1, Color color2) {
        return true;
    }

    /*
        Static methods
     */
    public static OldImageScope create() {
        return new OldImageScope();
    }

    public static boolean setValue(OldImageScope data, String column, Object value) {
        if (data == null || column == null) {
            return false;
        }
        try {
            switch (column) {
                case "image_location":
                    data.setFile(value == null ? null : (String) value);
                    return true;
                case "name":
                    data.setName(value == null ? null : (String) value);
                    return true;
                case "scope_type":
                    data.setScopeType(value == null ? null : OldImageScopeTools.scopeType((String) value));
                    return true;
                case "color_scope_type":
                    data.setColorScopeType(value == null ? null : ColorScopeType.valueOf((String) value));
                    return true;
                case "area_data":
                    data.setAreaData(value == null ? null : (String) value);
                    return true;
                case "color_data":
                    data.setColorData(value == null ? null : (String) value);
                    return true;
                case "color_distance":
                    data.setColorDistance(value == null ? 20 : (int) value);
                    return true;
                case "hsb_distance":
                    data.setHsbDistance(value == null ? 20 : Float.parseFloat(value + ""));
                    return true;
                case "area_excluded":
                    data.setAreaExcluded(value == null ? false : (int) value > 0);
                    return true;
                case "color_excluded":
                    data.setColorExcluded(value == null ? false : (int) value > 0);
                    return true;
                case "outline":
                    data.setOutlineName(value == null ? null : (String) value);
                    return true;
                case "create_time":
                    data.setCreateTime(value == null ? null : (Date) value);
                    return true;
                case "modify_time":
                    data.setCreateTime(value == null ? null : (Date) value);
                    return true;
            }
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
        return false;
    }

    public static Object getValue(OldImageScope data, String column) {
        if (data == null || column == null) {
            return null;
        }
        switch (column) {
            case "image_location":
                return data.getFile();
            case "name":
                return data.getName();
            case "scope_type":
                return data.getScopeType().name();
            case "color_scope_type":
                return data.getColorScopeType().name();
            case "area_data":
                return data.getAreaData();
            case "color_data":
                return data.getColorData();
            case "color_distance":
                return data.getColorDistance();
            case "hsb_distance":
                return data.getHsbDistance();
            case "area_excluded":
                return data.isAreaExcluded() ? 1 : 0;
            case "color_excluded":
                return data.isColorExcluded() ? 1 : 0;
            case "outline":
                return data.getOutline();
            case "create_time":
                return data.getCreateTime();
            case "modify_time":
                return data.getModifyTime();
        }
        return null;
    }

    public static boolean valid(OldImageScope data) {
        return data != null && data.getScopeType() != null;
    }


    /*
        customized get/set
     */
    public void setColorDistance(int colorDistance) {
        this.colorDistance = colorDistance;
        this.colorDistanceSquare = colorDistance * colorDistance;
    }

    public void setColorDistanceSquare(int colorDistanceSquare) {
        this.colorDistanceSquare = colorDistanceSquare;
        this.colorDistance = (int) Math.sqrt(colorDistanceSquare);
    }

    public String getColorTypeName() {
        if (colorScopeType == null) {
            return null;
        }
        return message(colorScopeType.name());
    }

    /*
        get/set
     */
    public List<Color> getColors() {
        return colors;
    }

    public OldImageScope setColors(List<Color> colors) {
        this.colors = colors;
        return this;
    }

    public DoubleRectangle getRectangle() {
        return rectangle;
    }

    public OldImageScope setRectangle(DoubleRectangle rectangle) {
        this.rectangle = rectangle;
        return this;
    }

    public DoubleCircle getCircle() {
        return circle;
    }

    public void setCircle(DoubleCircle circle) {
        this.circle = circle;
    }

    public Image getImage() {
        return image;
    }

    public OldImageScope setImage(Image image) {
        this.image = image;
        return this;
    }

    public int getColorDistance() {
        return colorDistance;
    }

    public float getMaskOpacity() {
        return maskOpacity;
    }

    public OldImageScope setMaskOpacity(float maskOpacity) {
        this.maskOpacity = maskOpacity;
        return this;
    }

    public Color getMaskColor() {
        return maskColor;
    }

    public OldImageScope setMaskColor(Color maskColor) {
        this.maskColor = maskColor;
        return this;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public OldImageScope setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    public ColorScopeType getColorScopeType() {
        return colorScopeType;
    }

    public void setColorScopeType(ColorScopeType colorScopeType) {
        this.colorScopeType = colorScopeType;
    }

    public boolean isAreaExcluded() {
        return areaExcluded;
    }

    public OldImageScope setAreaExcluded(boolean areaExcluded) {
        this.areaExcluded = areaExcluded;
        return this;
    }

    public boolean isColorExcluded() {
        return colorExcluded;
    }

    public OldImageScope setColorExcluded(boolean colorExcluded) {
        this.colorExcluded = colorExcluded;
        return this;
    }

    public List<IntPoint> getPoints() {
        return points;
    }

    public void setPoints(List<IntPoint> points) {
        this.points = points;
    }

    public int getColorDistanceSquare() {
        return colorDistanceSquare;
    }

    public float getHsbDistance() {
        return hsbDistance;
    }

    public void setHsbDistance(float hsbDistance) {
        this.hsbDistance = hsbDistance;
    }

    public DoubleEllipse getEllipse() {
        return ellipse;
    }

    public void setEllipse(DoubleEllipse ellipse) {
        this.ellipse = ellipse;
    }

    public DoublePolygon getPolygon() {
        return polygon;
    }

    public void setPolygon(DoublePolygon polygon) {
        this.polygon = polygon;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public BufferedImage getOutline() {
        return outline;
    }

    public OldImageScope setOutline(BufferedImage outline) {
        this.outline = outline;
        return this;
    }

    public BufferedImage getOutlineSource() {
        return outlineSource;
    }

    public OldImageScope setOutlineSource(BufferedImage outlineSource) {
        this.outlineSource = outlineSource;
        return this;
    }

    public Image getClip() {
        return clip;
    }

    public void setClip(Image clip) {
        this.clip = clip;
    }

    public boolean isEightNeighbor() {
        return eightNeighbor;
    }

    public OldImageScope setEightNeighbor(boolean eightNeighbor) {
        this.eightNeighbor = eightNeighbor;
        return this;
    }

    public boolean isDistanceSquareRoot() {
        return distanceSquareRoot;
    }

    public void setDistanceSquareRoot(boolean distanceSquareRoot) {
        this.distanceSquareRoot = distanceSquareRoot;
    }

    public String getAreaData() {
        return areaData;
    }

    public void setAreaData(String areaData) {
        this.areaData = areaData;
    }

    public String getColorData() {
        return colorData;
    }

    public void setColorData(String colorData) {
        this.colorData = colorData;
    }

    public String getOutlineName() {
        return outlineName;
    }

    public void setOutlineName(String outlineName) {
        this.outlineName = outlineName;
    }

}
