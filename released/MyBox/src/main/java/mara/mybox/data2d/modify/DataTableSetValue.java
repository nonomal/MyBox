package mara.mybox.data2d.modify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import mara.mybox.data.SetValue;
import mara.mybox.data2d.DataTable;
import mara.mybox.data2d.tools.Data2DRowTools;
import mara.mybox.db.Database;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.data.Data2DRow;
import static mara.mybox.value.Languages.message;

/**
 * @Author Mara
 * @CreateDate 2022-1-29
 * @License Apache License Version 2.0
 */
public class DataTableSetValue extends DataTableModify {

    public DataTableSetValue(DataTable data, SetValue value) {
        if (!setSourceTable(data)) {
            return;
        }
        initSetValue(value);
        sourceTable = data;
    }

    @Override
    public boolean go() {
        handledCount = 0;
        String sql = "SELECT * FROM " + tableName;
        showInfo(sql);
        try (Connection dconn = DerbyBase.getConnection();
                PreparedStatement query = dconn.prepareStatement(sql);
                ResultSet results = query.executeQuery();
                PreparedStatement dUpdate = dconn.prepareStatement(tableData2D.updateStatement())) {
            conn = dconn;
            conn.setAutoCommit(false);
            update = dUpdate;
            while (results.next() && !stopped && !reachMaxFiltered) {
                sourceTableRow = tableData2D.readData(results);
                sourceRow = Data2DRowTools.toStrings(sourceTableRow, columns);
                sourceRowIndex++;
                setValue(sourceRow, sourceRowIndex);
            }
            if (!stopped) {
                update.executeBatch();
                conn.commit();
                updateTable();
            }
            showInfo(message("Updated") + ": " + handledCount);
            conn.close();
            conn = null;
            return true;
        } catch (Exception e) {
            failStop(e.toString());
            return false;
        }
    }

    @Override
    public void writeRow() {
        try {
            if (stopped || !rowChanged
                    || targetRow == null || targetRow.isEmpty()) {
                return;
            }
            Data2DRow data2DRow = tableData2D.newRow();
            for (int i = 0; i < columnsNumber; ++i) {
                column = columns.get(i);
                columnName = column.getColumnName();
                data2DRow.setValue(columnName, column.fromString(targetRow.get(i), invalidAs));
            }
            if (tableData2D.setUpdateStatement(conn, update, data2DRow)) {
                update.addBatch();
                if (handledCount % Database.BatchSize == 0) {
                    update.executeBatch();
                    conn.commit();
                    showInfo(message("Updated") + ": " + handledCount);
                }
            }
        } catch (Exception e) {
            showError(e.toString());
        }
    }

}
