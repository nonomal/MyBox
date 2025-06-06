package mara.mybox.db.table;

import java.io.File;
import java.sql.Connection;
import mara.mybox.controller.BaseController;
import mara.mybox.db.data.ColumnDefinition;
import mara.mybox.db.data.ColumnDefinition.ColumnType;
import mara.mybox.db.data.DataNode;
import mara.mybox.fxml.FxTask;
import mara.mybox.fxml.image.FxImageTools;
import mara.mybox.value.Fxmls;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2021-4-23
 * @License Apache License Version 2.0
 */
public class TableNodeWebFavorite extends BaseNodeTable {

    public TableNodeWebFavorite() {
        tableName = "Node_Web_Favorite";
        treeName = message("WebFavorite");
        dataName = message("WebPageAddress");
        dataFxml = Fxmls.ControlDataWebFavoriteFxml;
        examplesFileName = "WebFavorite";
        majorColumnName = "address";
        nodeExecutable = true;
        defineColumns();
    }

    public final TableNodeWebFavorite defineColumns() {
        defineNodeColumns();
        addColumn(new ColumnDefinition("address", ColumnType.File)
                .setLength(FilenameMaxLength)
                .setLabel(message("Address")));
        addColumn(new ColumnDefinition("icon", ColumnType.Image)
                .setLength(FilenameMaxLength)
                .setLabel(message("Icon")));
        return this;
    }

    @Override
    public boolean isNodeExecutable(DataNode node) {
        if (node == null) {
            return false;
        }
        String address = node.getStringValue("address");
        return address != null && !address.isBlank();
    }

    @Override
    public String valuesHtml(FxTask task, Connection conn, BaseController controller, DataNode node) {
        try {
            String address = node.getStringValue("address");
            String icon = node.getStringValue("icon");
            if (address == null || address.isBlank()) {
                return null;
            }
            String html = "<A href=\"" + address + "\">";
            if (icon != null && !icon.isBlank()) {
                try {
                    String base64 = FxImageTools.base64(null, new File(icon), "png");
                    if (base64 != null) {
                        html += "<img src=\"data:image/png;base64," + base64 + "\" width=" + 40 + " >";
                    }
                } catch (Exception e) {
                }
            }
            html += address + "</A>\n";
            return html;
        } catch (Exception e) {
            return null;
        }
    }

}
