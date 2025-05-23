package mara.mybox.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import mara.mybox.controller.MyBoxLanguagesController.LanguageItem;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.fxml.PopTools;
import mara.mybox.fxml.WindowTools;
import mara.mybox.fxml.cell.TableAutoCommitCell;
import mara.mybox.fxml.style.NodeStyleTools;
import mara.mybox.tools.ConfigTools;
import mara.mybox.tools.FileDeleteTools;
import mara.mybox.value.AppVariables;
import static mara.mybox.value.AppVariables.useChineseWhenBlankTranslation;
import mara.mybox.value.Languages;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2019-12-27
 * @License Apache License Version 2.0
 */
public class MyBoxLanguagesController extends BaseTableViewController<LanguageItem> {

    protected String langName;
    protected boolean changed;

    @FXML
    protected ListView<String> listView;

    @FXML
    protected TableColumn<LanguageItem, String> keyColumn, englishColumn, chineseColumn, valueColumn;
    @FXML
    protected Label langLabel;
    @FXML
    protected Button addLangButton, useLangButton, deleteLangButton, editLangButton;
    @FXML
    protected CheckBox chineseCheck;

    public MyBoxLanguagesController() {
        baseTitle = message("ManageLanguages");
        TipsLabelKey = "MyBoxLanguagesTips";
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            chineseCheck.selectedProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    useChineseWhenBlankTranslation = chineseCheck.isSelected();
                    UserConfig.setBoolean("UseChineseWhenBlankTranslation", useChineseWhenBlankTranslation);
                }
            });
            chineseCheck.setSelected(useChineseWhenBlankTranslation);

            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object t, Object t1) {
                    checkListSelected();
                }
            });
            listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() > 1) {
                        editLang();
                    }
                }
            });

            refreshLang();
            checkListSelected();

            changed = false;

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void setControlsStyle() {
        try {
            super.setControlsStyle();

            NodeStyleTools.setTooltip(useLangButton, new Tooltip(message("SetAsInterfaceLanguage")));
            NodeStyleTools.setTooltip(editLangButton, new Tooltip(message("Edit") + "\n" + message("DoubleClick")));
            NodeStyleTools.setTooltip(chineseCheck, new Tooltip(message("BlankAsChinese")));
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    /*
        Languages List
     */
    @FXML
    public void refreshLang() {
        try {
            isSettingValues = true;
            listView.getItems().clear();
            listView.getItems().addAll(Languages.userLanguages());
            isSettingValues = false;
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @FXML
    public void addLang() {
        if (changed) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getTitle());
            alert.setHeaderText(getTitle());
            alert.setContentText(Languages.message("NeedSaveBeforeAction"));
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ButtonType buttonSave = new ButtonType(Languages.message("Save"));
            ButtonType buttonNotSave = new ButtonType(Languages.message("NotSave"));
            ButtonType buttonCancel = new ButtonType(Languages.message("Cancel"));
            alert.getButtonTypes().setAll(buttonSave, buttonNotSave, buttonCancel);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.toFront();

            Optional<ButtonType> result = alert.showAndWait();
            if (result == null || !result.isPresent()) {
                return;
            }
            if (result.get() == buttonSave) {
                saveAction();
                return;
            } else if (result.get() == buttonCancel) {
                return;
            }
        }
        String name = PopTools.askValue(getTitle(), message("InputLanguageComments"),
                message("InputLanguageName"), null);
        if (name == null || name.isBlank()) {
            return;
        }
        if ("en".equalsIgnoreCase(name) || "zh".equalsIgnoreCase(name)
                || name.startsWith("en_") || name.startsWith("zh_")) {
            popError(message("InputLanguageComments"));
            return;
        }
        langName = name.trim();
        langLabel.setText(langName);
        loadLanguage(null);
    }

    @FXML
    public void editLang() {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            popError(message("SelectToHandle"));
            return;
        }
        langName = selected;
        loadLanguage(selected);
    }

    @FXML
    public void deleteLang() {
        List<String> selected = listView.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            popError(message("SelectToHandle"));
            return;
        }
        if (!PopTools.askSure(getTitle(), Languages.message("SureDelete"))) {
            return;
        }
        String currentLang = Languages.getLangName();
        for (String name : selected) {
            File interfaceFile = Languages.interfaceLanguageFile(name);
            FileDeleteTools.delete(interfaceFile);
            if (name.equals(currentLang)) {
                UserConfig.deleteValue("language");
            }
        }
        isSettingValues = true;
        listView.getItems().removeAll(selected);
        langName = null;
        langLabel.setText("");
        tableData.clear();
        isSettingValues = false;
        checkListSelected();
        Languages.refreshBundle();
        popSuccessful();
    }

    @FXML
    public void useLang() {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            popError(message("SelectToHandle"));
            return;
        }
        Languages.setLanguage(selected);
        WindowTools.reloadAll();
    }

    protected void checkListSelected() {
        if (isSettingValues) {
            return;
        }
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            deleteLangButton.setDisable(true);
            editLangButton.setDisable(true);
            useLangButton.setDisable(true);
        } else {
            deleteLangButton.setDisable(false);
            editLangButton.setDisable(false);
            useLangButton.setDisable(false);
        }
    }

    @FXML
    public void openPath() {
        browseURI(AppVariables.MyBoxLanguagesPath.toURI());
    }

    /*
        Language Items
     */
    @Override
    public void initColumns() {
        try {
            keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
            englishColumn.setCellValueFactory(new PropertyValueFactory<>("english"));
            chineseColumn.setCellValueFactory(new PropertyValueFactory<>("chinese"));
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
            valueColumn.setCellFactory(new Callback<TableColumn<LanguageItem, String>, TableCell<LanguageItem, String>>() {
                @Override
                public TableCell<LanguageItem, String> call(TableColumn<LanguageItem, String> param) {
                    return new LanguageCell();
                }
            });
            valueColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<LanguageItem, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<LanguageItem, String> t) {
                    if (t == null) {
                        return;
                    }
                    LanguageItem row = t.getRowValue();
                    if (row == null) {
                        return;
                    }
                    String v = t.getNewValue();
                    String o = row.getValue();
                    if (v == null && o == null
                            || v != null && v.equals(o)) {
                        return;
                    }
                    row.setValue(v);
                    tableChanged(true);
                }
            });
            valueColumn.setEditable(true);
            valueColumn.getStyleClass().add("editable-column");

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void tableChanged(boolean changed) {
        if (isSettingValues) {
            return;
        }
        this.changed = changed;
        updateStatus();
    }

    @Override
    public void updateStatus() {
        setTitle(baseTitle + " - " + langName + (changed ? "*" : ""));
        langLabel.setText(langName + (changed ? "*" : ""));

        boolean isEmpty = tableData == null || tableData.isEmpty();
        boolean none = isNoneSelected();
        copyItemsButton.setDisable(none);
        saveButton.setDisable(isEmpty);
        chineseCheck.setDisable(isEmpty);
        lostFocusCommitCheck.setDisable(isEmpty);
    }

    public void loadLanguage(String name) {
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {
            private List<LanguageItem> items;

            @Override
            protected boolean handle() {
                try {
                    error = null;
                    Map<String, String> interfaceItems = null;
                    File interfaceFile = Languages.interfaceLanguageFile(name);
                    if (interfaceFile != null && interfaceFile.exists()) {
                        interfaceItems = ConfigTools.readValues(interfaceFile);
                    }
                    Enumeration<String> interfaceKeys = Languages.BundleEn.getKeys();
                    items = new ArrayList<>();
                    while (interfaceKeys.hasMoreElements()) {
                        String key = interfaceKeys.nextElement();
                        LanguageItem item = new LanguageItem(key,
                                Languages.BundleEn.getString(key), Languages.BundleZhCN.getString(key));
                        if (interfaceItems != null) {
                            item.setValue(interfaceItems.get(key));
                        }
                        items.add(item);
                    }

                } catch (Exception e) {
                    error = e.toString();
                }
                return true;
            }

            @Override
            protected void whenSucceeded() {
                if (error == null) {
                    isSettingValues = true;
                    tableData.setAll(items);
                    isSettingValues = false;
                    tableChanged(name == null);
                } else {
                    popError(error);
                }
            }
        };
        start(task);
    }

    @FXML
    public void copyEnglish() {
        List<LanguageItem> selected = tableView.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            return;
        }
        for (LanguageItem item : selected) {
            item.setValue(item.getEnglish());
        }
        tableView.refresh();
    }

    @FXML
    public void copyChinese() {
        List<LanguageItem> selected = tableView.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty()) {
            return;
        }
        for (LanguageItem item : selected) {
            item.setValue(item.getChinese());
        }
        tableView.refresh();
    }

    @FXML
    @Override
    public void saveAction() {
        if (langName == null) {
            return;
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {

            @Override
            protected boolean handle() {
                try {
                    error = null;
                    Map<String, String> interfaceItems = new HashMap();
                    File interfaceFile = Languages.interfaceLanguageFile(langName);
                    for (LanguageItem item : tableView.getItems()) {
                        String value = item.getValue();
                        if (value != null && !value.isBlank()) {
                            interfaceItems.put(item.getKey(), value);
                        }
                    }
                    ConfigTools.writeValues(interfaceFile, interfaceItems);
                } catch (Exception e) {
                    error = e.toString();
                }
                return true;
            }

            @Override
            protected void whenSucceeded() {
                if (error == null) {
                    if (!listView.getItems().contains(langName)) {
                        listView.getItems().add(0, langName);
                    }
                    tableChanged(false);
                    popSuccessful();
                    if (langName.equals(Languages.getLangName())) {
                        WindowTools.reloadAll();
                    }
                } else {
                    popError(error);
                }
            }
        };
        start(task);
    }

    @Override
    public boolean controlAltE() {
        copyEnglish();
        return true;
    }

    @FXML
    public void popCopyMenu(MouseEvent event) {
        popTableMenu(event);
    }

    @Override
    protected List<MenuItem> makeTableContextMenu() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem menu;
        menu = new MenuItem(Languages.message("CopyEnglish"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            copyEnglish();
        });
        items.add(menu);

        menu = new MenuItem(Languages.message("CopyChinese"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            copyChinese();
        });
        items.add(menu);

        return items;
    }

    public class LanguageCell extends TableAutoCommitCell {

        public LanguageCell() {
            super(new DefaultStringConverter());
        }

        protected void setCellValue(int rowIndex, String value) {
            if (isSettingValues || rowIndex < 0 || rowIndex >= tableData.size()) {
                return;
            }
            LanguageItem item = tableData.get(rowIndex);
            String currentValue = item.getValue();
            if ((currentValue == null && value == null)
                    || (currentValue != null && currentValue.equals(value))) {
                return;
            }
            item.setValue(value);
            tableData.set(rowIndex, item);
        }

        @Override
        public void editCell() {
            LanguageItem item = tableData.get(editingRow);
            String en = item.getEnglish();
            String value = item.getValue();
            if (value != null && value.contains("\n") || en != null && en.contains("\n")) {
                MyBoxLanguageInputController inputController
                        = MyBoxLanguageInputController.open((MyBoxLanguagesController) myController, item);
                inputController.getNotify().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        String value = inputController.getInput();
                        setCellValue(editingRow, value);
                        inputController.close();
                    }
                });
            } else {
                super.editCell();
            }
        }

    }

    public class LanguageItem {

        protected String key, english, chinese, value;

        public LanguageItem(String key, String english, String chinese) {
            this.key = key;
            this.english = english;
            this.chinese = chinese;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getEnglish() {
            return english;
        }

        public void setEnglish(String english) {
            this.english = english;
        }

        public String getChinese() {
            return chinese;
        }

        public void setChinese(String chinese) {
            this.chinese = chinese;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
