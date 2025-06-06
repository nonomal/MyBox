package mara.mybox.controller;

import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2023-3-27
 * @License Apache License Version 2.0
 */
public class RemotePathDeleteController extends RemotePathHandleFilesController {

    public RemotePathDeleteController() {
        baseTitle = message("RemotePathDelete");
        doneString = message("Deleted");
    }

    @Override
    public boolean handleFile(FxTask currentTask, String name) {
        manageController.remoteController.count = 0;
        boolean ok = manageController.remoteController.delete(currentTask, name);
        doneCount += manageController.remoteController.count;
        return ok;
    }

    @Override
    public void afterTask(boolean ok) {
        super.afterTask(ok);
        if (manageController != null) {
            manageController.loadPath();
        }
    }

    /*
        static methods
     */
    public static RemotePathDeleteController open(RemotePathManageController manageController) {
        try {
            if (manageController == null) {
                return null;
            }
            RemotePathDeleteController controller = (RemotePathDeleteController) WindowTools.referredTopStage(
                    manageController, Fxmls.RemotePathDeleteFxml);
            controller.setParameters(manageController);
            controller.requestMouse();
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

}
