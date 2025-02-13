package mara.mybox.controller;

import java.awt.image.BufferedImage;
import mara.mybox.image.data.ImageGray;
import mara.mybox.fxml.FxTask;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2018-9-23
 * @License Apache License Version 2.0
 */
public class ImageGreyBatchController extends BaseImageEditBatchController {

    public ImageGreyBatchController() {
        baseTitle = message("ImageBatch") + " - " + message("Grey");
    }

    @Override
    protected BufferedImage handleImage(FxTask currentTask, BufferedImage source) {
        try {
            ImageGray imageGray = new ImageGray(source);
            return imageGray.setTask(currentTask).start();
        } catch (Exception e) {
            displayError(e.toString());
            return null;
        }
    }

}
