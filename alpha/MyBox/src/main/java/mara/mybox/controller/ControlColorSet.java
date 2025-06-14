package mara.mybox.controller;

import java.sql.Connection;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import mara.mybox.db.table.TableColor;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.LocateTools;
import mara.mybox.fxml.WindowTools;
import mara.mybox.fxml.image.FxColorTools;
import mara.mybox.fxml.style.NodeStyleTools;
import mara.mybox.value.AppVariables;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2020-09-04
 * @License Apache License Version 2.0
 */
public class ControlColorSet extends BaseController {

    protected TableColor tableColor;
    protected String thisName;
    protected Object data;
    protected Color defaultColor;
    protected Connection conn;
    protected SimpleBooleanProperty setNotify;

    @FXML
    protected Rectangle rect;

    public ControlColorSet() {
        baseTitle = "ColorImport";
    }

    @Override
    public void initValues() {
        try {
            super.initValues();
            thisName = baseName;
            tableColor = new TableColor();
            setNotify = new SimpleBooleanProperty(false);

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public ControlColorSet init(BaseController parent, String name) {
        return init(parent, name, Color.GOLD);
    }

    public ControlColorSet init(BaseController parent, String name, Color defaultColor) {
        try {
            if (parent == null) {
                return this;
            }
            parentController = parent;
            thisName = name != null ? name : (parent.baseName + "Color");
            this.defaultColor = defaultColor;
            asSaved();

            rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showColorPalette();
                }
            });

            rect.fillProperty().addListener(new ChangeListener<Paint>() {
                @Override
                public void changed(ObservableValue<? extends Paint> v, Paint ov, Paint nv) {
                    if (isSettingValues || nv == null) {
                        return;
                    }
                    UserConfig.setString(thisName, ((Color) nv).toString());
                }
            });

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
        return this;
    }

    public void hideRect() {
        thisPane.getChildren().remove(rect);
    }

    // Notify is not set in this way
    public void setColor(Color color) {
        rect.setFill(color);
        NodeStyleTools.setTooltip(rect, message("ClickColorToPalette") + "\n---------\n"
                + FxColorTools.colorNameDisplay(tableColor, color));
    }

    public Color color() {
        return (Color) rect.getFill();
    }

    public String name() {
        return color().toString();
    }

    public java.awt.Color awtColor() {
        return FxColorTools.toAwtColor(color());
    }

    public String rgb() {
        return FxColorTools.color2rgb(color());
    }

    public String rgba() {
        return FxColorTools.color2rgba(color());
    }

    public String css() {
        return FxColorTools.color2css(color());
    }

    public Color saved() {
        return Color.web(UserConfig.getString(conn, thisName, FxColorTools.color2rgba(defaultColor)));
    }

    public void asSaved() {
        setColor(saved());
    }

    public Connection getConn() {
        return conn;
    }

    public ControlColorSet setConn(Connection conn) {
        this.conn = conn;
        return this;
    }

    public void showColorPalette() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    WindowTools.class.getResource(Fxmls.ColorPalettePopupFxml), AppVariables.CurrentBundle);
            Pane pane = fxmlLoader.load();
            ColorPalettePopupController controller = (ColorPalettePopupController) fxmlLoader.getController();
            controller.load(this, rect);
            controller.setNotify.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean ov, Boolean nv) {
                    if (isSettingValues) {
                        return;
                    }
                    setNotify.set(!setNotify.get());
                }
            });

            popup = makePopup();
            popup.getContent().add(pane);
            LocateTools.locateCenter(rect, popup);
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

}
