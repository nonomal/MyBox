package mara.mybox.controller;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import mara.mybox.bufferedimage.BufferedImageTools;
import mara.mybox.data.DoubleRectangle;
import mara.mybox.dev.MyBoxLog;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2019-9-5
 * @License Apache License Version 2.0
 */
public class ControlImageScale extends ControlImageSize {

    protected ImageManufactureScaleController scaleController;
    protected ImageManufactureController editor;

    @FXML
    protected RadioButton dragRadio;

    public void setParameters(ImageManufactureScaleController scaleController) {
        this.scaleController = scaleController;
        editor = scaleController.editor;
        imageController = editor;
        infoLabel = scaleController.commentsLabel;
        image = editor.imageView.getImage();
        checkScaleType();
    }

    @Override
    protected void resetControls() {
        try {
            if (editor != null) {
                editor.resetImagePane();
                editor.imageTab();
            }
            if (infoLabel != null) {
                infoLabel.setText("");
            }
            super.resetControls();
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @Override
    protected void switchType() {
        try {
            if (dragRadio.isSelected()) {
                scaleType = ScaleType.Dragging;
                setBox.getChildren().addAll(keepBox);
                if (infoLabel != null) {
                    infoLabel.setText(message("DragSizeComments"));
                }
                initDrag();
                adjustRadio();

            } else {

                super.switchType();
            }

        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    protected void initDrag() {
        try {
            if (image == null || !dragRadio.isSelected()) {
                return;
            }
            width = image.getWidth();
            height = image.getHeight();
            editor.maskRectangleData = new DoubleRectangle(0, 0, width - 1, height - 1);
            editor.showMaskRectangle();
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @Override
    protected void adjustRadio() {
        super.adjustRadio();
        if (editor == null || !dragRadio.isSelected()
                || editor.maskRectangleData == null) {
            return;
        }
        editor.maskRectangleData = new DoubleRectangle(
                editor.maskRectangleData.getSmallX(),
                editor.maskRectangleData.getSmallY(),
                editor.maskRectangleData.getSmallX() + width - 1,
                editor.maskRectangleData.getSmallY() + height - 1);
        editor.drawMaskRectangle();
    }

    public void paneClicked() {
        if (editor == null || !dragRadio.isSelected() || editor.maskRectangleData == null) {
            return;
        }
        width = editor.maskRectangleData.getWidth();
        height = editor.maskRectangleData.getHeight();
        if (keepRatioType != BufferedImageTools.KeepRatioType.None) {
            adjustRadio();

        } else {
            labelSize();
        }
    }

    @Override
    public void afterScaled(Image newImage, long cost) {
        editor.popSuccessful();
        String newSize = (int) Math.round(newImage.getWidth()) + "x" + (int) Math.round(newImage.getHeight());
        if (scaleType == ScaleType.Scale) {
            editor.updateImage(ImageManufactureController_Image.ImageOperation.Scale2, scale + "", newSize, newImage, cost);
        } else if (scaleType == ScaleType.Dragging || scaleType == ScaleType.Pixels) {
            editor.updateImage(ImageManufactureController_Image.ImageOperation.Scale2, "Pixels", newSize, newImage, cost);
        }

        String info = message("OriginalSize") + ": " + (int) Math.round(editor.image.getWidth())
                + "x" + (int) Math.round(editor.image.getHeight()) + "\n"
                + message("CurrentSize") + ": " + Math.round(newImage.getWidth())
                + "x" + Math.round(newImage.getHeight());
        infoLabel.setText(info);

        if (scaleType == ScaleType.Dragging) {
            initDrag();
        }
    }

}