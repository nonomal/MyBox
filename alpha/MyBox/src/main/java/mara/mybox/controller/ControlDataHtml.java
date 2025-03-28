package mara.mybox.controller;

import javafx.fxml.FXML;
import mara.mybox.db.data.DataNode;
import mara.mybox.dev.MyBoxLog;

/**
 * @Author Mara
 * @CreateDate 2024-8-8
 * @License Apache License Version 2.0
 */
public class ControlDataHtml extends BaseDataValuesController {

    @FXML
    protected ControlHtmlMaker htmlController;

    @Override
    public void initEditor() {
        try {
            htmlController.setParameters(this);

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    protected void editValues() {
        try {
            isSettingValues = true;
            if (nodeEditor.currentNode != null) {
                htmlController.loadContents(nodeEditor.currentNode.getStringValue("html"));
            } else {
                htmlController.loadContents(null);
            }
            htmlController.updateStatus(false);
            isSettingValues = false;
            valueChanged(false);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    protected DataNode pickValues(DataNode node) {
        try {
            node.setValue("html", htmlController.currentHtml());
            return node;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

}
