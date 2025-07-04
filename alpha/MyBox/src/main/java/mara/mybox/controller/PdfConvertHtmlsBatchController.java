package mara.mybox.controller;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.MessageFormat;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import mara.mybox.data.PdfInformation;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.tools.FileNameTools;
import mara.mybox.value.Languages;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import thridparty.pdfdom.PDFDomTree;
import thridparty.pdfdom.PDFDomTreeConfig;
import thridparty.pdfdom.IgnoreResourceHandler;
import thridparty.pdfdom.SaveResourceToDirHandler;

/**
 * @Author Mara
 * @CreateDate 2019-9-1
 * @License Apache License Version 2.0
 */
public class PdfConvertHtmlsBatchController extends BaseBatchPdfController {

    protected boolean separatedHtml;
    protected SaveType fontSaveType, imageSaveType;
    protected PDFDomTreeConfig domConfig;

    @FXML
    protected ToggleGroup saveGroup, fontGroup, imageGroup;
    @FXML
    protected RadioButton separateRadio;
    @FXML
    protected CheckBox appendColorCheck, appendCompressionCheck, appendQualityCheck, appendDensityCheck;

    protected enum SaveType {
        Embed, Ignore, File
    }

    public PdfConvertHtmlsBatchController() {
        baseTitle = Languages.message("PdfConvertHtmlsBatch");
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.PDF, VisitHistory.FileType.Html);
    }

    @Override
    public void initOptionsSection() {
        try {
            domConfig = PDFDomTreeConfig.createDefaultConfig();

            saveGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    separatedHtml = separateRadio.isSelected();
                }
            });
            separatedHtml = separateRadio.isSelected();

            fontGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    String selected = ((RadioButton) newValue).getText();
                    if (Languages.message("SaveAsFile").equals(selected)) {
                        fontSaveType = SaveType.File;
                    } else if (Languages.message("Embed").equals(selected)) {
                        fontSaveType = SaveType.Embed;
                    } else if (Languages.message("Ignore").equals(selected)) {
                        fontSaveType = SaveType.Ignore;
                    }
                }
            });
            fontSaveType = SaveType.File;

            imageGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    String selected = ((RadioButton) newValue).getText();
                    if (Languages.message("SaveAsFile").equals(selected)) {
                        imageSaveType = SaveType.File;
                    } else if (Languages.message("Embed").equals(selected)) {
                        imageSaveType = SaveType.Embed;
                    } else if (Languages.message("Ignore").equals(selected)) {
                        imageSaveType = SaveType.Ignore;
                    }
                }
            });
            imageSaveType = SaveType.File;

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public String handleFile(FxTask currentTask, File srcFile, File targetPath) {
        int generated = 0;
        doc = null;
        try {
            if (!isPreview) {
                PdfInformation info = currentPdf();
                actualParameters.fromPage = info.getFromPage();
                if (actualParameters.fromPage < 0) {
                    actualParameters.fromPage = 0;
                }
                actualParameters.toPage = info.getToPage();
                actualParameters.password = info.getUserPassword();
                actualParameters.startPage = actualParameters.fromPage;
                pageIndex = actualParameters.fromPage;
            }
            File pdfFile = currentSourceFile();
            try (PDDocument pd = Loader.loadPDF(pdfFile, currentParameters.password)) {
                doc = pd;

                if (currentParameters.toPage <= 0
                        || currentParameters.toPage > doc.getNumberOfPages()) {
                    currentParameters.toPage = doc.getNumberOfPages();
                }
                int total = currentParameters.toPage - currentParameters.fromPage;
                updateFileProgress(0, total);
                currentParameters.currentTargetPath = targetPath;

                String filePrefix = FileNameTools.prefix(pdfFile.getName());
                if (separatedHtml) {
                    currentParameters.currentTargetPath = new File(targetPath.getAbsolutePath() + File.separator + filePrefix);
                    if (!currentParameters.currentTargetPath.exists()) {
                        currentParameters.currentTargetPath.mkdirs();
                    }
                    for (pageIndex = currentParameters.startPage;
                            pageIndex < currentParameters.toPage; pageIndex++) {
                        if (currentTask == null || currentTask.isCancelled()) {
                            break;
                        }
                        int interfaceIndex = pageIndex + 1;
                        updateLogs(Languages.message("HandlingPage") + ":" + interfaceIndex, true, true);
                        String fileName = currentParameters.currentTargetPath + File.separator
                                + filePrefix + "_p" + interfaceIndex;
                        File htmlFile = writeHhml(fileName, interfaceIndex, interfaceIndex);
                        if (htmlFile != null) {
                            generated++;
                            targetFileGenerated(htmlFile);
                        }
                        updateFileProgress(pageIndex - currentParameters.fromPage + 1, total);
                    }

                } else {
                    String fileName = currentParameters.currentTargetPath + File.separator + filePrefix;
                    File htmlFile = writeHhml(fileName,
                            currentParameters.startPage + 1,
                            currentParameters.toPage);
                    if (htmlFile != null) {
                        generated++;
                        targetFileGenerated(htmlFile);
                    }
                    updateFileProgress(total, total);

                }

                doc.close();
            }
            currentParameters.startPage = 0;
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
        updateInterface("CompleteFile");
        return MessageFormat.format(Languages.message("HandlePagesGenerateNumber"),
                pageIndex - currentParameters.fromPage, generated);
    }

    // 1-base, include end
    protected File writeHhml(String fileName, int start, int end) {
        try {
            File htmlFile = new File(fileName + ".html");
            File subPath = new File(fileName);
            if (fontSaveType == SaveType.File || imageSaveType == SaveType.File) {
                subPath.mkdirs();
            }
            switch (fontSaveType) {
                case File:
                    domConfig.setFontHandler(new SaveResourceToDirHandler(subPath));
                    break;
                case Embed:
                    domConfig.setFontHandler(PDFDomTreeConfig.embedAsBase64());
                    break;
                default:
                    domConfig.setFontHandler(new IgnoreResourceHandler());
                    break;
            }
            switch (imageSaveType) {
                case File:
                    domConfig.setImageHandler(new SaveResourceToDirHandler(subPath));
                    break;
                case Embed:
                    domConfig.setImageHandler(PDFDomTreeConfig.embedAsBase64());
                    break;
                default:
                    domConfig.setImageHandler(new IgnoreResourceHandler());
                    break;
            }
            PDFDomTree parser = new PDFDomTree(domConfig);
            parser.setStartPage(start);                                       // 1-based
            parser.setEndPage(end);                                          // include
            try (Writer output = new PrintWriter(htmlFile, "utf-8")) {
                parser.writeText(doc, output);
                return htmlFile;
            } catch (Exception e) {
//                MyBoxLog.debug(e);
                return null;
            }
        } catch (Exception e) {
//            MyBoxLog.error(e);
            return null;
        }
    }

}
