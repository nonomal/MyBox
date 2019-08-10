package mara.mybox.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import mara.mybox.controller.base.BaseController;
import mara.mybox.controller.base.ImageManufactureController;
import mara.mybox.data.ConvolutionKernel;
import mara.mybox.db.TableConvolutionKernel;
import mara.mybox.fxml.ControlStyle;
import mara.mybox.fxml.FxmlControl;
import static mara.mybox.fxml.FxmlControl.badStyle;
import mara.mybox.image.ImageBinary;
import mara.mybox.image.ImageContrast;
import mara.mybox.image.ImageContrast.ContrastAlgorithm;
import mara.mybox.image.ImageConvolution;
import mara.mybox.image.ImageConvolution.BlurAlgorithm;
import mara.mybox.image.ImageGray;
import mara.mybox.image.ImageManufacture.Direction;
import mara.mybox.image.ImageQuantization;
import mara.mybox.image.ImageQuantization.QuantizationAlgorithm;
import mara.mybox.image.ImageScope;
import mara.mybox.image.PixelsOperation;
import mara.mybox.image.PixelsOperation.OperationType;
import static mara.mybox.value.AppVaribles.logger;
import static mara.mybox.value.AppVaribles.message;
import mara.mybox.value.CommonValues;

/**
 * @Author Mara
 * @CreateDate 2018-10-11
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureEffectsController extends ImageManufactureController {

    private OperationType effectType;
    protected int intPara1, intPara2, intPara3;
    private List<ConvolutionKernel> kernels;
    private ConvolutionKernel kernel;
    private ComboBox<String> intBox, stringBox;
    private Label intLabel, intLabel2, intLabel3, intLabel4, stringLabel;
    private CheckBox valueCheck;
    private TextField intInput, intInput2, intInput3, intInput4;
    private RadioButton radio1, radio2, radio3, radio4;
    private ToggleGroup radioGroup;
    private Button setButton;
    private QuantizationAlgorithm quantizationAlgorithm;
    private ContrastAlgorithm contrastAlgorithm;
    private BlurAlgorithm blurAlgorithm;

    @FXML
    protected HBox setBox, tabBox;
    @FXML
    protected ComboBox<String> objectBox;
    @FXML
    protected ImageView scopeTipsView, ditherTipsView;

    public ImageManufactureEffectsController() {
    }

    @Override
    public void initializeNext2() {
        try {
            initCommon();
            initEffectsTab();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void keyEventsHandler(KeyEvent event) {
        super.keyEventsHandler(event);
        if (event.isControlDown()) {
            String key = event.getText();
            if (key == null || key.isEmpty()) {
                return;
            }
            switch (key) {
                case "k":
                case "K":
                    if (stringBox != null) {
                        stringBox.show();
                    }
                    break;
            }
        }
    }

    protected void initEffectsTab() {
        try {
            List<String> objects = Arrays.asList(message("Contrast"), message("Clarity"),
                    message("Posterizing"), message("Thresholding"),
                    message("Gray"), message("BlackOrWhite"), message("Sepia"),
                    message("EdgeDetection"), message("Emboss"),
                    message("Blur"), message("Sharpen"), message("Convolution"));
            objectBox.getItems().addAll(objects);
            objectBox.setVisibleRowCount(objects.size());
            objectBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                    checkEffetcsOperationType();
                }
            });

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override
    protected void initInterface() {
        try {
            if (values == null || values.getImage() == null) {
                return;
            }
            super.initInterface();

            isSettingValues = true;
            tabPane.getSelectionModel().select(effectsTab);
            isSettingValues = false;

            objectBox.getSelectionModel().select(0);

        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    private void removeTmpControls() {
        intBox = null;
        valueCheck = null;
        intInput = intInput2 = intInput3 = intInput4 = null;
        intLabel = intLabel2 = intLabel3 = intLabel4 = stringLabel = null;
        radio1 = radio2 = radio3 = radio4 = null;
        setButton = null;
        tipsView = null;
        scopeListBox.setDisable(false);
    }

    private void checkEffetcsOperationType() {
        try {
            setBox.getChildren().clear();
            okButton.disableProperty().unbind();
            removeTmpControls();
            stringBox = null;
            radioGroup = null;

            String selectedString = objectBox.getSelectionModel().getSelectedItem();
            if (message("Blur").equals(selectedString)) {
                effectType = OperationType.Blur;
                makeBlurBox();

            } else if (message("Sharpen").equals(selectedString)) {
                effectType = OperationType.Sharpen;

            } else if (message("Clarity").equals(selectedString)) {
                effectType = OperationType.Clarity;

            } else if (message("EdgeDetection").equals(selectedString)) {
                effectType = OperationType.EdgeDetect;

            } else if (message("Emboss").equals(selectedString)) {
                effectType = OperationType.Emboss;
                makeEmbossBox();

            } else if (message("Posterizing").equals(selectedString)) {
                effectType = OperationType.Quantization;
                makePosterizingBox();

            } else if (message("Thresholding").equals(selectedString)) {
                effectType = OperationType.Thresholding;
                makeThresholdingBox();

            } else if (message("Gray").equals(selectedString)) {
                effectType = OperationType.Gray;

            } else if (message("BlackOrWhite").equals(selectedString)) {
                effectType = OperationType.BlackOrWhite;
                makeBlackWhiteBox();

            } else if (message("Sepia").equals(selectedString)) {
                effectType = OperationType.Sepia;
                makeSepiaBox();

            } else if (message("Contrast").equals(selectedString)) {
                effectType = OperationType.Contrast;
                makeContrastBox();

            } else if (message("Convolution").equals(selectedString)) {
                effectType = OperationType.Convolution;
                makeConvolutionBox();

            }

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void makeBlurBox() {
        try {
            intPara1 = 10;
            intLabel = new Label(message("Radius"));
            intBox = new ComboBox();
            intBox.setEditable(true);
            intBox.setPrefWidth(80);
            intBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(newValue);
                        if (v > 0) {
                            intPara1 = v;
                            FxmlControl.setEditorNormal(intBox);
                        } else {
                            FxmlControl.setEditorBadStyle(intBox);
                        }
                    } catch (Exception e) {
                        FxmlControl.setEditorBadStyle(intBox);
                    }
                }
            });
            intBox.getItems().addAll(Arrays.asList("3", "5", "10", "2", "1", "8", "15", "20", "30"));
            intBox.getSelectionModel().select(0);
            stringLabel = new Label(message("Algorithm"));
            stringBox = new ComboBox();
            stringBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        if (message("AverageBlur").equals(newValue)) {
                            blurAlgorithm = BlurAlgorithm.AverageBlur;
                        } else {
                            blurAlgorithm = BlurAlgorithm.GaussianBlur;
                        }
                        FxmlControl.setEditorNormal(stringBox);
                    } catch (Exception e) {
                        FxmlControl.setEditorBadStyle(stringBox);
                    }
                }
            });
            stringBox.getItems().addAll(Arrays.asList(message("GaussianBlur"), message("AverageBlur")));
            stringBox.getSelectionModel().select(message("GaussianBlur"));
            setBox.getChildren().addAll(stringLabel, stringBox, intLabel, intBox);
            okButton.disableProperty().bind(
                    intBox.getEditor().styleProperty().isEqualTo(badStyle)
                            .or(stringBox.getEditor().styleProperty().isEqualTo(badStyle))
            );
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void makeEmbossBox() {
        try {
            intPara1 = Direction.Top;
            stringLabel = new Label(message("Direction"));
            stringBox = new ComboBox();
            stringBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return;
                    }
                    if (message("Top").equals(newValue)) {
                        intPara1 = Direction.Top;
                    } else if (message("Bottom").equals(newValue)) {
                        intPara1 = Direction.Bottom;
                    } else if (message("Left").equals(newValue)) {
                        intPara1 = Direction.Top;
                    } else if (message("Right").equals(newValue)) {
                        intPara1 = Direction.Right;
                    } else if (message("LeftTop").equals(newValue)) {
                        intPara1 = Direction.LeftTop;
                    } else if (message("RightBottom").equals(newValue)) {
                        intPara1 = Direction.RightBottom;
                    } else if (message("LeftBottom").equals(newValue)) {
                        intPara1 = Direction.LeftBottom;
                    } else if (message("RightTop").equals(newValue)) {
                        intPara1 = Direction.RightTop;
                    } else {
                        intPara1 = Direction.Top;
                    }
                }
            });
            stringBox.getItems().addAll(Arrays.asList(message("Top"), message("Bottom"),
                    message("Left"), message("Right"),
                    message("LeftTop"), message("RightBottom"),
                    message("LeftBottom"), message("RightTop")));
            stringBox.getSelectionModel().select(message("Top"));
            intPara2 = 3;
            intLabel = new Label(message("Radius"));
            intBox = new ComboBox();
            intBox.setEditable(false);
            intBox.setPrefWidth(80);
            intBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(newValue);
                        if (v > 0) {
                            intPara2 = v;
                            FxmlControl.setEditorNormal(intBox);
                        } else {
                            FxmlControl.setEditorBadStyle(intBox);
                        }
                    } catch (Exception e) {
                        FxmlControl.setEditorBadStyle(intBox);
                    }
                }
            });
            intBox.getItems().addAll(Arrays.asList("3", "5"));
            intBox.getSelectionModel().select(0);
            valueCheck = new CheckBox(message("Gray"));
            valueCheck.setSelected(true);
            setBox.getChildren().addAll(stringLabel, stringBox, intLabel, intBox, valueCheck);
            okButton.disableProperty().bind(
                    intBox.getEditor().styleProperty().isEqualTo(badStyle)
                            .or(stringBox.getEditor().styleProperty().isEqualTo(badStyle))
            );
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void makePosterizingBox() {
        try {
            tipsView = new ImageView();
            tipsView.setFitWidth(20);
            tipsView.setFitHeight(20);
            ControlStyle.setStyle(tipsView, "quantizationTipsView", true);

            quantizationAlgorithm = QuantizationAlgorithm.RGB_Uniform;
            stringLabel = new Label(message("Algorithm"));
            stringBox = new ComboBox();
            stringBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (message("RGBUniformQuantization").equals(newValue)) {
                        quantizationAlgorithm = QuantizationAlgorithm.RGB_Uniform;
                    } else if (message("HSBUniformQuantization").equals(newValue)) {
                        quantizationAlgorithm = QuantizationAlgorithm.HSB_Uniform;
                    }
                }
            });
            stringBox.getItems().addAll(Arrays.asList(message("RGBUniformQuantization"),
                    message("HSBUniformQuantization")));
            stringBox.getSelectionModel().select(message("RGBUniformQuantization"));
            intPara1 = 64;
            intLabel = new Label(message("ColorsNumber"));
            intBox = new ComboBox();
            intBox.setEditable(false);
            intBox.setPrefWidth(120);
            intBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(newValue);
                        if (v > 0) {
                            intPara1 = v;
                            FxmlControl.setEditorNormal(intBox);
                        } else {
                            FxmlControl.setEditorBadStyle(intBox);
                        }
                    } catch (Exception e) {
                        FxmlControl.setEditorBadStyle(intBox);
                    }
                }
            });
            intBox.getItems().addAll(Arrays.asList(
                    "64", "512", "8", "4096", "216", "343", "27", "125", "1000", "729", "1728", "8000"));
            intBox.getSelectionModel().select(0);
            valueCheck = new CheckBox(message("Dithering"));
            valueCheck.setSelected(true);
            setBox.getChildren().addAll(tipsView, stringLabel, stringBox, intLabel, intBox, ditherTipsView, valueCheck);
            okButton.disableProperty().bind(
                    intBox.getEditor().styleProperty().isEqualTo(badStyle)
                            .or(stringBox.getEditor().styleProperty().isEqualTo(badStyle))
            );
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    private void makeThresholdingBox() {
        try {
            tipsView = new ImageView();
            tipsView.setFitWidth(20);
            tipsView.setFitHeight(20);
            ControlStyle.setStyle(tipsView, "thresholdingTipsView", true);

            intPara1 = 128;
            intLabel = new Label(message("Threshold"));
            intInput = new TextField();
            intInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(newValue);
                        if (v >= 0 && v <= 255) {
                            intPara1 = v;
                            intInput.setStyle(null);
                        } else {
                            popError("0~255");
                            intInput.setStyle(badStyle);
                        }
                    } catch (Exception e) {
                        popError("0~255");
                        intInput.setStyle(badStyle);
                    }
                }
            });
            intInput.setPrefWidth(100);
            intInput.setText("128");
            FxmlControl.setTooltip(intInput, new Tooltip("0~255"));

            intPara2 = 0;
            intLabel2 = new Label(message("SmallValue"));
            intInput2 = new TextField();
            intInput2.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(newValue);
                        if (v >= 0 && v <= 255) {
                            intPara2 = v;
                            intInput2.setStyle(null);
                        } else {
                            popError("0~255");
                            intInput2.setStyle(badStyle);
                        }
                    } catch (Exception e) {
                        popError("0~255");
                        intInput2.setStyle(badStyle);
                    }
                }
            });
            intInput2.setPrefWidth(100);
            intInput2.setText("0");
            FxmlControl.setTooltip(intInput2, new Tooltip("0~255"));

            intPara3 = 255;
            intLabel3 = new Label(message("BigValue"));
            intInput3 = new TextField();
            intInput3.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(newValue);
                        if (v >= 0 && v <= 255) {
                            intPara3 = v;
                            intInput3.setStyle(null);
                        } else {
                            popError("0~255");
                            intInput3.setStyle(badStyle);
                        }
                    } catch (Exception e) {
                        popError("0~255");
                        intInput3.setStyle(badStyle);
                    }
                }
            });
            intInput3.setPrefWidth(100);
            intInput3.setText("255");
            FxmlControl.setTooltip(intInput3, new Tooltip("0~255"));

            setBox.getChildren().addAll(tipsView, intLabel, intInput,
                    intLabel2, intInput2,
                    intLabel3, intInput3);
            okButton.disableProperty().bind(
                    intInput.styleProperty().isEqualTo(badStyle)
                            .or(intInput3.styleProperty().isEqualTo(badStyle))
                            .or(intInput2.styleProperty().isEqualTo(badStyle))
            );
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    private void makeBlackWhiteBox() {
        try {
            tipsView = new ImageView();
            tipsView.setFitWidth(20);
            tipsView.setFitHeight(20);
            ControlStyle.setStyle(tipsView, "BWThresholdTipsView", true);

            intPara2 = 128;
            intInput = new TextField();
            intInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(intInput.getText());
                        if (v >= 0 && v <= 255) {
                            intPara2 = v;
                            intInput.setStyle(null);
                        } else {
                            intInput.setStyle(badStyle);
                        }
                    } catch (Exception e) {
                        intInput.setStyle(badStyle);
                    }
                }
            });
            intInput.setPrefWidth(100);
            intInput.setText("128");
            FxmlControl.setTooltip(intInput, new Tooltip("0~255"));

            setButton = new Button(message("Calculate"));
            setButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int scaleValue = ImageBinary.calculateThreshold(imageView.getImage());
                    intInput.setText(scaleValue + "");
                }
            });

            intPara1 = 1;
            radioGroup = new ToggleGroup();
            radio1 = new RadioButton(message("OTSU"));
            radio1.setToggleGroup(radioGroup);
            radio1.setUserData(1);
            radio2 = new RadioButton(message("Default"));
            radio2.setToggleGroup(radioGroup);
            radio2.setUserData(2);
            radio3 = new RadioButton(message("Threshold"));
            radio3.setToggleGroup(radioGroup);
            radio3.setUserData(3);
            radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    if (radioGroup.getSelectedToggle() == null) {
                        return;
                    }
                    intPara1 = (int) ((RadioButton) new_toggle).getUserData();
                    intInput.setDisable(intPara1 != 3);
                    setButton.setDisable(intPara1 != 3);
                }
            });
            radio1.setSelected(true);

            valueCheck = new CheckBox(message("Dithering"));
            extraScopeCheck();

            setBox.getChildren().addAll(tipsView, radio1, radio2, radio3,
                    intInput, setButton, ditherTipsView, valueCheck);
            okButton.disableProperty().bind(
                    intInput.styleProperty().isEqualTo(badStyle)
            );
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    @Override
    protected void extraScopeCheck() {
        if (valueCheck == null || !valueCheck.getText().equals(message("Dithering"))) {
            return;
        }
        if (scope.getScopeType() == ImageScope.ScopeType.Matting) {
            valueCheck.setSelected(false);
            valueCheck.setDisable(true);
        } else {
            valueCheck.setSelected(true);
            valueCheck.setDisable(false);
        }
    }

    private void makeSepiaBox() {
        try {
            intPara1 = 80;
            intLabel = new Label(message("Intensity"));
            intInput = new TextField();
            intInput.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                        String oldValue, String newValue) {
                    try {
                        int v = Integer.valueOf(intInput.getText());
                        if (v >= 0 && v <= 255) {
                            intPara1 = v;
                            intInput.setStyle(null);
                        } else {
                            intInput.setStyle(badStyle);
                            popError("0~255");
                        }
                    } catch (Exception e) {
                        popError("0~255");
                        intInput.setStyle(badStyle);
                    }
                }
            });
            intInput.setPrefWidth(100);
            intInput.setText("80");
            FxmlControl.setTooltip(intInput, new Tooltip("0~255"));

            setBox.getChildren().addAll(intLabel, intInput);
            okButton.disableProperty().bind(
                    intInput.styleProperty().isEqualTo(badStyle)
            );
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    private void makeConvolutionBox() {
        try {
            stringLabel = new Label(message("ConvolutionKernel"));
            stringBox = new ComboBox();
            kernel = null;
            if (kernels == null) {
                kernels = TableConvolutionKernel.read();
            }
            loadKernelsList(kernels);
            stringBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue ov, Number oldValue, Number newValue) {
                    int index = newValue.intValue();
                    if (index < 0 || index >= kernels.size()) {
                        kernel = null;
                        FxmlControl.setEditorBadStyle(stringBox);
                        return;
                    }
                    kernel = kernels.get(index);
                    FxmlControl.setEditorNormal(stringBox);
                }
            });
            FxmlControl.setTooltip(stringBox, new Tooltip(message("CTRL+k")));
            setButton = new Button(message("ManageDot"));
            setButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    BaseController c = openStage(CommonValues.ConvolutionKernelManagerFxml);
                    c.setParentController(myController);
                    c.setParentFxml(myFxml);
                }
            });
            setBox.getChildren().addAll(stringLabel, stringBox, setButton);
            okButton.disableProperty().bind(
                    stringBox.getEditor().styleProperty().isEqualTo(badStyle)
            );
        } catch (Exception e) {
            logger.error(e.toString());
        }

    }

    private void makeContrastBox() {
        try {
            contrastAlgorithm = ContrastAlgorithm.HSB_Histogram_Equalization;
            stringLabel = new Label(message("Algorithm"));
            stringBox = new ComboBox();
            stringBox.getItems().addAll(Arrays.asList(message("HSBHistogramEqualization"),
                    message("GrayHistogramEqualization"),
                    message("GrayHistogramStretching"),
                    message("GrayHistogramShifting")
            //                    getMessage("LumaHistogramEqualization"),
            //                    getMessage("AdaptiveHistogramEqualization")
            ));
            stringBox.getSelectionModel().select(0);
            stringBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    if (setBox.getChildren() != null) {
                        if (setBox.getChildren().contains(intInput)) {
                            setBox.getChildren().removeAll(intLabel, intInput);
                        }
                        if (setBox.getChildren().contains(intInput2)) {
                            setBox.getChildren().removeAll(intLabel2, intInput2);
                        }
                    }
                    okButton.disableProperty().unbind();
                    if (message("GrayHistogramEqualization").equals(newValue)) {
                        contrastAlgorithm = ContrastAlgorithm.Gray_Histogram_Equalization;
                    } else if (message("GrayHistogramStretching").equals(newValue)) {
                        contrastAlgorithm = ContrastAlgorithm.Gray_Histogram_Stretching;
                        intPara1 = 100;
                        intLabel = new Label(message("LeftThreshold"));
                        intInput = new TextField();
                        intInput.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable,
                                    String oldValue, String newValue) {
                                try {
                                    int v = Integer.valueOf(intInput.getText());
                                    if (v >= 0) {
                                        intPara1 = v;
                                        intInput.setStyle(null);
                                    } else {
                                        intInput.setStyle(badStyle);
                                    }
                                } catch (Exception e) {
                                    intInput.setStyle(badStyle);
                                }
                            }
                        });
                        intInput.setPrefWidth(100);
                        intInput.setText("100");

                        intPara2 = 100;
                        intLabel2 = new Label(message("RightThreshold"));
                        intInput2 = new TextField();
                        intInput2.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable,
                                    String oldValue, String newValue) {
                                try {
                                    int v = Integer.valueOf(intInput2.getText());
                                    if (v >= 0) {
                                        intPara2 = v;
                                        intInput2.setStyle(null);
                                    } else {
                                        intInput2.setStyle(badStyle);
                                    }
                                } catch (Exception e) {
                                    intInput2.setStyle(badStyle);
                                }
                            }
                        });
                        intInput2.setPrefWidth(100);
                        intInput2.setText("100");

                        setBox.getChildren().addAll(intLabel, intInput, intLabel2, intInput2);
                        okButton.disableProperty().bind(
                                intInput.styleProperty().isEqualTo(badStyle)
                                        .or(intInput2.styleProperty().isEqualTo(badStyle))
                                        .or(stringBox.getEditor().styleProperty().isEqualTo(badStyle))
                        );
                    } else if (message("GrayHistogramShifting").equals(newValue)) {
                        contrastAlgorithm = ContrastAlgorithm.Gray_Histogram_Shifting;
                        intPara1 = 80;
                        intLabel = new Label(message("Offset"));
                        intInput = new TextField();
                        intInput.textProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable,
                                    String oldValue, String newValue) {
                                try {
                                    int v = Integer.valueOf(intInput.getText());
                                    if (v >= -255 && v <= 255) {
                                        intPara1 = v;
                                        intInput.setStyle(null);
                                    } else {
                                        intInput.setStyle(badStyle);
                                        popError("-255 ~ 255");
                                    }
                                } catch (Exception e) {
                                    intInput.setStyle(badStyle);
                                    popError("-255 ~ 255");
                                }
                            }
                        });
                        intInput.setPrefWidth(100);
                        intInput.setText("10");
                        FxmlControl.setTooltip(intInput, new Tooltip("-255 ~ 255"));
                        setBox.getChildren().addAll(intLabel, intInput);
                        okButton.disableProperty().bind(
                                intInput.styleProperty().isEqualTo(badStyle)
                                        .or(stringBox.getEditor().styleProperty().isEqualTo(badStyle))
                        );
                    } else if (message("LumaHistogramEqualization").equals(newValue)) {
                        contrastAlgorithm = ContrastAlgorithm.Luma_Histogram_Equalization;
                    } else if (message("HSBHistogramEqualization").equals(newValue)) {
                        contrastAlgorithm = ContrastAlgorithm.HSB_Histogram_Equalization;
                    } else if (message("AdaptiveHistogramEqualization").equals(newValue)) {
                        contrastAlgorithm = ContrastAlgorithm.Adaptive_Histogram_Equalization;
                    }
                }
            });

            setBox.getChildren().addAll(stringLabel, stringBox);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public void applyKernel(ConvolutionKernel kernel) {
        objectBox.getSelectionModel().select(message("Convolution"));
        if (stringBox.getItems().contains(kernel.getName())) {
            stringBox.getSelectionModel().select(kernel.getName());
        } else {
            stringBox.getSelectionModel().select(-1);
        }
        this.kernel = kernel;
        okAction();
    }

    public void loadKernelsList(List<ConvolutionKernel> records) {
        if (effectType != OperationType.Convolution || stringBox == null) {
            return;
        }
        kernels = records;
        stringBox.getItems().clear();
        if (kernels != null && !kernels.isEmpty()) {
            List<String> names = new ArrayList<>();
            for (ConvolutionKernel k : kernels) {
                names.add(k.getName());
            }
            stringBox.getItems().addAll(names);
            stringBox.getSelectionModel().select(0);
            FxmlControl.setEditorNormal(stringBox);
        } else {
            FxmlControl.setEditorBadStyle(stringBox);
        }
    }

    @FXML
    public void popObjectBox() {
        objectBox.show();
    }

    @FXML
    @Override
    public void okAction() {
        if (null == effectType) {
            return;
        }
        task = new Task<Void>() {
            private Image newImage;
            private boolean ok;

            @Override
            protected Void call() throws Exception {
                PixelsOperation pixelsOperation;
                ImageConvolution imageConvolution;
                switch (effectType) {
                    case Contrast:
                        ImageContrast imageContrast = new ImageContrast(imageView.getImage(), contrastAlgorithm);
                        imageContrast.setIntPara1(intPara1);
                        imageContrast.setIntPara2(intPara2);
                        newImage = imageContrast.operateFxImage();
                        break;
                    case Convolution:
                        if (kernel == null) {
                            int index = stringBox.getSelectionModel().getSelectedIndex();
                            if (kernels == null || kernels.isEmpty() || index < 0) {
                                return null;
                            }
                            kernel = kernels.get(index);
                        }
                        imageConvolution = new ImageConvolution(imageView.getImage(), scope, kernel);
                        newImage = imageConvolution.operateFxImage();
                        break;
                    case Blur:
                        switch (blurAlgorithm) {
                            case AverageBlur:
                                kernel = ConvolutionKernel.makeAverageBlur(intPara1);
                                break;
                            case GaussianBlur:
                                kernel = ConvolutionKernel.makeGaussKernel(intPara1);
                                break;
                            default:
                                return null;
                        }
                        imageConvolution = new ImageConvolution(imageView.getImage(), scope, kernel);
                        newImage = imageConvolution.operateFxImage();
                        break;
                    case Sharpen:
                        kernel = ConvolutionKernel.makeSharpen3b();
                        imageConvolution = new ImageConvolution(imageView.getImage(), scope, kernel);
                        newImage = imageConvolution.operateFxImage();
                        break;
                    case Clarity:
                        kernel = ConvolutionKernel.makeUnsharpMasking5();
                        imageConvolution = new ImageConvolution(imageView.getImage(), scope, kernel);
                        newImage = imageConvolution.operateFxImage();
                        break;
                    case EdgeDetect:
                        kernel = ConvolutionKernel.makeEdgeDetection3b();
                        imageConvolution = new ImageConvolution(imageView.getImage(), scope, kernel);
                        newImage = imageConvolution.operateFxImage();
                        break;
                    case Emboss:
                        kernel = ConvolutionKernel.makeEmbossKernel(intPara1, intPara2, valueCheck.isSelected());
                        imageConvolution = new ImageConvolution(imageView.getImage(), scope, kernel);
                        newImage = imageConvolution.operateFxImage();
                        break;
                    case Quantization:
                        int channelSize = (int) Math.round(Math.pow(intPara1, 1.0 / 3.0));
                        ImageQuantization quantization = new ImageQuantization(imageView.getImage());
                        quantization.setScope(scope);
                        quantization.set(quantizationAlgorithm, channelSize);
                        quantization.setIsDithering(valueCheck.isSelected());
                        newImage = quantization.operateFxImage();
                        break;
                    case Thresholding:
                        pixelsOperation = PixelsOperation.newPixelsOperation(imageView.getImage(), scope, effectType);
                        pixelsOperation.setIntPara1(intPara1);
                        pixelsOperation.setIntPara2(intPara2);
                        pixelsOperation.setIntPara3(intPara3);
                        pixelsOperation.setIsDithering(false);
                        newImage = pixelsOperation.operateFxImage();
                        break;
                    case BlackOrWhite:
                        ImageBinary imageBinary;
                        switch (intPara1) {
                            case 2:
                                imageBinary = new ImageBinary(imageView.getImage(), scope, -1);
                                break;
                            case 3:
                                imageBinary = new ImageBinary(imageView.getImage(), scope, intPara2);
                                break;
                            default:
                                int t = ImageBinary.calculateThreshold(imageView.getImage());
                                imageBinary = new ImageBinary(imageView.getImage(), scope, t);
                                break;
                        }
                        imageBinary.setIsDithering(valueCheck.isSelected());
                        newImage = imageBinary.operateFxImage();
                        break;
                    case Gray:
                        ImageGray imageGray = new ImageGray(imageView.getImage(), scope);
                        newImage = imageGray.operateFxImage();
                        break;
                    case Sepia:
                        pixelsOperation = PixelsOperation.newPixelsOperation(imageView.getImage(), scope, effectType);
                        pixelsOperation.setIntPara1(intPara1);
                        newImage = pixelsOperation.operateFxImage();
                        break;
                    default:
                        return null;
                }
                if (task.isCancelled() || newImage == null) {
                    return null;
                }
                recordImageHistory(ImageOperationType.Effects, newImage);

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