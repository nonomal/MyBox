package mara.mybox.controller;

import mara.mybox.controller.base.ImageManufactureController;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import static mara.mybox.value.AppVaribles.logger;
import mara.mybox.fxml.FxmlControl;
import mara.mybox.fxml.FxmlImageManufacture;

/**
 * @Author Mara
 * @CreateDate 2018-10-11
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureTransformController extends ImageManufactureController {

    protected float shearX;

    @FXML
    protected ToolBar transformBar;
    @FXML
    protected ComboBox angleBox, shearBox;
    @FXML
    protected Slider angleSlider;
    @FXML
    protected Button shearButton;
    @FXML
    protected HBox tranBox;

    public ImageManufactureTransformController() {
    }

    @Override
    public void initializeNext2() {
        try {
            initCommon();
            initTransformTab();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

//    @Override
//    protected void initInterface() {
//        try {
//            if (values == null || values.getImage() == null) {
//                return;
//            }
//            super.initInterface();
//
//
//            isSettingValues = true;
//
//            isSettingValues = false;
//        } catch (Exception e) {
//            logger.debug(e.toString());
//        }
//
//    }
    protected void initTransformTab() {
        try {

            List<String> shears = Arrays.asList(
                    "0.5", "-0.5", "0.4", "-0.4", "0.2", "-0.2", "0.1", "-0.1",
                    "0.7", "-0.7", "0.9", "-0.9", "0.8", "-0.8", "1", "-1",
                    "1.5", "-1.5", "2", "-2");
            shearBox.getItems().addAll(shears);
            shearBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        shearX = Float.valueOf(newValue);
                        shearButton.setDisable(false);
                        FxmlControl.setEditorNormal(shearBox);
                    } catch (Exception e) {
                        shearX = 0;
                        shearButton.setDisable(true);
                        FxmlControl.setEditorBadStyle(shearBox);
                    }
                }
            });
            shearBox.getSelectionModel().select(0);

            angleSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    rotateAngle = newValue.intValue();
                    angleBox.getEditor().setText(rotateAngle + "");
                    rotateLeftButton.setDisable(false);
                    rotateRightButton.setDisable(false);
                }
            });

            angleBox.getItems().addAll(Arrays.asList("90", "180", "45", "30", "60", "15", "75", "120", "135"));
            angleBox.setVisibleRowCount(10);
            angleBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        rotateAngle = Integer.valueOf(newValue);
                        angleSlider.setValue(rotateAngle);
                        rotateLeftButton.setDisable(false);
                        rotateRightButton.setDisable(false);
                        FxmlControl.setEditorNormal(angleBox);
                    } catch (Exception e) {
                        rotateAngle = 0;
                        rotateLeftButton.setDisable(true);
                        rotateRightButton.setDisable(true);
                        FxmlControl.setEditorBadStyle(angleBox);
                    }
                }
            });
            angleBox.getSelectionModel().select(0);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @FXML
    @Override
    public void rotateRight() {
        task = new Task<Void>() {
            private Image newImage;
            private boolean ok;

            @Override
            protected Void call() throws Exception {
                newImage = FxmlImageManufacture.rotateImage(imageView.getImage(), rotateAngle);
                if (task == null || task.isCancelled()) {
                    return null;
                }
                recordImageHistory(ImageOperationType.Transform, newImage);

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
                            setImageChanged(true);
                            updateLabelTitle();
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

    @FXML
    @Override
    public void rotateLeft() {
        task = new Task<Void>() {
            private Image newImage;
            private boolean ok;

            @Override
            protected Void call() throws Exception {
                newImage = FxmlImageManufacture.rotateImage(imageView.getImage(), 360 - rotateAngle);
                if (task == null || task.isCancelled()) {
                    return null;
                }
                recordImageHistory(ImageOperationType.Transform, newImage);

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
                            setImageChanged(true);
                            updateLabelTitle();
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

    @FXML
    public void horizontalAction() {
        task = new Task<Void>() {
            private Image newImage;
            private boolean ok;

            @Override
            protected Void call() throws Exception {
                newImage = FxmlImageManufacture.horizontalImage(imageView.getImage());
                if (task == null || task.isCancelled()) {
                    return null;
                }
                recordImageHistory(ImageOperationType.Transform, newImage);

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
                            setImageChanged(true);
                            updateLabelTitle();
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

    @FXML
    public void verticalAction() {
        task = new Task<Void>() {
            private Image newImage;
            private boolean ok;

            @Override
            protected Void call() throws Exception {
                newImage = FxmlImageManufacture.verticalImage(imageView.getImage());
                if (task == null || task.isCancelled()) {
                    return null;
                }
                recordImageHistory(ImageOperationType.Transform, newImage);

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
                            setImageChanged(true);
                            updateLabelTitle();
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

    @FXML
    public void shearAction() {
        task = new Task<Void>() {
            private Image newImage;
            private boolean ok;

            @Override
            protected Void call() throws Exception {
                newImage = FxmlImageManufacture.shearImage(imageView.getImage(), shearX, 0);
                if (task == null || task.isCancelled()) {
                    return null;
                }
                recordImageHistory(ImageOperationType.Transform, newImage);

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
                            setImageChanged(true);
                            updateLabelTitle();
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

}