package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.fxml.FXML;
import mara.mybox.image.data.ImageConvolution;
import mara.mybox.db.data.ConvolutionKernel;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.image.PixelDemos;
import mara.mybox.fxml.FxTask;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2018-9-23
 * @License Apache License Version 2.0
 */
public class ImageSharpenBatchController extends BaseImageEditBatchController {

    protected ConvolutionKernel kernel;

    @FXML
    protected ControlImageSharpen sharpenController;

    public ImageSharpenBatchController() {
        baseTitle = message("ImageBatch") + " - " + message("Sharpen");
    }

    @Override
    public boolean makeMoreParameters() {
        if (!super.makeMoreParameters()) {
            return false;
        }
        kernel = sharpenController.pickValues();
        return kernel != null;
    }

    @Override
    protected BufferedImage handleImage(FxTask currentTask, BufferedImage source) {
        try {
            return ImageConvolution.create()
                    .setImage(source).setKernel(kernel)
                    .setTask(currentTask)
                    .start();
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    @Override
    public void makeDemoFiles(FxTask currentTask, List<String> files, File demoFile, BufferedImage demoImage) {
        try {
            ImageConvolution convolution = ImageConvolution.create()
                    .setImage(demoImage);
            PixelDemos.sharpen(currentTask, files, convolution, demoFile);
        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

}
