package bumba;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emcee on 8/26/13.
 */
public class Database {
    List<Schema> schemas = new ArrayList<Schema>();
    public Database(Connection c) throws SQLException, ClassNotFoundException {

        DatabaseMetaData dbmd = c.getMetaData();
        ResultSet schemaData = dbmd.getSchemas();

        while (schemaData.next()) {
            schemas.add(new Schema(schemaData));
        }
        schemaData.close();
    }
    public Database() throws SQLException, ClassNotFoundException {
        EasyConnector ec = new EasyConnector(EasyConnector.ConnectionType.ORDERS);
        Connection c = ec.connection;
        DatabaseMetaData dbmd = c.getMetaData();
        ResultSet schemaData = dbmd.getSchemas();

        while (schemaData.next()) {
            schemas.add(new Schema(schemaData));
        }
        schemaData.close();
        ec.close();
        setKeyReferences();
    }

    public static Database getDatabase() throws SQLException, ClassNotFoundException {
        EasyConnector ec = new EasyConnector(EasyConnector.ConnectionType.ORDERS);
        Connection c = ec.connection;
        return new Database(c);
    }

    public void setKeyReferences() throws SQLException {
        EasyConnector ec = new EasyConnector(EasyConnector.ConnectionType.ORDERS);
        DatabaseMetaData dbmd = ec.connection.getMetaData();
        ResultSet rs = dbmd.getExportedKeys(null, null, null);
        while (rs.next()) {
            String pkTableName = rs.getString("PKTABLE_NAME");
            String fkTableName = rs.getString("FKTABLE_NAME");
            String pkColumnName = rs.getString("PKCOLUMN_NAME");
            String fkColumnName = rs.getString("FKCOLUMN_NAME");
            if (pkTableName != null && fkTableName != null && pkColumnName != null && fkColumnName != null) {
                Table pkTable = null;
                try {
                    pkTable = findByName(pkTableName, false);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                Table fkTable = null;
                try {
                    fkTable = findByName(fkTableName, false);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                System.out.println(String.format("-------\n" +
                        "PK: %s.%s\n" +
                        "FK: %s.%s\n" +
                        "--------\n",
                        pkTableName, pkColumnName,
                        fkTableName, fkColumnName
                ));
                Column pkColumn = pkTable.findByName(pkColumnName);
                Column fkColumn = fkTable.findByName(fkColumnName);
                fkColumn.setReferencedColumn(pkColumn);
                pkColumn.addReferencedBy(fkColumn);
            }
        }
        ec.close();
    }

    public void setUniqueColumns() throws Exception {
        EasyConnector ec = new EasyConnector(EasyConnector.ConnectionType.ORDERS);
        DatabaseMetaData dbmd = ec.connection.getMetaData();
        ResultSet rs = dbmd.getIndexInfo(null, null, null, true, false);
        while (rs.next()) {

        }
        ec.close();
    }

    @Override
    public String toString() {
        return "Database{" +
                "schemas=" + schemas +
                '}';
    }

    public Schema getSchema(String schemaName) throws Exception {
        for (Schema schema : schemas) {
            if (schemaName.equals(schema.getSchema())) {
                return schema;
            }
        }
        throw new Exception("schema not found: " + schemaName);
    }

    public Table findByName(String tableName, boolean returnOnlyFirst) throws Exception {
        boolean found = false;
        Table t = null;
        Table firstMatch = null;
        for (Schema schema : schemas) {
            try {
                t = schema.getTable(tableName);
            } catch (Exception e) {
                // not found; that's okay
            }
            if (t != null) {
                if (returnOnlyFirst) {
                    return t;
                }
                if (found) {
                    throw new Exception("Multiple tables matching name found");
                } else {
                    found = true;
                    firstMatch = t;
                    t = null;
                }
            }
        }
        return firstMatch;
    }
}
