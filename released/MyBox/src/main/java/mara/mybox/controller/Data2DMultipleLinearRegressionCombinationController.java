package mara.mybox.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import mara.mybox.calculation.OLSLinearRegression;
import mara.mybox.data2d.Data2D_Attributes.InvalidAs;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.SingletonTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.tools.DoubleTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2022-8-19
 * @License Apache License Version 2.0
 */
public class Data2DMultipleLinearRegressionCombinationController extends BaseData2DRegressionController {

    protected ObservableList<List<String>> results;
    protected Map<String, List<String>> namesMap;
    protected OLSLinearRegression regression;
    protected List<String> names;

    @FXML
    protected ControlData2DMultipleLinearRegressionTable resultsController;

    public Data2DMultipleLinearRegressionCombinationController() {
        baseTitle = message("MultipleLinearRegressionCombination");
        TipsLabelKey = "MultipleLinearRegressionCombinationTips";
        defaultScale = 8;
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            resultsController.setParameters(this);

        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    @Override
    public boolean initData() {
        try {
            if (!super.initData()) {
                return false;
            }
            invalidAs = InvalidAs.Blank;

            dataColsIndices = new ArrayList<>();
            if (otherColsIndices == null || otherColsIndices.isEmpty()) {
                otherColsIndices = data2D.columnIndices();
            }
            dataColsIndices.addAll(otherColsIndices);
            if (checkedColsIndices == null || checkedColsIndices.isEmpty()) {
                checkedColsIndices = data2D.columnIndices();
            }
            dataColsIndices.addAll(checkedColsIndices);

            names = new ArrayList<>();
            for (int col : dataColsIndices) {
                names.add(data2D.columnName(col));
            }
            regression = null;
            return true;
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return false;
        }
    }

    @Override
    protected void startOperation() {
        if (task != null) {
            task.cancel();
        }
        resultsController.clear();
        namesMap = new HashMap<>();
        task = new SingletonTask<Void>(this) {

            List<List<String>> data;
            int n, xLen, yLen;

            @Override
            protected boolean handle() {
                try {
                    data2D.startTask(task, filterController.filter);
                    if (isAllPages()) {
                        data = data2D.allRows(dataColsIndices, false);
                    } else {
                        data = filtered(dataColsIndices, false);
                    }
                    data2D.stopFilter();
                    if (data == null || data.isEmpty()) {
                        error = message("NoData");
                        return false;
                    }
                    n = data.size();
                    xLen = checkedColsIndices.size();
                    yLen = otherColsIndices.size();
                    List<Integer> xList = new ArrayList<>();
                    for (int i = yLen; i < dataColsIndices.size(); i++) {
                        xList.add(i);
                    }
                    for (int yIndex = 0; yIndex < yLen; yIndex++) {
                        for (int i = 0; i < xLen; i++) {
                            for (int j = i + 1; j <= xLen; j++) {
                                List<Integer> xIndices = xList.subList(i, j);
                                regress(yIndex, xIndices);
                            }
                        }
                    }
                    return true;
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
            }

            protected void regress(int yIndex, List<Integer> cIndices) {
                try {
                    String yName = names.get(yIndex);
                    List<String> xnames = new ArrayList<>();
                    List<Integer> xIndices = new ArrayList<>();
                    for (int i : cIndices) {
                        String name = names.get(i);
                        if (name.equals(yName)) {
                            continue;
                        }
                        xnames.add(name);
                        xIndices.add(i);
                    }
                    int k = xIndices.size();
                    if (k == 0) {
                        return;
                    }
                    String[] sy = new String[n];
                    String[][] sx = new String[n][k];
                    for (int r = 0; r < n; r++) {
                        List<String> row = data.get(r);
                        sy[r] = row.get(yIndex);
                        for (int c = 0; c < k; c++) {
                            sx[r][c] = row.get(xIndices.get(c));
                        }
                    }
                    regression = new OLSLinearRegression(interceptCheck.isSelected())
                            .setTask(task).setScale(scale)
                            .setInvalidAs(invalidAs)
                            .setyName(yName).setxNames(xnames);
                    regression.calculate(sy, sx);
                    List<String> row = new ArrayList<>();
                    String namesString = xnames.toString();
                    namesMap.put(namesString, xnames);
                    row.add(yName);
                    row.add(namesString);
                    row.add(DoubleTools.format(regression.getAdjustedRSqure(), scale));
                    row.add(DoubleTools.format(regression.getrSqure(), scale));
                    row.add(Arrays.toString(regression.getCoefficients()));
                    row.add(DoubleTools.format(regression.getIntercept(), scale));

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            resultsController.addRow(row);
                        }
                    });

                } catch (Exception e) {
                    error = e.toString();
                }
            }

            @Override
            protected void whenSucceeded() {
                resultsController.afterRegression();
            }

            @Override
            protected void finalAction() {
                super.finalAction();
                data2D.stopTask();
                task = null;
            }

        };
        start(task);

    }

    @FXML
    @Override
    public void dataAction() {
        resultsController.dataAction();
    }

    @FXML
    @Override
    public void viewAction() {
        resultsController.editAction();
    }


    /*
        static
     */
    public static Data2DMultipleLinearRegressionCombinationController open(ControlData2DLoad tableController) {
        try {
            Data2DMultipleLinearRegressionCombinationController controller = (Data2DMultipleLinearRegressionCombinationController) WindowTools.openChildStage(
                    tableController.getMyWindow(), Fxmls.Data2DMultipleLinearRegressionCombinationFxml, false);
            controller.setParameters(tableController);
            controller.requestMouse();
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

}