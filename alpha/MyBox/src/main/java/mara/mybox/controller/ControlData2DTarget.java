package mara.mybox.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mara.mybox.data2d.Data2D;
import mara.mybox.data2d.Data2D_Attributes.TargetType;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Append;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.CSV;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.DatabaseTable;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Excel;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.HTML;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Insert;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.JSON;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Matrix;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.MyBoxClipboard;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.PDF;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Replace;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.SystemClipboard;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Text;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.XML;
import mara.mybox.data2d.DataMatrix;
import mara.mybox.data2d.TmpTable;
import mara.mybox.data2d.tools.Data2DConvertTools;
import mara.mybox.data2d.writer.Data2DWriter;
import mara.mybox.data2d.writer.SystemClipboardWriter;
import static mara.mybox.db.data.ColumnDefinition.DefaultInvalidAs;
import mara.mybox.db.data.ColumnDefinition.InvalidAs;
import mara.mybox.db.data.Data2DColumn;
import mara.mybox.db.data.VisitHistory.FileType;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.tools.FileTmpTools;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2021-12-11
 * @License Apache License Version 2.0
 */
public class ControlData2DTarget extends BaseDataConvertController {

    protected BaseData2DLoadController tableController;
    protected TargetType format;
    protected boolean notInTable;
    protected ChangeListener<Boolean> tableStatusListener;
    protected Data2D data2D;
    protected String targetName;

    @FXML
    protected ToggleGroup targetGroup;
    @FXML
    protected RadioButton csvRadio, excelRadio, textsRadio, matrixRadio, databaseRadio,
            jsonRadio, xmlRadio, htmlRadio, pdfRadio,
            systemClipboardRadio, myBoxClipboardRadio, replaceRadio, insertRadio, appendRadio;
    @FXML
    protected ComboBox<String> rowSelector, colSelector;
    @FXML
    protected VBox externalBox, externalDefBox, fileBox, inTableBox;
    @FXML
    protected HBox dataNameBox, locationBox;
    @FXML
    protected TextField nameInput;
    @FXML
    protected TabPane optionsPane;
    @FXML
    protected Tab csvTab, excelTab, textTab, matrixTab, htmlTab, pdfTab, dbTab;
    @FXML
    protected RadioButton useInvalidRadio, zeroInvalidRadio,
            emptyInvalidRadio, skipInvalidRadio, nullInvalidRadio;
    @FXML
    protected VBox optionsBox, csvBox, excelBox, textBox, matrixBox, htmlBox, pdfBox, dbBox;
    @FXML
    protected ControlNewDataTable dbController;
    @FXML
    protected FlowPane invalidAsPane, extFormatPane, internalFormatPane;

    public boolean isInvalid() {
        if (tableController == null) {
            return false;
        }
        return !tableController.isShowing()
                || tableController.invalidData()
                || (data2D != null && tableController.data2D.getDataID() != data2D.getDataID());
    }

    public void setParameters(BaseController parent, BaseData2DLoadController controller) {
        try {
            tableController = controller;
            if (isInvalid()) {
                close();
                return;
            }

            baseName = parent.baseName + "_" + baseName;

            initControls(baseName);

            if (tableController != null) {
                data2D = tableController.data2D.cloneAll().setController(parent);
                dbController.setParameters(this, data2D);
                if (matrixOptionsController != null) {
                    matrixOptionsController.setType(data2D.getSheet());
                }
            } else {
                data2D = null;
                databaseRadio.setDisable(true);
            }

            optionsPane.getTabs().clear();
            optionsBox.getChildren().clear();

            initTarget(TargetType.valueOf(UserConfig.getString(baseName + "DataTarget", "CSV")));
            targetGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue ov, Toggle oldValue, Toggle newValue) {
                    checkTarget();
                }
            });
            checkTarget();

            if (tableController != null) {
                sourceChanged();
                tableStatusListener = new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                        sourceChanged();
                    }
                };
                tableController.statusNotify.addListener(tableStatusListener);
            }

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public void initTarget(TargetType type) {
        try {
            format = type;
            isSettingValues = true;
            if (type == null) {
                csvRadio.setSelected(true);
            } else {
                switch (type) {
                    case CSV:
                        csvRadio.setSelected(true);
                        break;
                    case Excel:
                        excelRadio.setSelected(true);
                        break;
                    case Text:
                        textsRadio.setSelected(true);
                        break;
                    case Matrix:
                        matrixRadio.setSelected(true);
                        break;
                    case MyBoxClipboard:
                        myBoxClipboardRadio.setSelected(true);
                        break;
                    case DatabaseTable:
                        if (tableController != null) {
                            databaseRadio.setSelected(true);
                            databaseRadio.setDisable(false);
                        } else {
                            csvRadio.setSelected(true);
                            databaseRadio.setDisable(true);
                        }
                        break;
                    case SystemClipboard:
                        systemClipboardRadio.setSelected(true);
                        break;
                    case JSON:
                        jsonRadio.setSelected(true);
                        break;
                    case XML:
                        xmlRadio.setSelected(true);
                        break;
                    case HTML:
                        htmlRadio.setSelected(true);
                        break;
                    case PDF:
                        pdfRadio.setSelected(true);
                        break;
                    case Append:
                        if (notInTable && inTableBox != null) {
                            appendRadio.setSelected(true);
                        } else {
                            csvRadio.setSelected(true);
                        }
                        break;
                    case Insert:
                        if (notInTable && inTableBox != null) {
                            insertRadio.setSelected(true);
                        } else {
                            csvRadio.setSelected(true);
                        }
                        break;
                    case Replace:
                        if (notInTable && inTableBox != null) {
                            replaceRadio.setSelected(true);
                        } else {
                            csvRadio.setSelected(true);
                        }
                        break;
                    default:
                        csvRadio.setSelected(true);
                }
            }
            isSettingValues = false;
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public TargetType checkTarget() {
        try {
            format = TargetType.CSV;
            optionsBox.getChildren().clear();
            if (isSettingValues) {
                return format;
            }
            String dataName = dataName();
            if (dataName == null || dataName.isBlank()) {
                if (data2D != null) {
                    dataName = data2D.getName();
                }
            }
            if (dataName == null || dataName.isBlank()) {
                if (tableController != null) {
                    dataName = tableController.getTitle();
                }
            }
            if (dataName == null || dataName.isBlank()) {
                dataName = "Data2D";
            }
            if (csvRadio.isSelected()) {
                format = TargetType.CSV;
                targetFileController.setFile(FileType.CSV,
                        baseName + "TargetType" + FileType.CSV, dataName, "csv");
                optionsBox.getChildren().add(csvBox);

            } else if (excelRadio.isSelected()) {
                format = TargetType.Excel;
                targetFileController.setFile(FileType.Excel,
                        baseName + "TargetType" + FileType.Excel, dataName, "xlsx");
                optionsBox.getChildren().add(excelBox);
                if (currentSheetOnlyCheck != null) {
                    currentSheetOnlyCheck.setVisible(data2D != null && data2D.isExcel());
                }

            } else if (textsRadio.isSelected()) {
                format = TargetType.Text;
                targetFileController.setFile(FileType.Text,
                        baseName + "TargetType" + FileType.Text, dataName, "txt");
                optionsBox.getChildren().add(textBox);

            } else if (matrixRadio.isSelected()) {
                format = TargetType.Matrix;
                targetFileController.setFile(FileType.Text,
                        baseName + "TargetTypeMatrix", DataMatrix.file(dataName));
                optionsBox.getChildren().add(matrixBox);

            } else if (myBoxClipboardRadio.isSelected()) {
                format = TargetType.MyBoxClipboard;

            } else if (databaseRadio.isSelected()) {
                format = TargetType.DatabaseTable;
                optionsBox.getChildren().add(dbBox);

            } else if (systemClipboardRadio != null && systemClipboardRadio.isSelected()) {
                format = TargetType.SystemClipboard;

            } else if (jsonRadio != null && jsonRadio.isSelected()) {
                format = TargetType.JSON;
                targetFileController.setFile(FileType.JSON,
                        baseName + "TargetType" + FileType.JSON, dataName, "json");

            } else if (xmlRadio != null && xmlRadio.isSelected()) {
                format = TargetType.XML;
                targetFileController.setFile(FileType.XML,
                        baseName + "TargetType" + FileType.XML, dataName, "xml");

            } else if (htmlRadio != null && htmlRadio.isSelected()) {
                format = TargetType.HTML;
                targetFileController.setFile(FileType.Html,
                        baseName + "TargetType" + FileType.Html, dataName, "html");
                optionsBox.getChildren().add(htmlBox);

            } else if (pdfRadio != null && pdfRadio.isSelected()) {
                format = TargetType.PDF;
                targetFileController.setFile(FileType.PDF,
                        baseName + "TargetType" + FileType.PDF, dataName, "pdf");
                optionsBox.getChildren().add(pdfBox);

            } else if (inTableBox != null) {
                if (replaceRadio.isSelected()) {
                    if (!notInTable) {
                        format = TargetType.Replace;
                    } else {
                        format = TargetType.CSV;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                csvRadio.setSelected(true);
                            }
                        });
                    }
                } else if (insertRadio.isSelected()) {
                    if (!notInTable) {
                        format = TargetType.Insert;
                    } else {
                        format = TargetType.CSV;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                csvRadio.setSelected(true);
                            }
                        });
                    }
                } else if (appendRadio.isSelected()) {
                    if (!notInTable) {
                        format = TargetType.Append;
                    } else {
                        format = TargetType.CSV;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                csvRadio.setSelected(true);
                            }
                        });
                    }
                }
            }

            if (inTableBox != null) {
                if (notInTable) {
                    if (thisPane.getChildren().contains(inTableBox)) {
                        thisPane.getChildren().remove(inTableBox);
                    }
                } else {
                    if (!thisPane.getChildren().contains(inTableBox)) {
                        thisPane.getChildren().add(2, inTableBox);
                    }
                }
            }
            if (inTable()) {
                if (!inTableBox.getChildren().contains(locationBox)) {
                    inTableBox.getChildren().add(locationBox);
                }
                if (externalBox != null) {
                    if (externalBox.getChildren().contains(externalDefBox)) {
                        externalBox.getChildren().remove(externalDefBox);
                    }
                }
            } else {
                if (inTableBox != null && inTableBox.getChildren().contains(locationBox)) {
                    inTableBox.getChildren().remove(locationBox);
                }
                if (externalBox != null) {
                    if (externalBox.getChildren().contains(externalDefBox)) {
                        externalBox.getChildren().remove(externalDefBox);
                    }
                }
                if ((systemClipboardRadio != null && systemClipboardRadio.isSelected())
                        || myBoxClipboardRadio.isSelected()
                        || databaseRadio.isSelected()) {
                    if (externalDefBox.getChildren().contains(fileBox)) {
                        externalDefBox.getChildren().remove(fileBox);
                    }
                } else {
                    if (!externalDefBox.getChildren().contains(fileBox)) {
                        externalDefBox.getChildren().add(fileBox);
                    }
                }
            }

            refreshStyle(thisPane);

            UserConfig.setString(baseName + "DataTarget", format.name());

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
        return format;
    }

    public void setTarget(TargetType type) {
        if (type == null) {
            return;
        }
        initTarget(type);
        checkTarget();
        if (extFormatPane != null) {
            extFormatPane.setDisable(true);
        }
        if (internalFormatPane != null) {
            internalFormatPane.setDisable(true);
        }
        if (matrixOptionsController != null && tableController != null) {
            matrixBox.setDisable(true);
        }
    }

    public synchronized void sourceChanged() {
        try {
            if (tableController == null || tableController.data2D == null) {
                return;
            }
            nameInput.setText(tableController.data2D.getName());
            if (rowSelector == null) {
                return;
            }
            int thisSelect = rowSelector.getSelectionModel().getSelectedIndex();
            List<String> rows = new ArrayList<>();
            if (tableController.tableData != null) {
                for (long i = 0; i < tableController.tableData.size(); i++) {
                    rows.add("" + (i + 1));
                }
            }
            rowSelector.getItems().setAll(rows);
            rowSelector.getSelectionModel().select(thisSelect >= 0 ? thisSelect : 0);

            String selectedCol = colSelector.getSelectionModel().getSelectedItem();
            if (tableController.data2D.getColumns() != null) {
                List<String> names = tableController.data2D.columnNames();
                for (Data2DColumn column : tableController.data2D.getColumns()) {
                    if (!column.isId()) {
                        names.add(column.getColumnName());
                    }
                }
                colSelector.getItems().setAll(names);
                if (selectedCol != null) {
                    colSelector.setValue(selectedCol);
                } else {
                    colSelector.getSelectionModel().select(0);
                }
            } else {
                colSelector.getItems().clear();
            }

            dbController.setData(data2D);

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public void setNotInTable(boolean notInTable) {
        this.notInTable = notInTable;
        checkTarget();
    }

    public void setNoFormat() {
        thisPane.getChildren().clear();
        thisPane.getChildren().add(invalidAsPane);
    }

    public boolean inTable() {
        return !notInTable && inTableBox != null
                && (insertRadio.isSelected() || appendRadio.isSelected() || replaceRadio.isSelected());
    }

    public boolean validateTarget() {
        try {
            if (format == null) {
                return false;
            }
            switch (format) {
                case CSV:
                case Excel:
                case Text:
                case Matrix:
                case JSON:
                case XML:
                case HTML:
                case PDF:
                    File file = file();
                    if (file == null) {
                        popError(message("InvalidParameter") + ": " + message("FileName"));
                        return false;
                    } else {
                        return true;
                    }
            }
            return true;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return false;
        }
    }

    public TargetType format() {
        return format;
    }

    public String dataName() {
        String name = nameInput.getText();
        return name != null && !name.isBlank() ? name.trim() : null;
    }

    public File file() {
        if (targetFileController == null) {
            return FileTmpTools.getTempFile();
        } else {
            return targetFileController.makeTargetFile();
        }
    }

    public int row() {
        if (!inTable()) {
            return -1;
        }
        return rowSelector.getSelectionModel().getSelectedIndex();
    }

    public int col() {
        if (!inTable() || tableController == null) {
            return -1;
        }
        return tableController.data2D.colOrder(colSelector.getSelectionModel().getSelectedItem());
    }

    public InvalidAs invalidAs() {
        if (zeroInvalidRadio != null && zeroInvalidRadio.isSelected()) {
            return InvalidAs.Zero;
        } else if (emptyInvalidRadio != null && emptyInvalidRadio.isSelected()) {
            return InvalidAs.Empty;
        } else if (skipInvalidRadio != null && skipInvalidRadio.isSelected()) {
            return InvalidAs.Skip;
        } else if (nullInvalidRadio != null && nullInvalidRadio.isSelected()) {
            return InvalidAs.Null;
        } else if (useInvalidRadio != null && useInvalidRadio.isSelected()) {
            return InvalidAs.Use;
        } else {
            return DefaultInvalidAs;
        }
    }

    public Data2DWriter pickWriter() {
        try {
            if (isInvalid()) {
                close();
                return null;
            }
            if (format == null || !validateTarget()) {
                return null;
            }
            targetName = dataName();
            if (targetName == null || targetName.isBlank()) {
                if (data2D != null) {
                    targetName = data2D.getName();
                }
            }
            if (targetName == null || targetName.isBlank()) {
                targetName = "Data2D";
            } else if (targetName.startsWith(TmpTable.TmpTablePrefix)
                    || targetName.startsWith(TmpTable.TmpTablePrefix.toLowerCase())) {
                targetName = targetName.substring(TmpTable.TmpTablePrefix.length());
            }
            Data2DWriter writer;
            switch (format) {
                case DatabaseTable:
                    writer = dbController.pickTableWriter();
                    break;
                default:
                    if (format != TargetType.Matrix) {
                        targetFile = file();
                        if (targetFile == null) {
                            targetFile = Data2DConvertTools.targetFile(targetName, format);
                        }
                        if (targetFile == null) {
                            popError(message("InvalidParameter") + ": " + message("TargetFile"));
                            return null;
                        }
                    }
                    writer = pickWriter(format);
                    break;
            }
            if (writer == null) {
                return null;
            }
            if (data2D != null) {
                writer.setColumns(data2D.getColumns())
                        .setHeaderNames(data2D.columnNames());
            }
            writer.setDataName(targetName)
                    .setPrintFile(targetFile)
                    .setRecordTargetFile(true)
                    .setRecordTargetData(true)
                    .setInvalidAs(invalidAs());
            if (writer instanceof SystemClipboardWriter) {
                ((SystemClipboardWriter) writer).setController(tableController);
            }
            return writer;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    @Override
    public void cleanPane() {
        try {
            if (tableController != null) {
                tableController.statusNotify.removeListener(tableStatusListener);
            }
            tableStatusListener = null;
            tableController = null;
        } catch (Exception e) {
        }
        super.cleanPane();
    }

}
