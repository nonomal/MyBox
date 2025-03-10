package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import mara.mybox.image.tools.ImageTextTools;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.image.ShapeDemos;
import mara.mybox.fxml.FxTask;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2018-9-25
 * @License Apache License Version 2.0
 */
public class ImageTextBatchController extends BaseImageEditBatchController {

    @FXML
    protected ControlImageText optionsController;

    public ImageTextBatchController() {
        baseTitle = message("ImageBatch") + " - " + message("Text");
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            optionsController.setParameters(this);

            startButton.disableProperty().unbind();
            startButton.disableProperty().bind(Bindings.isEmpty(tableView.getItems())
                    .or(Bindings.isEmpty(optionsController.textArea.textProperty()))
                    .or(optionsController.xInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(optionsController.yInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(optionsController.marginInput.styleProperty().isEqualTo(UserConfig.badStyle()))
            );

        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @Override
    public boolean makeMoreParameters() {
        return super.makeMoreParameters() && optionsController.checkValues();
    }

    @Override
    public boolean beforeHandleFiles(FxTask currentTask) {
        return optionsController.pickValues();
    }

    @Override
    protected BufferedImage handleImage(FxTask currentTask, BufferedImage source) {
        return ImageTextTools.addText(currentTask, source, optionsController);
    }

    @Override
    public void makeDemoFiles(FxTask currentTask, List<String> files, File demoFile, BufferedImage demoImage) {
        ShapeDemos.text(currentTask, files, demoImage, optionsController, demoFile);
    }

}
