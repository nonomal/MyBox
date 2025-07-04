package mara.mybox.controller;

import java.sql.Connection;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.data.DataNode;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2022-3-14
 * @License Apache License Version 2.0
 */
public class DataTreeCopyController extends BaseDataTreeHandleController {

    @FXML
    protected ControlDataTreeSource sourceController;
    @FXML
    protected ControlDataTreeTarget targetController;
    @FXML
    protected RadioButton nodeAndDescendantsRadio, descendantsRadio,
            nodeAndChildrenRadio, childrenRadio, nodeRadio;

    public void setParameters(BaseDataTreeController parent, DataNode node) {
        try {
            super.setParameters(parent);

            sourceController.setParameters(parent, node);
            targetController.setParameters(parent);

            baseTitle = nodeTable.getTreeName() + " - " + message("CopyNodes");
            setTitle(baseTitle);

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @FXML
    @Override
    public void okAction() {
        List<Long> sourceIDs = sourceController.selectedIDs();
        if (sourceIDs == null || sourceIDs.isEmpty()) {
            popError(message("SelectSourceNodes"));
            return;
        }
        DataNode targetNode = targetController.selectedNode();
        if (targetNode == null) {
            popError(message("SelectNodeCopyInto"));
            return;
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {
            private int count;

            @Override
            protected boolean handle() {
                count = 0;
                try (Connection conn = DerbyBase.getConnection()) {
                    if (!targetController.equalOrDescendant(this, conn, targetNode, sourceIDs)) {
                        error = message("TreeTargetComments");
                        return false;
                    }
                    for (long sourceID : sourceIDs) {
                        DataNode nodeValues = nodeTable.query(conn, sourceID);
                        int ret;
                        if (nodeAndDescendantsRadio.isSelected()) {
                            ret = nodeTable.copyNodeAndDescendants(this, conn, nodeValues, targetNode, true);
                        } else if (descendantsRadio.isSelected()) {
                            ret = nodeTable.copyDescendants(this, conn, nodeValues, targetNode, true, 0);
                        } else if (nodeAndChildrenRadio.isSelected()) {
                            ret = nodeTable.copyNodeAndDescendants(this, conn, nodeValues, targetNode, false);
                        } else if (childrenRadio.isSelected()) {
                            ret = nodeTable.copyDescendants(this, conn, nodeValues, targetNode, false, 0);
                        } else {
                            ret = nodeTable.copyNode(conn, nodeValues, targetNode) != null ? 1 : 0;
                        }
                        if (ret <= 0) {
                            return false;
                        }
                        count += ret;
                    }
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
                return count >= 0;
            }

            @Override
            protected void whenSucceeded() {
                if (dataRunning()) {
                    dataController.refreshNode(targetNode);
                    dataController.popInformation(message("Copied") + ": " + count);
                }
                if (closeAfterCheck.isSelected()) {
                    close();
                } else {
                    sourceController.refreshNode(targetNode);
                    targetController.refreshNode(targetNode);
                }
            }
        };
        start(task);
    }

    /*
        static methods
     */
    public static DataTreeCopyController open(BaseDataTreeController parent, DataNode node) {
        DataTreeCopyController controller
                = (DataTreeCopyController) WindowTools.childStage(parent, Fxmls.DataTreeCopyFxml);
        controller.setParameters(parent, node);
        controller.requestMouse();
        return controller;
    }

}
