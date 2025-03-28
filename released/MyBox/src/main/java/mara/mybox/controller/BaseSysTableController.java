package mara.mybox.controller;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.table.BaseTable;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxFileTools;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.fxml.FxTask;
import mara.mybox.value.FileFilters;
import mara.mybox.value.Languages;

/**
 * @param <P> Data
 * @Author Mara
 * @CreateDate 2019-12-18
 * @License Apache License Version 2.0
 */
public abstract class BaseSysTableController<P> extends BaseTablePagesController<P> {

    protected BaseTable tableDefinition;

    @FXML
    protected Button examplesButton, resetButton,
            importButton, exportButton, chartsButton, queryButton, moveDataButton, orderby;
    @FXML
    protected Label queryConditionsLabel;

    public BaseSysTableController() {
        tableName = "";
        TipsLabelKey = "TableTips";
    }

    @Override
    public void initValues() {
        try {
            super.initValues();
            setTableDefinition();
            setTableDefinition(tableDefinition);

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    // define tableDefinition here
    public void setTableDefinition() {
    }

    public void setTableDefinition(BaseTable t) {
        tableDefinition = t;
        if (tableDefinition != null) {
            tableName = tableDefinition.getTableName();
            idColumnName = tableDefinition.getIdColumnName();
        }
    }

    @Override
    public void postLoadedTableData() {
        super.postLoadedTableData();
        if (queryConditionsLabel != null) {
            queryConditionsLabel.setText(queryConditionsString);
        }
    }

    @Override
    public List<P> readPageData(FxTask currentTask, Connection conn) {
        if (tableDefinition != null) {
            return tableDefinition.queryConditions(conn, queryConditions, orderColumns,
                    pagination.startRowOfCurrentPage, pagination.pageSize);
        } else {
            return null;
        }
    }

    @Override
    public long readDataSize(FxTask currentTask, Connection conn) {
        long size = 0;
        if (tableDefinition != null) {
            if (queryConditions != null) {
                size = tableDefinition.conditionSize(conn, queryConditions);
            } else {
                size = tableDefinition.size(conn);
            }
        }
        dataSizeLoaded = true;
        return size;
    }

    @Override
    protected int deleteData(FxTask currentTask, List<P> data) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        if (tableDefinition != null) {
            return tableDefinition.deleteData(data);
        }
        return 0;
    }

    @Override
    protected long clearData(FxTask currentTask) {
        if (tableDefinition != null) {
            return tableDefinition.deleteCondition(queryConditions);
        } else {
            return 0;
        }
    }

    @FXML
    @Override
    public void refreshAction() {
        loadTableData();
    }

    @FXML
    protected void importAction() {
        File file = FxFileTools.selectFile(this);
        if (file == null) {
            return;
        }
        if (task != null && !task.isQuit()) {
            return;
        }
        task = new FxSingletonTask<Void>(this) {

            @Override
            protected boolean handle() {
                importData(file);
                return true;
            }

            @Override
            protected void whenSucceeded() {
                popSuccessful();
                refreshAction();
            }
        };
        start(task);
    }

    protected void importData(File file) {
        DerbyBase.importData(tableName, file.getAbsolutePath(), false);
    }

    @FXML
    protected void exportAction() {
        final File file = chooseFile(defaultTargetPath(),
                Languages.message(tableName) + ".txt", FileFilters.AllExtensionFilter);
        if (file == null) {
            return;
        }
        if (task != null && !task.isQuit()) {
            return;
        }
        task = new FxSingletonTask<Void>(this) {

            @Override
            protected boolean handle() {
                DerbyBase.exportData(tableName, file.getAbsolutePath());
                recordFileWritten(file);
                return true;
            }

            @Override
            protected void whenSucceeded() {
                popSuccessful();
                TextEditorController.open(file);
            }
        };
        start(task);
    }

    @FXML
    protected void analyseAction() {

    }

}
