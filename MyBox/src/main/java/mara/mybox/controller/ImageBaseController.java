package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javax.imageio.ImageIO;
import static mara.mybox.controller.BaseController.logger;
import mara.mybox.objects.ImageFileInformation;
import mara.mybox.tools.FileTools;
import mara.mybox.imagefile.ImageFileReaders;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.CommonValues;
import mara.mybox.objects.FileInformation;
import mara.mybox.objects.ImageAttributes;
import static mara.mybox.objects.AppVaribles.getMessage;
import mara.mybox.tools.DateTools;
import mara.mybox.tools.ValueTools;

/**
 * @Author Mara
 * @CreateDate 2018-6-24
 * @Description
 * @License Apache License Version 2.0
 */
public abstract class ImageBaseController extends BaseController {

    protected ImageFileInformation imageInformation;
    protected BufferedImage bufferImage;
    protected Image image;
    protected ImageAttributes attributes;

    protected class ProcessParameters {

        protected File sourceFile;
        protected int startIndex, currentIndex, currentTotalHandled;
        protected String status, targetPath, targetPrefix, targetRootPath, finalTargetName;
        protected Date startTime, endTime;
        protected boolean fill, aSize, aColor, aCompression, aQuality, isBatch, createSubDir;
    }

    protected ProcessParameters actualParameters, previewParameters, currentParameters;

    @FXML
    protected ScrollPane scrollPane;
    @FXML
    protected ImageView imageView;
    @FXML
    protected Pane imageConverterAttributes;
    @FXML
    protected ImageConverterAttributesController imageConverterAttributesController;

    public ImageBaseController() {
        targetPathKey = "ImageTargetPath";
        creatSubdirKey = "ImageCreatSubdir";
        fillZeroKey = "ImageFillZero";
        previewKey = "ImagePreview";
        sourcePathKey = "ImageSourcePath";
        appendColorKey = "ImageAppendColor";
        appendCompressionTypeKey = "ImageAppendCompressionType";
        appendDensityKey = "ImageAppendDensity";
        appendQualityKey = "ImageAppendQuality";
        appendSizeKey = "ImageAppendSize";

        fileExtensionFilter = CommonValues.ImageExtensionFilter;
    }

    @Override
    protected void initializeNext() {
        try {

            initializeNext2();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    protected void initializeNext2() {

    }

    @FXML
    @Override
    protected void startProcess(ActionEvent event) {
        isPreview = false;
        makeActualParameters();
        currentParameters = actualParameters;
        doCurrentProcess();
    }

    @FXML
    protected void preview(ActionEvent event) {
        isPreview = true;
        makeActualParameters();
        previewParameters = copyParameters(actualParameters);
        previewParameters.status = "start";
        currentParameters = previewParameters;
        doCurrentProcess();
    }

    protected void doCurrentProcess() {

    }

    public void loadImage(final File file, final boolean onlyInformation) {
        sourceFile = file;
        final String fileName = file.getPath();
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                imageInformation = ImageFileReaders.readImageMetaData(fileName);
                bufferImage = null;
                String format = FileTools.getFileSuffix(fileName).toLowerCase();
                if (!"raw".equals(format) && !onlyInformation) {
                    bufferImage = ImageIO.read(file);
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        afterImageLoaded();
                    }
                });
                return null;
            }
        };
        openLoadingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    protected void afterImageLoaded() {

    }

    protected void makeActualParameters() {
        if (actualParameters != null && "Paused".equals(actualParameters.status)) {
            actualParameters.startIndex = actualParameters.currentIndex;
            return;
        }
        actualParameters = new ProcessParameters();
        actualParameters.currentIndex = 0;

        if (fillZero != null) {
            actualParameters.fill = fillZero.isSelected();
            AppVaribles.setConfigValue(fillZeroKey, actualParameters.fill);
        }

        if (targetSelectionController != null) {
            actualParameters.targetRootPath = targetSelectionController.targetPathInput.getText();
            actualParameters.targetPath = actualParameters.targetRootPath;
            if (targetSelectionController.subdirCheck != null) {
                actualParameters.createSubDir = targetSelectionController.subdirCheck.isSelected();
                AppVaribles.setConfigValue(creatSubdirKey, actualParameters.createSubDir);
            }
        }
        if (targetPrefixInput != null) {
            actualParameters.targetPrefix = targetFileInput.getText();
        }

        if (imageConverterAttributesController != null) {
            actualParameters.aSize = appendSize.isSelected();
            actualParameters.aColor = appendColor.isSelected();
            actualParameters.aCompression = appendCompressionType.isSelected();
            actualParameters.aQuality = appendQuality.isSelected();

            AppVaribles.setConfigValue(appendSizeKey, actualParameters.aSize);
            AppVaribles.setConfigValue(appendColorKey, actualParameters.aColor);
            AppVaribles.setConfigValue(appendCompressionTypeKey, actualParameters.aCompression);
            AppVaribles.setConfigValue(appendQualityKey, actualParameters.aQuality);
        }

        makeMoreParameters();
    }

    protected void makeMoreParameters() {

    }

    protected void makeSingleParameters() {
        actualParameters.isBatch = false;

        sourceFiles = new ArrayList();
        sourceFiles.add(sourceFile);
        actualParameters.sourceFile = sourceFile;

        if (targetSelectionController != null) {
            if (targetSelectionController.targetPrefixInput != null) {
                actualParameters.targetPrefix = targetSelectionController.targetPrefixInput.getText();
            }
            if (targetSelectionController.targetFileInput != null) {
                actualParameters.targetPrefix = targetSelectionController.targetFileInput.getText();
            }
        }

    }

    protected void makeBatchParameters() {
        actualParameters.isBatch = true;

        sourceFilesInformation = filesTableController.getTableData();
        if (sourceFilesInformation == null || sourceFilesInformation.isEmpty()) {
            actualParameters = null;
            return;
        }
        sourceFiles = new ArrayList();
        for (FileInformation f : sourceFilesInformation) {
            sourceFiles.add(new File(f.getFileName()));
        }
    }

    protected ProcessParameters copyParameters(ProcessParameters theConversion) {
        ProcessParameters newConversion = new ProcessParameters();
        newConversion.aColor = theConversion.aColor;
        newConversion.aCompression = theConversion.aCompression;
        newConversion.aSize = theConversion.aSize;
        newConversion.aQuality = theConversion.aQuality;
        newConversion.currentTotalHandled = theConversion.currentTotalHandled;
        newConversion.sourceFile = theConversion.sourceFile;
        newConversion.targetRootPath = theConversion.targetRootPath;
        newConversion.targetPath = theConversion.targetPath;
        newConversion.targetPrefix = theConversion.targetPrefix;
        newConversion.fill = theConversion.fill;
        newConversion.createSubDir = theConversion.createSubDir;
        newConversion.status = theConversion.status;
        newConversion.startTime = theConversion.startTime;
        newConversion.isBatch = theConversion.isBatch;
        return newConversion;
    }

    protected void updateInterface(final String newStatus) {
        currentParameters.status = newStatus;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                try {
                    if (operationBarController.fileProgressBar != null) {
                        operationBarController.fileProgressBar.setProgress(currentParameters.currentIndex / sourceFiles.size());
                        operationBarController.fileProgressValue.setText(currentParameters.currentIndex + " / " + sourceFiles.size());
                    }
                    if (null != newStatus) {
                        switch (newStatus) {
                            case "StartFile":
                                operationBarController.statusLabel.setText(currentParameters.sourceFile.getName() + " "
                                        + getMessage("Handling...") + " "
                                        + getMessage("StartTime")
                                        + ": " + DateTools.datetimeToString(currentParameters.startTime));
                                if (operationBarController.fileProgressBar != null) {
                                    operationBarController.fileProgressBar.setProgress(currentParameters.currentIndex / sourceFiles.size());
                                    operationBarController.fileProgressValue.setText(currentParameters.currentIndex + " / " + sourceFiles.size());
                                }
                                break;

                            case "Started":
                                operationBarController.startButton.setText(AppVaribles.getMessage("Cancel"));
                                operationBarController.startButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        cancelProcess(event);
                                    }
                                });
                                operationBarController.pauseButton.setVisible(true);
                                operationBarController.pauseButton.setDisable(false);
                                operationBarController.pauseButton.setText(AppVaribles.getMessage("Pause"));
                                operationBarController.pauseButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        pauseProcess(event);
                                    }
                                });
                                paraBox.setDisable(true);
                                break;

                            case "Paused":
                                operationBarController.startButton.setText(AppVaribles.getMessage("Cancel"));
                                operationBarController.startButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        cancelProcess(event);
                                    }
                                });
                                operationBarController.pauseButton.setVisible(true);
                                operationBarController.pauseButton.setDisable(false);
                                operationBarController.pauseButton.setText(AppVaribles.getMessage("Continue"));
                                operationBarController.pauseButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        startProcess(event);
                                    }
                                });
                                paraBox.setDisable(true);
                                showCost();
                                break;

                            case "CompleteFile":
                                showCost();
                                if (operationBarController.fileProgressBar != null) {
                                    operationBarController.fileProgressBar.setProgress(currentParameters.currentIndex / sourceFiles.size());
                                    operationBarController.fileProgressValue.setText(currentParameters.currentIndex + " / " + sourceFiles.size());
                                }
                                break;

                            case "Done":
                                if (isPreview || !currentParameters.isBatch) {
                                    if (currentParameters.finalTargetName == null
                                            || !new File(currentParameters.finalTargetName).exists()) {
                                        popInformation(AppVaribles.getMessage("NoDataNotSupported"));
                                    } else {
                                        showImageManufacture(currentParameters.finalTargetName);
                                    }
                                }

                            default:
                                operationBarController.startButton.setText(AppVaribles.getMessage("Start"));
                                operationBarController.startButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        startProcess(event);
                                    }
                                });
                                operationBarController.pauseButton.setVisible(false);
                                operationBarController.pauseButton.setDisable(true);
                                paraBox.setDisable(false);
                                showCost();

                        }
                    }

                } catch (Exception e) {
                    logger.error(e.toString());
                }

            }
        });

    }

    protected void showCost() {
        if (operationBarController.statusLabel == null) {
            return;
        }
        long cost = (new Date().getTime() - currentParameters.startTime.getTime()) / 1000;
        double avg = 0;
        if (currentParameters.currentTotalHandled != 0) {
            avg = ValueTools.roundDouble3((double) cost / currentParameters.currentTotalHandled);
        }
        String s = getMessage(currentParameters.status) + ". "
                + getMessage("HandledThisTime") + ": " + currentParameters.currentTotalHandled + " "
                + getMessage("Cost") + ": " + cost + " " + getMessage("Seconds") + ". "
                + getMessage("Average") + ": " + avg + " " + getMessage("SecondsPerItem") + ". "
                + getMessage("StartTime") + ": " + DateTools.datetimeToString(currentParameters.startTime) + ", "
                + getMessage("EndTime") + ": " + DateTools.datetimeToString(new Date());
        operationBarController.statusLabel.setText(s);
    }

    @FXML
    @Override
    protected void pauseProcess(ActionEvent event) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        updateInterface("Paused");
    }

    protected void cancelProcess(ActionEvent event) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }
        updateInterface("Canceled");
    }

    public ImageFileInformation getImageInformation() {
        return imageInformation;
    }

    public void setImageInformation(ImageFileInformation imageInformation) {
        this.imageInformation = imageInformation;
    }

    public BufferedImage getBufferImage() {
        return bufferImage;
    }

    public void setBufferImage(BufferedImage bufferImage) {
        this.bufferImage = bufferImage;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ImageAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(ImageAttributes attributes) {
        this.attributes = attributes;
    }

    public ProcessParameters getActualParameters() {
        return actualParameters;
    }

    public void setActualParameters(ProcessParameters actualParameters) {
        this.actualParameters = actualParameters;
    }

    public ProcessParameters getPreviewParameters() {
        return previewParameters;
    }

    public void setPreviewParameters(ProcessParameters previewParameters) {
        this.previewParameters = previewParameters;
    }

    public ProcessParameters getCurrentParameters() {
        return currentParameters;
    }

    public void setCurrentParameters(ProcessParameters currentParameters) {
        this.currentParameters = currentParameters;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

}