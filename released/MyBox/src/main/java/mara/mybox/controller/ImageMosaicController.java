package mara.mybox.controller;

import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import mara.mybox.bufferedimage.ImageMosaic;
import mara.mybox.bufferedimage.ImageScope;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fximage.PixelDemos;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2019-9-2
 * @License Apache License Version 2.0
 */
public class ImageMosaicController extends BasePixelsController {

    protected ImageMosaic mosaic;

    @FXML
    protected ControlImageMosaic mosaicController;

    public ImageMosaicController() {
        baseTitle = message("Mosaic");
    }

    @Override
    protected void initMore() {
        try {
            super.initMore();
            operation = message("Mosaic");
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    protected boolean checkOptions() {
        if (!super.checkOptions()) {
            return false;
        }
        mosaic = mosaicController.pickValues(ImageMosaic.MosaicType.Mosaic);
        return mosaic != null;
    }

    @Override
    protected Image handleImage(FxTask currentTask, Image inImage, ImageScope inScope) {
        try {
            mosaic.setImage(inImage).setScope(inScope)
                    .setExcludeScope(excludeScope())
                    .setSkipTransparent(skipTransparent())
                    .setTask(currentTask);
            opInfo = message("Intensity") + ": " + mosaicController.intensity;
            return mosaic.startFx();
        } catch (Exception e) {
            displayError(e.toString());
            return null;
        }
    }

    @Override
    protected void makeDemoFiles(FxTask currentTask, List<String> files, Image demoImage) {
        PixelDemos.mosaic(currentTask, files,
                SwingFXUtils.fromFXImage(demoImage, null),
                ImageMosaic.MosaicType.Mosaic, srcFile());
    }


    /*
        static methods
     */
    public static ImageMosaicController open(BaseImageController parent) {
        try {
            if (parent == null) {
                return null;
            }
            ImageMosaicController controller = (ImageMosaicController) WindowTools.branchStage(
                    parent, Fxmls.ImageMosaicFxml);
            controller.setParameters(parent);
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

}