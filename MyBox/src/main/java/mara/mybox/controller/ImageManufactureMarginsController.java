package mara.mybox.controller;

import mara.mybox.controller.base.ImageManufactureController;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import mara.mybox.data.DoubleRectangle;
import mara.mybox.fxml.FxmlControl;
import static mara.mybox.value.AppVaribles.logger;
import mara.mybox.value.CommonValues;
import mara.mybox.value.AppVaribles;
import static mara.mybox.fxml.FxmlControl.badStyle;
import mara.mybox.fxml.FxmlImageManufacture;
import static mara.mybox.value.AppVaribles.message;
import static mara.mybox.value.AppVaribles.message;

/**
 * @Author Mara
 * @CreateDate 2018-10-11
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureMarginsController extends ImageManufactureController {

    protected int addedWidth, distance;
    private OperationType opType;

    @FXML
    protected ToggleGroup opGroup;
    @FXML
    protected ComboBox marginWidthBox;
    @FXML
    protected CheckBox marginsTopCheck, marginsBottomCheck, marginsLeftCheck, marginsRightCheck,
            preAlphaCheck;
    @FXML
    private HBox colorBox, distanceBox, widthBox, setBox, marginsBox, setBox2, alphaBox;
    @FXML
    private TextField distanceInput;
    @FXML
    protected RadioButton blurMarginsRadio, dragRadio;
    @FXML
    protected ImageView preAlphaTipsView;
    @FXML
    protected VBox barsBox;
    @FXML
    protected ToolBar tmpToolbar;

    public ImageManufactureMarginsController() {
    }

    public enum OperationType {
        SetMarginsByDragging,
        CutMarginsByColor,
        CutMarginsByWidth,
        AddMargins,
        BlurMargins
    }

    @Override
    public void initializeNext2() {
        try {
            initCommon();
            initMarginsTab();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void afterSceneLoaded() {
        super.afterSceneLoaded();

        distanceInput.setText("20");
        checkOperationType();

    }

    @Override
    protected void initInterface() {
        try {
            if (values == null || values.getImage() == null) {
                return;
            }
            super.initInterface();

            isSettingValues = true;
            tabPane.getSelectionModel().select(marginsTab);

            if (values.getImageInfo() != null
                    && CommonValues.NoAlphaImages.contains(values.getImageInfo().getImageFormat())) {
                transparentButton.setDisable(true);
                preAlphaCheck.setSelected(true);
                preAlphaCheck.setDisable(true);
            } else {
                transparentButton.setDisable(false);
                preAlphaCheck.setSelected(false);
                preAlphaCheck.setDisable(false);
            }

            marginWidthBox.getItems().clear();
            marginWidthBox.getItems().addAll(Arrays.asList((int) values.getImage().getWidth() / 6 + "",
                    (int) values.getImage().getWidth() / 8 + "",
                    (int) values.getImage().getWidth() / 4 + "",
                    (int) values.getImage().getWidth() / 10 + "",
                    "20", "10", "5", "100", "200", "300", "50", "150", "500"));
            marginWidthBox.getSelectionModel().select(0);
            isSettingValues = false;

            checkOperationType();

        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    protected void initMarginsTab() {
        try {
            opGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkOperationType();
                }
            });

            distanceInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    checkColor();
                }
            });

            marginWidthBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (isSettingValues) {
                        return;
                    }
                    checkMarginWidth();
                }
            });

            okButton.disableProperty().bind(
                    marginWidthBox.getEditor().styleProperty().isEqualTo(badStyle)
                            .or(distanceInput.styleProperty().isEqualTo(badStyle))
            );

            setBox2.getChildren().clear();
            barsBox.getChildren().remove(tmpToolbar);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkOperationType() {
        setBox.getChildren().clear();
        promptLabel.setText("");
        pickColorButton.setSelected(false);

        RadioButton selected = (RadioButton) opGroup.getSelectedToggle();
        if (message("Dragging").equals(selected.getText())) {
            opType = OperationType.SetMarginsByDragging;
            setBox.getChildren().addAll(colorBox);
            promptLabel.setText(message("DragMarginsComments"));
            initMaskRectangleLine(true);

        } else {

            initMaskRectangleLine(false);

            if (message("AddMargins").equals(selected.getText())) {
                opType = OperationType.AddMargins;
                setBox.getChildren().addAll(colorBox, widthBox, marginsBox);
                checkMarginWidth();

            } else if (message("CutMarginsByWidth").equals(selected.getText())) {
                opType = OperationType.CutMarginsByWidth;
                setBox.getChildren().addAll(widthBox, marginsBox);
                checkMarginWidth();

            } else if (message("CutMarginsByColor").equals(selected.getText())) {
                opType = OperationType.CutMarginsByColor;
                setBox.getChildren().addAll(colorBox, distanceBox, marginsBox);
                marginWidthBox.getEditor().setStyle(null);
                checkColor();

            } else if (message("Blur").equals(selected.getText())) {
                opType = OperationType.BlurMargins;
                setBox.getChildren().addAll(alphaBox, widthBox, marginsBox);
                checkMarginWidth();

            }
        }
    }

    private void checkMarginWidth() {
        try {
            addedWidth = Integer.valueOf((String) marginWidthBox.getSelectionModel().getSelectedItem());
            if (addedWidth > 0) {
                FxmlControl.setEditorNormal(marginWidthBox);
            } else {
                addedWidth = 0;
                FxmlControl.setEditorBadStyle(marginWidthBox);
            }

        } catch (Exception e) {
            addedWidth = 0;
            FxmlControl.setEditorBadStyle(marginWidthBox);
        }
    }

    protected void checkColor() {
        try {
            distance = Integer.valueOf(distanceInput.getText());
            distanceInput.setStyle(null);
            if (distance >= 0 && distance <= 255) {
                distanceInput.setStyle(null);
            } else {
                distanceInput.setStyle(badStyle);
                distance = 0;
            }
        } catch (Exception e) {
            distanceInput.setStyle(badStyle);
            distance = 0;
        }
    }

    @FXML
    public void setTransparentAction() {
        colorPicker.setValue(Color.TRANSPARENT);
    }

    @FXML
    public void setBlackAction() {
        colorPicker.setValue(Color.BLACK);
    }

    @FXML
    public void setWhiteAction() {
        colorPicker.setValue(Color.WHITE);
    }

    @FXML
    @Override
    public void imageClicked(MouseEvent event) {
        if (imageView.getImage() == null || opType == OperationType.CutMarginsByWidth) {
            imageView.setCursor(Cursor.OPEN_HAND);
            return;
        }
        imageView.setCursor(Cursor.HAND);

        int x = (int) Math.round(event.getX() * imageView.getImage().getWidth() / imageView.getBoundsInParent().getWidth());
        int y = (int) Math.round(event.getY() * imageView.getImage().getHeight() / imageView.getBoundsInParent().getHeight());

        PixelReader pixelReader = imageView.getImage().getPixelReader();
        Color color = pixelReader.getColor(x, y);
        colorPicker.setValue(color);

    }

    @Override
    public void setDafultMaskRectangleValues() {
        if (imageView == null || maskPane == null || maskRectangleLine == null) {
            return;
        }
        maskRectangleData = new DoubleRectangle(0, 0,
                imageView.getImage().getWidth() - 1, imageView.getImage().getHeight() - 1);
    }

    @FXML
    @Override
    public void okAction() {
        if (opType != OperationType.SetMarginsByDragging) {
            if (!marginsTopCheck.isSelected()
                    && !marginsBottomCheck.isSelected()
                    && !marginsLeftCheck.isSelected()
                    && !marginsRightCheck.isSelected()) {
                popError(AppVaribles.message("NothingHandled"));
                return;
            }
        }
        task = new Task<Void>() {
            private Image newImage;
            private boolean ok;

            @Override
            protected Void call() throws Exception {

                switch (opType) {
                    case SetMarginsByDragging:
                        newImage = FxmlImageManufacture.dragMarginsFx(imageView.getImage(),
                                colorPicker.getValue(), maskRectangleData);
                        recordImageHistory(ImageOperationType.Add_Margins, newImage);
                        break;
                    case CutMarginsByWidth:
                        newImage = FxmlImageManufacture.cutMarginsByWidth(imageView.getImage(), addedWidth,
                                marginsTopCheck.isSelected(), marginsBottomCheck.isSelected(),
                                marginsLeftCheck.isSelected(), marginsRightCheck.isSelected());
                        recordImageHistory(ImageOperationType.Cut_Margins, newImage);
                        break;
                    case CutMarginsByColor:
                        newImage = FxmlImageManufacture.cutMarginsByColor(imageView.getImage(),
                                colorPicker.getValue(), distance,
                                marginsTopCheck.isSelected(), marginsBottomCheck.isSelected(),
                                marginsLeftCheck.isSelected(), marginsRightCheck.isSelected());
                        recordImageHistory(ImageOperationType.Cut_Margins, newImage);
                        break;
                    case AddMargins:
                        newImage = FxmlImageManufacture.addMarginsFx(imageView.getImage(),
                                colorPicker.getValue(), addedWidth,
                                marginsTopCheck.isSelected(), marginsBottomCheck.isSelected(),
                                marginsLeftCheck.isSelected(), marginsRightCheck.isSelected());
                        recordImageHistory(ImageOperationType.Add_Margins, newImage);
                        break;
                    case BlurMargins:
                        if (preAlphaCheck.isSelected()) {
                            newImage = FxmlImageManufacture.blurMarginsNoAlpha(imageView.getImage(), addedWidth,
                                    marginsTopCheck.isSelected(), marginsBottomCheck.isSelected(),
                                    marginsLeftCheck.isSelected(), marginsRightCheck.isSelected());
                        } else {
                            newImage = FxmlImageManufacture.blurMarginsAlpha(imageView.getImage(), addedWidth,
                                    marginsTopCheck.isSelected(), marginsBottomCheck.isSelected(),
                                    marginsLeftCheck.isSelected(), marginsRightCheck.isSelected());
                        }
                        recordImageHistory(ImageOperationType.Blur_Margins, newImage);
                        break;
                    default:
                        return null;
                }

                if (task == null || task.isCancelled()) {
                    return null;
                }

                ok = true;
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (ok) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            values.setUndoImage(imageView.getImage());
                            values.setCurrentImage(newImage);
                            imageView.setImage(newImage);
                            paneSize();
                            setImageChanged(true);
                            if (opType == OperationType.SetMarginsByDragging) {
                                drawMaskAroundLine(imageView);
                            }
                        }
                    });
                }
            }
        };
        openHandlingStage(task, Modality.WINDOW_MODAL);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void setDragMode() {
        dragRadio.fire();
    }

}