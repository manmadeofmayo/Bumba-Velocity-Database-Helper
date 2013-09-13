package bumba.database.core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emcee on 8/20/13.
 */
public class Schema {
    protected String schema;
    // a catalog is the table definition table, like information_schema
    protected String catalogTableName;
    Connection providedConnection;
    List<Table> tableList;

    // expects a ResultSet from Connection.getMetaData().getSchemas with next() already called
    public Schema(ResultSet rsMetaData) throws SQLException, ClassNotFoundException {

        tableList = new ArrayList<Table>();
        schema = rsMetaData.getString("TABLE_SCHEM");
        catalogTableName = rsMetaData.getString("TABLE_CATALOG");
        providedConnection = rsMetaData.getStatement().getConnection();
        initTables();
    }

    public void initTables() throws SQLException, ClassNotFoundException {
        ResultSet metaData = providedConnection.getMetaData().getTables(null, schema, null, null);
        while (metaData.next()) {
            tableList.add(new Table(metaData, this));
        }
        metaData.close();
    }

    public String getSchema() {
        return schema;
    }

    public String getCatalogTableName() {
        return catalogTableName;
    }

    public Connection getProvidedConnection() {
        return providedConnection;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    @Override
    public String toString() {
        return "Schema{" +
                "schema='" + schema + '\'' +
                ", catalogTableName='" + catalogTableName + '\'' +
                ", providedConnection=" + providedConnection +
                ", tableList=" + tableList +
                '}';
    }

    public Table getTable(String tableName) throws Exception {
        for (Table table : tableList) {
            if (tableName.equals(table.getTableName())) {
                return table;
            }
        }
        throw new Exception("Table not found with name: " + tableName);
    }
}
