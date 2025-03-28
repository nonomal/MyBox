package mara.mybox.controller;

import mara.mybox.dev.MyBoxLog;
import mara.mybox.fxml.WebViewTools;
import mara.mybox.fxml.style.HtmlStyles;

/**
 * @Author Mara
 * @CreateDate 2018-7-31
 * @License Apache License Version 2.0
 */
public class ControlHtmlMaker extends BaseHtmlFormat {

    protected ControlDataHtml htmlController;

    public void setParameters(ControlDataHtml controller) {
        try {
            this.htmlController = controller;

            webViewController.linkInNewTab = true;
            webViewController.defaultStyle = HtmlStyles.TableStyle;

        } catch (Exception e) {
            MyBoxLog.error(e);
        }
    }

    @Override
    protected void updateStatus(boolean changed) {
        super.updateStatus(changed);
        if (!isSettingValues && htmlController != null) {
            htmlController.valueChanged(changed);
        }
    }

    @Override
    public String htmlCodes(String html) {
        return html;
//        return HtmlReadTools.body(html, false);
    }

    @Override
    public String htmlInWebview() {
        return WebViewTools.getHtml(webEngine);
//        return HtmlReadTools.body(WebViewTools.getHtml(webEngine), false);
    }

    @Override
    public String htmlByRichEditor() {
        return richEditorController.getContents();
//        return HtmlReadTools.body(richEditorController.getContents(), false);
    }
}
