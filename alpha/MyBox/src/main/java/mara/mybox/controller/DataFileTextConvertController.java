package mara.mybox.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import mara.mybox.db.data.VisitHistory;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.tools.TextFileTools;
import mara.mybox.tools.TextTools;
import mara.mybox.value.Languages;
import static mara.mybox.value.Languages.message;
import mara.mybox.value.UserConfig;

/**
 * @Author Mara
 * @CreateDate 2021-9-17
 * @License Apache License Version 2.0
 */
public class DataFileTextConvertController extends BaseDataConvertController {

    protected String sourceDelimiterName;
    protected Charset sourceCharset;
    protected boolean sourceWithName;
    protected boolean skip;

    @FXML
    protected ControlTextOptions readOptionsController;

    public DataFileTextConvertController() {
        baseTitle = Languages.message("TextDataConvert");
    }

    @Override
    public void setFileType() {
        setFileType(VisitHistory.FileType.Text, VisitHistory.FileType.All);
    }

    @Override
    public void initOptionsSection() {
        try {
            super.initOptionsSection();
            readOptionsController.setControls(baseName + "Read", true);
            convertController.setControls(this, pdfOptionsController);

        } catch (Exception e) {
            MyBoxLog.error(e.toString());
        }
    }

    @Override
    public boolean makeMoreParameters() {
        if (readOptionsController.delimiterController.delimiterInput.getStyle().equals(UserConfig.badStyle())
                || (!readOptionsController.autoDetermine && readOptionsController.charset == null)) {
            return false;
        }
        sourceCharset = readOptionsController.charset;
        sourceDelimiterName = readOptionsController.delimiterName;
        sourceWithName = readOptionsController.withNamesCheck.isSelected();
        skip = targetPathController.isSkip();
        return super.makeMoreParameters();
    }

    @Override
    public String handleFile(File srcFile, File targetPath) {
        String result;
        if (readOptionsController.autoDetermine) {
            sourceCharset = TextFileTools.charset(srcFile);
        }
        List<String> names = null;
        try ( BufferedReader reader = new BufferedReader(new FileReader(srcFile, sourceCharset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (task == null || task.isCancelled()) {
                    return message("Cancelled");
                }
                List<String> rowData = TextTools.parseLine(line, sourceDelimiterName);
                if (rowData == null || rowData.isEmpty()) {
                    continue;
                }
                if (names == null) {
                    if (sourceWithName) {
                        names = rowData;
                    } else {
                        names = new ArrayList<>();
                        for (int i = 1; i <= rowData.size(); i++) {
                            names.add(message("col") + i);
                        }
                    }
                    convertController.names = names;
                    convertController.openWriters(filePrefix(srcFile), skip);
                    if (sourceWithName) {
                        continue;
                    }
                }
                convertController.writeRow(rowData);
            }
            convertController.closeWriters();
            result = message("Handled");
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }

}