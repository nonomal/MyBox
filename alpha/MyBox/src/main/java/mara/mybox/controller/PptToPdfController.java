package mara.mybox.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.tools.FileTools;
import mara.mybox.tools.PdfTools;
import mara.mybox.value.AppVariables;
import mara.mybox.value.Languages;
import static mara.mybox.value.Languages.message;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.SlideShowFactory;

/**
 * @Author Mara
 * @CreateDate 2021-10-10
 * @License Apache License Version 2.0
 */
public class PptToPdfController extends BaseBatchFileController {

    @FXML
    protected ControlPdfWriteOptions pdfOptionsController;

    public PptToPdfController() {
        baseTitle = Languages.message("PptToPdf");
        targetFileSuffix = "pdf";
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.PPTS, VisitHistory.FileType.PDF);
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            pdfOptionsController.set(baseName, true);

            startButton.disableProperty().unbind();
            startButton.disableProperty().bind(Bindings.isEmpty(tableView.getItems()));

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public boolean makeMoreParameters() {
        return pdfOptionsController.pickValues() && super.makeMoreParameters();
    }

    @Override
    public String handleFile(FxTask currentTask, File srcFile, File targetPath) {
        File target = makeTargetFile(srcFile, targetPath);
        if (target == null) {
            return message("Skip");
        }
        File tmpFile = FileTmpTools.getTempFile();
        try (PDDocument document = new PDDocument(); SlideShow ppt = SlideShowFactory.create(srcFile)) {
            List<Slide> slides = ppt.getSlides();
            int width = ppt.getPageSize().width;
            int height = ppt.getPageSize().height;
            int count = 0, total = slides.size();
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
                if (currentTask == null || !currentTask.isWorking()) {
                    return message("Canceled");
                }
                PdfTools.writePage(currentTask,
                        document, "png", slideImage, ++count, total, pdfOptionsController);
            }
            if (currentTask == null || !currentTask.isWorking()) {
                return message("Canceled");
            }
            PdfTools.setAttributes(document,
                    pdfOptionsController.getAuthor(),
                    pdfOptionsController.getZoom());
            document.save(tmpFile);
            document.close();
        } catch (Exception e) {
            updateLogs(e.toString());
            return e.toString();
        }
        if (FileTools.override(tmpFile, target)) {
            targetFileGenerated(target);
            return message("Successful");
        } else {
            return message("Failed");
        }
    }

}
