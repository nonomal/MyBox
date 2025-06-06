package mara.mybox.controller;

import java.io.File;
import java.text.MessageFormat;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.tools.FileDeleteTools;
import mara.mybox.value.Languages;

/**
 * @Author Mara
 * @CreateDate 2020-1-6
 * @License Apache License Version 2.0
 */
public class FilesDeleteEmptyDirectoriesController extends BaseBatchFileController {

    protected int totalDeleted;

    @FXML
    protected RadioButton trashRadio;

    public FilesDeleteEmptyDirectoriesController() {
        baseTitle = Languages.message("DeleteEmptyDirectories");
    }

    @Override
    public void initControls() {
        try {
            super.initControls();
            operationBarController.deleteOpenControls();

        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @Override
    public boolean makeMoreParameters() {
        totalDeleted = 0;
        return super.makeMoreParameters();
    }

    @Override
    public void handleCurrentFile(FxTask currentTask) {
        try {
            String result;
            File file = currentSourceFile();
            if (!file.exists()) {
                result = Languages.message("NotFound");
            } else if (file.isDirectory()) {
                int count = FileDeleteTools.deleteEmptyDir(currentTask,
                        file, trashRadio.isSelected());
                result = MessageFormat.format(Languages.message("DeleteEmptyDirectoriesCount"), count);
                totalDeleted += count;
            } else {
                result = Languages.message("Skip");
            }
            if (verboseCheck == null || verboseCheck.isSelected()) {
                updateLogs(result);
            }
            totalItemsHandled++;
            tableController.markFileHandled(currentParameters.currentSourceFile, result);
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @Override
    public void afterTask(boolean ok) {
        updateLogs(MessageFormat.format(Languages.message("DeleteEmptyDirectoriesTotalCount"), totalDeleted));
        super.afterTask(ok);
    }

}
