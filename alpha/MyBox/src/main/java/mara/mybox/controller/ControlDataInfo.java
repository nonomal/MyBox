package mara.mybox.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import mara.mybox.db.data.DataValues;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.PopTools;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2024-8-8
 * @License Apache License Version 2.0
 */
public class ControlDataInfo extends BaseDataNodeValues {

    @FXML
    protected TextArea infoInput;
    @FXML
    protected CheckBox wrapCheck;

    @Override
    public void initEditor() {
        try {
            infoInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue v, String ov, String nv) {
                    nodeEditor.valueChanged(true);
                }
            });

            wrapCheck.setSelected(UserConfig.getBoolean(baseName + "Wrap", false));
            wrapCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                    UserConfig.setBoolean(baseName + "Wrap", newValue);
                    infoInput.setWrapText(newValue);
                }
            });
            infoInput.setWrapText(wrapCheck.isSelected());

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    protected void editValues(DataValues values) {
        try {
            currentValues = values;
            Object v;
            if (values == null) {
                v = null;
            } else {
                v = values.getValue("info");
            }
            infoInput.setText(v != null ? (String) v : null);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    protected DataValues pickNodeValues() {
        try {
            DataValues data;
            if (currentValues != null) {
                data = currentValues.copy();
            } else {
                data = new DataValues();
            }
            data.setValue("info", infoInput.getText());
            return data;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    @FXML
    public void clearValue() {
        infoInput.clear();
    }

    @FXML
    protected void popHistories(Event event) {
        if (UserConfig.getBoolean(baseName + "HistoriesPopWhenMouseHovering", false)) {
            showHistories(event);
        }
    }

    @FXML
    protected void showHistories(Event event) {
        PopTools.popStringValues(this, infoInput, event, baseName + "Histories", false);
    }

}