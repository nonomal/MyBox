package mara.mybox.fxml.style;

import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2021-7-30
 * @License Apache License Version 2.0
 */
public class StyleImageView {

    public static StyleData imageViewStyle(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        if (id.startsWith("rightTips")) {
            return new StyleData(id, "", "", "iconTipsRight.png");
        }
        if (id.startsWith("leftPane")) {
            return new StyleData(id, message("ControlLeftPane") + "\n" + message("ControlLeftPaneShortcut"), "", "iconDoubleLeft.png");
        }
        if (id.startsWith("rightPane")) {
            return new StyleData(id, message("ControlRightPane") + "\n" + message("ControlRightPaneShortcut"), "", "iconDoubleRight.png");
        }
        if (id.startsWith("links")) {
            return new StyleData(id, "", message("Links"), "iconLink.png");
        }
        if (id.startsWith("tableTipsView")) {
            return new StyleData(id, "", message("TableTips"), "iconTipsRight.png");
        }
        if (id.toLowerCase().endsWith("tipsview")) {
            switch (id) {
                case "pdfPageSizeTipsView":
                    return new StyleData(id, "", message("PdfPageSizeComments"), "", "iconTips.png");
                case "preAlphaTipsView":
                    return new StyleData(id, "", message("PremultipliedAlphaTips"), "", "iconTips.png");
                default:
                    return new StyleData(id, "", "", "iconTips.png");
            }
        }
        return null;
    }

}
