package mara.mybox.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import mara.mybox.data.StringTable;
import mara.mybox.data2d.tools.Data2DColumnTools;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.data.Data2DColumn;
import mara.mybox.db.table.TableData2DDefinition;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.cell.TableBooleanCell;
import mara.mybox.fxml.cell.TableCheckboxCell;
import mara.mybox.fxml.style.NodeStyleTools;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2021-10-16
 * @License Apache License Version 2.0
 */
public class ControlData2DColumns extends BaseData2DColumnsController {

    protected Data2DAttributesController attributesController;
    protected TableData2DDefinition tableData2DDefinition;

    @FXML
    protected Button headerButton;

    @Override
    public void initColumns() {
        try {
            super.initColumns();

            primaryColumn.setCellFactory(new TableBooleanCell());

            autoColumn.setCellFactory(new TableBooleanCell());

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void setControlsStyle() {
        try {
            super.setControlsStyle();
            NodeStyleTools.setTooltip(headerButton, new Tooltip(message("FirstLineDefineNames")));
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    protected void setParameters(Data2DAttributesController controller) {
        try {
            attributesController = controller;

            loadValues();
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    protected void loadValues() {
        try {
            data2D = attributesController.data2D;
            tableData2DDefinition = attributesController.tableData2DDefinition;
            tableData2DColumn = attributesController.tableData2DColumn;
            setData2DColumns();
            loadColumns();
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    protected void setData2DColumns() {
        try {
            if (data2D == null) {
                return;
            }
            if (data2D.isTable() && data2D.getSheet() != null) {
                typeColumn.setEditable(false);
                typeColumn.getStyleClass().clear();

                nameColumn.setEditable(false);
                nameColumn.getStyleClass().clear();

                notNullColumn.setEditable(false);
                notNullColumn.setCellFactory(new TableBooleanCell());
                notNullColumn.getStyleClass().clear();

                defaultColumn.setEditable(false);
                defaultColumn.getStyleClass().clear();

                lengthColumn.setEditable(false);
                lengthColumn.getStyleClass().clear();

                if (!tableView.getColumns().contains(primaryColumn)) {
                    tableView.getColumns().add(primaryColumn);
                    tableView.getColumns().add(autoColumn);
                }

            } else {
                if (data2D.isMatrix()) {
                    typeColumn.setEditable(false);
                    typeColumn.getStyleClass().clear();
                } else {
                    typeColumn.setEditable(true);
                    typeColumn.getStyleClass().add("editable-column");
                }

                nameColumn.setEditable(true);
                nameColumn.getStyleClass().add("editable-column");

                notNullColumn.setCellFactory(new Callback<TableColumn<Data2DColumn, Boolean>, TableCell<Data2DColumn, Boolean>>() {
                    @Override
                    public TableCell<Data2DColumn, Boolean> call(TableColumn<Data2DColumn, Boolean> param) {
                        try {
                            TableCheckboxCell<Data2DColumn, Boolean> cell = new TableCheckboxCell<>() {
                                @Override
                                protected boolean getCellValue(int rowIndex) {
                                    try {
                                        return tableData.get(rowIndex).isNotNull();
                                    } catch (Exception e) {
                                        return false;
                                    }
                                }

                                @Override
                                protected void setCellValue(int rowIndex, boolean value) {
                                    try {
                                        if (isChanging || rowIndex < 0) {
                                            return;
                                        }
                                        Data2DColumn column = tableData.get(rowIndex);
                                        if (column == null) {
                                            return;
                                        }
                                        if (value != column.isNotNull()) {
                                            isChanging = true;
                                            column.setNotNull(value);
                                            changed(true);
                                            isChanging = false;
                                        }
                                    } catch (Exception e) {
                                        MyBoxLog.debug(e);
                                    }
                                }
                            };
                            return cell;
                        } catch (Exception e) {
                            return null;
                        }
                    }
                });
                notNullColumn.setEditable(true);
                notNullColumn.getStyleClass().add("editable-column");

                lengthColumn.setEditable(true);
                lengthColumn.getStyleClass().add("editable-column");

                defaultColumn.setEditable(true);
                defaultColumn.getStyleClass().add("editable-column");

                if (tableView.getColumns().contains(primaryColumn)) {
                    tableView.getColumns().remove(primaryColumn);
                    tableView.getColumns().remove(autoColumn);
                }

            }

            checkButtons();

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public void loadColumns() {
        try {
            if (isSettingValues) {
                return;
            }
            isSettingValues = true;
            tableData.clear();
            if (data2D != null && data2D.hasColumns()) {
                int colIndex = 0;
                for (Data2DColumn column : data2D.getColumns()) {
                    Data2DColumn c = column.cloneAll();
                    c.setIndex(colIndex++);
                    tableData.add(c);
                }
            }
            isSettingValues = false;
            tableChanged(false);
            changed(false);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void changed(boolean changed) {
        if (isSettingValues) {
            return;
        }
        this.changed = changed;
        if (attributesController != null) {
            attributesController.columnsChanged(changed);
        }
    }

    @Override
    public void checkButtons() {
        super.checkButtons();
        numberColumnsButton.setDisable(data2D == null || data2D.isTable() || tableData.isEmpty());
        addRowsButton.setDisable(data2D == null || data2D.isInternalTable());
        deleteRowsButton.setDisable(data2D == null || data2D.isInternalTable() || isNoneSelected());
    }

    public List<Data2DColumn> pickColumns() {
        try {
            StringTable validateTable = Data2DColumnTools.validate(tableData);
            if (validateTable != null && !validateTable.isEmpty()) {
                validateTable.htmlTable();
                return null;
            }
            List<Data2DColumn> columns = new ArrayList<>();
            for (int i = 0; i < tableData.size(); i++) {
                Data2DColumn column = tableData.get(i).cloneAll();
                columns.add(column);
            }
            return columns;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    @FXML
    @Override
    public void recoverAction() {
        loadValues();
    }

    @FXML
    public void headerAction() {
        try {
            if (attributesController.data2D == null
                    || attributesController.tableData.isEmpty()) {
                popError(message("NoData"));
                return;
            }
            List<String> row = attributesController.tableData.get(0);
            if (row == null || row.size() < 2) {
                popError(message("InvalidData"));
                return;
            }
            List<String> names = new ArrayList<>();
            for (int i = 1; i < row.size(); i++) {
                String name = row.get(i);
                if (name == null || name.isBlank()) {
                    name = message("Column") + i;
                }
                DerbyBase.checkIdentifier(names, name, true);
            }
            setNames(names);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

}
