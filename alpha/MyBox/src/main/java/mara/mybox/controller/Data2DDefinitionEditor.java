package mara.mybox.controller;

import javafx.fxml.FXML;
import mara.mybox.data2d.Data2D;
import mara.mybox.data2d.Data2DTools;
import mara.mybox.data2d.DataFileCSV;
import mara.mybox.db.data.InfoNode;

/**
 * @Author Mara
 * @CreateDate 2022-3-20
 * @License Apache License Version 2.0
 */
public class Data2DDefinitionEditor extends BaseInfoTreeNodeController {

    protected Data2DDefinitionController manageController;
    protected Data2D data2D;

    @FXML
    protected ControlData2DDefAttributes defAttributesController;
    @FXML
    protected ControlData2DDefColumns columnsController;

    public Data2DDefinitionEditor() {
        defaultExt = "csv";
    }

    protected void setParameters(Data2DDefinitionController manager) {
        manageController = manager;
        attributesController = defAttributesController;
        data2D = null;
        columnsController.setParameters(this);
        super.setParameters(manageController);
    }

    public String toXML() {
        return Data2DTools.toXML(data2D);
    }

    protected void load(Data2D data) {
        data2D = data;
        columnsController.load(data2D);
        defAttributesController.editNode(null, data2D);
        nodeChanged(false);
    }

    @Override
    protected void editNode(InfoNode node) {
        if (node != null) {
            data2D = Data2DTools.fromXML(node.getValue());
        } else {
            data2D = null;
        }
        if (data2D == null) {
            data2D = new DataFileCSV();
        }
        columnsController.load(data2D);
        defAttributesController.editNode(node, data2D);
        nodeChanged(false);
    }

    @Override
    public InfoNode pickNodeData() {
        InfoNode node = super.pickNodeData();
        if (node == null) {
            return null;
        }
        if (data2D == null) {
            data2D = new DataFileCSV();
        }
        data2D.setColumns(columnsController.tableData)
                .setColsNumber(columnsController.tableData.size())
                .setScale(defAttributesController.scale)
                .setMaxRandom(defAttributesController.maxRandom)
                .setComments(defAttributesController.descInput.getText())
                .setDataName(node.getTitle());
        node.setValue(Data2DTools.toXML(data2D));
        return node;
    }

    @FXML
    @Override
    public void clearValue() {
        columnsController.clearAction();
    }

}