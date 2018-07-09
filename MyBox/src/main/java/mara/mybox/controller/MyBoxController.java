package mara.mybox.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import mara.mybox.objects.AppVaribles;
import mara.mybox.objects.CommonValues;

/**
 * @Author Mara
 * @CreateDate 2018-6-4 17:48:15
 * @Description
 * @License Apache License Version 2.0
 */
public class MyBoxController extends BaseController {

    private ContextMenu pdfMenus, imageMenu, fileMenu;

    @FXML
    private VBox imageBox;

    @FXML
    private VBox pdfBox;

    @FXML
    private VBox fileBox;

    @Override
    protected void initializeNext() {

        MenuItem pdfExtractImages = new MenuItem(AppVaribles.getMessage("PdfExtractImages"));
        pdfExtractImages.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractImagesFxml, AppVaribles.getMessage("PdfExtractImages"));
            }
        });
        MenuItem pdfExtractImagesBatch = new MenuItem(AppVaribles.getMessage("PdfExtractImagesBatch"));
        pdfExtractImagesBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractImagesBatchFxml, AppVaribles.getMessage("PdfExtractImagesBatch"));
            }
        });
        MenuItem pdfExtractTexts = new MenuItem(AppVaribles.getMessage("PdfExtractTexts"));
        pdfExtractTexts.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractTextsFxml, AppVaribles.getMessage("pdfExtractTexts"));
            }
        });
        MenuItem pdfExtractTextsBatch = new MenuItem(AppVaribles.getMessage("PdfExtractTextsBatch"));
        pdfExtractTextsBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfExtractTextsBatchFxml, AppVaribles.getMessage("PdfExtractTextsBatch"));
            }
        });
        MenuItem pdfConvertImages = new MenuItem(AppVaribles.getMessage("PdfConvertImages"));
        pdfConvertImages.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfConvertImagesFxml, AppVaribles.getMessage("PdfConvertImages"));
            }
        });
        MenuItem pdfConvertImagesBatch = new MenuItem(AppVaribles.getMessage("PdfConvertImagesBatch"));
        pdfConvertImagesBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.PdfConvertImagesBatchFxml, AppVaribles.getMessage("PdfConvertImagesBatch"));
            }
        });
        pdfMenus = new ContextMenu();
        pdfMenus.getItems().addAll(pdfExtractImages, pdfExtractTexts, pdfConvertImages, new SeparatorMenuItem(),
                pdfExtractImagesBatch, pdfExtractTextsBatch, pdfConvertImagesBatch);

        MenuItem imageViewer = new MenuItem(AppVaribles.getMessage("ImageViewer"));
        imageViewer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageViewerFxml, AppVaribles.getMessage("ImageViewer"));
            }
        });
        MenuItem imagesViewer = new MenuItem(AppVaribles.getMessage("MultipleImagesViewer"));
        imagesViewer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImagesViewerFxml, AppVaribles.getMessage("MultipleImagesViewer"));
            }
        });
        MenuItem ImageManufacture = new MenuItem(AppVaribles.getMessage("ImageManufacture"));
        ImageManufacture.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageManufactureFxml, AppVaribles.getMessage("ImageManufacture"));
            }
        });
        MenuItem imageConverter = new MenuItem(AppVaribles.getMessage("ImageConverter"));
        imageConverter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageConverterFxml, AppVaribles.getMessage("ImageConverter"));
            }
        });
        MenuItem imageConverterBatch = new MenuItem(AppVaribles.getMessage("ImageConverterBatch"));
        imageConverterBatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.ImageConverterBatchFxml, AppVaribles.getMessage("ImageConverterBatch"));
            }
        });
        MenuItem pixelsCalculator = new MenuItem(AppVaribles.getMessage("PixelsCalculator"));
        pixelsCalculator.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openStage(CommonValues.PixelsCalculatorFxml, AppVaribles.getMessage("PixelsCalculator"), false);
            }
        });
        imageMenu = new ContextMenu();
        imageMenu.getItems().addAll(ImageManufacture, imagesViewer, new SeparatorMenuItem(),
                imageConverter, imageConverterBatch, new SeparatorMenuItem(),
                pixelsCalculator);

        MenuItem filesRename = new MenuItem(AppVaribles.getMessage("FilesRename"));
        filesRename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.FilesRenameFxml, AppVaribles.getMessage("FilesRename"));
            }
        });
        MenuItem dirsRename = new MenuItem(AppVaribles.getMessage("DirectoriesRename"));
        dirsRename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.DirectoriesRenameFxml, AppVaribles.getMessage("DirectoriesRename"));
            }
        });
        MenuItem dirSynchronize = new MenuItem(AppVaribles.getMessage("DirectorySynchronize"));
        dirSynchronize.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadStage(CommonValues.DirectorySynchronizeFxml, AppVaribles.getMessage("DirectorySynchronize"));
            }
        });
        fileMenu = new ContextMenu();
        fileMenu.getItems().addAll(filesRename, dirsRename, new SeparatorMenuItem(), dirSynchronize);
    }

    @FXML
    void showPdfMenu(MouseEvent event) {
        if (pdfMenus.isShowing()) {
            return;
        }
        Bounds bounds = pdfBox.localToScreen(pdfBox.getBoundsInLocal());
        pdfMenus.show(pdfBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);

    }

    @FXML
    void hidePdfMenu(MouseEvent event) {
//        pdfMenus.hide();
    }

    @FXML
    void showImageMenu(MouseEvent event) {
        if (imageMenu.isShowing()) {
            return;
        }
        Bounds bounds = imageBox.localToScreen(imageBox.getBoundsInLocal());
        imageMenu.show(imageBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);

    }

    @FXML
    void hideImageMenu(MouseEvent event) {

    }

    @FXML
    void showFileMenu(MouseEvent event) {
        if (fileMenu.isShowing()) {
            return;
        }
        Bounds bounds = fileBox.localToScreen(fileBox.getBoundsInLocal());
        fileMenu.show(fileBox, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
    }

    @FXML
    void hideFileMenu(MouseEvent event) {

    }

    @FXML
    private void pdfTools() {
        reloadStage(CommonValues.PdfConvertImagesFxml, AppVaribles.getMessage("PdfConvertImages"));
    }

    @FXML
    private void imageTools() {
        reloadStage(CommonValues.ImageViewerFxml, AppVaribles.getMessage("ImageViewer"));
    }

    @FXML
    private void fileTools() {
        popInformation(AppVaribles.getMessage("Developing..."));
    }

    @FXML
    private void setEnglish(MouseEvent event) {
        AppVaribles.CurrentBundle = CommonValues.BundleEnUS;
        reloadStage(myFxml);
    }

    @FXML
    private void setChinese(MouseEvent event) {
        AppVaribles.CurrentBundle = CommonValues.BundleZhCN;
        reloadStage(myFxml);
    }

    @FXML
    private void showAbout(MouseEvent event) {
        openStage(CommonValues.AboutFxml, true);
    }
}