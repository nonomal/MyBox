package mara.mybox.controller;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import mara.mybox.data.TesseractOptions;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.image.data.ImageInformation;
import mara.mybox.image.file.ImageFileWriters;
import mara.mybox.image.tools.AlphaTools;
import mara.mybox.tools.DateTools;
import mara.mybox.tools.FileDeleteTools;
import mara.mybox.tools.FileNameTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.tools.TextFileTools;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;
import net.sourceforge.tess4j.Word;

/**
 * @Author Mara
 * @CreateDate 2019-9-17
 * @License Apache License Version 2.0
 */
/*
https://github.com/nguyenq/tess4j
https://github.com/tesseract-ocr/tesseract/wiki/Data-Files
Images intended for OCR should have at least 200 DPI in resolution, typically 300 DPI, 1 bpp (bit per pixel) monochome
or 8 bpp grayscale uncompressed TIFF or PNG format.
PNG is usually smaller in size than other image formats and still keeps high quality due to its employing lossless data compression algorithms;
TIFF has the advantage of the ability to contain multiple images (pages) in a file.
 */
public class ImageOCRController extends BaseController {

    protected TesseractOptions tesseractOptions;
    protected float scale;
    protected int threshold, rotate;
    protected LoadingController loading;
    protected Process process;

    @FXML
    protected BaseImageController sourceController;
    @FXML
    protected Tab imageTab, processTab;
    @FXML
    protected TextArea textArea;
    @FXML
    protected Label resultLabel;
    @FXML
    protected CheckBox startCheck;
    @FXML
    protected HtmlTableController regionsTableController, wordsTableController, htmlController;
    @FXML
    protected ImageOCRProcessController preprocessController;
    @FXML
    protected TabPane resultsTabPane;
    @FXML
    protected Tab txtTab, htmlTab, regionsTab, wordsTab;
    @FXML
    protected VBox resultsBox, optionsBox;

    public ImageOCRController() {
        baseTitle = message("ImageOCR");
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.Image, VisitHistory.FileType.Text);
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            tesseractOptions = new TesseractOptions()
                    .setSetFormats(false)
                    .setOutHtml(true)
                    .setOutPdf(false);

            preprocessController.OCRController = this;

            startCheck.setSelected(UserConfig.getBoolean(baseName + "Start", false));
            startCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    UserConfig.setBoolean(baseName + "Start", startCheck.isSelected());
                }
            });

            sourceController.loadNotify.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    sourceLoaded();
                }
            });

            startButton.disableProperty().bind(Bindings.isNull(sourceController.imageView.imageProperty()));

        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    public File sourceFile() {
        return sourceController.sourceFile;
    }

    public Image sourceImage() {
        return sourceController.imageView.getImage();
    }

    public ImageInformation sourceInfo() {
        return sourceController.imageInformation;
    }

    public void sourceLoaded() {
        try {
            sourceFile = sourceController.sourceFile;
            String name = sourceFile != null ? FileNameTools.prefix(sourceFile.getName()) : "";
            regionsTableController.baseTitle = name + "_regions";
            wordsTableController.baseTitle = name + "_words";
            htmlController.baseTitle = name + "_texts";

            preprocessController.recoverAction();

            if (startCheck.isSelected()) {
                startAction();
            }
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @FXML
    public void ocrOptions() {
        TesseractOptionsController.open(this, tesseractOptions);
    }

    @FXML
    @Override
    public boolean menuAction(Event event) {
        if (optionsBox.isFocused() || optionsBox.isFocusWithin()) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (tab == imageTab) {
                sourceController.menuAction(event);
                return true;
            } else if (tab == processTab) {
                preprocessController.menuAction(event);
                return true;
            }
        }

        if (htmlTab.isSelected()) {
            htmlController.menuAction(event);
            return true;

        } else if (regionsTab.isSelected()) {
            regionsTableController.menuAction(event);
            return true;

        } else if (wordsTab.isSelected()) {
            wordsTableController.menuAction(event);
            return true;
        }

        Point2D localToScreen = textArea.localToScreen(textArea.getWidth() - 80, 80);
        MenuTextEditController.textMenu(myController, textArea, localToScreen.getX(), localToScreen.getY());
        return true;
    }

    @FXML
    @Override
    public boolean popAction() {
        if (optionsBox.isFocused() || optionsBox.isFocusWithin()) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (tab == imageTab) {
                sourceController.popAction();
                return true;
            } else if (tab == processTab) {
                preprocessController.popAction();
                return true;
            }
        }

        if (htmlTab.isSelected()) {
            htmlController.popAction();
            return true;

        } else if (regionsTab.isSelected()) {
            regionsTableController.popAction();
            return true;

        } else if (wordsTab.isSelected()) {
            wordsTableController.popAction();
            return true;
        }

        TextPopController.openInput(this, textArea);
        return true;
    }

    @Override
    public boolean keyEventsFilter(KeyEvent event) {
        if (optionsBox.isFocused() || optionsBox.isFocusWithin()) {
            Tab tab = tabPane.getSelectionModel().getSelectedItem();
            if (tab == imageTab) {
                if (sourceController.keyEventsFilter(event)) {
                    return true;
                }
            } else if (tab == processTab) {
                if (preprocessController.keyEventsFilter(event)) {
                    return true;
                }
            }
        }

        Tab tab = resultsTabPane.getSelectionModel().getSelectedItem();
        if (tab == htmlTab) {
            if (htmlController.keyEventsFilter(event)) {
                return true;
            }

        } else if (regionsTab.isSelected()) {
            if (regionsTableController.keyEventsFilter(event)) {
                return true;
            }

        } else if (wordsTab.isSelected()) {
            if (wordsTableController.keyEventsFilter(event)) {
                return true;
            }
        }

        return super.keyEventsFilter(event);
    }

    /*
        OCR
     */
    @FXML
    @Override
    public void startAction() {
        if (tesseractOptions.isEmbed()) {
            embedded();
        } else {
            command();
        }
    }

    protected void command() {
        if (preprocessController.imageView.getImage() == null || timer != null || process != null) {
            return;
        }
        task = new FxSingletonTask<Void>(this) {
            private String outputs = "", texts, html;

            @Override
            protected boolean handle() {
                try {
                    Image selected = preprocessController.imageView.getImage();
                    if (selected == null) {
                        selected = preprocessController.imageView.getImage();
                    }
                    File imageFile = FileTmpTools.getTempFile(".png");
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(selected, null);
                    bufferedImage = AlphaTools.removeAlpha(this, bufferedImage);
                    ImageFileWriters.writeImageFile(this, bufferedImage, "png", imageFile.getAbsolutePath());
                    String fileBase = FileTmpTools.getTempFile().getAbsolutePath();
                    process = tesseractOptions.process(imageFile, fileBase);
                    if (process == null) {
                        return false;
                    }
                    startTime = new Date();
                    try (BufferedReader inReader = process.inputReader(Charset.defaultCharset())) {
                        String line;
                        while ((line = inReader.readLine()) != null) {
                            outputs += line + "\n";
                        }
                    } catch (Exception e) {
                        outputs += e.toString() + "\n";
                    }
                    process.waitFor();

                    File txtFile = new File(fileBase + ".txt");
                    if (txtFile.exists()) {
                        texts = TextFileTools.readTexts(this, txtFile);
                        FileDeleteTools.delete(txtFile);
                    } else {
                        texts = null;
                    }

                    File htmlFile = new File(fileBase + ".hocr");
                    if (htmlFile.exists()) {
                        html = TextFileTools.readTexts(this, htmlFile);
                        FileDeleteTools.delete(htmlFile);
                    } else {
                        html = null;
                    }
                    if (process != null) {
                        process.destroy();
                        process = null;
                    }
                    return true;
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
            }

            @Override
            protected void whenSucceeded() {
                if (texts != null) {
                    textArea.setText(texts);
                    String i = MessageFormat.format(message("OCRresults"),
                            texts.length(), DateTools.datetimeMsDuration(new Date().getTime() - startTime.getTime()));
                    resultLabel.setText(i);
                    resultsTabPane.getSelectionModel().select(txtTab);
                } else {
                    if (outputs != null && !outputs.isBlank()) {
                        alertError(outputs);
                    } else {
                        popFailed();
                    }
                }
                if (html != null) {
                    htmlController.loadHtml(html);
                }
            }

        };
        start(task);
    }

    protected void embedded() {
        if (preprocessController.imageView.getImage() == null) {
            return;
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {

            @Override
            protected boolean handle() {
                try {
                    Image selected = preprocessController.imageView.getImage();
                    if (selected == null) {
                        selected = preprocessController.imageView.getImage();
                    }
                    return tesseractOptions.imageOCR(this, selected, true);
                } catch (Exception e) {
                    error = e.toString();
                    MyBoxLog.debug(e);
                    return false;
                }
            }

            @Override
            protected void whenSucceeded() {
                String texts = tesseractOptions.getTexts();
                if (texts == null || texts.length() == 0) {
                    popWarn(message("OCRMissComments"));
                    resultLabel.setText(null);
                } else {
                    resultLabel.setText(MessageFormat.format(message("OCRresults"),
                            texts.length(), DateTools.datetimeMsDuration(cost)));
                }
                textArea.setText(texts);

                resultsTabPane.getSelectionModel().select(txtTab);

                htmlController.loadHtml(tesseractOptions.getHtml());

                List<Rectangle> rectangles = tesseractOptions.getRectangles();
                if (rectangles != null) {
                    List<String> names = new ArrayList<>();
                    names.addAll(Arrays.asList(message("Index"),
                            message("CoordinateX"), message("CoordinateY"),
                            message("Width"), message("Height")
                    ));
                    regionsTableController.initTable(message(""), names);
                    for (int i = 0; i < rectangles.size(); ++i) {
                        Rectangle rect = rectangles.get(i);
                        List<String> data = new ArrayList<>();
                        data.addAll(Arrays.asList(
                                i + "", rect.x + "", rect.y + "", rect.width + "", rect.height + ""
                        ));
                        regionsTableController.addData(data);
                    }
                    regionsTableController.displayHtml();
                } else {
                    regionsTableController.clear();
                }

                List<Word> words = tesseractOptions.getWords();
                if (words != null) {
                    List<String> names = new ArrayList<>();
                    names.addAll(Arrays.asList(message("Index"),
                            message("Contents"), message("Confidence"),
                            message("CoordinateX"), message("CoordinateY"),
                            message("Width"), message("Height")
                    ));
                    wordsTableController.initTable(message(""), names);
                    for (int i = 0; i < words.size(); ++i) {
                        Word word = words.get(i);
                        Rectangle rect = word.getBoundingBox();
                        List<String> data = new ArrayList<>();
                        data.addAll(Arrays.asList(
                                i + "", word.getText(), word.getConfidence() + "",
                                rect.x + "", rect.y + "", rect.width + "", rect.height + ""
                        ));
                        wordsTableController.addData(data);
                        wordsTableController.displayHtml();
                    }
                } else {
                    wordsTableController.clear();
                }

            }

            @Override
            protected void finalAction() {
                super.finalAction();
                tesseractOptions.clearResults();
            }

        };
        start(task);
    }

    @Override
    public void cleanPane() {
        try {
            if (process != null) {
                process.destroy();
                process = null;
            }
            if (loading != null) {
                loading.closeStage();
                loading = null;
            }
        } catch (Exception e) {
        }
        super.cleanPane();
    }

}
