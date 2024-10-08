package mara.mybox.data;

import static mara.mybox.db.data.ColumnDefinition.DefaultInvalidAs;
import mara.mybox.db.data.ColumnDefinition.InvalidAs;
import static mara.mybox.db.data.ColumnDefinition.InvalidAs.Empty;
import mara.mybox.tools.DoubleTools;

/**
 * @Author Mara
 * @CreateDate 2022-8-13
 * @License Apache License Version 2.0
 */
public class SetValue {

    public ValueType type;
    public String value;
    public int start, digit, scale;
    public boolean fillZero, aotoDigit;
    public InvalidAs invalidAs;

    public static enum ValueType {
        Value, Zero, One, Empty, Null, Random, RandomNonNegative,
        Scale, Prefix, Suffix,
        NumberSuffix, NumberPrefix, NumberReplace, NumberSuffixString, NumberPrefixString,
        Expression, GaussianDistribution, Identify, UpperTriangle, LowerTriangle
    }

    public SetValue() {
        init();
    }

    public final void init() {
        type = ValueType.Value;
        value = null;
        start = 0;
        digit = 0;
        scale = 5;
        fillZero = true;
        aotoDigit = true;
        invalidAs = DefaultInvalidAs;
    }

    public int countFinalDigit(long dataSize) {
        int finalDigit = digit;
        if (isFillZero()) {
            if (isAotoDigit()) {
                finalDigit = (dataSize + "").length();
            }
        } else {
            finalDigit = 0;
        }
        return finalDigit;
    }

    public String scale(String value) {
        double d = DoubleTools.toDouble(value, invalidAs);
        if (DoubleTools.invalidDouble(d)) {
            if (null == invalidAs) {
                return value;
            } else {
                switch (invalidAs) {
                    case Zero:
                        return "0";
                    case Empty:
                        return "";
                    case Null:
                        return null;
                    case Skip:
                        return null;
                    default:
                        return value;
                }
            }
        } else {
            return DoubleTools.scaleString(d, scale);
        }
    }

    /*
        set
     */
    public SetValue setType(ValueType type) {
        this.type = type;
        return this;
    }

    public SetValue setValue(String value) {
        this.value = value;
        return this;
    }

    public SetValue setStart(int start) {
        this.start = start;
        return this;
    }

    public SetValue setDigit(int digit) {
        this.digit = digit;
        return this;
    }

    public SetValue setFillZero(boolean fillZero) {
        this.fillZero = fillZero;
        return this;
    }

    public SetValue setAotoDigit(boolean aotoDigit) {
        this.aotoDigit = aotoDigit;
        return this;
    }

    public SetValue setScale(int scale) {
        this.scale = scale;
        return this;
    }

    public SetValue setInvalidAs(InvalidAs invalidAs) {
        this.invalidAs = invalidAs;
        return this;
    }

    /*
        get
     */
    public ValueType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getStart() {
        return start;
    }

    public int getDigit() {
        return digit;
    }

    public boolean isFillZero() {
        return fillZero;
    }

    public boolean isAotoDigit() {
        return aotoDigit;
    }

    public int getScale() {
        return scale;
    }

    public InvalidAs getInvalidAs() {
        return invalidAs;
    }

}
