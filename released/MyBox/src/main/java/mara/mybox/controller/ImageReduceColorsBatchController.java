package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.image.ColorDemos;
import mara.mybox.image.data.ImageQuantization;
import mara.mybox.image.data.ImageQuantizationFactory;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2018-9-23
 * @License Apache License Version 2.0
 */
public class ImageReduceColorsBatchController extends BaseImageEditBatchController {

    @FXML
    protected ControlImageQuantization optionsController;

    public ImageReduceColorsBatchController() {
        baseTitle = message("ImageBatch") + " - " + message("ReduceColors");
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            optionsController.defaultForAnalyse();

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public boolean makeMoreParameters() {
        return super.makeMoreParameters() && optionsController.pickValues();
    }

    @Override
    protected BufferedImage handleImage(FxTask currentTask, BufferedImage source) {
        try {
            ImageQuantization quantization = ImageQuantizationFactory.create(
                    currentTask, source, null, optionsController, false);
            return quantization.setTask(currentTask).start();
        } catch (Exception e) {
            displayError(e.toString());
            return null;
        }
    }

    @Override
    public void makeDemoFiles(FxTask currentTask, List<String> files, File demoFile, BufferedImage demoImage) {
        ColorDemos.reduceColors(currentTask, files, demoImage, demoFile);
    }

}
