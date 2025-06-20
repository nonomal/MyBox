package mara.mybox.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxFileTools;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.fxml.HelpTools;
import mara.mybox.fxml.WindowTools;
import static mara.mybox.fxml.style.NodeStyleTools.attributeTextStyle;
import mara.mybox.fxml.style.StyleTools;
import mara.mybox.tools.FileCopyTools;
import mara.mybox.tools.FileNameTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.tools.SvgTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;
import org.w3c.dom.Node;

/**
 * @Author Mara
 * @CreateDate 2023-2-12
 * @License Apache License Version 2.0
 */
public class SvgEditorController extends XmlEditorController {

    @FXML
    protected ControlSvgTree treeController;
    @FXML
    protected ControlSvgHtml htmlController;
    @FXML
    protected ControlSvgViewOptions optionsController;
    @FXML
    protected VBox htmlBox;

    public SvgEditorController() {
        baseTitle = message("SVGEditor");
        TipsLabelKey = "SVGEditorTips";
        typeName = "SVG";
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.SVG);
    }

    @Override
    public void initValues() {
        try {
            domController = treeController;
            treeController.editorController = this;
            treeController.svgNodeController.editor = this;

            super.initValues();

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            htmlController.setParameters(optionsController);

            treeController.loadedNotify.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldValue, Boolean newValue) {
                    drawSVG();
                }
            });

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public void drawSVG() {
        drawSVG(treeController.selectedNode());
    }

    public void drawSVG(Node focusNode) {
        if (treeController.doc == null) {
            htmlController.drawSVG("");
            return;
        }
        if (backgroundTask != null) {
            backgroundTask.cancel();
        }
        backgroundTask = new FxSingletonTask<Void>(this) {
            String svg;

            @Override
            protected boolean handle() {
                try {
                    optionsController.loadFocus(this, treeController.doc, focusNode);
                    svg = optionsController.toXML(this);
                    return true;
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
            }

            @Override
            protected void whenSucceeded() {
                htmlController.drawSVG(svg);
            }

        };
        start(backgroundTask, false);
    }

    @Override
    public void domChanged(boolean changed) {
        super.domChanged(changed);
        drawSVG();
    }

    @Override
    public String makeBlank() {
        return SvgTools.blankSVG(500, 500);
    }

    @Override
    public void openSavedFile(File file) {
        SvgEditorController.open(file);
    }

    @FXML
    public void goView() {
        if (!optionsController.pickValues()) {
            return;
        }
        drawSVG();
    }

    @FXML
    protected void popExamplesMenu(Event event) {
        if (UserConfig.getBoolean("SVGExamplesPopWhenMouseHovering", true)) {
            showExamplesMenu(event);
        }
    }

    @FXML
    protected void showExamplesMenu(Event event) {
        try {
            Menu w3menu = new Menu("w3");
            List<MenuItem> items = new ArrayList<>();
            items.add(exampleMenu("yinyang.svg"));
            items.add(exampleMenu("accessible.svg"));
            items.add(exampleMenu("AJ_Digital_Camera.svg"));
            items.add(exampleMenu("alphachannel.svg"));
            items.add(exampleMenu("android.svg"));
            items.add(exampleMenu("basura.svg"));
            items.add(exampleMenu("cartman.svg"));
            items.add(exampleMenu("compuserver_msn_Ford_Focus.svg"));
            items.add(exampleMenu("displayWebStats.svg"));
            items.add(exampleMenu("gaussian3.svg"));
            items.add(exampleMenu("jsonatom.svg"));
            items.add(exampleMenu("lineargradient2.svg"));
            items.add(exampleMenu("mouseEvents.svg"));
            items.add(exampleMenu("ny1.svg"));
            items.add(exampleMenu("radialgradient2.svg"));
            items.add(exampleMenu("rg1024_eggs.svg"));
            items.add(exampleMenu("rg1024_green_grapes.svg"));
            items.add(exampleMenu("rg1024_metal_effect.svg"));
            items.add(exampleMenu("rg1024_Ufo_in_metalic_style.svg"));
            items.add(exampleMenu("snake.svg"));
            items.add(exampleMenu("star.svg"));
            items.add(exampleMenu("Steps.svg"));
            items.add(exampleMenu("svg2009.svg"));
            items.add(exampleMenu("tiger.svg"));
            items.add(exampleMenu("USStates.svg"));

            items.add(new SeparatorMenuItem());

            MenuItem menuItem = new MenuItem("http://dev.w3.org/SVG/tools/svgweb/samples/svg-files/");
            menuItem.setStyle(attributeTextStyle());
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    WebBrowserController.openAddress("http://dev.w3.org/SVG/tools/svgweb/samples/svg-files/", true);
                }
            });
            items.add(menuItem);

            w3menu.getItems().addAll(items);

            Menu batikMenu = new Menu("batik");
            items.clear();
            items.add(exampleMenu("anne.svg"));
            items.add(exampleMenu("3D.svg"));
            items.add(exampleMenu("asf-logo.svg"));
            items.add(exampleMenu("barChart.svg"));
            items.add(exampleMenu("batik3D.svg"));
            items.add(exampleMenu("batikYin.svg"));
            items.add(exampleMenu("batikFX.svg"));
            items.add(exampleMenu("logoShadowOffset.svg"));
            items.add(exampleMenu("mapSpain.svg"));
            items.add(exampleMenu("mapWaadt.svg"));
            items.add(exampleMenu("moonPhases.svg"));
            items.add(exampleMenu("sizeOfSun.svg"));
            items.add(exampleMenu("strokeFont.svg"));

            items.add(new SeparatorMenuItem());

            menuItem = new MenuItem("https://github.com/apache/xmlgraphics-batik/tree/main/samples");
            menuItem.setStyle(attributeTextStyle());
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    WebBrowserController.openAddress("https://github.com/apache/xmlgraphics-batik/tree/main/samples", true);
                }
            });
            items.add(menuItem);

            batikMenu.getItems().addAll(items);

            items.clear();
            items.add(w3menu);
            items.add(batikMenu);

            items.add(new SeparatorMenuItem());

            CheckMenuItem pMenu = new CheckMenuItem(message("PopMenuWhenMouseHovering"), StyleTools.getIconImageView("iconPop.png"));
            pMenu.setSelected(UserConfig.getBoolean("SVGExamplesPopWhenMouseHovering", true));
            pMenu.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    UserConfig.setBoolean("SVGExamplesPopWhenMouseHovering", pMenu.isSelected());
                }
            });
            items.add(pMenu);

            popEventMenu(event, items);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    protected MenuItem exampleMenu(String filename) {
        try {
            MenuItem menu = new MenuItem(filename);
            menu.setOnAction((ActionEvent mevent) -> {
                File exampleFile = FxFileTools.getInternalFile("/data/examples/" + filename,
                        "data", filename, false);
                if (exampleFile == null || !exampleFile.exists()) {
                    return;
                }
                File tmpFile = FileTmpTools.generateFile(FileNameTools.prefix(filename), FileNameTools.ext(filename));
                FileCopyTools.copyFile(exampleFile, tmpFile);
                if (tmpFile == null || !tmpFile.exists()) {
                    return;
                }
                sourceFileChanged(tmpFile);
            });
            return menu;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    @Override
    protected List<MenuItem> helpMenus(Event event) {
        return HelpTools.svgHelps(false);
    }

    @Override
    public boolean keyEventsFilter(KeyEvent event) {
        if (htmlBox.isFocused() || htmlBox.isFocusWithin()) {
            if (htmlController.keyEventsFilter(event)) {
                return true;
            }
        }
        if (super.keyEventsFilter(event)) {
            return true;
        }
        return htmlController.keyEventsFilter(event);
    }


    /*
        static
     */
    public static SvgEditorController open(File file) {
        try {
            SvgEditorController controller = (SvgEditorController) WindowTools.openStage(Fxmls.SvgEditorFxml);
            controller.sourceFileChanged(file);
            controller.requestMouse();
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public static SvgEditorController load(String xml) {
        try {
            SvgEditorController controller = (SvgEditorController) WindowTools.openStage(Fxmls.SvgEditorFxml);
            controller.writePanes(xml);
            controller.requestMouse();
            return controller;
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

}
