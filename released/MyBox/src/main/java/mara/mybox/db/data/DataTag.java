package mara.mybox.db.data;

import javafx.scene.paint.Color;
import mara.mybox.db.table.BaseTable;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.image.FxColorTools;

/**
 * @Author Mara
 * @CreateDate 2021-3-20
 * @License Apache License Version 2.0
 */
public class DataTag extends BaseData {

    protected BaseTable dataTable;
    protected long tagid;
    protected String tag;
    protected Color color;

    private void init() {
        tagid = -1;
        tag = null;
        color = FxColorTools.randomColor();
    }

    public DataTag() {
        init();
    }

    @Override
    public boolean setValue(String column, Object value) {
        return setValue(this, column, value);
    }

    @Override
    public Object getValue(String column) {
        return getValue(this, column);
    }

    @Override
    public boolean valid() {
        return valid(this);
    }

    public DataTag setColorString(String v) {
        this.color = v == null ? FxColorTools.randomColor() : Color.web((String) v);
        return this;
    }

    /*
        Static methods
     */
    public static DataTag create() {
        return new DataTag();
    }

    public static boolean setValue(DataTag data, String column, Object value) {
        if (data == null || column == null) {
            return false;
        }
        try {
            switch (column) {
                case "tagid":
                    data.setTagid(value == null ? -1 : (long) value);
                    return true;
                case "tag":
                    data.setTag(value == null ? null : (String) value);
                    return true;
                case "color":
                    data.setColor(value == null ? FxColorTools.randomColor() : Color.web((String) value));
                    return true;

            }
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
        return false;
    }

    public static Object getValue(DataTag data, String column) {
        if (data == null || column == null) {
            return null;
        }
        switch (column) {
            case "tagid":
                return data.getTagid();
            case "tag":
                return data.getTag();
            case "color":
                return data.getColor() == null ? FxColorTools.randomColor().toString() : data.getColor().toString();
        }
        return null;
    }

    public static boolean valid(DataTag data) {
        return data != null
                && data.getTag() != null && !data.getTag().isBlank();
    }

    /*
        get/set
     */
    public long getTagid() {
        return tagid;
    }

    public DataTag setTagid(long tagid) {
        this.tagid = tagid;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public DataTag setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public DataTag setColor(Color color) {
        this.color = color;
        return this;
    }

}
