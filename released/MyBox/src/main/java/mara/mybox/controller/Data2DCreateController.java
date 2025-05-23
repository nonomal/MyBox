package mara.mybox.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import mara.mybox.data2d.Data2D;
import mara.mybox.data2d.Data2D_Attributes.TargetType;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.CSV;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.DatabaseTable;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Excel;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Matrix;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.MyBoxClipboard;
import static mara.mybox.data2d.Data2D_Attributes.TargetType.Text;
import mara.mybox.data2d.DataClipboard;
import mara.mybox.data2d.DataFileCSV;
import mara.mybox.data2d.DataFileExcel;
import mara.mybox.data2d.DataFileText;
import mara.mybox.data2d.DataMatrix;
import mara.mybox.data2d.DataTable;
import mara.mybox.data2d.operate.Data2DSaveAs;
import mara.mybox.data2d.writer.Data2DWriter;
import mara.mybox.db.data.Data2DDefinition;
import mara.mybox.db.table.TableData2DColumn;
import mara.mybox.db.table.TableData2DDefinition;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2025-4-11
 * @License Apache License Version 2.0
 */
public class Data2DCreateController extends Data2DAttributesController {

    protected BaseController caller;
    protected int rowsNumber;

    @FXML
    protected ControlData2DNew attributesController;
    @FXML
    protected ComboBox<String> rowsSelector;
    @FXML
    protected RadioButton randomRadio, randomNonnegativeRadio,
            emptyRadio, nullRadio, zeroRadio, oneRadio;

    @Override
    public void initValues() {
        try {
            super.initValues();

            tableData2DDefinition = new TableData2DDefinition();
            tableData2DColumn = new TableData2DColumn();
            tableData = FXCollections.observableArrayList();

            dataNameInput = attributesController.nameInput;
            descInput = attributesController.descInput;
            scaleSelector = attributesController.scaleSelector;
            randomSelector = attributesController.randomSelector;

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    protected void setCaller(BaseController controller) {
        try {
            caller = controller;
            attributesController.setParameters(this);

            rowsSelector.getItems().addAll(
                    Arrays.asList("3", "10", "0", "5", "1", "20", "50", "100", "300", "500")
            );
            rowsSelector.setValue("3");

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void loadValues() {
        try {
            TargetType type = attributesController.format;
            switch (type) {
                case CSV:
                    data2D = new DataFileCSV();
                    break;
                case Excel:
                    data2D = new DataFileExcel();
                    break;
                case Text:
                    data2D = new DataFileText();
                    break;
                case Matrix:
                    data2D = new DataMatrix(attributesController.matrixType());
                    break;
                case MyBoxClipboard:
                    data2D = new DataClipboard();
                    break;
                case DatabaseTable:
                    data2D = new DataTable();
                    break;
                default:
                    data2D = new DataFileCSV();
            }
            data2D.setColumns(data2D.tmpColumns(3));
            columnsController.setParameters(this);
            attributesController.dbController.setParameters(this, data2D);

            isSettingValues = true;
            dataNameInput.setText(data2D.getDataName());
            scaleSelector.setValue(data2D.getScale() + "");
            randomSelector.setValue(data2D.getMaxRandom() + "");
            descInput.setText(data2D.getComments());
            attributesChanged(false);
            columnsChanged(false);
            isSettingValues = false;
            checkStatus();
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void columnsChanged(boolean changed) {
        super.columnsChanged(changed);
        data2D.setColumns(columnsController.pickColumns());
        if (data2D.isTable()) {
            attributesController.dbController.setData(data2D);
        }
    }

    @Override
    public void checkStatus() {
        setTitle(baseTitle + " - " + data2D.displayName());
    }

    @FXML
    @Override
    public void okAction() {
        data2D = pickValues();
        if (data2D == null) {
            return;
        }
        try {
            rowsNumber = Integer.parseInt(rowsSelector.getValue());
        } catch (Exception e) {
            rowsNumber = -1;
        }
        if (rowsNumber < 0) {
            popError(message("InvalidParameter") + ": " + message("RowsNumber"));
            return;
        }
        attributesController.data2D = data2D;
        Data2DWriter writer = attributesController.pickWriter();
        if (writer == null) {
            return;
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {
            Data2D sourceData, targetData;
            Random random;
            int colsNumber;

            @Override
            protected boolean handle() {
                try {
                    sourceData = Data2D.create(Data2DDefinition.DataType.CSV);
                    sourceData.setColumns(data2D.getColumns());
                    colsNumber = sourceData.columnsNumber();
                    random = new Random();
                    if (rowsNumber > data2D.pagination.pageSize) {
                        ok = writeSourceFile();
                    } else {
                        ok = writeSourcePage();
                    }
                    if (!ok) {
                        return false;
                    }
                    data2D.startTask(this, null);
                    Data2DSaveAs operate = Data2DSaveAs.writeTo(sourceData, writer);
                    if (operate == null) {
                        return false;
                    }
                    operate.setController(myController)
                            .setTask(this)
                            .start();
                    if (operate.isFailed()) {
                        return false;
                    }
                    targetData = writer.getTargetData();
                    return targetData != null;
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
            }

            protected boolean writeSourceFile() {
                try {
                    File tmpFile = FileTmpTools.getTempFile();
                    Charset charset = Charset.forName("utf-8");
                    String line, value;
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile, charset, false))) {
                        line = null;
                        for (String name : sourceData.columnNames()) {
                            if (line == null) {
                                line = name;
                            } else {
                                line += "," + name;
                            }
                        }
                        writer.write(line + System.lineSeparator());
                        for (int i = 0; i < rowsNumber; i++) {
                            line = null;
                            for (int j = 0; j < colsNumber; j++) {
                                value = makeValue();
                                if (line == null) {
                                    line = value;
                                } else {
                                    line += "," + value;
                                }
                            }
                            writer.write(line + System.lineSeparator());
                        }
                        writer.flush();
                    } catch (Exception e) {
                        MyBoxLog.error(e);
                        return false;
                    }
                    sourceData.setFile(tmpFile).setHasHeader(true).setCharset(charset).setDelimiter(",");
                    sourceData.saveAttributes();
                    return true;
                } catch (Exception e) {
                    MyBoxLog.error(e);
                    return false;
                }
            }

            protected boolean writeSourcePage() {
                try {
                    tableData.clear();
                    for (int i = 0; i < rowsNumber; i++) {
                        List<String> row = new ArrayList<>();
                        row.add("" + (i + 1));
                        for (int j = 0; j < colsNumber; j++) {
                            if (randomRadio.isSelected()) {
                                row.add(data2D.randomString(random, false));
                            } else if (randomNonnegativeRadio.isSelected()) {
                                row.add(data2D.randomString(random, true));
                            } else if (emptyRadio.isSelected()) {
                                row.add("");
                            } else if (nullRadio.isSelected()) {
                                row.add(null);
                            } else if (zeroRadio.isSelected()) {
                                row.add("0");
                            } else if (oneRadio.isSelected()) {
                                row.add("1");
                            }
                        }
                        tableData.add(row);
                    }
                    sourceData.setFile(null);
                    sourceData.setPageData(tableData);
                    return true;
                } catch (Exception e) {
                    MyBoxLog.error(e);
                    return false;
                }
            }

            protected String makeValue() {
                if (randomRadio.isSelected()) {
                    return data2D.randomString(random, false);
                } else if (randomNonnegativeRadio.isSelected()) {
                    return data2D.randomString(random, true);
                } else if (emptyRadio.isSelected()) {
                    return "";
                } else if (nullRadio.isSelected()) {
                    return "";
                } else if (zeroRadio.isSelected()) {
                    return "0";
                } else if (oneRadio.isSelected()) {
                    return "1";
                } else {
                    return "";
                }
            }

            @Override
            protected void whenSucceeded() {
                if (caller == null) {
                    Data2DManufactureController.openDef(targetData);

                } else if (caller instanceof Data2DManufactureController c) {
                    c.loadDef(targetData, false);
                    c.popInformation(message("Created"));

                } else if (caller instanceof Data2DManageController c) {
                    c.loadList();
                    c.loadDef(targetData);
                    c.popInformation(message("Created"));
                }
                close();
            }

            @Override
            protected void finalAction() {
                super.finalAction();
                data2D.stopTask();
            }
        };
        start(task);
    }

    @Override
    public boolean keyEventsFilter(KeyEvent event) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == attributesTab) {
            if (attributesController.keyEventsFilter(event)) {
                return true;
            }
        } else if (tab == columnsTab) {
            if (columnsController.keyEventsFilter(event)) {
                return true;
            }
        }
        return super.keyEventsFilter(event);
    }

    /*
        static
     */
    public static Data2DCreateController open(BaseController parent) {
        try {
            Data2DCreateController controller = (Data2DCreateController) WindowTools.childStage(
                    parent, Fxmls.Data2DCreateFxml);
            controller.setCaller(parent);
            controller.requestMouse();
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

}
