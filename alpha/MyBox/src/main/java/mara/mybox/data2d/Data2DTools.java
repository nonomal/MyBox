package mara.mybox.data2d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.paint.Color;
import mara.mybox.data.StringTable;
import mara.mybox.db.data.ColumnDefinition;
import mara.mybox.db.data.ColumnDefinition.ColumnType;
import mara.mybox.db.data.Data2DColumn;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.tools.CsvTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.tools.FileTools;
import mara.mybox.tools.JsonTools;
import mara.mybox.tools.StringTools;
import mara.mybox.tools.XmlTools;
import mara.mybox.value.AppValues;
import static mara.mybox.value.Languages.message;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @Author Mara
 * @CreateDate 2023-9-12
 * @License Apache License Version 2.0
 */
public class Data2DTools {

    public static List<Data2DColumn> clone(List<Data2DColumn> columns) {
        try {
            if (columns == null) {
                return null;
            }
            List<Data2DColumn> cols = new ArrayList<>();
            int index = 0;
            for (Data2DColumn c : columns) {
                Data2DColumn col = c.cloneAll();
                col.setIndex(index++);
                cols.add(col);
            }
            return cols;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static StringTable validate(List<Data2DColumn> columns) {
        try {
            if (columns == null || columns.isEmpty()) {
                return null;
            }
            List<String> colsNames = new ArrayList<>();
            List<String> tNames = new ArrayList<>();
            tNames.addAll(Arrays.asList(message("ID"), message("Name"), message("Reason")));
            StringTable colsTable = new StringTable(tNames, message("InvalidColumns"));
            for (int c = 0; c < columns.size(); c++) {
                Data2DColumn column = columns.get(c);
                if (!column.valid()) {
                    List<String> row = new ArrayList<>();
                    row.addAll(Arrays.asList(c + 1 + "", column.getColumnName(), message("Invalid")));
                    colsTable.add(row);
                }
                if (colsNames.contains(column.getColumnName())) {
                    List<String> row = new ArrayList<>();
                    row.addAll(Arrays.asList(c + 1 + "", column.getColumnName(), message("Duplicated")));
                    colsTable.add(row);
                }
                colsNames.add(column.getColumnName());
            }
            return colsTable;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static String toString(Data2DColumn column) {
        try {
            if (column == null) {
                return null;
            }
            StringBuilder s = new StringBuilder();
            s.append(message("Name")).append(": ").append(column.getColumnName()).append("\n");
            s.append(message("Type")).append(": ").append(column.getType()).append("\n");
            s.append(message("Length")).append(": ").append(column.getLength()).append("\n");
            s.append(message("Width")).append(": ").append(column.getWidth()).append("\n");
            s.append(message("DisplayFormat")).append(": ").append(column.getFormat()).append("\n");
            s.append(message("NotNull")).append(": ").append(column.isNotNull()).append("\n");
            s.append(message("Editable")).append(": ").append(column.isEditable()).append("\n");
            s.append(message("PrimaryKey")).append(": ").append(column.isIsPrimaryKey()).append("\n");
            s.append(message("AutoGenerated")).append(": ").append(column.isAuto()).append("\n");
            s.append(message("DefaultValue")).append(": ").append(column.getDefaultValue()).append("\n");
            s.append(message("Color")).append(": ").append(column.getColor()).append("\n");
            s.append(message("ToInvalidValue")).append(": ").append(column.getInvalidAs()).append("\n");
            s.append(message("DecimalScale")).append(": ").append(column.getScale()).append("\n");
            s.append(message("Century")).append(": ").append(column.getCentury()).append("\n");
            s.append(message("FixTwoDigitYears")).append(": ").append(column.isFixTwoDigitYear()).append("\n");
            s.append(message("Description")).append(": ").append(column.getDescription()).append("\n");
            return s.toString();
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    /*
        from
     */
    public static List<Data2DColumn> toColumns(List<String> names) {
        try {
            if (names == null) {
                return null;
            }
            List<Data2DColumn> cols = new ArrayList<>();
            int index = -1;
            for (String c : names) {
                Data2DColumn col = new Data2DColumn(c, ColumnDefinition.ColumnType.String);
                col.setIndex(index--);
                cols.add(col);
            }
            return cols;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static List<Data2DColumn> definition() {
        List<Data2DColumn> columns = new ArrayList<>();
        columns.add(new Data2DColumn(message("Name"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("Type"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("Length"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("Width"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("DisplayFormat"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("NotNull"), ColumnDefinition.ColumnType.Boolean));
        columns.add(new Data2DColumn(message("Editable"), ColumnDefinition.ColumnType.Boolean));
        columns.add(new Data2DColumn(message("PrimaryKey"), ColumnDefinition.ColumnType.Boolean));
        columns.add(new Data2DColumn(message("AutoGenerated"), ColumnDefinition.ColumnType.Boolean));
        columns.add(new Data2DColumn(message("DefaultValue"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("Color"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("ToInvalidValue"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("DecimalScale"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("Century"), ColumnDefinition.ColumnType.String));
        columns.add(new Data2DColumn(message("FixTwoDigitYears"), ColumnDefinition.ColumnType.Boolean));
        columns.add(new Data2DColumn(message("Description"), ColumnDefinition.ColumnType.String));
        return columns;
    }

    public static DataFileCSV fromXML(String s) {
        try {
            if (s == null || s.isBlank()) {
                return null;
            }
            Element e = XmlTools.toElement(null, s);
            if (e == null) {
                return null;
            }
            String tag = e.getTagName();
            if (!"data2d".equalsIgnoreCase(tag)) {
                return null;
            }
            NodeList children = e.getChildNodes();
            if (children == null) {
                return null;
            }
            DataFileCSV csv = new DataFileCSV();
            for (int dIndex = 0; dIndex < children.getLength(); dIndex++) {
                Node child = children.item(dIndex);
                if (!(child instanceof Element)) {
                    continue;
                }
                Element dElement = (Element) child;
                if ("attributes".equalsIgnoreCase(child.getNodeName())) {
                    NodeList attrNodes = dElement.getChildNodes();
                    if (attrNodes == null) {
                        continue;
                    }
                    for (int aIndex = 0; aIndex < attrNodes.getLength(); aIndex++) {
                        Node attrNode = attrNodes.item(aIndex);
                        if (!(attrNode instanceof Element)) {
                            continue;
                        }
                        String attrName = attrNode.getNodeName();
                        String attrValue = attrNode.getTextContent();
                        if (attrName == null || attrName.isBlank()
                                || attrValue == null || attrValue.isBlank()) {
                            continue;
                        }
                        switch (attrName.toLowerCase()) {
                            case "dataname":
                                csv.setDataName(attrValue);
                                break;
                            case "scale": {
                                try {
                                    csv.setScale(Short.parseShort(attrValue));
                                } catch (Exception ex) {
                                }
                                break;
                            }
                            case "maxRandom": {
                                try {
                                    csv.setMaxRandom(Integer.parseInt(attrValue));
                                } catch (Exception ex) {
                                }
                                break;
                            }
                            case "description":
                                csv.setComments(attrValue);
                                break;
                        }
                    }

                } else if ("columns".equalsIgnoreCase(child.getNodeName())) {
                    NodeList columnNodes = dElement.getChildNodes();
                    if (columnNodes == null) {
                        continue;
                    }
                    List<Data2DColumn> columns = new ArrayList<>();
                    for (int cIndex = 0; cIndex < columnNodes.getLength(); cIndex++) {
                        Node columnNode = columnNodes.item(cIndex);
                        if (!(columnNode instanceof Element)
                                || !"column".equalsIgnoreCase(columnNode.getNodeName())) {
                            continue;
                        }
                        NodeList columnAttributes = columnNode.getChildNodes();
                        if (columnAttributes == null) {
                            continue;
                        }
                        Data2DColumn column = new Data2DColumn();
                        for (int aIndex = 0; aIndex < columnAttributes.getLength(); aIndex++) {
                            Node attrNode = columnAttributes.item(aIndex);
                            if (!(attrNode instanceof Element)) {
                                continue;
                            }
                            String attrName = attrNode.getNodeName();
                            String attrValue = attrNode.getTextContent();
                            if (attrName == null || attrName.isBlank()
                                    || attrValue == null || attrValue.isBlank()) {
                                continue;
                            }
                            switch (attrName.toLowerCase()) {
                                case "name":
                                    column.setColumnName(attrValue);
                                    break;
                                case "type":
                                    column.setType(Data2DColumn.columnType(attrValue));
                                    break;
                                case "length": {
                                    try {
                                        column.setLength(Integer.parseInt(attrValue));
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                                case "width": {
                                    try {
                                        column.setWidth(Integer.parseInt(attrValue));
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                                case "format": {
                                    String format = attrValue;
                                    try {
                                        NodeList fNodes = attrNode.getChildNodes();
                                        if (fNodes != null) {
                                            for (int f = 0; f < fNodes.getLength(); f++) {
                                                Node c = fNodes.item(f);
                                                if (c.getNodeType() == Node.CDATA_SECTION_NODE) {
                                                    format = c.getNodeValue();
                                                    break;
                                                }
                                            }
                                        }
                                    } catch (Exception ex) {
                                    }
                                    column.setFormat(format);
                                    break;
                                }
                                case "notNull":
                                    column.setNotNull(StringTools.isTrue(attrValue));
                                    break;
                                case "editable":
                                    column.setEditable(StringTools.isTrue(attrValue));
                                    break;
                                case "primaryKey":
                                    column.setIsPrimaryKey(StringTools.isTrue(attrValue));
                                    break;
                                case "auto":
                                    column.setAuto(StringTools.isTrue(attrValue));
                                    break;
                                case "color": {
                                    try {
                                        column.setColor(Color.web(attrValue));
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                                case "defaultValue":
                                    column.setDefaultValue(attrValue);
                                    break;
                                case "invalidAs":
                                    column.setInvalidAs(ColumnDefinition.invalidAs(attrValue));
                                    break;
                                case "scale": {
                                    try {
                                        column.setScale(Integer.parseInt(attrValue));
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                                case "century": {
                                    try {
                                        column.setCentury(Integer.parseInt(attrValue));
                                    } catch (Exception ex) {
                                    }
                                    break;
                                }
                                case "isFixTwoDigitYear":
                                    column.setFixTwoDigitYear(StringTools.isTrue(attrValue));
                                    break;

                                case "description":
                                    column.setDescription(attrValue);
                                    break;
                            }
                        }
                        columns.add(column);
                    }
                    csv.setColumns(columns).setColsNumber(columns.size());
                }
            }
            return csv;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    /*
        to
     */
    public static List<String> toNames(List<Data2DColumn> cols) {
        try {
            if (cols == null) {
                return null;
            }
            List<String> names = new ArrayList<>();
            for (Data2DColumn c : cols) {
                names.add(c.getColumnName());
            }
            return names;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static DataFileCSV toCSVFile(Data2D data2d, File file) {
        try {
            if (data2d == null || file == null) {
                return null;
            }
            File tmpFile = FileTmpTools.getTempFile();
            List<Data2DColumn> definition = Data2DTools.definition();
            try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(tmpFile,
                    Charset.forName("UTF-8")), CsvTools.csvFormat(",", true))) {
                csvPrinter.printComment("The first row defines attributes of the data. And other rows define columns.");
                List<String> row = new ArrayList<>();
                for (Data2DColumn col : definition) {
                    row.add(col.getColumnName());
                }
                csvPrinter.printRecord(row);
                row.clear();
                row.add(data2d.getDataName());
                row.add("TableAttributes");
                row.add(data2d.getMaxRandom() + "");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add("");
                row.add(data2d.getScale() + "");
                row.add("");
                row.add("");
                row.add(data2d.getComments());
                csvPrinter.printRecord(row);
                List<Data2DColumn> columns = data2d.getColumns();
                if (columns != null) {
                    for (Data2DColumn col : columns) {
                        row.clear();
                        row.add(col.getColumnName());
                        row.add(col.getType().name());
                        row.add(ColumnType.String == col.getType() ? col.getLength() + "" : "");
                        row.add(col.getWidth() + "");
                        row.add(col.getFormat());
                        row.add(col.isNotNull() ? "1" : "0");
                        row.add(col.isEditable() ? "1" : "0");
                        row.add(col.isIsPrimaryKey() ? "1" : "0");
                        row.add(col.isAuto() ? "1" : "0");
                        row.add(col.getDefaultValue());
                        row.add(col.getColor().toString());
                        row.add(col.getInvalidAs().name());
                        row.add(col.getScale() + "");
                        row.add(col.getCentury() + "");
                        row.add(col.isFixTwoDigitYear() ? "1" : "0");
                        row.add(col.getDescription());
                        csvPrinter.printRecord(row);
                    }
                }
                csvPrinter.flush();
                csvPrinter.close();
            }
            if (!FileTools.rename(tmpFile, file, true)) {
                return null;
            }
            DataFileCSV csv = new DataFileCSV();
            csv.setColumns(definition)
                    .setFile(file)
                    .setCharset(Charset.forName("UTF-8"))
                    .setDelimiter(",")
                    .setHasHeader(true)
                    .setColsNumber(definition.size());
            csv.saveAttributes();
            return csv;
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

    public static boolean toXMLFile(Data2D data2d, File file) {
        if (data2d == null || file == null) {
            return false;
        }
        File tmpFile = FileTmpTools.getTempFile();
        try (BufferedWriter xmlWriter = new BufferedWriter(new FileWriter(tmpFile, Charset.forName("UTF-8")))) {
            xmlWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlWriter.write(toXML(data2d));
            xmlWriter.flush();
            xmlWriter.close();
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return false;
        }
        return FileTools.rename(tmpFile, file, true);
    }

    public static String toXML(Data2D data2d) {
        try {
            if (data2d == null) {
                return null;
            }
            String indent = AppValues.Indent;
            StringBuilder s = new StringBuilder();
            s.append("<data2d>\n");
            s.append(indent).append("<attributes>\n");
            String v = data2d.getDataName();
            if (v != null && !v.isBlank()) {
                s.append(indent).append(indent).append("<dataName>")
                        .append("<![CDATA[").append(v).append("]]>")
                        .append("</dataName>\n");
            }
            s.append(indent).append(indent).append("<scale>")
                    .append(data2d.getScale()).append("</scale>\n");
            s.append(indent).append(indent).append("<maxRandom>")
                    .append(data2d.getMaxRandom()).append("</maxRandom>\n");
            v = data2d.getComments();
            if (v != null && !v.isBlank()) {
                s.append(indent).append(indent).append("<description>")
                        .append("<![CDATA[").append(v).append("]]>")
                        .append("</description>\n");
            }
            s.append(indent).append("</attributes>\n");
            s.append(indent).append("<columns>\n");
            List<Data2DColumn> columns = data2d.getColumns();
            if (columns != null) {
                for (Data2DColumn col : columns) {
                    if (col.getColumnName() == null) {
                        continue;
                    }
                    s.append(indent).append(indent).append("<column>\n");
                    s.append(indent).append(indent).append(indent).append("<name>")
                            .append("<![CDATA[").append(col.getColumnName()).append("]]>")
                            .append("</name>\n");
                    if (col.getType() != null) {
                        s.append(indent).append(indent).append(indent).append("<type>")
                                .append(col.getType().name()).append("</type>\n");
                    }
                    if (ColumnType.String == col.getType()) {
                        s.append(indent).append(indent).append(indent).append("<length>")
                                .append(col.getLength()).append("</length>\n");
                    }
                    s.append(indent).append(indent).append(indent).append("<width>")
                            .append(col.getWidth()).append("</width>\n");
                    if (col.getFormat() != null) {
                        s.append(indent).append(indent).append(indent).append("<format>");
                        if (ColumnType.Enumeration == col.getType()) {
                            s.append("\n").append(indent).append(indent).append(indent)
                                    .append("<![CDATA[").append(col.getFormat()).append("]]>\n")
                                    .append(indent).append(indent);
                        } else {
                            s.append(col.getFormat());
                        }
                        s.append("</format>\n");
                    }
                    s.append(indent).append(indent).append(indent).append("<notNull>")
                            .append(col.isNotNull() ? "true" : "false").append("</notNull>\n");
                    s.append(indent).append(indent).append(indent).append("<editable>")
                            .append(col.isEditable() ? "true" : "false").append("</editable>\n");
                    s.append(indent).append(indent).append(indent).append("<primaryKey>")
                            .append(col.isIsPrimaryKey() ? "true" : "false").append("</primaryKey>\n");
                    s.append(indent).append(indent).append(indent).append("<auto>")
                            .append(col.isAuto() ? "true" : "false").append("</auto>\n");
                    if (col.getDefaultValue() != null) {
                        s.append(indent).append(indent).append(indent).append("<defaultValue>")
                                .append("<![CDATA[").append(col.getDefaultValue()).append("]]>")
                                .append("</defaultValue>\n");
                    }
                    if (col.getColor() != null) {
                        s.append(indent).append(indent).append(indent).append("<color>")
                                .append(col.getColor()).append("</color>\n");
                    }
                    if (col.getInvalidAs() != null) {
                        s.append(indent).append(indent).append(indent).append("<invalidAs>")
                                .append(col.getInvalidAs().name()).append("</invalidAs>\n");
                    }
                    s.append(indent).append(indent).append(indent).append("<scale>")
                            .append(col.getScale()).append("</scale>\n");
                    s.append(indent).append(indent).append(indent).append("<century>")
                            .append(col.getCentury()).append("</century>\n");
                    s.append(indent).append(indent).append(indent).append("<isFixTwoDigitYear>")
                            .append(col.isFixTwoDigitYear() ? "true" : "false").append("</isFixTwoDigitYear>\n");
                    if (col.getDescription() != null) {
                        s.append(indent).append(indent).append(indent).append("<description>")
                                .append("<![CDATA[").append(col.getDescription()).append("]]>")
                                .append("</description>\n");
                    }
                    s.append(indent).append(indent).append("</column>\n");
                }
            }
            s.append(indent).append("</columns>\n");
            s.append("</data2d>\n");
            return s.toString();
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

    public static boolean toJSONFile(Data2D data2d, File file) {
        try {
            File tmpFile = FileTmpTools.getTempFile();
            try (BufferedWriter jsonWriter = new BufferedWriter(new FileWriter(tmpFile, Charset.forName("UTF-8")))) {
                jsonWriter.write(toJSON(data2d));
                jsonWriter.flush();
                jsonWriter.close();
            }
            return FileTools.rename(tmpFile, file, true);
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return false;
        }
    }

    public static String toJSON(Data2D data2d) {
        try {
            if (data2d == null) {
                return null;
            }
            String indent = AppValues.Indent;
            StringBuilder s = new StringBuilder();
            s.append("{\"data2d\": {\n");
            s.append(indent).append("\"attributes\": {\n");
            String v = data2d.getDataName();
            if (v != null && !v.isBlank()) {
                s.append(indent).append(indent)
                        .append("\"dataName\": ")
                        .append(JsonTools.encode(v));
            }
            s.append(indent).append(indent).append("\"scale\": ").append(data2d.getScale());
            s.append(indent).append(indent).append("\"maxRandom\": ").append(data2d.getMaxRandom());
            v = data2d.getComments();
            if (v != null && !v.isBlank()) {
                s.append(indent).append(indent)
                        .append("\"description\": ")
                        .append(JsonTools.encode(v));
            }
            s.append(indent).append("},\n");
            s.append(indent).append("\"columns\": [\n");
            boolean firstRow = true;
            List<Data2DColumn> columns = data2d.getColumns();
            if (columns != null) {
                for (Data2DColumn col : columns) {
                    if (firstRow) {
                        firstRow = false;
                    } else {
                        s.append(indent).append(indent).append(",\n");
                    }
                    s.append(indent).append(indent).append("{").append("\n");
                    if (col.getColumnName() == null) {
                        continue;
                    }
                    s.append(indent).append(indent).append(indent)
                            .append("\"name\": ")
                            .append(JsonTools.encode(col.getColumnName()));
                    if (col.getType() != null) {
                        s.append(",\n").append(indent).append(indent).append(indent)
                                .append("\"type\": \"").append(col.getType().name()).append("\"");
                    }
                    if (ColumnType.String == col.getType()) {
                        s.append(",\n").append(indent).append(indent).append(indent)
                                .append("\"length\": ").append(col.getLength());
                    }
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"width\": ").append(col.getWidth());
                    if (col.getFormat() != null) {
                        s.append(",\n").append(indent).append(indent).append(indent)
                                .append("\"format\": ")
                                .append(JsonTools.encode(col.getFormat()));
                    }
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"isNotNull\": ").append(col.isNotNull() ? "true" : "false");
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"isEditable\": ").append(col.isEditable() ? "true" : "false");
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"isPrimaryKey\": ").append(col.isIsPrimaryKey() ? "true" : "false");
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"isAuto\": ").append(col.isAuto() ? "true" : "false");
                    if (col.getDefaultValue() != null) {
                        s.append(",\n").append(indent).append(indent).append(indent)
                                .append("\"defaultValue\": ")
                                .append(JsonTools.encode(col.getDefaultValue()));
                    }
                    if (col.getColor() != null) {
                        s.append(",\n").append(indent).append(indent).append(indent)
                                .append("\"color\": \"").append(col.getColor()).append("\"");
                    }
                    if (col.getInvalidAs() != null) {
                        s.append(",\n").append(indent).append(indent).append(indent)
                                .append("\"invalidAs\": \"").append(col.getInvalidAs().name()).append("\"");
                    }
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"scale\": ").append(col.getScale());
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"century\": ").append(col.getCentury());
                    s.append(",\n").append(indent).append(indent).append(indent)
                            .append("\"isFixTwoDigitYear\": ").append(col.isFixTwoDigitYear() ? "true" : "false");
                    if (col.getDescription() != null) {
                        s.append(",\n").append(indent).append(indent).append(indent)
                                .append("\"description\": ")
                                .append(JsonTools.encode(col.getDescription()));
                    }
                    s.append("\n").append(indent).append(indent).append("}");
                }
                s.append(indent).append(indent).append("\n]\n");
                s.append("}");
            }
            return s.toString();
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

    public static DataFileExcel toExcelFile(Data2D data2d, File file) {
        try {
            File tmpFile = FileTmpTools.getTempFile();
            List<Data2DColumn> definition = Data2DTools.definition();
            try (XSSFWorkbook xssfBook = new XSSFWorkbook()) {
                XSSFSheet xssfSheet = xssfBook.createSheet("sheet1");
                xssfSheet.setDefaultColumnWidth(20);
                int rowIndex = 0;
                XSSFRow commentsRow = xssfSheet.createRow(rowIndex++);
                commentsRow.createCell(0).setCellValue("The first row defines attributes of the data. And other rows define columns.");
                XSSFRow titleRow = xssfSheet.createRow(rowIndex++);
                XSSFCellStyle horizontalCenter = xssfBook.createCellStyle();
                horizontalCenter.setAlignment(HorizontalAlignment.CENTER);
                for (int i = 0; i < definition.size(); i++) {
                    XSSFCell cell = titleRow.createCell(i);
                    cell.setCellValue(definition.get(i).getColumnName());
                    cell.setCellStyle(horizontalCenter);
                    xssfSheet.autoSizeColumn(i);
                }
                XSSFRow attributesRow = xssfSheet.createRow(rowIndex++);
                int cellIndex = 0;
                attributesRow.createCell(cellIndex++).setCellValue(data2d.getDataName());
                attributesRow.createCell(cellIndex++).setCellValue("TableAttributes");
                attributesRow.createCell(cellIndex++).setCellValue(data2d.getMaxRandom() + "");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue(data2d.getScale() + "");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue("");
                attributesRow.createCell(cellIndex++).setCellValue(data2d.getComments());
                List<Data2DColumn> columns = data2d.getColumns();
                if (columns != null) {
                    for (Data2DColumn col : columns) {
                        XSSFRow columnRow = xssfSheet.createRow(rowIndex++);
                        cellIndex = 0;
                        columnRow.createCell(cellIndex++).setCellValue(col.getColumnName());
                        columnRow.createCell(cellIndex++).setCellValue(col.getType().name());
                        columnRow.createCell(cellIndex++).setCellValue(ColumnType.String == col.getType() ? col.getLength() + "" : "");
                        columnRow.createCell(cellIndex++).setCellValue(col.getWidth() + "");
                        columnRow.createCell(cellIndex++).setCellValue(col.getFormat());
                        columnRow.createCell(cellIndex++).setCellValue(col.isNotNull() ? "1" : "0");
                        columnRow.createCell(cellIndex++).setCellValue(col.isEditable() ? "1" : "0");
                        columnRow.createCell(cellIndex++).setCellValue(col.isIsPrimaryKey() ? "1" : "0");
                        columnRow.createCell(cellIndex++).setCellValue(col.isAuto() ? "1" : "0");
                        columnRow.createCell(cellIndex++).setCellValue(col.getDefaultValue());
                        columnRow.createCell(cellIndex++).setCellValue(col.getColor().toString());
                        columnRow.createCell(cellIndex++).setCellValue(col.getInvalidAs().name());
                        columnRow.createCell(cellIndex++).setCellValue(col.getScale() + "");
                        columnRow.createCell(cellIndex++).setCellValue(col.getCentury() + "");
                        columnRow.createCell(cellIndex++).setCellValue(col.isFixTwoDigitYear() ? "1" : "0");
                        columnRow.createCell(cellIndex++).setCellValue(col.getDescription());
                    }
                }
                try (FileOutputStream fileOut = new FileOutputStream(tmpFile)) {
                    xssfBook.write(fileOut);
                }
                xssfBook.close();
            }
            if (!FileTools.rename(tmpFile, file, true)) {
                return null;
            }
            DataFileExcel excel = new DataFileExcel();
            excel.setColumns(definition)
                    .setFile(file).setSheet("sheet1")
                    .setHasHeader(true)
                    .setColsNumber(definition.size());
            excel.saveAttributes();
            return excel;
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

}