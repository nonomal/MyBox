package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import mara.mybox.image.tools.MarginTools;
import mara.mybox.image.data.PixelsBlend;
import mara.mybox.image.tools.TransformTools;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.ValidationTools;
import mara.mybox.image.file.ImageFileReaders;
import mara.mybox.value.Colors;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2021-6-22
 * @License Apache License Version 2.0
 */
public class ImagePasteBatchController extends BaseImageEditBatchController {

    protected int margin, posX, posY;
    protected BufferedImage clipSource;
    protected int rotateAngle;
    protected PixelsBlend blend;

    @FXML
    protected ComboBox<String> angleSelector;
    @FXML
    protected CheckBox enlargeCheck;
    @FXML
    protected ToggleGroup positionGroup;
    @FXML
    protected RadioButton centerRadio, leftTopRadio, leftBottomRadio, rightTopRadio, rightBottomRadio, customizeRadio;
    @FXML
    protected TextField xInput, yInput, marginInput;
    @FXML
    protected ControlColorsBlend blendController;

    public ImagePasteBatchController() {
        baseTitle = message("ImageBatch") + " - " + message("Paste");
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            startButton.disableProperty().unbind();
            startButton.disableProperty().bind(Bindings.isEmpty(tableView.getItems())
                    .or(Bindings.isEmpty(sourceFileInput.textProperty()))
                    .or(sourceFileInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(xInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(yInput.styleProperty().isEqualTo(UserConfig.badStyle()))
                    .or(marginInput.styleProperty().isEqualTo(UserConfig.badStyle()))
            );

        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    @Override
    public void initOptionsSection() {
        try {
            rotateAngle = 0;

            enlargeCheck.setSelected(UserConfig.getBoolean(baseName + "EnlargerImageAsClip", true));
            enlargeCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
                    UserConfig.setBoolean(baseName + "EnlargerImageAsClip", enlargeCheck.isSelected());
                }
            });

            blendController.setParameters(this);

            angleSelector.getItems().addAll(Arrays.asList("0", "90", "180", "45", "30", "60", "15", "5", "10", "1", "75", "120", "135"));
            angleSelector.setVisibleRowCount(10);
            angleSelector.setValue("0");
            angleSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        rotateAngle = Integer.parseInt(newValue);
                        ValidationTools.setEditorNormal(angleSelector);
                    } catch (Exception e) {
                        ValidationTools.setEditorBadStyle(angleSelector);
                    }
                }
            });

            margin = UserConfig.getInt(baseName + "Margin", 20);
            marginInput.setText(margin + "");
            marginInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> v, String ov, String nv) {
                    checkMargin();
                }
            });

            xInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> v, String ov, String nv) {
                    checkPosition();
                }
            });
            yInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> v, String ov, String nv) {
                    checkPosition();
                }
            });

            positionGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                    checkPositionType();
                }
            });
            checkPositionType();

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    private void checkPositionType() {
        marginInput.setDisable(true);
        marginInput.setStyle(null);
        xInput.setDisable(true);
        xInput.setStyle(null);
        yInput.setDisable(true);
        yInput.setStyle(null);

        if (customizeRadio.isSelected()) {
            xInput.setDisable(false);
            yInput.setDisable(false);
            checkPosition();

        } else if (centerRadio.isSelected()) {

        } else {
            marginInput.setDisable(false);
            checkMargin();
        }
    }

    private void checkMargin() {
        try {
            int v = Integer.parseInt(marginInput.getText());
            if (v >= 0) {
                margin = v;
                UserConfig.setInt(baseName + "Margin", margin);
                marginInput.setStyle(null);
            } else {
                marginInput.setStyle(UserConfig.badStyle());
            }
        } catch (Exception e) {
            marginInput.setStyle(UserConfig.badStyle());
        }
    }

    private void checkPosition() {
        try {
            int v = Integer.parseInt(xInput.getText());
            if (v >= 0) {
                posX = v;
                xInput.setStyle(null);
            } else {
                xInput.setStyle(UserConfig.badStyle());
            }
        } catch (Exception e) {
            xInput.setStyle(UserConfig.badStyle());
        }

        try {
            int v = Integer.parseInt(yInput.getText());
            if (v >= 0) {
                posY = v;
                yInput.setStyle(null);
            } else {
                yInput.setStyle(UserConfig.badStyle());
            }
        } catch (Exception e) {
            yInput.setStyle(UserConfig.badStyle());
        }
    }

    @Override
    public boolean beforeHandleFiles(FxTask currentTask) {
        blend = blendController.pickValues(-1);
        if (blend == null) {
            return false;
        }
        clipSource = ImageFileReaders.readImage(currentTask, sourceFile);
        if (currentTask == null || !currentTask.isWorking()) {
            return false;
        }
        if (clipSource != null) {
            clipSource = TransformTools.rotateImage(currentTask, clipSource, rotateAngle, true);
        }
        return clipSource != null;
    }

    @Override
    protected BufferedImage handleImage(FxTask currentTask, BufferedImage source) {
        try {
            BufferedImage bgImage = source;
            if (enlargeCheck.isSelected()) {
                if (clipSource.getWidth() > bgImage.getWidth()) {
                    bgImage = MarginTools.addMargins(currentTask, bgImage,
                            Colors.TRANSPARENT, clipSource.getWidth() - bgImage.getWidth() + 1,
                            false, false, false, true);
                }
                if (clipSource.getHeight() > bgImage.getHeight()) {
                    bgImage = MarginTools.addMargins(currentTask, bgImage,
                            Colors.TRANSPARENT, clipSource.getHeight() - bgImage.getHeight() + 1,
                            false, true, false, false);
                }
            }
            if (currentTask == null || !currentTask.isWorking()) {
                return null;
            }
            if (bgImage == null) {
                return null;
            }

            int x, y;
            if (centerRadio.isSelected()) {
                x = (bgImage.getWidth() - clipSource.getWidth()) / 2;
                y = (bgImage.getHeight() - clipSource.getHeight()) / 2;

            } else if (leftTopRadio.isSelected()) {
                x = margin;
                y = margin;

            } else if (leftBottomRadio.isSelected()) {
                x = margin;
                y = bgImage.getHeight() - clipSource.getHeight() - margin;

            } else if (rightTopRadio.isSelected()) {
                x = bgImage.getWidth() - clipSource.getWidth() - margin;
                y = margin;

            } else if (rightBottomRadio.isSelected()) {
                x = bgImage.getWidth() - clipSource.getWidth() - margin;
                y = bgImage.getHeight() - clipSource.getHeight() - margin;

            } else if (customizeRadio.isSelected()) {
                x = posX;
                y = posY;

            } else {
                return null;
            }
            BufferedImage target = PixelsBlend.blend(currentTask, clipSource, bgImage, x, y, blend);
            return target;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }

    }

}
