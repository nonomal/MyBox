package mara.mybox.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.SoundTools;
import mara.mybox.fxml.WindowTools;
import mara.mybox.tools.DateTools;
import mara.mybox.value.Fxmls;
import mara.mybox.value.Languages;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2021-12-8
 * @License Apache License Version 2.0
 */
public class Data2DExportController extends BaseTaskController {

    protected ControlData2DEditTable tableController;
    protected String filePrefix;
    protected List<String> selectedColumnsNames;
    protected List<Integer> selectedColumnsIndices, selectedRowsIndices;

    @FXML
    protected ControlData2DSelect selectController;
    @FXML
    protected ToggleGroup rowGroup;
    @FXML
    protected RadioButton rowAllRadio, rowTableRadio;
    @FXML
    protected VBox dataVBox, formatVBox, pdfOptionsVBox, targetVBox;
    @FXML
    protected ControlPdfWriteOptions pdfOptionsController;
    @FXML
    protected ControlDataConvert convertController;
    @FXML
    protected CheckBox rowNumberCheck;

    public Data2DExportController() {
        baseTitle = Languages.message("Export");
    }

    @Override
    public void setStageStatus() {
        setAsPop(baseName);
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            convertController.setControls(this, pdfOptionsController);

            pdfOptionsController.pixSizeRadio.setDisable(true);
            pdfOptionsController.standardSizeRadio.fire();

            rowNumberCheck.setSelected(UserConfig.getBoolean(baseName + "WithRowNumber", false));
            rowNumberCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    UserConfig.setBoolean(baseName + "WithRowNumber", rowNumberCheck.isSelected());
                }
            });

        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    public void setParameters(ControlData2DEditTable tableController) {
        try {
            this.tableController = tableController;
            selectController.setParameters(tableController);
            getMyStage().setTitle(tableController.getBaseTitle());

        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    @Override
    public boolean checkOptions() {
        try {
            targetPath = targetPathController.file;
            if (targetPath == null) {
                popError(message("InvalidParameters"));
                return false;
            }
            if (!tableController.data2D.hasData()) {
                popError(message("NoData"));
                return false;
            }

            selectedColumnsIndices = selectController.selectedColumnsIndices();
            if (selectedColumnsIndices.isEmpty()) {
                popError(message("SelectToHandle"));
                return false;
            }
            selectedColumnsNames = selectController.selectedColumnsNames();

            if (rowTableRadio.isSelected()) {
                selectedRowsIndices = selectController.selectedRowsIndices();
                if (selectedRowsIndices.isEmpty()) {
                    popError(message("SelectToHandle"));
                    return false;
                }
            } else {
                selectedRowsIndices = null;
            }
            if (rowNumberCheck.isSelected()) {
                selectedColumnsNames.add(0, message("RowNumber"));
            }

            filePrefix = tableController.data2D.getDataName();
            if (filePrefix == null || filePrefix.isBlank()) {
                filePrefix = DateTools.nowFileString();
            }
            return convertController.initParameters();
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return false;
        }
    }

    @Override
    protected void beforeTask() {
        try {
            dataVBox.setDisable(true);
            formatVBox.setDisable(true);
            targetVBox.setDisable(true);
            pdfOptionsVBox.setDisable(true);

        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    @Override
    protected boolean doTask() {
        try {
            convertController.targetPath = targetPath;
            convertController.names = selectedColumnsNames;
            convertController.openWriters(filePrefix, targetPathController.isSkip());

            if (rowTableRadio.isSelected()) {
                for (Integer row : selectedRowsIndices) {
                    List<String> dataRow = tableController.tableData.get(row);
                    List<String> exportRow = new ArrayList<>();
                    if (rowNumberCheck.isSelected()) {
                        exportRow.add(message("Row") + (row + 1));
                    }
                    for (Integer col : selectedColumnsIndices) {
                        exportRow.add(dataRow.get(col + 1));
                    }
                    convertController.writeRow(exportRow);
                }
            } else if (!tableController.data2D.isMutiplePages()) {
                for (int row = 0; row < tableController.tableData.size(); row++) {
                    List<String> dataRow = tableController.tableData.get(row);
                    List<String> exportRow = new ArrayList<>();
                    if (rowNumberCheck.isSelected()) {
                        exportRow.add(message("Row") + (row + 1));
                    }
                    for (Integer col : selectedColumnsIndices) {
                        exportRow.add(dataRow.get(col + 1));
                    }
                    convertController.writeRow(exportRow);
                }
            } else {
                tableController.data2D.setTask(task);
                tableController.data2D.export(convertController, selectedColumnsIndices, rowNumberCheck.isSelected());
                tableController.data2D.setTask(null);
            }

            convertController.closeWriters();
            return true;
        } catch (Exception e) {
            if (task != null) {
                task.setError(e.toString());
            }
            MyBoxLog.error(e.toString());
            return false;
        }
    }

    @Override
    protected void afterSuccess() {
        try {
            SoundTools.miao3();
            if (targetPath != null && targetPath.exists()) {
                browseURI(targetPath.toURI());
                recordFileOpened(targetPath);
            } else {
                popInformation(message("NoFileGenerated"));
            }
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    @Override
    protected void afterTask() {
        try {
            dataVBox.setDisable(false);
            formatVBox.setDisable(false);
            targetVBox.setDisable(false);
            pdfOptionsVBox.setDisable(false);
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    @Override
    public void cancelTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        convertController.closeWriters();
    }

    @Override
    public void cancelAction() {
        cancelTask();
        close();
    }


    /*
        static
     */
    public static Data2DExportController open(ControlData2DEditTable tableController) {
        try {
            Data2DExportController controller = (Data2DExportController) WindowTools.openChildStage(
                    tableController.getMyWindow(), Fxmls.Data2DExportFxml, false);
            controller.setParameters(tableController);
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

}