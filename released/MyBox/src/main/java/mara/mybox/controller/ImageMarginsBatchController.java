package mara.mybox.controller;

import java.awt.image.BufferedImage;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import mara.mybox.image.tools.MarginTools;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2018-9-26
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageMarginsBatchController extends BaseImageEditBatchController {

    @FXML
    protected ControlImageMargins marginsController;

    public ImageMarginsBatchController() {
        baseTitle = message("ImageBatch") + " - " + message("Margins");
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            marginsController.setParameters(null);

            startButton.disableProperty().unbind();
            startButton.disableProperty().bind(Bindings.isEmpty(tableView.getItems())
                    .or(marginsController.widthSelector.getEditor().styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(marginsController.distanceInput.styleProperty().isEqualTo(UserConfig.badStyle()))
            );

        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @Override
    public boolean makeMoreParameters() {
        return super.makeMoreParameters() && marginsController.pickValues();
    }

    @Override
    protected BufferedImage handleImage(FxTask currentTask, BufferedImage source) {
        try {
            BufferedImage target = null;
            if (marginsController.addRadio.isSelected()) {
                target = MarginTools.addMargins(currentTask, source,
                        marginsController.colorController.awtColor(),
                        marginsController.margin,
                        marginsController.marginsTopCheck.isSelected(),
                        marginsController.marginsBottomCheck.isSelected(),
                        marginsController.marginsLeftCheck.isSelected(),
                        marginsController.marginsRightCheck.isSelected());

            } else if (marginsController.cutWidthRadio.isSelected()) {
                target = MarginTools.cutMarginsByColor(currentTask, source,
                        marginsController.colorController.awtColor(),
                        marginsController.marginsTopCheck.isSelected(),
                        marginsController.marginsBottomCheck.isSelected(),
                        marginsController.marginsLeftCheck.isSelected(),
                        marginsController.marginsRightCheck.isSelected());

            } else if (marginsController.cutColorRadio.isSelected()) {
                target = MarginTools.cutMarginsByWidth(currentTask, source,
                        marginsController.margin,
                        marginsController.marginsTopCheck.isSelected(),
                        marginsController.marginsBottomCheck.isSelected(),
                        marginsController.marginsLeftCheck.isSelected(),
                        marginsController.marginsRightCheck.isSelected());

            } else if (marginsController.blurRadio.isSelected()) {
                target = MarginTools.blurMarginsAlpha(currentTask, source,
                        marginsController.margin,
                        marginsController.marginsTopCheck.isSelected(),
                        marginsController.marginsBottomCheck.isSelected(),
                        marginsController.marginsLeftCheck.isSelected(),
                        marginsController.marginsRightCheck.isSelected());

            }

            return target;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }

    }
}
