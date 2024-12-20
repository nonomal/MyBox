package mara.mybox.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.data.InfoNode;
import static mara.mybox.db.data.InfoNode.TitleSeparater;
import mara.mybox.db.data.InfoNodeTag;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fximage.FxColorTools;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.PopTools;
import mara.mybox.fxml.style.HtmlStyles;
import mara.mybox.fxml.style.StyleTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.tools.HtmlWriteTools;
import mara.mybox.tools.StringTools;
import mara.mybox.value.AppVariables;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2021-4-23
 * @License Apache License Version 2.0
 */
public class ControlInfoTreeListManage extends ControlInfoTreeList {

    protected InfoTreeManageController manager;
    protected boolean nodeExecutable;

    public void setManager(InfoTreeManageController parent) {
        manager = parent;
        nodeExecutable = manager != null
                && (manager.startButton != null || manager.goButton != null
                || (manager.editor != null
                && (manager.editor.startButton != null
                || manager.editor.goButton != null)));
    }

    @Override
    public void loadTree() {
        try (Connection conn = DerbyBase.getConnection()) {
            if (tableTreeNode.categoryEmpty(conn, category)) {
                File file = InfoNode.exampleFile(category);
                if (file != null) {
                    if (AppVariables.isTesting
                            || PopTools.askSure(getTitle(), message("ImportExamples") + ": " + message(category))) {
                        importExamples();
                        return;
                    }
                }
            }
            loadTree(null);
        } catch (Exception e) {
            MyBoxLog.debug(e);
        }
    }

    public String chainName(Connection conn, InfoNode node) {
        if (node == null) {
            return null;
        }
        String chainName = "";
        List<InfoNode> ancestor = ancestor(conn, node);
        if (ancestor != null) {
            for (InfoNode a : ancestor) {
                chainName += a.getTitle() + TitleSeparater;
            }
        }
        chainName += node.getTitle();
        return chainName;
    }

    @Override
    public void itemClicked(MouseEvent event, TreeItem<InfoNode> item) {
        clicked(UserConfig.getString(baseName + "WhenLeftClickNode", "Edit"), item);
    }

    @Override
    public void doubleClicked(MouseEvent event, TreeItem<InfoNode> item) {
        clicked(UserConfig.getString(baseName + "WhenDoubleClickNode", "PopNode"), item);
    }

    @Override
    public void rightClicked(MouseEvent event, TreeItem<InfoNode> item) {
        clicked(UserConfig.getString(baseName + "WhenRightClickNode", "PopMenu"), item);
    }

    public void clicked(String clickAction, TreeItem<InfoNode> item) {
        if (item == null || clickAction == null) {
            return;
        }
        switch (clickAction) {
            case "PopMenu":
                showPopMenu(item);
                break;
            case "Edit":
                editNode(item);
                break;
            case "Paste":
                pasteNode(item);
                break;
            case "PopNode":
                popNode(item);
                break;
            case "Execute":
                executeNode(item);
                break;
            case "LoadChildren":
                listChildren(item);
                break;
            case "LoadDescendants":
                listDescentants(item);
                break;
            default:
                break;
        }
    }

    @Override
    public List<MenuItem> operationsMenuItems(TreeItem<InfoNode> treeItem) {
        List<MenuItem> items = new ArrayList<>();

        items.addAll(updateMenuItems(treeItem));

        items.add(new SeparatorMenuItem());

        items.add(leftClickMenu(treeItem));
        items.add(doubleClickMenu(treeItem));
        items.add(rightClickMenu(treeItem));

        return items;
    }

    public List<MenuItem> updateMenuItems(TreeItem<InfoNode> treeItem) {
        boolean isRoot = treeItem == null || isRoot(treeItem.getValue());

        List<MenuItem> items = new ArrayList<>();

        MenuItem menu = new MenuItem(message("AddChildNode"), StyleTools.getIconImageView("iconAdd.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            addChild(treeItem);
        });
        items.add(menu);

        menu = new MenuItem(message("DeleteNodeAndDescendants"), StyleTools.getIconImageView("iconDelete.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            deleteNode(treeItem);
        });
        items.add(menu);

        menu = new MenuItem(message("ChangeNodeTitle"), StyleTools.getIconImageView("iconInput.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            renameNode(treeItem);
        });
        menu.setDisable(isRoot);
        items.add(menu);

        menu = new MenuItem(message("CopyNodes"), StyleTools.getIconImageView("iconCopy.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            copyNode(treeItem);
        });
        menu.setDisable(isRoot);
        items.add(menu);

        menu = new MenuItem(message("MoveNodes"), StyleTools.getIconImageView("iconMove.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            moveNode(treeItem);
        });
        menu.setDisable(isRoot);
        menu.setDisable(treeItem == null);
        items.add(menu);

        if (nodeExecutable) {
            menu = new MenuItem(message("Execute"), StyleTools.getIconImageView("iconGo.png"));
            menu.setOnAction((ActionEvent menuItemEvent) -> {
                executeNode(treeItem);
            });
            menu.setDisable(treeItem == null);
            items.add(menu);
        }

        menu = new MenuItem(message("EditNode"), StyleTools.getIconImageView("iconEdit.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            editNode(treeItem);
        });
        menu.setDisable(treeItem == null);
        items.add(menu);

        menu = new MenuItem(message("PasteNodeValueToCurrentEdit"), StyleTools.getIconImageView("iconPaste.png"));
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pasteNode(treeItem);
            }
        });
        items.add(menu);

        return items;
    }

    @FXML
    public void popDataMenu(Event event) {
        if (UserConfig.getBoolean(baseName + "TreeDataPopWhenMouseHovering", true)) {
            showDataMenu(event);
        }
    }

    @FXML
    public void showDataMenu(Event event) {
        TreeItem<InfoNode> item = selectedItem();
        if (item == null) {
            return;
        }
        List<MenuItem> items = new ArrayList<>();

        MenuItem menu = new MenuItem(StringTools.menuPrefix(label(item)));
        menu.setStyle("-fx-text-fill: #2e598a;");
        items.add(menu);
        items.add(new SeparatorMenuItem());

        items.addAll(dataMenuItems(item));

        items.add(new SeparatorMenuItem());

        CheckMenuItem popItem = new CheckMenuItem(message("PopMenuWhenMouseHovering"), StyleTools.getIconImageView("iconPop.png"));
        popItem.setSelected(UserConfig.getBoolean(baseName + "TreeDataPopWhenMouseHovering", true));
        popItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setBoolean(baseName + "TreeDataPopWhenMouseHovering", popItem.isSelected());
            }
        });
        items.add(popItem);

        if (event == null) {
            popNodeMenu(treeView, items);
        } else {
            popEventMenu(event, items);
        }
    }

    public List<MenuItem> dataMenuItems(TreeItem<InfoNode> item) {
        if (item == null) {
            return null;
        }
        List<MenuItem> items = new ArrayList<>();

        MenuItem menu = new MenuItem(message("TreeView"), StyleTools.getIconImageView("iconHtml.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            infoTree();
        });
        items.add(menu);

        menu = new MenuItem(message("Examples"), StyleTools.getIconImageView("iconExamples.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            importExamples();
        });
        items.add(menu);

        menu = new MenuItem(message("Export"), StyleTools.getIconImageView("iconExport.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            exportNode(item);
        });
        items.add(menu);

        menu = new MenuItem(message("Import"), StyleTools.getIconImageView("iconImport.png"));
        menu.setOnAction((ActionEvent menuItemEvent) -> {
            importAction();
        });
        items.add(menu);

        return items;
    }

    public Menu leftClickMenu(TreeItem<InfoNode> treeItem) {
        Menu clickMenu = new Menu(message("WhenLeftClickNode"), StyleTools.getIconImageView("iconSelect.png"));
        clickMenu(treeItem, clickMenu, "WhenLeftClickNode", "Edit");
        return clickMenu;
    }

    public Menu doubleClickMenu(TreeItem<InfoNode> treeItem) {
        Menu clickMenu = new Menu(message("WhenDoubleClickNode"), StyleTools.getIconImageView("iconSelectAll.png"));
        clickMenu(treeItem, clickMenu, "WhenDoubleClickNode", "PopNode");
        return clickMenu;
    }

    public Menu rightClickMenu(TreeItem<InfoNode> treeItem) {
        Menu clickMenu = new Menu(message("WhenRightClickNode"), StyleTools.getIconImageView("iconSelectNone.png"));
        clickMenu(treeItem, clickMenu, "WhenRightClickNode", "PopMenu");
        return clickMenu;
    }

    public Menu clickMenu(TreeItem<InfoNode> treeItem, Menu menu, String key, String defaultAction) {
        ToggleGroup clickGroup = new ToggleGroup();
        String currentClick = UserConfig.getString(baseName + key, defaultAction);

        RadioMenuItem editNodeMenu = new RadioMenuItem(message("EditNode"), StyleTools.getIconImageView("iconEdit.png"));
        editNodeMenu.setSelected("Edit".equals(currentClick));
        editNodeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setString(baseName + key, "Edit");
            }
        });
        editNodeMenu.setToggleGroup(clickGroup);

        RadioMenuItem popNodeMenu = new RadioMenuItem(message("PopNode"), StyleTools.getIconImageView("iconPop.png"));
        popNodeMenu.setSelected("PopNode".equals(currentClick));
        popNodeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setString(baseName + key, "PopNode");
            }
        });
        popNodeMenu.setToggleGroup(clickGroup);

        RadioMenuItem pasteNodeMenu = new RadioMenuItem(message("PasteNodeValueToCurrentEdit"), StyleTools.getIconImageView("iconPaste.png"));
        pasteNodeMenu.setSelected("Paste".equals(currentClick));
        pasteNodeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setString(baseName + key, "Paste");
            }
        });
        pasteNodeMenu.setToggleGroup(clickGroup);

        menu.getItems().addAll(editNodeMenu, pasteNodeMenu, popNodeMenu);

        if (nodeExecutable) {
            RadioMenuItem executeNodeMenu = new RadioMenuItem(message("Execute"), StyleTools.getIconImageView("iconGo.png"));
            executeNodeMenu.setSelected("Execute".equals(currentClick));
            executeNodeMenu.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    UserConfig.setString(baseName + key, "Execute");
                }
            });
            executeNodeMenu.setToggleGroup(clickGroup);
            menu.getItems().add(executeNodeMenu);
        }

        RadioMenuItem loadChildrenMenu = new RadioMenuItem(message("LoadChildren"), StyleTools.getIconImageView("iconList.png"));
        loadChildrenMenu.setSelected("LoadChildren".equals(currentClick));
        loadChildrenMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setString(baseName + key, "LoadChildren");
            }
        });
        loadChildrenMenu.setToggleGroup(clickGroup);

        RadioMenuItem loadDescendantsMenu = new RadioMenuItem(message("LoadDescendants"), StyleTools.getIconImageView("iconList.png"));
        loadDescendantsMenu.setSelected("LoadDescendants".equals(currentClick));
        loadDescendantsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setString(baseName + key, "LoadDescendants");
            }
        });
        loadDescendantsMenu.setToggleGroup(clickGroup);

        RadioMenuItem nothingMenu = new RadioMenuItem(message("DoNothing"));
        nothingMenu.setSelected(currentClick == null || "DoNothing".equals(currentClick));
        nothingMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setString(baseName + key, "DoNothing");
            }
        });
        nothingMenu.setToggleGroup(clickGroup);

        RadioMenuItem clickPopMenu = new RadioMenuItem(message("ContextMenu"), StyleTools.getIconImageView("iconMenu.png"));
        clickPopMenu.setSelected("PopMenu".equals(currentClick));
        clickPopMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserConfig.setString(baseName + key, "PopMenu");
            }
        });
        clickPopMenu.setToggleGroup(clickGroup);

        menu.getItems().addAll(loadChildrenMenu, loadDescendantsMenu, clickPopMenu, nothingMenu);

        return menu;
    }

    @FXML
    public void showPopMenu(TreeItem<InfoNode> item) {
        if (item == null) {
            return;
        }
        List<MenuItem> items = new ArrayList<>();

        MenuItem menu = new MenuItem(StringTools.menuPrefix(label(item)));
        menu.setStyle("-fx-text-fill: #2e598a;");
        items.add(menu);
        items.add(new SeparatorMenuItem());

        Menu dataMenu = new Menu(message("Data"), StyleTools.getIconImageView("iconData.png"));
        dataMenu.getItems().addAll(dataMenuItems(item));
        items.add(dataMenu);

        Menu treeMenu = new Menu(message("View"), StyleTools.getIconImageView("iconView.png"));
        treeMenu.getItems().addAll(viewMenuItems(item));
        items.add(treeMenu);

        items.add(new SeparatorMenuItem());

        items.addAll(operationsMenuItems(item));

        popNodeMenu(treeView, items);
    }

    @Override
    public void nodeAdded(InfoNode parent, InfoNode newNode) {
        manager.nodeAdded(parent, newNode);
    }

    @Override
    protected void viewNode(TreeItem<InfoNode> item) {
        popNode(item);
    }

    protected void deleteNode(TreeItem<InfoNode> targetItem) {
        if (targetItem == null) {
            popError(message("SelectToHandle"));
            return;
        }
        InfoNode node = targetItem.getValue();
        if (node == null) {
            popError(message("SelectToHandle"));
            return;
        }
        boolean isRoot = isRoot(node);
        if (isRoot) {
            if (!PopTools.askSure(getTitle(), message("Delete"), message("SureDeleteAll"))) {
                return;
            }
        } else {
            String chainName = chainName(targetItem);
            if (!PopTools.askSure(getTitle(), chainName, message("Delete"))) {
                return;
            }
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {

            private TreeItem<InfoNode> rootItem;

            @Override
            protected boolean handle() {
                try (Connection conn = DerbyBase.getConnection()) {
                    if (isRoot) {
                        tableTreeNode.deleteChildren(conn, node.getNodeid());
                        InfoNode rootNode = root(conn);
                        rootItem = new TreeItem(rootNode);
                    } else {
                        tableTreeNode.deleteData(conn, node);
                    }
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
                return true;
            }

            @Override
            protected void whenSucceeded() {
                if (isRoot) {
                    setRoot(rootItem);
                } else {
                    targetItem.getChildren().clear();
                    if (targetItem.getParent() != null) {
                        targetItem.getParent().getChildren().remove(targetItem);
                    }
                }
                popSuccessful();
                manager.nodeDeleted(node);
            }

        };
        start(task, treeView);
    }

    protected void renameNode(TreeItem<InfoNode> item) {
        if (item == null) {
            popError(message("SelectToHandle"));
            return;
        }
        InfoNode nodeValue = item.getValue();
        if (nodeValue == null || isRoot(nodeValue)) {
            popError(message("SelectToHandle"));
            return;
        }
        String chainName = chainName(item);
        String name = PopTools.askValue(getBaseTitle(), chainName, message("ChangeNodeTitle"), nodeValue.getTitle() + "m");
        if (name == null || name.isBlank()) {
            return;
        }
        if (name.contains(TitleSeparater)) {
            popError(message("NameShouldNotInclude") + " \"" + TitleSeparater + "\"");
            return;
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {
            private InfoNode updatedNode;

            @Override
            protected boolean handle() {
                nodeValue.setTitle(name);
                updatedNode = tableTreeNode.updateData(nodeValue);
                return updatedNode != null;
            }

            @Override
            protected void whenSucceeded() {
                item.setValue(updatedNode);
                treeView.refresh();
                manager.nodeRenamed(updatedNode);
                popSuccessful();
            }
        };
        start(task, treeView);
    }

    protected void copyNode(TreeItem<InfoNode> item) {
        if (item == null || isRoot(item.getValue())) {
            return;
        }
        String chainName = chainName(item);
        InfoTreeNodeCopyController controller
                = (InfoTreeNodeCopyController) childStage(Fxmls.InfoTreeNodeCopyFxml);
        controller.setParameters(manager, item.getValue(), chainName);
    }

    protected void moveNode(TreeItem<InfoNode> item) {
        if (item == null || isRoot(item.getValue())) {
            return;
        }
        String chainName = chainName(item);
        InfoTreeNodeMoveController controller = (InfoTreeNodeMoveController) childStage(Fxmls.InfoTreeNodeMoveFxml);
        controller.setParameters(manager, item.getValue(), chainName);
    }

    protected void nodeMoved(InfoNode parent, InfoNode node) {
        manager.nodeMoved(parent, node);
    }

    protected void editNode(TreeItem<InfoNode> item) {
        if (item == null) {
            return;
        }
        manager.editNode(item.getValue());
    }

    protected void pasteNode(TreeItem<InfoNode> item) {
        if (item == null) {
            return;
        }
        manager.pasteNode(item.getValue());
    }

    protected void executeNode(TreeItem<InfoNode> item) {
        if (item == null) {
            return;
        }
        manager.executeNode(item.getValue());
    }

    protected void exportNode(TreeItem<InfoNode> item) {
//        InfoTreeNodeExportController exportController
//                = (InfoTreeNodeExportController) childStage(Fxmls.InfoTreeNodeExportFxml);
//        exportController.setParamters(infoController, item);
    }

    @FXML
    protected void importAction() {
        InfoTreeNodeImportController controller
                = (InfoTreeNodeImportController) childStage(Fxmls.InfoTreeNodeImportFxml);
        controller.setCaller(manager);
    }

    @FXML
    protected void importExamples() {
        InfoTreeNodeImportController controller
                = (InfoTreeNodeImportController) childStage(Fxmls.InfoTreeNodeImportFxml);
        controller.setCaller(infoController);
        controller.importExamples();
    }

    @FXML
    public void infoTree() {
        infoTree(selectedItem());
    }

    public void infoTree(TreeItem<InfoNode> node) {
        if (node == null) {
            return;
        }
        InfoNode nodeValue = node.getValue();
        if (nodeValue == null) {
            return;
        }
        FxTask infoTask = new FxTask<Void>(this) {
            private File file;

            @Override
            protected boolean handle() {
                file = FileTmpTools.generateFile(message(category), "htm");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, Charset.forName("utf-8"), false))) {
                    writer.write(HtmlWriteTools.htmlPrefix(chainName(node), "utf-8", HtmlStyles.TableStyle));
                    // https://www.jb51.net/article/116957.htm
                    writer.write("<BODY>\n");
                    writer.write(" <script>\n"
                            + "    function nodeClicked(id) {\n"
                            + "      var obj = document.getElementById(id);\n"
                            + "      var objv = obj.style.display;\n"
                            + "      if (objv == 'none') {\n"
                            + "        obj.style.display = 'block';\n"
                            + "      } else {\n"
                            + "        obj.style.display = 'none';\n"
                            + "      }\n"
                            + "    }\n"
                            + "    function showClass(className, show) {\n"
                            + "      var nodes = document.getElementsByClassName(className);  　\n"
                            + "      if ( show) {\n"
                            + "           for (var i = 0 ; i < nodes.length; i++) {\n"
                            + "              nodes[i].style.display = '';\n"
                            + "           }\n"
                            + "       } else {\n"
                            + "           for (var i = 0 ; i < nodes.length; i++) {\n"
                            + "              nodes[i].style.display = 'none';\n"
                            + "           }\n"
                            + "       }\n"
                            + "    }\n"
                            + "  </script>\n\n");
                    writer.write("<DIV>\n<DIV>\n"
                            + "    <INPUT type=\"checkbox\" checked onclick=\"showClass('TreeNode', this.checked);\">"
                            + message("Unfold") + "</INPUT>\n"
                            + "    <INPUT type=\"checkbox\" checked onclick=\"showClass('SerialNumber', this.checked);\">"
                            + message("HierarchyNumber") + "</INPUT>\n"
                            + "    <INPUT type=\"checkbox\" checked onclick=\"showClass('NodeTag', this.checked);\">"
                            + message("Tags") + "</INPUT>\n"
                            + "    <INPUT type=\"checkbox\" checked onclick=\"showClass('nodeValue', this.checked);\">"
                            + message("Values") + "</INPUT>\n"
                            + "</DIV>\n<HR>\n");
                    try (Connection conn = DerbyBase.getConnection()) {
                        treeView(this, writer, conn, nodeValue, 4, "");
                    } catch (Exception e) {
                        error = e.toString();
                        return false;
                    }
//                    writer.write("\n<HR>\n<TreeNode style=\"font-size:0.8em\">* "
//                            + message("HtmlEditableComments") + "</P>\n");
                    writer.write("</BODY>\n</HTML>\n");
                    writer.flush();
                    return true;
                } catch (Exception e) {
                    error = e.toString();
                    return false;
                }
            }

            @Override
            protected void whenSucceeded() {
                WebBrowserController.openFile(file);
            }
        };
        start(infoTask, false);
    }

    protected void treeView(FxTask infoTask, BufferedWriter writer, Connection conn,
            InfoNode node, int indent, String serialNumber) {
        try {
            if (conn == null || node == null) {
                return;
            }
            List<InfoNode> children = tableTreeNode.children(conn, node.getNodeid());
            String indentNode = " ".repeat(indent);
            String spaceNode = "&nbsp;".repeat(indent);
            String nodePageid = "item" + node.getNodeid();
            String nodeName = node.getTitle();
            String displayName = "<SPAN class=\"SerialNumber\">" + serialNumber + "&nbsp;&nbsp;</SPAN>" + nodeName;
            if (children != null && !children.isEmpty()) {
                displayName = "<a href=\"javascript:nodeClicked('" + nodePageid + "')\">" + displayName + "</a>";
            }
            writer.write(indentNode + "<DIV style=\"padding: 2px;\">" + spaceNode
                    + displayName + "\n");
            List<InfoNodeTag> tags = tableTreeNodeTag.nodeTags(conn, node.getNodeid());
            if (tags != null && !tags.isEmpty()) {
                String indentTag = " ".repeat(indent + 8);
                String spaceTag = "&nbsp;".repeat(2);
                writer.write(indentTag + "<SPAN class=\"NodeTag\">\n");
                for (InfoNodeTag nodeTag : tags) {
                    if (infoTask != null && !infoTask.isWorking()) {
                        return;
                    }
                    Color color = nodeTag.getTag().getColor();
                    if (color == null) {
                        color = FxColorTools.randomColor();
                    }
                    writer.write(indentTag + spaceTag
                            + "<SPAN style=\"border-radius:4px; padding: 2px; font-size:0.8em;  background-color: "
                            + FxColorTools.color2rgb(color)
                            + "; color: " + FxColorTools.color2rgb(FxColorTools.foreColor(color))
                            + ";\">" + nodeTag.getTag().getTag() + "</SPAN>\n");
                }
                writer.write(indentTag + "</SPAN>\n");
            }
            writer.write(indentNode + "</DIV>\n");
            String infoDisplay = InfoNode.infoHtml(infoTask, myController, category, node.getInfo(), true, true);
            if (infoDisplay != null && !infoDisplay.isBlank()) {
                writer.write(indentNode + "<DIV class=\"nodeValue\">"
                        + "<DIV style=\"padding: 0 0 0 " + (indent + 4) * 6 + "px;\">"
                        + "<DIV class=\"valueBox\">\n");
                writer.write(indentNode + infoDisplay + "\n");
                writer.write(indentNode + "</DIV></DIV></DIV>\n");
            }
            if (children != null && !children.isEmpty()) {
                writer.write(indentNode + "<DIV class=\"TreeNode\" id='" + nodePageid + "'>\n");
                for (int i = 0; i < children.size(); i++) {
                    if (infoTask != null && !infoTask.isWorking()) {
                        return;
                    }
                    InfoNode child = children.get(i);
                    String ps = serialNumber == null || serialNumber.isBlank() ? "" : serialNumber + ".";
                    treeView(infoTask, writer, conn, child, indent + 4, ps + (i + 1));
                }
                writer.write(indentNode + "</DIV>\n");
            }
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @FXML
    @Override
    public void cancelAction() {

    }

}
