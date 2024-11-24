package mara.mybox.controller;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.data.DataNode;
import mara.mybox.db.data.DataNodeTag;
import mara.mybox.db.data.DataNodeTools;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.db.table.BaseNodeTable;
import mara.mybox.db.table.TableDataNodeTag;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.style.HtmlStyles;
import mara.mybox.tools.FileNameTools;
import mara.mybox.tools.TextTools;
import static mara.mybox.value.AppValues.Indent;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2021-4-30
 * @License Apache License Version 2.0
 */
public class DataTreeExportController extends BaseTaskController {

    protected DataTreeController treeController;
    protected BaseNodeTable nodeTable;
    protected TableDataNodeTag nodeTagsTable;
    protected TreeItem<DataNode> sourceItem;
    protected File xmlFile, jsonFile, htmlFile, framesetFile, framesetNavFile;
    protected FileWriter htmlWriter, xmlWriter, jsonWriter, framesetNavWriter;
    protected String dataName;
    protected int count, level;
    protected Charset charset;
    protected boolean firstRow;

    @FXML
    protected CheckBox idCheck, timeCheck, tagsCheck, orderCheck, parentCheck,
            htmlCheck, xmlCheck, jsonCheck, framesetCheck;
    @FXML
    protected ComboBox<String> charsetSelector;
    @FXML
    protected TextArea styleInput;
    @FXML
    protected Label nodeLabel;

    public void setParamters(DataTreeController controller, TreeItem<DataNode> item) {
        try {
            if (controller == null) {
                close();
                return;
            }
            this.treeController = controller;
            this.nodeTable = controller.nodeTable;
            this.nodeTagsTable = controller.nodeTagsTable;
            this.dataName = controller.dataName;
            sourceItem = item;
            sourceItem = item != null ? item : treeController.treeView.getRoot();

            baseName = baseName + "_" + dataName;
            baseTitle = nodeTable.getTreeName() + " - "
                    + message("Export") + " : " + sourceItem.getValue().getTitle();

            setControls();

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    public void setControls() {
        try {
            setTitle(baseTitle);

            nodeLabel.setText(message("Node") + ": " + treeController.shortDescription(sourceItem));

            idCheck.setSelected(UserConfig.getBoolean(baseName + "ID", false));
            idCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "ID", idCheck.isSelected());
                }
            });

            timeCheck.setSelected(UserConfig.getBoolean(baseName + "Time", false));
            timeCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Time", timeCheck.isSelected());
                }
            });

            tagsCheck.setSelected(UserConfig.getBoolean(baseName + "Tags", true));
            tagsCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Tags", tagsCheck.isSelected());
                }
            });

            orderCheck.setSelected(UserConfig.getBoolean(baseName + "Order", true));
            orderCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Order", orderCheck.isSelected());
                }
            });

            parentCheck.setSelected(UserConfig.getBoolean(baseName + "Parent", false));
            parentCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Parent", parentCheck.isSelected());
                }
            });

            htmlCheck.setSelected(UserConfig.getBoolean(baseName + "Html", false));
            htmlCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Html", htmlCheck.isSelected());
                }
            });

            framesetCheck.setSelected(UserConfig.getBoolean(baseName + "Frameset", false));
            framesetCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Frameset", framesetCheck.isSelected());
                }
            });

            xmlCheck.setSelected(UserConfig.getBoolean(baseName + "Xml", false));
            xmlCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Xml", xmlCheck.isSelected());
                }
            });

            jsonCheck.setSelected(UserConfig.getBoolean(baseName + "Json", false));
            jsonCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> v, Boolean oldV, Boolean newV) {
                    UserConfig.setBoolean(baseName + "Json", jsonCheck.isSelected());
                }
            });

            List<String> setNames = TextTools.getCharsetNames();
            charsetSelector.getItems().addAll(setNames);
            try {
                charset = Charset.forName(UserConfig.getString(baseName + "Charset", Charset.defaultCharset().name()));
            } catch (Exception e) {
                charset = Charset.defaultCharset();
            }
            charsetSelector.setValue(charset.name());
            charsetSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    charset = Charset.forName(charsetSelector.getSelectionModel().getSelectedItem());
                    UserConfig.setString(baseName + "Charset", charset.name());
                }
            });

            styleInput.setText(UserConfig.getString(baseName + "Style", HtmlStyles.styleValue("Default")));

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public boolean checkOptions() {
        if (treeController == null || sourceItem == null || sourceItem.getValue() == null) {
            close();
            return false;
        }
        xmlFile = null;
        htmlFile = null;
        jsonFile = null;
        framesetFile = null;
        htmlWriter = null;
        xmlWriter = null;
        jsonWriter = null;
        framesetNavWriter = null;
        level = count = 0;
        if (!htmlCheck.isSelected() && !framesetCheck.isSelected()
                && !xmlCheck.isSelected() && !jsonCheck.isSelected()) {
            popError(message("NothingSave"));
            return false;
        }

        targetPath = targetPathController.pickFile();
        if (targetPath == null) {
            popError(message("InvalidParameters") + ": " + message("TargetPath"));
            return false;
        }
        return true;
    }

    @FXML
    public void popDefaultStyle(MouseEvent mouseEvent) {
        try {
            List<MenuItem> items = new ArrayList<>();
            MenuItem menu;
            for (HtmlStyles.HtmlStyle style : HtmlStyles.HtmlStyle.values()) {
                menu = new MenuItem(message(style.name()));
                menu.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        styleInput.setText(HtmlStyles.styleValue(style));
                        UserConfig.setString(baseName + "Style", styleInput.getText());
                    }
                });
                items.add(menu);
            }

            items.add(new SeparatorMenuItem());

            popEventMenu(mouseEvent, items);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @FXML
    public void clearStyle() {
        styleInput.clear();
    }

    @Override
    public boolean doTask(FxTask currentTask) {
        if (sourceItem == null || targetPath == null) {
            return false;
        }
        if (!openWriters()) {
            closeWriters();
            return false;
        }
        count = level = 0;
        firstRow = true;
        try (Connection conn = DerbyBase.getConnection()) {
            long nodeid = sourceItem.getValue().getNodeid();
            String chainName = treeController.chainName(sourceItem.getParent());
            showLogs("Export started. Node: " + nodeid + " - " + chainName);
            exportNode(currentTask, conn, nodeid, chainName);
        } catch (Exception e) {
            updateLogs(e.toString());
        }
        return closeWriters();
    }

    protected boolean openWriters() {
        if (sourceItem == null || targetPath == null) {
            return false;
        }
        try {
            String chainName = treeController.chainName(sourceItem);
            String prefix = chainName.replaceAll(DataNode.TitleSeparater, "-");

            if (htmlCheck.isSelected()) {
                htmlFile = makeTargetFile(prefix, ".html", targetPath);
                if (htmlFile != null) {
                    showLogs(message("Writing") + " " + htmlFile.getAbsolutePath());
                    htmlWriter = new FileWriter(htmlFile, charset);
                    writeHtmlHead(htmlWriter, chainName);
                    htmlWriter.write(Indent + "<BODY>\n" + Indent + Indent + "<H2>" + chainName + "</H2>\n");
                } else if (targetPathController.isSkip()) {
                    showLogs(message("Skipped"));
                }
            }
            if (framesetCheck.isSelected()) {
                framesetFile = makeTargetFile(prefix, "-frameset.html", targetPath);
                if (framesetFile != null) {
                    showLogs(message("Writing") + " " + framesetFile.getAbsolutePath());
                    StringBuilder s;
                    String subPath = FileNameTools.filter(prefix) + "-frameset";
                    File path = new File(targetPath + File.separator + subPath + File.separator);
                    path.mkdirs();
                    framesetNavFile = new File(path.getAbsolutePath() + File.separator + "nav.html");
                    File coverFile = new File(path.getAbsolutePath() + File.separator + "cover.html");
                    try (FileWriter coverWriter = new FileWriter(coverFile, charset)) {
                        writeHtmlHead(coverWriter, chainName);
                        coverWriter.write("<BODY>\n<BR><BR><BR><BR><H1>" + message("Notes") + "</H1>\n</BODY></HTML>");
                        coverWriter.flush();
                    }
                    try (FileWriter framesetWriter = new FileWriter(framesetFile, charset)) {
                        writeHtmlHead(framesetWriter, chainName);
                        s = new StringBuilder();
                        s.append("<FRAMESET border=2 cols=240,240,*>\n")
                                .append("<FRAME name=nav src=\"").append(subPath).append("/").append(framesetNavFile.getName()).append("\" />\n")
                                .append("<FRAME name=booknav />\n")
                                .append("<FRAME name=main src=\"").append(subPath).append("/cover.html\" />\n</HTML>\n");
                        framesetWriter.write(s.toString());
                        framesetWriter.flush();
                    }
                    framesetNavWriter = new FileWriter(framesetNavFile, charset);
                    writeHtmlHead(framesetNavWriter, chainName);
                    s = new StringBuilder();
                    s.append(Indent).append("<BODY>\n");
                    s.append(Indent).append(Indent).append("<H2>").append(chainName).append("</H2>\n");
                    framesetNavWriter.write(s.toString());
                } else if (targetPathController.isSkip()) {
                    showLogs(message("Skipped"));
                }
            }
            if (xmlCheck.isSelected()) {
                xmlFile = makeTargetFile(prefix, ".xml", targetPath);
                if (xmlFile != null) {
                    showLogs(message("Writing") + " " + xmlFile.getAbsolutePath());
                    xmlWriter = new FileWriter(xmlFile, charset);
                    StringBuilder s = new StringBuilder();
                    s.append("<?xml version=\"1.0\" encoding=\"")
                            .append(charset.name()).append("\"?>\n")
                            .append("<").append(nodeTable.getTableName()).append(">\n");
                    xmlWriter.write(s.toString());
                } else if (targetPathController.isSkip()) {
                    showLogs(message("Skipped"));
                }
            }
            if (jsonCheck.isSelected()) {
                jsonFile = makeTargetFile(prefix, ".json", targetPath);
                if (jsonFile != null) {
                    showLogs(message("Writing") + " " + jsonFile.getAbsolutePath());
                    jsonWriter = new FileWriter(jsonFile, Charset.forName("UTF-8"));
                    StringBuilder s = new StringBuilder();
                    s.append("{\"").append(nodeTable.getTreeName()).append("\": [\n");
                    jsonWriter.write(s.toString());
                } else if (targetPathController.isSkip()) {
                    showLogs(message("Skipped"));
                }
            }
        } catch (Exception e) {
            updateLogs(e.toString());
            return false;
        }
        return true;
    }

    protected void writeHtmlHead(FileWriter writer, String title) {
        try {
            StringBuilder s = new StringBuilder();
            s.append("<HTML>\n").append(Indent).append("<HEAD>\n")
                    .append(Indent).append(Indent).append("<title>").append(title).append("</title>\n")
                    .append(Indent).append(Indent)
                    .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=")
                    .append(charset.name()).append("\" />\n");
            String style = styleInput.getText();
            if (style != null && !style.isBlank()) {
                s.append(Indent).append(Indent).append("<style type=\"text/css\">\n");
                s.append(Indent).append(Indent).append(Indent).append(style).append("\n");
                s.append(Indent).append(Indent).append("</style>\n");
            }
            s.append(Indent).append("</HEAD>\n");
            writer.write(s.toString());
        } catch (Exception e) {
            updateLogs(e.toString());
        }
    }

    protected boolean closeWriters() {
        if (sourceItem == null || targetPath == null) {
            return false;
        }
        boolean well = true;
        if (htmlWriter != null) {
            try {
                htmlWriter.write(Indent + "</BODY>\n</HTML>\n");
                htmlWriter.flush();
                htmlWriter.close();
                htmlWriter = null;
                targetFileGenerated(htmlFile, VisitHistory.FileType.Html);
            } catch (Exception e) {
                updateLogs(e.toString());
                well = false;
            }
        }
        if (framesetNavWriter != null) {
            try {
                framesetNavWriter.write(Indent + "</BODY>\n</HTML>\n");
                framesetNavWriter.flush();
                framesetNavWriter.close();
                framesetNavWriter = null;
                targetFileGenerated(framesetFile, VisitHistory.FileType.Html);
            } catch (Exception e) {
                updateLogs(e.toString());
                well = false;
            }
        }
        if (xmlWriter != null) {
            try {
                xmlWriter.write("</" + nodeTable.getTableName() + ">\n");
                xmlWriter.flush();
                xmlWriter.close();
                xmlWriter = null;
                targetFileGenerated(xmlFile, VisitHistory.FileType.XML);
            } catch (Exception e) {
                updateLogs(e.toString());
                well = false;
            }
        }
        if (jsonWriter != null) {
            try {
                jsonWriter.write("\n]}\n");
                jsonWriter.flush();
                jsonWriter.close();
                jsonWriter = null;
                targetFileGenerated(jsonFile, VisitHistory.FileType.JSON);
            } catch (Exception e) {
                updateLogs(e.toString());
                well = false;
            }
        }
        return well;
    }

    public void exportNode(FxTask currentTask, Connection conn, long nodeid, String parentChainName) {
        level++;
        if (conn == null || nodeid < 0) {
            return;
        }
        try {
            count++;

            DataNode node = nodeTable.query(conn, nodeid);
            String nodeChainName;
            if (parentChainName != null && !parentChainName.isBlank()) {
                nodeChainName = parentChainName + DataNode.TitleSeparater + node.getTitle();
            } else {
                nodeChainName = node.getTitle();
            }
            if (isLogsVerbose()) {
                showLogs("Handling node: " + node.getNodeid() + " - " + nodeChainName);
            }
            List<DataNodeTag> tags = null;
            if (tagsCheck.isSelected()) {
                tags = nodeTagsTable.nodeTags(conn, node.getNodeid());
            }
            String xmlPrefix = "";
            boolean isRootNode = node.isRoot();
            if (!isRootNode) {
                for (int i = 1; i < level; i++) {
                    xmlPrefix += Indent;
                }
                String pname = parentCheck.isSelected() ? parentChainName : null;
                if (xmlWriter != null) {
                    xmlWriter.write(xmlPrefix + "<TreeNode>\n");
                    writeXML(currentTask, conn, xmlPrefix + Indent, pname, node, tags);
                }
                if (jsonWriter != null) {
                    writeJson(currentTask, conn, pname, node, tags);
                }
                if (htmlWriter != null) {
                    writeHtml(currentTask, conn, pname, node, htmlWriter, tags);
                }
            }

            if (currentTask == null || !currentTask.isWorking()) {
                return;
            }

            if (framesetNavWriter != null) {
                String nodeTitle = node.getTitle() + "_" + node.getNodeid();
                File bookNavFile = new File(framesetNavFile.getParent() + File.separator + FileNameTools.filter(nodeTitle) + "_nav.html");
                FileWriter bookNavWriter = new FileWriter(bookNavFile, charset);
                writeHtmlHead(bookNavWriter, nodeTitle);
                bookNavWriter.write(Indent + "<BODY>\n");

                File nodeFile = new File(framesetNavFile.getParent() + File.separator + FileNameTools.filter(nodeTitle) + ".html");
                FileWriter bookWriter = new FileWriter(nodeFile, charset);
                writeHtmlHead(bookWriter, nodeTitle);
                bookWriter.write(Indent + "<BODY>\n");
                String prefix = "";
                for (int i = 1; i < level; i++) {
                    prefix += "&nbsp;&nbsp;&nbsp;&nbsp;";
                }
                framesetNavWriter.write(prefix + "<A href=\"" + bookNavFile.getName() + "\"  target=booknav>" + node.getTitle() + "</A><BR>\n");

                writeHtml(currentTask, conn, nodeChainName, node, bookWriter, tags);
                try {
                    bookWriter.write(Indent + "\n</BODY>\n</HTML>");
                    bookWriter.flush();
                    bookWriter.close();
                } catch (Exception e) {
                    showLogs(e.toString());
                }

                String sql = "SELECT nodeid, title FROM " + nodeTable.getTableName()
                        + " WHERE parentid=? AND parentid<>nodeid  ORDER BY " + nodeTable.getOrderColumns();
                boolean hasChildren = false;
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setLong(1, nodeid);
                    try (ResultSet results = statement.executeQuery()) {
                        String title;
                        while (results != null && results.next()) {
                            if (currentTask == null || !currentTask.isWorking()) {
                                return;
                            }
                            title = results.getString("title");
                            File childFile = new File(framesetNavFile.getParent() + File.separator
                                    + FileNameTools.filter(title + "_" + results.getLong("nodeid")) + ".html");
                            bookNavWriter.write("<A href=\"" + childFile.getName() + "\"  target=main>" + title + "</A><BR>\n");
                            hasChildren = true;
                        }
                    } catch (Exception e) {
                        showLogs(e.toString());
                    }
                } catch (Exception e) {
                    showLogs(e.toString());
                }

                if (!hasChildren) {
                    bookNavWriter.write("<A href=\"" + nodeFile.getName() + "\"  target=main>" + node.getTitle() + "</A><BR>\n");
                }
                try {
                    bookNavWriter.write(Indent + "\n</BODY>\n</HTML>");
                    bookNavWriter.flush();
                    bookNavWriter.close();
                } catch (Exception e) {
                    showLogs(e.toString());
                }
            }
            if (currentTask == null || !currentTask.isWorking()) {
                return;
            }

            String sql = "SELECT nodeid FROM " + nodeTable.getTableName()
                    + " WHERE parentid=? AND parentid<>nodeid  ORDER BY " + nodeTable.getOrderColumns();
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, nodeid);
                try (ResultSet results = statement.executeQuery()) {
                    while (results != null && results.next()) {
                        if (currentTask == null || !currentTask.isWorking()) {
                            return;
                        }
                        exportNode(currentTask, conn, results.getLong("nodeid"), nodeChainName);
                    }
                } catch (Exception e) {
                    showLogs(e.toString());
                }
            } catch (Exception e) {
                showLogs(e.toString());
            }

            if (!isRootNode && xmlWriter != null) {
                xmlWriter.write(xmlPrefix + "</TreeNode>\n");
            }
        } catch (Exception e) {
            showLogs(e.toString());
        }
        level--;
    }

    protected void writeXML(FxTask currentTask, Connection conn, String prefix,
            String parentName, DataNode node, List<DataNodeTag> tags) {
        try {
            String xml = DataNodeTools.toXML(currentTask, conn,
                    myController, nodeTable, prefix, parentName, node, tags,
                    idCheck.isSelected(), timeCheck.isSelected(), orderCheck.isSelected());
            xmlWriter.write(xml);
        } catch (Exception e) {
            updateLogs(e.toString());
        }
    }

    protected void writeHtml(FxTask currentTask, Connection conn,
            String parentName, DataNode node, FileWriter writer, List<DataNodeTag> tags) {
        try {
            String html = DataNodeTools.toHtml(currentTask, conn,
                    myController, nodeTable, parentName, node, tags,
                    idCheck.isSelected(), timeCheck.isSelected(), orderCheck.isSelected());
            writer.write(html);
        } catch (Exception e) {
            updateLogs(e.toString());
        }
    }

    protected void writeJson(FxTask currentTask, Connection conn,
            String parentName, DataNode node, List<DataNodeTag> tags) {
        try {
            if (!firstRow) {
                jsonWriter.write(",\n");
            } else {
                firstRow = false;
            }
            String json = DataNodeTools.toJson(currentTask, conn,
                    myController, nodeTable, parentName, node, tags,
                    idCheck.isSelected(), timeCheck.isSelected(), orderCheck.isSelected());
            jsonWriter.write(json);
        } catch (Exception e) {
            updateLogs(e.toString());
        }
    }

    @Override
    public void afterSuccess() {
        try {
            if (openCheck.isSelected()) {
                if (framesetFile != null && framesetFile.exists()) {
                    WebBrowserController.openFile(framesetFile);
                }
                if (htmlFile != null && htmlFile.exists()) {
                    WebBrowserController.openFile(htmlFile);
                }
                if (xmlFile != null && xmlFile.exists()) {
                    XmlEditorController.open(xmlFile);
                }
                if (jsonFile != null && jsonFile.exists()) {
                    JsonEditorController.open(jsonFile);
                }
            }
            showLogs(message("Count") + ": " + count);
        } catch (Exception e) {
            updateLogs(e.toString());
        }
    }

    @Override
    public void openTarget() {
        browseURI(targetPath.toURI());
    }

}
