package mara.mybox.fxml;

import javafx.util.StringConverter;
import mara.mybox.tools.StringTools;

/**
 * @Author Mara
 * @CreateDate 2021-11-12
 * @License Apache License Version 2.0
 */
public class FloatStringFromatConverter extends StringConverter<Float> {

    @Override
    public Float fromString(String value) {
        try {
            return Float.valueOf(value.trim().replaceAll(",", ""));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString(Float value) {
        try {
            return StringTools.format(value);
        } catch (Exception e) {
            return "";
        }
    }
}