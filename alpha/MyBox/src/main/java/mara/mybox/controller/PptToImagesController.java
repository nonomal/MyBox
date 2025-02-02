package mara.mybox.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import mara.mybox.image.tools.ImageConvertTools;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.image.file.ImageFileWriters;
import mara.mybox.tools.FileNameTools;
import mara.mybox.value.AppVariables;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.SlideShowFactory;

/**
 * @Author Mara
 * @CreateDate 2021-5-18
 * @License Apache License Version 2.0
 */
public class PptToImagesController extends BaseBatchFileController {

    @FXML
    protected ControlImageFormat formatController;

    public PptToImagesController() {
        baseTitle = message("PptToImages");
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.PPTS, VisitHistory.FileType.Image);
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            formatController.setParameters(this, false);

            startButton.disableProperty().unbind();
            startButton.disableProperty().bind(Bindings.isEmpty(tableView.getItems())
                    .or(formatController.qualitySelector.getEditor().styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(formatController.dpiSelector.getEditor().styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(formatController.profileInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(formatController.binaryController.thresholdInput.styleProperty().isEqualTo(UserConfig.badStyle()))
            );

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public String handleFile(FxTask currentTask, File srcFile, File targetPath) {
        try (SlideShow ppt = SlideShowFactory.create(srcFile)) {
            List<Slide> slides = ppt.getSlides();
            int width = ppt.getPageSize().width;
            int height = ppt.getPageSize().height;
            int index = 0;
            for (Slide slide : slides) {
                if (currentTask == null || !currentTask.isWorking()) {
                    return message("Canceled");
                }
                BufferedImage slideImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = slideImage.createGraphics();
                if (AppVariables.ImageHints != null) {
                    g.addRenderingHints(AppVariables.ImageHints);
                }
                slide.draw(g);
                BufferedImage targetImage = ImageConvertTools.convertColorSpace(currentTask,
                        slideImage, formatController.attributes);
                if (currentTask == null || !currentTask.isWorking()) {
                    return message("Canceled");
                }
                if (targetImage != null) {
                    targetFile = makeTargetFile(srcFile, ++index, targetPath);
                    if (ImageFileWriters.writeImageFile(currentTask,
                            targetImage, formatController.attributes, targetFile.getAbsolutePath())) {
                        targetFileGenerated(targetFile);
                    }
                }
            }
        } catch (Exception e) {
            updateLogs(e.toString());
            return e.toString();
        }

        return message("Successful");
    }

    public File makeTargetFile(File srcFile, int index, File targetPath) {
        try {
            String filePrefix = FileNameTools.prefix(srcFile.getName());
            String slidePrefix = filePrefix + "_slide" + index;
            String slideSuffix = "." + formatController.attributes.getImageFormat();

            File slidePath = targetPath;
            if (targetSubdirCheck.isSelected()) {
                slidePath = new File(targetPath, filePrefix);
            }
            return makeTargetFile(slidePrefix, slideSuffix, slidePath);
        } catch (Exception e) {
            updateLogs(e.toString());
            return null;
        }
    }

}
