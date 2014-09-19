package bumba.database.core;

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
    protected Database() {

    }

    public static Database getDatabase(Connection c) throws SQLException, ClassNotFoundException {
        return new Database(c);
    }

    public void setKeyReferences(Connection c) throws SQLException {
        DatabaseMetaData dbmd = c.getMetaData();
        ResultSet rs = dbmd.getExportedKeys(null, null, null);
        while (rs.next()) {
            String pkTableName = rs.getString("PKTABLE_NAME");
            String pkTableSchema = rs.getString("PKTABLE_SCHEM");

            String fkTableName = rs.getString("FKTABLE_NAME");
            String fkTableSchema = rs.getString("FKTABLE_SCHEM");

            String pkColumnName = rs.getString("PKCOLUMN_NAME");
            String fkColumnName = rs.getString("FKCOLUMN_NAME");
            if (pkTableName != null && fkTableName != null && pkColumnName != null && fkColumnName != null) {
                Table pkTable = null;
                List<Table> pkTables = findAllByName(pkTableName);
                List<Table> fkTables = findAllByName(fkTableName);



                try {
                    pkTable = getSchema(pkTableSchema).getTable(pkTableName);
                } catch (Exception e) {
                    System.err.println(pkTableName + ": " + e.getMessage());
                }
                Table fkTable = null;
                try {
                    fkTable = getSchema(fkTableSchema).getTable(fkTableName);
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
    }

    public void setUniqueColumns(Connection c) throws Exception {
        DatabaseMetaData dbmd = c.getMetaData();
        ResultSet rs = dbmd.getIndexInfo(null, null, null, true, false);
        while (rs.next()) {

        }
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

    public List<Table> findAllByName(String tableName) {
        List<Table> tablesFound = new ArrayList<>();
        for (Schema schema : schemas) {
            try {
                tablesFound.add(schema.getTable(tableName));
            } catch (Exception e) {
                // do nothing
            }
        }
        return tablesFound;
    }

    public List<Schema> getSchemas() {
        return schemas;
    }
}
