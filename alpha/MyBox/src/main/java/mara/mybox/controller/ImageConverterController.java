package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.fxml.WindowTools;
import mara.mybox.image.file.ImageFileWriters;
import mara.mybox.image.tools.ImageConvertTools;
import mara.mybox.tools.DateTools;
import mara.mybox.tools.FileNameTools;
import mara.mybox.value.FileFilters;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2023-6-29
 * @License Apache License Version 2.0
 */
public class ImageConverterController extends BaseChildController {

    protected BaseImageController imageController;

    @FXML
    protected ControlImageFormat formatController;
    @FXML
    protected ToggleGroup framesSaveGroup;
    @FXML
    protected RadioButton saveAllFramesRadio;
    @FXML
    protected FlowPane saveFramesPane;

    public ImageConverterController() {
        baseTitle = message("ImageConverter");
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.Image);
    }

    public void setParameters(BaseImageController controller) {
        try {
            if (controller == null) {
                close();
                return;
            }
            imageController = controller;
            formatController.setParameters(this, false);

            if (imageController.sourceFile != null) {
                setTitle(baseTitle + " " + imageController.sourceFile.getAbsolutePath());
            }

            if (imageController.sourceFile == null || imageController.framesNumber <= 1) {
                if (thisPane.getChildren().contains(saveFramesPane)) {
                    thisPane.getChildren().remove(saveFramesPane);
                }

            } else {
                if (!thisPane.getChildren().contains(saveFramesPane)) {
                    thisPane.getChildren().add(0, saveFramesPane);
                }
            }
            saveAllFramesRadio.setSelected(true);
            saveAllFramesSelected();

            saveAsButton.disableProperty().bind(
                    formatController.qualitySelector.getEditor().styleProperty().isEqualTo(UserConfig.badStyle())
                            .or(formatController.profileInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                            .or(formatController.binaryController.thresholdInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                            .or(formatController.icoWidthSelector.getEditor().styleProperty().isEqualTo(UserConfig.badStyle()))
            );

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @FXML
    public void saveAllFramesSelected() {
        if (imageController.sourceFile != null && imageController.sourceFile.exists()
                && imageController.framesNumber > 1) {
            formatController.formatPane.getChildren().setAll(
                    formatController.tifRadio, formatController.gifRadio);
            if ("gif".equalsIgnoreCase(FileNameTools.ext(imageController.sourceFile.getName()))) {
                formatController.gifRadio.setSelected(true);
            } else {
                formatController.tifRadio.setSelected(true);
            }
        } else {
            saveCurrentFramesSelected();
        }
    }

    @FXML
    public void saveCurrentFramesSelected() {
        formatController.formatPane.getChildren().setAll(
                formatController.pngRadio, formatController.jpgRadio,
                formatController.tifRadio, formatController.gifRadio,
                formatController.icoRadio, formatController.webpRadio,
                formatController.pcxRadio, formatController.pnmRadio,
                formatController.bmpRadio, formatController.wbmpRadio);
    }

    @FXML
    @Override
    public void saveAsAction() {
        String targetFormat = formatController.attributes.getImageFormat();
        File srcFile = imageController.sourceFile;
        String fname;
        if (srcFile != null) {
            fname = FileNameTools.filter(FileNameTools.prefix(srcFile.getName()))
                    + (imageController.framesNumber > 1 && (saveAllFramesRadio == null || !saveAllFramesRadio.isSelected())
                    ? "-" + message("Frame") + (imageController.frameIndex + 1) : "")
                    + "_" + DateTools.nowFileString();
        } else {
            fname = DateTools.nowFileString();
        }
        fname += "." + targetFormat;
        targetFile = chooseFile(defaultTargetPath(), fname,
                FileFilters.imageFilter(targetFormat));
        if (targetFile == null) {
            return;
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {

            @Override
            protected boolean handle() {
                Object imageToSave = imageController.imageToSaveAs(this);
                if (imageToSave == null) {
                    return false;
                }
                BufferedImage bufferedImage;
                if (imageToSave instanceof Image) {
                    bufferedImage = SwingFXUtils.fromFXImage((Image) imageToSave, null);
                } else if (imageToSave instanceof BufferedImage) {
                    bufferedImage = (BufferedImage) imageToSave;
                } else {
                    return false;
                }
                if (bufferedImage == null || task == null || isCancelled()) {
                    return false;
                }
                boolean multipleFrames = srcFile != null
                        && imageController.framesNumber > 1
                        && saveAllFramesRadio != null && saveAllFramesRadio.isSelected();
                if (formatController != null) {
                    if (multipleFrames) {
                        error = ImageFileWriters.writeFrame(this, srcFile,
                                imageController.frameIndex, bufferedImage,
                                targetFile, formatController.attributes);
                        return error == null;
                    } else {
                        BufferedImage converted = ImageConvertTools.convertColorSpace(this,
                                bufferedImage, formatController.attributes);
                        return ImageFileWriters.writeImageFile(this, converted,
                                formatController.attributes, targetFile.getAbsolutePath());
                    }
                } else {
                    if (multipleFrames) {
                        error = ImageFileWriters.writeFrame(this, srcFile,
                                imageController.frameIndex, bufferedImage, targetFile, null);
                        return error == null;
                    } else {
                        return ImageFileWriters.writeImageFile(this, bufferedImage, targetFile);
                    }
                }
            }

            @Override
            protected void whenSucceeded() {
                popInformation(message("Saved"));
                recordFileWritten(targetFile);
                ImageEditorController.openFile(targetFile);
                if (closeAfterCheck.isSelected()) {
                    close();
                }

            }
        };
        start(task);
    }

    /*
        static methods
     */
    public static ImageConverterController open(BaseImageController parent) {
        ImageConverterController controller = (ImageConverterController) WindowTools.referredTopStage(
                parent, Fxmls.ImageConverterFxml);
        if (controller != null) {
            controller.setParameters(parent);
            controller.requestMouse();
        }
        return controller;
    }

}
