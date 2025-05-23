package mara.mybox.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import mara.mybox.calculation.Normalization;
import mara.mybox.data2d.writer.Data2DWriter;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.tools.DoubleTools;
import mara.mybox.tools.NumberTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2021-12-28
 * @License Apache License Version 2.0
 */
public class Data2DNormalizeController extends BaseData2DTaskTargetsController {

    @FXML
    protected ControlData2DNormalize normalizeController;

    public Data2DNormalizeController() {
        baseTitle = message("Normalize");
    }

    @Override
    public boolean handleRows() {
        try {
            List<Integer> filteredRowsIndices = sourceController.filteredRowsIndices;
            if (filteredRowsIndices == null || filteredRowsIndices.isEmpty()
                    || checkedColsIndices == null || checkedColsIndices.isEmpty()) {
                if (task != null) {
                    task.setError(message("NoData"));
                }
                return false;
            }
            int rowsNumber = filteredRowsIndices.size();
            int colsNumber = checkedColsIndices.size();
            String[][] matrix = new String[rowsNumber][colsNumber];
            for (int r = 0; r < rowsNumber; r++) {
                int row = filteredRowsIndices.get(r);
                List<String> tableRow = sourceController.tableData.get(row);
                for (int c = 0; c < colsNumber; c++) {
                    int col = checkedColsIndices.get(c);
                    matrix[r][c] = tableRow.get(col + 1);
                }
            }
            matrix = normalizeController.calculate(matrix, invalidAs);
            if (matrix == null) {
                return false;
            }
            boolean showRowNumber = showRowNumber();
            outputColumns = data2D.targetColumns(checkedColsIndices, otherColsIndices,
                    showRowNumber, message("Normalize"));
            outputData = new ArrayList<>();
            int otherColsNumber = otherColsIndices != null ? otherColsIndices.size() : 0;
            for (int r = 0; r < rowsNumber; r++) {
                List<String> row = new ArrayList<>();
                if (showRowNumber) {
                    row.add((filteredRowsIndices.get(r) + 1) + "");
                }
                for (int c = 0; c < colsNumber; c++) {
                    String s = matrix[r][c];
                    double d = DoubleTools.toDouble(s, invalidAs);
                    if (DoubleTools.invalidDouble(d)) {
                        row.add(null);
                    } else {
                        row.add(NumberTools.format(d, scale));
                    }
                }
                if (otherColsNumber > 0) {
                    int rowIndex = filteredRowsIndices.get(r);
                    List<String> tableRow = sourceController.tableData.get(rowIndex);
                    for (int c = 0; c < otherColsNumber; c++) {
                        row.add(tableRow.get(otherColsIndices.get(c) + 1));
                    }
                }
                outputData.add(row);
            }
            return true;
        } catch (Exception e) {
            if (task != null) {
                task.setError(e.toString());
            }
            return false;
        }
    }

    @Override
    public boolean handleAllData(FxTask currentTask, Data2DWriter writer) {
        if (normalizeController.rowsRadio.isSelected()) {
            Normalization.Algorithm a;
            if (normalizeController.sumRadio.isSelected()) {
                a = Normalization.Algorithm.Sum;
            } else if (normalizeController.zscoreRadio.isSelected()) {
                a = Normalization.Algorithm.ZScore;
            } else {
                a = Normalization.Algorithm.MinMax;
            }
            return data2D.normalizeRows(currentTask, writer,
                    a, checkedColsIndices, otherColsIndices,
                    normalizeController.from, normalizeController.to,
                    rowNumberCheck.isSelected(), colNameCheck.isSelected(), scale, invalidAs);

        } else if (normalizeController.allRadio.isSelected()) {
            if (normalizeController.minmaxRadio.isSelected()) {
                return data2D.normalizeMinMaxAll(currentTask, writer,
                        checkedColsIndices, otherColsIndices,
                        normalizeController.from, normalizeController.to,
                        rowNumberCheck.isSelected(), colNameCheck.isSelected(), scale, invalidAs);

            } else if (normalizeController.sumRadio.isSelected()) {
                return data2D.normalizeSumAll(currentTask, writer,
                        checkedColsIndices, otherColsIndices,
                        rowNumberCheck.isSelected(), colNameCheck.isSelected(), scale, invalidAs);

            } else if (normalizeController.zscoreRadio.isSelected()) {
                return data2D.normalizeZscoreAll(currentTask, writer,
                        checkedColsIndices, otherColsIndices,
                        rowNumberCheck.isSelected(), colNameCheck.isSelected(), scale, invalidAs);
            }

        } else {
            if (normalizeController.minmaxRadio.isSelected()) {
                return data2D.normalizeMinMaxColumns(currentTask, writer,
                        checkedColsIndices, otherColsIndices,
                        normalizeController.from, normalizeController.to,
                        rowNumberCheck.isSelected(), colNameCheck.isSelected(), scale, invalidAs);

            } else if (normalizeController.sumRadio.isSelected()) {
                return data2D.normalizeSumColumns(currentTask, writer,
                        checkedColsIndices, otherColsIndices,
                        rowNumberCheck.isSelected(), colNameCheck.isSelected(), scale, invalidAs);

            } else if (normalizeController.zscoreRadio.isSelected()) {
                return data2D.normalizeZscoreColumns(currentTask, writer,
                        checkedColsIndices, otherColsIndices,
                        rowNumberCheck.isSelected(), colNameCheck.isSelected(), scale, invalidAs);
            }
        }
        return false;
    }

    /*
        static
     */
    public static Data2DNormalizeController open(BaseData2DLoadController tableController) {
        try {
            Data2DNormalizeController controller = (Data2DNormalizeController) WindowTools.referredStage(
                    tableController, Fxmls.Data2DNormalizeFxml);
            controller.setParameters(tableController);
            controller.requestMouse();
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

}
