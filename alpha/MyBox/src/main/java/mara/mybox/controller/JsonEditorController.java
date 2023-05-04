package mara.mybox.controller;

import java.io.File;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import mara.mybox.data.JsonDomNode;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.SingletonTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.tools.FileTools;
import mara.mybox.tools.HtmlWriteTools;
import mara.mybox.tools.StringTools;
import mara.mybox.tools.TextFileTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2023-4-30
 * @License Apache License Version 2.0
 */
public class JsonEditorController extends BaseController {

    protected boolean domChanged, textsChanged, fileChanged;
    protected String title;
    protected final SimpleBooleanProperty loadNotify;

    @FXML
    protected Tab domTab, textsTab, backupTab;
    @FXML
    protected TextArea textsArea;
    @FXML
    protected Label textsLabel;
    @FXML
    protected ControlJsonDom domController;
    @FXML
    protected ControlFileBackup backupController;
    @FXML
    protected CheckBox wrapTextsCheck;

    public JsonEditorController() {
        baseTitle = message("JsonEditor");
        loadNotify = new SimpleBooleanProperty(false);
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.JSON);
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                @Override
                public void changed(ObservableValue ov, Tab oldValue, Tab newValue) {
                    tabChanged();
                }
            });
            initTextsTab();

            backupController.setParameters(this, baseName);

            tabChanged();

        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    protected void initTextsTab() {
        try {
            textsArea.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (isSettingValues) {
                        return;
                    }
                    textsChanged(true);
                }
            });

            wrapTextsCheck.setSelected(UserConfig.getBoolean(baseName + "WrapText", true));
            textsArea.setWrapText(wrapTextsCheck.isSelected());
            wrapTextsCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    UserConfig.setBoolean(baseName + "WrapText", wrapTextsCheck.isSelected());
                    textsArea.setWrapText(wrapTextsCheck.isSelected());
                }
            });

        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    /*
        source
     */
    @Override
    public void sourceFileChanged(final File file) {
        sourceFile = file;
        writePanes(TextFileTools.readTexts(file));
    }

    public boolean loadContents(String contents) {
        return writePanes(contents);
    }

    public boolean writePanes(String json) {
        fileChanged = false;
        isSettingValues = true;
        loadDom(json, false);
        loadText(json, false);
        isSettingValues = false;
        if (backupController != null) {
            backupController.loadBackups(sourceFile);
        }
        loadNotify.set(!loadNotify.get());
        return true;
    }

    @FXML
    @Override
    public void refreshAction() {
        fileChanged = false;

    }

    /*
        file
     */
    @FXML
    @Override
    public void saveAction() {
        if (task != null && !task.isQuit()) {
            return;
        }
        if (sourceFile == null) {
            targetFile = chooseSaveFile();
        } else {
            targetFile = sourceFile;
        }
        if (targetFile == null) {
            return;
        }
        String html = currentJSON(true);
        if (html == null || html.isBlank()) {
            popError(message("NoData"));
            return;
        }
        task = new SingletonTask<Void>(this) {
            @Override
            protected boolean handle() {
                try {
                    File tmpFile = HtmlWriteTools.writeHtml(html);
                    if (tmpFile == null || !tmpFile.exists()) {
                        return false;
                    }
                    if (sourceFile != null && backupController != null && backupController.needBackup()) {
                        backupController.addBackup(task, sourceFile);
                    }
                    return FileTools.rename(tmpFile, targetFile);
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
            }

            @Override
            protected void whenSucceeded() {
                popSaved();
                recordFileWritten(targetFile);
                fileChanged = false;
                sourceFileChanged(targetFile);
            }
        };
        start(task);
    }

    public String currentJSON(boolean synchronize) {
        try {
            Tab currentTab = tabPane.getSelectionModel().getSelectedItem();

            if (currentTab == domTab) {
                String json = htmlByDom();
                if (synchronize) {
                    loadText(json, false);
                }
                return json;

            } else if (currentTab == textsTab) {
                String json = htmlByText();
                if (synchronize) {
                    loadDom(json, false);
                }
                return json;

            }
        } catch (Exception e) {
            MyBoxLog.debug(e.toString());
        }
        return null;
    }

    public boolean create() {
        try {
            if (!checkBeforeNextAction()) {
                return false;
            }
            loadContents(HtmlWriteTools.emptyHmtl(null));
            getMyStage().setTitle(getBaseTitle());
            fileChanged = false;
            if (backupController != null) {
                backupController.loadBackups(null);
            }
            return true;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return false;
        }
    }

    public void updateStageTitle() {
        if (getMyStage() == null) {
            return;
        }
        if (fileChanged) {
            myStage.setTitle(myStage.getTitle() + " *");
        }
    }

    protected void updateFileStatus(boolean changed) {
        fileChanged = changed;
        updateStageTitle();
        if (!changed) {
            domChanged(false);
            textsChanged(false);
        }
    }

    /*
        dom
     */
    public void loadDom(String json, boolean updated) {
        if (!tabPane.getTabs().contains(domTab)) {
            return;
        }
        domController.makeTree(json);
        domChanged(updated);
    }

    public String htmlByDom() {
        return null;
//        return domController.html();
    }

    public void domChanged(boolean changed) {
        domChanged = changed;
        domTab.setText("dom" + (changed ? " *" : ""));
        if (changed) {
            updateFileStatus(true);
        }
    }

    public void updateNode(TreeItem<JsonDomNode> item) {
        domChanged(true);
    }

    public void clearDom() {
        domController.clearDom();
        domChanged(true);
    }

    /*
        texts
     */
    public void loadText(String html, boolean updated) {
        if (!tabPane.getTabs().contains(textsTab)) {
            return;
        }
        try {
            textsArea.setText(HtmlWriteTools.htmlToText(html));
            textsChanged(updated);
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    public String htmlByText() {
        String body = HtmlWriteTools.stringToHtml(textsArea.getText());
        return HtmlWriteTools.html(title, body);
    }

    protected void textsChanged(boolean changed) {
//        MyBoxLog.debug(changed);
        textsChanged = changed;
        textsTab.setText(message("Texts") + (changed ? " *" : ""));
        textsLabel.setText(message("CharactersNumber") + ": " + StringTools.format(textsArea.getLength()));
        if (changed) {
            updateFileStatus(true);
        }
    }

    @FXML
    protected void editTexts() {
        TextEditorController controller = (TextEditorController) WindowTools.openStage(Fxmls.TextEditorFxml);
        controller.loadContents(textsArea.getText());
        controller.requestMouse();
    }

    @FXML
    protected void clearTexts() {
        textsArea.clear();
        textsChanged(true);
    }


    /*
        buttons
     */
    @FXML
    @Override
    public boolean popAction() {
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (tab == textsTab) {
                TextPopController.openInput(this, textsArea);
                return true;

            }
        } catch (Exception e) {
            MyBoxLog.debug(e.toString());
        }
        return false;
    }

    @FXML
    @Override
    public boolean synchronizeAction() {
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();

            if (tab == domTab) {
                synchronizeDom();
                return true;

            } else if (tab == textsTab) {
                synchronizeTexts();
                return true;

            }
        } catch (Exception e) {
            MyBoxLog.debug(e.toString());
        }
        return false;
    }

    public void synchronizeDom() {
        Platform.runLater(() -> {
            String html = htmlByDom();
            loadText(html, true);
        });
    }

    public void synchronizeTexts() {
        Platform.runLater(() -> {
            String html = htmlByText();
            loadDom(html, true);
        });
    }

    @FXML
    @Override
    public boolean menuAction() {
        try {
            closePopup();

            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (tab == domTab) {
                domController.popFunctionsMenu(null);
                return true;

            } else if (tab == textsTab) {
                MenuTextEditController.open(this, textsArea);
                return true;

            }
        } catch (Exception e) {
            MyBoxLog.debug(e.toString());
        }
        return false;
    }

    @FXML
    @Override
    public void myBoxClipBoard() {
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (tab == textsTab) {
                TextClipboardPopController.open(this, textsArea);
            }
        } catch (Exception e) {
            MyBoxLog.debug(e.toString());
        }
    }

    @FXML
    @Override
    public void clearAction() {
        try {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
//            if (tab == backupTab) {
//                return;
//            }
//            if (!PopTools.askSure(getTitle(), message("SureClearData"))) {
//                return;
//            }
            if (tab == domTab) {
                clearDom();
            } else if (tab == textsTab) {
                clearTexts();

            }
        } catch (Exception e) {
            MyBoxLog.debug(e.toString());
        }
    }

    /*
        panes
     */
    public void tabChanged() {
        try {
            TextClipboardPopController.closeAll();
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            menuButton.setDisable(tab == backupTab);
            synchronizeButton.setDisable(tab == backupTab);
            clearButton.setDisable(tab == backupTab);
            saveButton.setDisable(tab == backupTab);
        } catch (Exception e) {
            MyBoxLog.debug(e.toString());
        }
    }

    @Override
    public boolean checkBeforeNextAction() {
        if (isPop || !fileChanged) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(getMyStage().getTitle());
            alert.setContentText(message("FileChanged"));
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ButtonType buttonSave = new ButtonType(message("Save"));
            ButtonType buttonNotSave = new ButtonType(message("NotSave"));
            ButtonType buttonCancel = new ButtonType(message("Cancel"));
            alert.getButtonTypes().setAll(buttonSave, buttonNotSave, buttonCancel);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.toFront();

            Optional<ButtonType> result = alert.showAndWait();
            if (result == null || !result.isPresent()) {
                return false;
            }
            if (result.get() == buttonSave) {
                saveAction();
                return false;
            } else {
                return result.get() == buttonNotSave;
            }
        }
    }

    /*
        static
     */
    public static JsonEditorController load(String html) {
        try {
            JsonEditorController controller = (JsonEditorController) WindowTools.openStage(Fxmls.JsonEditorFxml);
            controller.loadContents(html);
            controller.requestMouse();
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
            return null;
        }
    }

}