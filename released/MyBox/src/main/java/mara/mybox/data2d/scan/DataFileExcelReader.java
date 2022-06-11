package mara.mybox.data2d.scan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mara.mybox.data2d.DataFileExcel;
import mara.mybox.dev.MyBoxLog;
import mara.mybox.tools.MicrosoftDocumentTools;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @Author Mara
 * @CreateDate 2022-1-27
 * @License Apache License Version 2.0
 */
public class DataFileExcelReader extends Data2DReader {

    protected DataFileExcel readerExcel;
    protected String readerSheet;
    protected Iterator<Row> iterator;

    public DataFileExcelReader(DataFileExcel data) {
        this.readerExcel = data;
        readerSheet = data.getSheet();
        init(data);
    }

    @Override
    public void scanData() {
        try ( Workbook wb = WorkbookFactory.create(readerFile)) {
            Sheet sourceSheet;
            if (readerSheet != null) {
                sourceSheet = wb.getSheet(readerSheet);
            } else {
                sourceSheet = wb.getSheetAt(0);
                readerSheet = sourceSheet.getSheetName();
            }
            if (readerExcel != null) {
                int sheetsNumber = wb.getNumberOfSheets();
                List<String> sheetNames = new ArrayList<>();
                for (int i = 0; i < sheetsNumber; i++) {
                    sheetNames.add(wb.getSheetName(i));
                }
                readerExcel.setSheetNames(sheetNames);
                readerExcel.setSheet(readerSheet);
            }
            iterator = sourceSheet.iterator();
            handleData();
            wb.close();
        } catch (Exception e) {
            MyBoxLog.error(e);
            if (readerTask != null) {
                readerTask.setError(e.toString());
            }
            failed = true;
        }
    }

    @Override
    public void readColumnNames() {
        if (iterator == null) {
            return;
        }
        while (iterator.hasNext() && !readerStopped()) {
            readRecord();
            if (record == null || record.isEmpty()) {
                continue;
            }
            handleHeader();
            return;
        }
    }

    @Override
    public void readTotal() {
        if (iterator == null) {
            return;
        }
        rowIndex = 0;
        skipHeader();
        while (iterator.hasNext()) {
            if (readerStopped()) {
                rowIndex = 0;
                return;
            }
            readRecord();
            if (record != null && !record.isEmpty()) {
                ++rowIndex;
            }
        }
    }

    public void skipHeader() {
        if (!readerHasHeader || iterator == null) {
            return;
        }
        while (iterator.hasNext() && (iterator.next() == null) && !readerStopped()) {
        }
    }

    @Override
    public void readPage() {
        if (iterator == null) {
            return;
        }
        skipHeader();
        rowIndex = -1;
        while (iterator.hasNext() && !readerStopped()) {
            if (++rowIndex < rowsStart) {
                iterator.next();
                continue;
            }
            if (rowIndex >= rowsEnd) {
                readerStopped = true;
                break;
            }
            readRecord();
            if (record == null || record.isEmpty()) {
                continue;
            }
            handlePageRow();
        }
    }

    @Override
    public void readRecords() {
        if (iterator == null) {
            return;
        }
        skipHeader();
        rowIndex = 0;
        while (iterator.hasNext() && !readerStopped()) {
            readRecord();
            if (record == null || record.isEmpty()) {
                continue;
            }
            handleRecord();
            ++rowIndex;
        }
    }

    public void readRecord() {
        try {
            record = null;
            if (readerStopped() || iterator == null) {
                return;
            }
            Row readerFileRow = iterator.next();
            if (readerFileRow == null) {
                return;
            }
            record = new ArrayList<>();
            for (int cellIndex = readerFileRow.getFirstCellNum(); cellIndex < readerFileRow.getLastCellNum(); cellIndex++) {
                String v = MicrosoftDocumentTools.cellString(readerFileRow.getCell(cellIndex));
                record.add(v);
            }
        } catch (Exception e) {
            MyBoxLog.error(e);
            if (readerTask != null) {
                readerTask.setError(e.toString());
            }
        }
    }

}