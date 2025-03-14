package mara.mybox.data2d.modify;

import java.sql.PreparedStatement;
import java.sql.Statement;
import mara.mybox.data2d.DataMatrix;
import mara.mybox.db.table.TableMatrixCell;

/**
 * @Author Mara
 * @CreateDate 2022-8-16
 * @License Apache License Version 2.0
 */
public abstract class DataMatrixModify extends Data2DModify {

    protected DataMatrix sourceMatrix;
    protected TableMatrixCell tableMatrixCell;
    protected long dataID, colsNumber;
    protected PreparedStatement insertCellStatement, deleteRowStatement;
    protected Statement dbStatement;

    public boolean setSourceMatrix(DataMatrix data) {
        if (!setSourceData(data)) {
            return false;
        }
        sourceMatrix = data;
        tableMatrixCell = sourceMatrix.tableMatrixCell;
        dataID = sourceMatrix.dataID;
        rowsNumber = sourceMatrix.pagination.rowsNumber;
        colsNumber = sourceMatrix.colsNumber;
        return true;
    }

    @Override
    public boolean end() {
        return true;
    }

}
