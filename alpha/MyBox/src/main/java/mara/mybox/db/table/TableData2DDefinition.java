package mara.mybox.db.table;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import mara.mybox.db.DerbyBase;
import mara.mybox.db.data.ColumnDefinition;
import mara.mybox.db.data.ColumnDefinition.ColumnType;
import mara.mybox.db.data.Data2DDefinition;
import mara.mybox.db.data.Data2DDefinition.Type;
import mara.mybox.dev.MyBoxLog;

/**
 * @Author Mara
 * @CreateDate 2021-11-2
 * @License Apache License Version 2.0
 */
public class TableData2DDefinition extends BaseTable<Data2DDefinition> {

    public TableData2DDefinition() {
        tableName = "Data2D_Definition";
        defineColumns();
    }

    public TableData2DDefinition(boolean defineColumns) {
        tableName = "Data2D_Definition";
        if (defineColumns) {
            defineColumns();
        }
    }

    public final TableData2DDefinition defineColumns() {
        addColumn(new ColumnDefinition("d2did", ColumnType.Long, true, true).setIsID(true));
        addColumn(new ColumnDefinition("data_type", ColumnType.Short, true));
        addColumn(new ColumnDefinition("data_name", ColumnType.String, true).setLength(4096));
        addColumn(new ColumnDefinition("file", ColumnType.File).setLength(32672));
        addColumn(new ColumnDefinition("charset", ColumnType.String).setLength(32));
        addColumn(new ColumnDefinition("delimiter", ColumnType.String).setLength(128));
        addColumn(new ColumnDefinition("has_header", ColumnType.Boolean));
        addColumn(new ColumnDefinition("columns_number", ColumnType.Long));
        addColumn(new ColumnDefinition("rows_number", ColumnType.Long));
        addColumn(new ColumnDefinition("scale", ColumnType.Short));
        addColumn(new ColumnDefinition("max_random", ColumnType.Integer));
        addColumn(new ColumnDefinition("modify_time", ColumnType.Datetime));
        addColumn(new ColumnDefinition("comments", ColumnType.String).setLength(32672));
        return this;
    }

    public static final String Query_TypeFile
            = "SELECT * FROM Data2D_Definition WHERE data_type=? AND file=?";

    public static final String Query_TypeName
            = "SELECT * FROM Data2D_Definition WHERE data_type=? AND data_name=?";

    public static final String Query_TypeFileName
            = "SELECT * FROM Data2D_Definition WHERE data_type=? AND file=? AND data_name=?";

    public static final String Query_Type
            = "SELECT * FROM Data2D_Definition WHERE data_type=?";

    public static final String DeleteID
            = "DELETE FROM Data2D_Definition WHERE d2did=?";

    public static final String Delete_TypeFile
            = "DELETE FROM Data2D_Definition WHERE data_type=? AND file=?";

    public static final String Delete_TypeName
            = "DELETE FROM Data2D_Definition WHERE data_type=? AND data_name=?";

    public static final String Delete_TypeFileName
            = "DELETE FROM Data2D_Definition WHERE data_type=? AND file=? AND data_name=?";

    /*
        local methods
     */
    public Data2DDefinition queryFile(Type type, File file) {
        if (file == null) {
            return null;
        }
        try ( Connection conn = DerbyBase.getConnection();) {
            return queryFile(conn, type, file);
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public Data2DDefinition queryFile(Connection conn, Type type, File file) {
        if (conn == null || file == null) {
            return null;
        }
        try ( PreparedStatement statement = conn.prepareStatement(Query_TypeFile)) {
            statement.setShort(1, Data2DDefinition.type(type));
            statement.setString(2, DerbyBase.stringValue(file.getAbsolutePath()));
            return query(conn, statement);
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public int deleteFile(Connection conn, Type type, File file) {
        if (conn == null || file == null) {
            return -1;
        }
        try ( PreparedStatement statement = conn.prepareStatement(Delete_TypeFile)) {
            statement.setShort(1, Data2DDefinition.type(type));
            statement.setString(2, DerbyBase.stringValue(file.getAbsolutePath()));
            return statement.executeUpdate();
        } catch (Exception e) {
            MyBoxLog.error(e);
            return -1;
        }
    }

    public Data2DDefinition queryName(Type type, String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try ( Connection conn = DerbyBase.getConnection();) {
            return queryName(conn, type, name);
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public Data2DDefinition queryName(Connection conn, Type type, String name) {
        if (conn == null || name == null || name.isBlank()) {
            return null;
        }
        try ( PreparedStatement statement = conn.prepareStatement(Query_TypeName)) {
            statement.setShort(1, Data2DDefinition.type(type));
            statement.setString(2, DerbyBase.stringValue(name));
            return query(conn, statement);
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public int deleteName(Connection conn, Type type, String name) {
        if (conn == null || name == null || name.isBlank()) {
            return -1;
        }
        try ( PreparedStatement statement = conn.prepareStatement(Delete_TypeName)) {
            statement.setShort(1, Data2DDefinition.type(type));
            statement.setString(2, DerbyBase.stringValue(name));
            return statement.executeUpdate();
        } catch (Exception e) {
            MyBoxLog.error(e);
            return -1;
        }
    }

    public Data2DDefinition queryFileName(Type type, File file, String name) {
        if (file == null || name == null || name.isBlank()) {
            return null;
        }
        try ( Connection conn = DerbyBase.getConnection();) {
            return queryFileName(conn, type, file, name);
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public Data2DDefinition queryFileName(Connection conn, Type type, File file, String name) {
        if (conn == null || file == null) {
            return null;
        }
        if (name == null || name.isBlank()) {
            return queryFile(conn, type, file);
        }
        try ( PreparedStatement statement = conn.prepareStatement(Query_TypeFileName)) {
            statement.setShort(1, Data2DDefinition.type(type));
            statement.setString(2, DerbyBase.stringValue(file.getAbsolutePath()));
            statement.setString(3, DerbyBase.stringValue(name));
            return query(conn, statement);
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public int deleteFileName(Connection conn, Type type, File file, String name) {
        if (conn == null || file == null) {
            return -1;
        }
        if (name == null || name.isBlank()) {
            return deleteFile(conn, type, file);
        }
        try ( PreparedStatement statement = conn.prepareStatement(Delete_TypeFileName)) {
            statement.setShort(1, Data2DDefinition.type(type));
            statement.setString(2, DerbyBase.stringValue(file.getAbsolutePath()));
            statement.setString(3, DerbyBase.stringValue(name));
            return statement.executeUpdate();
        } catch (Exception e) {
            MyBoxLog.error(e);
            return -1;
        }
    }

    public Data2DDefinition queryClipboard(Connection conn, File file) {
        if (conn == null || file == null) {
            return null;
        }
        try ( PreparedStatement statement = conn.prepareStatement(Query_TypeFile)) {
            statement.setShort(1, Data2DDefinition.type(Type.DataClipboard));
            statement.setString(2, DerbyBase.stringValue(file.getAbsolutePath()));
            return query(conn, statement);
        } catch (Exception e) {
            MyBoxLog.error(e);
            return null;
        }
    }

    public int deleteClipboard(Connection conn, File file) {
        if (conn == null || file == null) {
            return -1;
        }
        try ( PreparedStatement statement = conn.prepareStatement(Delete_TypeFile)) {
            statement.setShort(1, Data2DDefinition.type(Type.DataClipboard));
            statement.setString(2, DerbyBase.stringValue(file.getAbsolutePath()));
            return statement.executeUpdate();
        } catch (Exception e) {
            MyBoxLog.error(e);
            return -1;
        }
    }

    public boolean clear(Connection conn, Data2DDefinition data) {
        if (conn == null || data == null) {
            return false;
        }
        boolean ret = true;
        try ( PreparedStatement statement = conn.prepareStatement(TableData2DColumn.ClearData)) {
            statement.setLong(1, data.getD2did());
            statement.executeUpdate();
        } catch (Exception e) {
            MyBoxLog.error(e);
            ret = false;
        }
        try ( PreparedStatement statement = conn.prepareStatement(DeleteID)) {
            statement.setLong(1, data.getD2did());
            statement.executeUpdate();
        } catch (Exception e) {
            MyBoxLog.error(e);
            ret = false;
        }
        return ret;
    }

    public int clearInvalid(Connection conn) {
        int count = 0;
        try {
            conn.setAutoCommit(true);
            String sql = "SELECT * FROM Data2D_Definition WHERE data_type="
                    + Data2DDefinition.type(Type.DataClipboard)
                    + " OR data_type=" + Data2DDefinition.type(Type.DataFileCSV)
                    + " OR data_type=" + Data2DDefinition.type(Type.DataFileExcel)
                    + " OR data_type=" + Data2DDefinition.type(Type.DataFileText);
            List<Data2DDefinition> invalid = new ArrayList<>();
            try ( PreparedStatement statement = conn.prepareStatement(sql);
                     ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Data2DDefinition data = readData(results);
                    try {
                        File file = data.getFile();
                        if (!file.exists()) {
                            invalid.add(data);
                        }
                    } catch (Exception e) {
                        invalid.add(data);
                    }
                }
            }
            count = invalid.size();
            deleteData(conn, invalid);
            conn.setAutoCommit(true);
        } catch (Exception e) {
            MyBoxLog.error(e);
        }
        return count;
    }

}