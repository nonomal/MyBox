package mara.mybox.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.data.DataTag;
import mara.mybox.db.table.BaseTable;
import mara.mybox.db.table.TableColor;
import mara.mybox.db.table.TableDataNode;
import mara.mybox.db.table.TableDataTag;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fximage.FxColorTools;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.fxml.cell.TableAutoCommitCell;
import mara.mybox.fxml.cell.TableColorEditCell;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2024-11-4
 * @License Apache License Version 2.0
 */
public class DataTreeTagsController extends BaseTableViewController<DataTag> {

    protected ControlDataNodeTags nodeTagsController;
    protected TableDataNode dataNodeTable;
    protected TableDataTag dataTagTable;
    protected BaseTable dataTable;
    protected List<DataTag> deleted = new ArrayList<>();

    @FXML
    protected TableColumn<DataTag, String> tagColumn;
    @FXML
    protected TableColumn<DataTag, Color> colorColumn;
    @FXML
    protected Label titleLabel;

    @Override
    public void initColumns() {
        try {
            super.initColumns();

            tableView.setEditable(true);

            tagColumn.setEditable(true);
            tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
            tagColumn.setCellFactory(TableAutoCommitCell.forStringColumn());
            tagColumn.setCellFactory(new Callback<TableColumn<DataTag, String>, TableCell<DataTag, String>>() {
                @Override
                public TableCell<DataTag, String> call(
                        TableColumn<DataTag, String> param) {
                    try {
                        TableAutoCommitCell<DataTag, String> cell
                                = new TableAutoCommitCell<DataTag, String>(new DefaultStringConverter()) {

                            @Override
                            public boolean setCellValue(String value) {
                                try {
                                    if (value == null || value.isBlank() || !isEditingRow()) {
                                        return false;
                                    }
                                    for (int i = 0; i < tableData.size(); i++) {
                                        String tagName = tableData.get(i).getTag();
                                        if (value.equals(tagName)) {
                                            cancelEdit();
                                            return false;
                                        }
                                    }
                                    DataTag row = tableData.get(editingRow);
                                    if (row == null) {
                                        cancelEdit();
                                        return false;
                                    }
                                    row.setTag(value);
                                    return super.setCellValue(value);
                                } catch (Exception e) {
                                    MyBoxLog.debug(e);
                                    return false;
                                }
                            }

                        };
                        return cell;
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            tagColumn.getStyleClass().add("editable-column");

            colorColumn.setEditable(true);
            TableColor tableColor = new TableColor();
            colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
            colorColumn.setCellFactory(new Callback<TableColumn<DataTag, Color>, TableCell<DataTag, Color>>() {
                @Override
                public TableCell<DataTag, Color> call(
                        TableColumn<DataTag, Color> param) {
                    TableColorEditCell<DataTag> cell = new TableColorEditCell<DataTag>(myController, tableColor) {
                        @Override
                        public void colorChanged(int index, Color color) {
                            if (isSettingValues || color == null
                                    || index < 0 || index >= tableData.size()) {
                                return;
                            }
                            if (color.equals(tableData.get(index).getColor())) {
                                return;
                            }
                            tableData.get(index).setColor(color);
                        }
                    };
                    return cell;
                }
            });
            colorColumn.getStyleClass().add("editable-column");

            checkButtons();
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public void setParameters(ControlDataNodeTags controller) {
        try {
            nodeTagsController = controller;
            parentController = controller;
            baseName = controller.baseName;
            dataTable = controller.dataTable;
            dataNodeTable = controller.dataNodeTable;
            dataTagTable = controller.dataTagTable;
            baseTitle = message("Tags") + " - " + message(dataTable.getTableName());

            loadTags();
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public void loadTags() {
        if (task != null) {
            task.cancel();
        }
        tableData.clear();
        deleted.clear();
        if (dataTagTable == null) {
            return;
        }
        task = new FxTask<Void>(this) {
            private List<DataTag> tags;

            @Override
            protected boolean handle() {
                try (Connection conn = DerbyBase.getConnection()) {
                    tags = dataTagTable.readAll(conn);
                } catch (Exception e) {
                    MyBoxLog.error(e);
                    return false;
                }
                return true;
            }

            @Override
            protected void whenSucceeded() {
                if (tags != null && !tags.isEmpty()) {
                    tableData.setAll(tags);
                }
            }

            @Override
            protected void finalAction() {
                super.finalAction();
                tableChanged(false);
                notifyLoaded();
            }

        };
        start(task);
    }

    @Override
    public void tableChanged(boolean changed) {
        if (isSettingValues || isSettingTable) {
            return;
        }
        super.tableChanged(changed);
        setTitle(baseTitle + (changed ? " *" : ""));
    }

    @FXML
    @Override
    public void recoverAction() {
        loadTags();
    }

    @FXML
    @Override
    public void addAction() {
        DataTag tag = DataTag.create().setTag(message("Tag") + new Date().getTime());
        tableData.add(0, tag);
    }

    @FXML
    @Override
    public void deleteAction() {
        try {
            List<DataTag> selected = selectedItems();
            if (selected == null || selected.isEmpty()) {
                clearAction();
                return;
            }
            for (DataTag tag : selected) {
                if (tag.getTagid() >= 0 && !deleted.contains(tag)) {
                    deleted.add(tag);
                }
            }
            tableData.removeAll(selected);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @FXML
    public void randomColors() {
        try {
            isSettingValues = true;
            Random r = new Random();
            for (int i = 0; i < tableData.size(); i++) {
                tableData.get(i).setColor(FxColorTools.randomColor(r));
            }
            tableView.refresh();
            isSettingValues = false;
            tableChanged(true);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @FXML
    @Override
    public void saveAction() {
        if (task != null) {
            task.cancel();
        }
        task = new FxTask<Void>(this) {

            @Override
            protected boolean handle() {
                try (Connection conn = DerbyBase.getConnection()) {
                    conn.setAutoCommit(false);
                    for (DataTag tag : tableData) {
                        if (tag.getTagid() >= 0) {
                            dataTagTable.updateData(conn, tag);
                        } else {
                            dataTagTable.insertData(conn, tag);
                        }
                    }
                    for (DataTag tag : deleted) {
                        dataTagTable.deleteData(conn, tag);
                    }
                    conn.commit();
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
                return true;
            }

            @Override
            protected void whenSucceeded() {
                deleted.clear();
                tableChanged(false);
                if (nodeTagsController != null) {
                    nodeTagsController.recoverAction();
                }
            }

        };
        start(task);
    }

    @FXML
    public void queryTags() {
        List<DataTag> selected = selectedItems();
        if (selected == null || selected.isEmpty()) {
            selected = tableData;
        }
        if (selected == null || selected.isEmpty()) {
            popError(message("SelectToHandle"));
            return;
        }
//        dataController.tableController.queryTags(selected);
    }

    @Override
    public boolean keyESC() {
        cancelAction();
        close();
        return true;
    }


    /*
        static methods
     */
    public static DataTreeTagsController open(ControlDataNodeTags tagsController) {
        DataTreeTagsController controller = (DataTreeTagsController) WindowTools.branchStage(
                tagsController, Fxmls.DataTreeTagsFxml);
        controller.setParameters(tagsController);
        controller.requestMouse();
        return controller;
    }

}