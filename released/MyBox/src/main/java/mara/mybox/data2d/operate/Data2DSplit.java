package mara.mybox.data2d.operate;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import mara.mybox.data2d.Data2D_Edit;
import mara.mybox.data2d.DataFileCSV;
import mara.mybox.db.data.ColumnDefinition;
import mara.mybox.db.data.Data2DColumn;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.tools.CsvTools;
import mara.mybox.tools.FileTmpTools;
import mara.mybox.tools.FileTools;
import static mara.mybox.value.Languages.message;
import org.apache.commons.csv.CSVPrinter;

/**
 * @Author Mara
 * @CreateDate 2022-2-25
 * @License Apache License Version 2.0
 */
public class Data2DSplit extends Data2DOperate {

    protected long splitSize, startIndex, currentSize;
    protected String prefix;
    protected List<Data2DColumn> targetColumns;
    protected List<String> names;
    protected File currentFile;
    protected List<DataFileCSV> files;
    protected CSVPrinter csvPrinter;

    public static Data2DSplit create(Data2D_Edit data) {
        Data2DSplit op = new Data2DSplit();
        return op.setSourceData(data) ? op : null;
    }

    @Override
    public boolean checkParameters() {
        try {
            if (!super.checkParameters()
                    || cols == null || cols.isEmpty() || splitSize <= 0) {
                return false;
            }
            startIndex = 1;
            currentSize = 0;
            prefix = sourceData.getName();
            names = new ArrayList<>();
            targetColumns = new ArrayList<>();
            for (int c : cols) {
                Data2DColumn column = sourceData.column(c);
                names.add(column.getColumnName());
                targetColumns.add(column.copy());
            }
            if (includeRowNumber) {
                targetColumns.add(0, new Data2DColumn(message("SourceRowNumber"), ColumnDefinition.ColumnType.Long));
            }
            files = new ArrayList<>();
            return true;
        } catch (Exception e) {
            if (task != null) {
                task.setError(e.toString());
            } else {
                MyBoxLog.error(e);
            }
            return false;
        }
    }

    @Override
    public boolean handleRow() {
        try {
            if (sourceRow == null) {
                return false;
            }
            List<String> row = new ArrayList<>();
            for (int col : cols) {
                if (col >= 0 && col < sourceRow.size()) {
                    row.add(sourceRow.get(col));
                } else {
                    row.add(null);
                }
            }
            if (row.isEmpty()) {
                return false;
            }
            if (currentFile == null) {
                currentFile = FileTmpTools.getTempFile(".csv");
                csvPrinter = CsvTools.csvPrinter(currentFile);
                csvPrinter.printRecord(names);
                startIndex = sourceRowIndex;
            }
            if (includeRowNumber) {
                row.add(0, sourceRowIndex + "");
            }
            csvPrinter.printRecord(row);
            currentSize++;
            if (currentSize % splitSize == 0) {
                closeFile();
            }
            return true;
        } catch (Exception e) {
            if (task != null) {
                task.setError(e.toString());
            } else {
                MyBoxLog.error(e);
            }
            return false;
        }
    }

    public void closeFile() {
        try {
            if (csvPrinter == null) {
                return;
            }
            csvPrinter.flush();
            csvPrinter.close();
            csvPrinter = null;
            File file = sourceData.tmpFile(prefix + "_" + startIndex + "-" + sourceRowIndex, null, "csv");
            if (FileTools.override(currentFile, file) && file.exists()) {
                DataFileCSV dataFileCSV = new DataFileCSV();
                dataFileCSV.setTask(task);
                dataFileCSV.setColumns(targetColumns)
                        .setFile(file)
                        .setCharset(Charset.forName("UTF-8"))
                        .setDelimiter(",")
                        .setHasHeader(true)
                        .setColsNumber(targetColumns.size())
                        .setRowsNumber(currentSize);
                dataFileCSV.saveAttributes();
                dataFileCSV.stopTask();
                files.add(dataFileCSV);
            }
            currentFile = null;
            currentSize = 0;
        } catch (Exception e) {
            if (task != null) {
                task.setError(e.toString());
            } else {
                MyBoxLog.error(e);
            }
        }
    }

    @Override
    public boolean end() {
        closeFile();
        return super.end();
    }

    /*
        set
     */
    public Data2DSplit setSplitSize(int splitSize) {
        this.splitSize = splitSize;
        return this;
    }

    /*
        get
     */
    public List<DataFileCSV> getFiles() {
        return files;
    }

}
