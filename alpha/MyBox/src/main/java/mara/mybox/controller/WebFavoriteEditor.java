package mara.mybox.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import mara.mybox.db.data.InfoNode;
import static mara.mybox.db.data.InfoNode.ValueSeparater;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.FxSingletonTask;
import mara.mybox.imagefile.ImageFileReaders;
import mara.mybox.tools.IconTools;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2022-3-11
 * @License Apache License Version 2.0
 */
public class WebFavoriteEditor extends InfoTreeNodeEditor {

    @FXML
    protected ControlFileSelecter iconController;
    @FXML
    protected ImageView iconView;

    @Override
    public void initValues() {
        try {
            super.initValues();

            moreInput = iconController.fileInput;
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    public void initControls() {
        try {
            super.initControls();

            iconController.isDirectory(false).isSource(true)
                    .mustExist(true).permitNull(true)
                    .type(VisitHistory.FileType.Image)
                    .parent(this, baseName + "Icon");
            iconController.notify.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue ov, Boolean oldTab, Boolean newTab) {
                    updateIcon(iconController.text());
                }
            });

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    protected void editValue(InfoNode node) {
        Map<String, String> values = InfoNode.parseInfo(node);
        if (values != null) {
            valueInput.setText(values.get("Address"));
            moreInput.setText(values.get("Icon"));
            updateIcon(values.get("Icon"));
        } else {
            valueInput.setText("");
            moreInput.setText("");
            updateIcon(null);
        }
    }

    @Override
    protected InfoNode pickValue(InfoNode node) {
        if (node == null) {
            return null;
        }
        String address = valueInput.getText();
        String icon = moreInput.getText();
        String info;
        if (icon == null || icon.isBlank()) {
            info = address == null || address.isBlank() ? null : address.trim();
        } else {
            info = (address == null ? "" : address.trim()) + ValueSeparater + "\n"
                    + icon.trim();
        }
        return node.setInfo(info);
    }

    protected void updateIcon(String icon) {
        try {
            iconView.setImage(null);
            if (icon != null) {
                File file = new File(icon);
                if (file.exists()) {
                    BufferedImage image = ImageFileReaders.readImage(null, file);
                    if (image != null) {
                        iconView.setImage(SwingFXUtils.toFXImage(image, null));
                    }
                }
            }
        } catch (Exception e) {
            MyBoxLog.console(e);
        }
    }

    @FXML
    protected void downloadIcon() {
        String address;
        try {
            URI uri = new URI(valueInput.getText());
            address = uri.toString();
        } catch (Exception e) {
            popError(message("InvalidData"));
            return;
        }
        if (task != null) {
            task.cancel();
        }
        task = new FxSingletonTask<Void>(this) {
            private File iconFile;

            @Override
            protected boolean handle() {
                iconFile = IconTools.readIcon(this, address, true);
                return iconFile != null && iconFile.exists();
            }

            @Override
            protected void whenSucceeded() {
                iconController.input(iconFile.getAbsolutePath());
            }
        };
        start(task);
    }

    @FXML
    @Override
    public void goAction() {
        String address = valueInput.getText();
        if (address == null || address.isBlank()) {
            return;
        }
        WebBrowserController.openAddress(address, true);
    }

}
