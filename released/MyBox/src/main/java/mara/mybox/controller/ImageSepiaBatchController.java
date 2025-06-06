package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import mara.mybox.image.data.PixelsOperation;
import mara.mybox.image.data.PixelsOperationFactory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.image.ColorDemos;
import mara.mybox.fxml.FxTask;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2018-9-23
 * @License Apache License Version 2.0
 */
public class ImageSepiaBatchController extends BaseImageEditBatchController {

    protected PixelsOperation pixelsOperation;

    @FXML
    protected ControlImageSepia sepiaController;

    public ImageSepiaBatchController() {
        baseTitle = message("ImageBatch") + " - " + message("Sepia");
    }

    @Override
    public boolean makeMoreParameters() {
        if (!super.makeMoreParameters()) {
            return false;
        }
        pixelsOperation = sepiaController.pickValues();
        return pixelsOperation != null;
    }

    @Override
    protected BufferedImage handleImage(FxTask currentTask, BufferedImage source) {
        return pixelsOperation.setImage(source).setTask(currentTask).start();
    }

    @Override
    public void makeDemoFiles(FxTask currentTask, List<String> files, File demoFile, BufferedImage demoImage) {
        try {
            PixelsOperation op = PixelsOperationFactory.create(
                    demoImage, null, PixelsOperation.OperationType.Sepia)
                    .setTask(currentTask);
            ColorDemos.sepia(currentTask, files, op, demoFile);
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

}
