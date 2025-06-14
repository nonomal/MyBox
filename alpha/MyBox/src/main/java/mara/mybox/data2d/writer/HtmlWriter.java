package mara.mybox.data2d.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.Charset;
import mara.mybox.controller.WebBrowserController;
import mara.mybox.data.StringTable;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.fxml.style.HtmlStyles;
import mara.mybox.tools.FileDeleteTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.tools.FileTools;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2022-1-29
 * @License Apache License Version 2.0
 */
public class HtmlWriter extends Data2DWriter {

    protected BufferedWriter fileWriter;
    protected String css;

    public HtmlWriter() {
        fileSuffix = "htm";
        css = HtmlStyles.TableStyle;
    }

    @Override
    public boolean openWriter() {
        try {
            if (!super.openWriter()) {
                return false;
            }
            if (printFile == null) {
                showInfo(message("InvalidParameter") + ": " + message("TargetFile"));
                return false;
            }
            showInfo(message("Writing") + " " + printFile.getAbsolutePath());
            tmpFile = FileTmpTools.getTempFile(".htm");
            fileWriter = new BufferedWriter(new FileWriter(tmpFile, Charset.forName("utf-8")));
            StringBuilder s = new StringBuilder();
            s.append("<HTML>\n").
                    append(indent).append("<HEAD>\n").
                    append(indent).append(indent).
                    append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
            if (css != null && !css.isBlank()) {
                s.append(indent).append(indent).append("<style type=\"text/css\">\n");
                s.append(indent).append(indent).append(indent).append(css).append("\n");
                s.append(indent).append(indent).append("</style>\n");
            }
            s.append(indent).append("</HEAD>\n").append(indent).append("<BODY>\n");
            s.append(StringTable.tablePrefix(new StringTable(headerNames)));
            fileWriter.write(s.toString());
            status = Status.Openned;
            return true;
        } catch (Exception e) {
            showError(e.toString());
            return false;
        }
    }

    @Override
    public void printTargetRow() {
        try {
            if (printRow == null) {
                return;
            }
            fileWriter.write(StringTable.tableRow(printRow));
        } catch (Exception e) {
            showError(e.toString());
        }
    }

    @Override
    public void finishWork() {
        try {
            if (fileWriter == null || printFile == null) {
                showInfo(message("Failed") + ": " + printFile);
                status = Status.Failed;
                return;
            }
            if (isFailed() || tmpFile == null || !tmpFile.exists()) {
                fileWriter.close();
                fileWriter = null;
                FileDeleteTools.delete(tmpFile);
                showInfo(message("Failed") + ": " + printFile);
                status = Status.Failed;
                return;
            }
            if (targetRowIndex == 0) {
                fileWriter.close();
                fileWriter = null;
                FileDeleteTools.delete(tmpFile);
                showInfo(message("NoData") + ": " + printFile);
                status = Status.NoData;
                return;
            }
            fileWriter.write(StringTable.tableSuffix(new StringTable(headerNames)));
            fileWriter.write(indent + "<BODY>\n</HTML>\n");
            fileWriter.flush();
            fileWriter.close();
            fileWriter = null;
            if (!FileTools.override(tmpFile, printFile)) {
                FileDeleteTools.delete(tmpFile);
                showInfo(message("Failed") + ": " + printFile);
                status = Status.Failed;
                return;
            }
            if (printFile == null || !printFile.exists()) {
                showInfo(message("Failed") + ": " + printFile);
                status = Status.Failed;
                return;
            }
            recordFileGenerated(printFile, VisitHistory.FileType.Html);
            status = Status.Created;
        } catch (Exception e) {
            showError(e.toString());
        }
    }

    @Override
    public boolean showResult() {
        if (printFile == null) {
            return false;
        }
        WebBrowserController.openFile(printFile);
        return true;
    }

    /*
        get/set
     */
    public String getCss() {
        return css;
    }

    public HtmlWriter setCss(String css) {
        this.css = css;
        return this;
    }

}
