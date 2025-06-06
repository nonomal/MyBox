package mara.mybox.controller;

import javafx.fxml.FXML;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.WindowTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2019-8-13
 * @License Apache License Version 2.0
 */
public class ImageEllipseController extends BaseImageShapeController {

    @FXML
    protected ControlEllipse ellipseController;

    public ImageEllipseController() {
        baseTitle = message("Ellipse");
    }

    @Override
    protected void initMore() {
        try {
            super.initMore();
            operation = message("Ellipse");

            ellipseController.setParameters(this);

            anchorCheck.setSelected(true);
            showAnchors = true;
            popShapeMenu = true;

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void setInputs() {
        ellipseController.loadValues();
    }

    @Override
    public boolean pickShape() {
        return ellipseController.pickValues();
    }

    @Override
    public void initShape() {
        try {
            showMaskEllipse();

            goAction();
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    /*
        static methods
     */
    public static ImageEllipseController open(ImageEditorController parent) {
        try {
            if (parent == null) {
                return null;
            }
            ImageEllipseController controller = (ImageEllipseController) WindowTools.referredStage(
                    parent, Fxmls.ImageEllipseFxml);
            controller.setParameters(parent);
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

}
