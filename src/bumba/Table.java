package bumba;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by emcee on 8/20/13.
 */
public class Table {
    protected Schema owningSchema;
    String schema = null;
    protected String tableName;
    protected String tableType;
    Column primaryKey = null;
    List<Column> columnList;
    Connection providedConnection;

    // must be from Connection.getMetaData().getTables(...) with next() called.  See Schema
    public Table(ResultSet resultSet, Schema owningSchema) throws SQLException, ClassNotFoundException {
        this.owningSchema = owningSchema;
        tableName = resultSet.getString("TABLE_NAME");
        tableType = resultSet.getString("TABLE_TYPE");
        providedConnection = owningSchema.getProvidedConnection();
        columnList = new ArrayList<Column>();
        initColumns();
    }

    private void initColumns() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = providedConnection.getMetaData().getColumns(owningSchema.getCatalogTableName(), owningSchema.getSchema(), tableName, "%");
        ResultSet rs = providedConnection.getMetaData().getPrimaryKeys("", owningSchema.getSchema(), tableName);
        String pkName = null;
        if (rs.next()) {
            pkName = rs.getString("COLUMN_NAME");
            //System.out.println("primary key: " + pkName);
        }
        while (resultSet.next()) {
            Column column = new Column(resultSet, this);
            if (column.getColumnName().equals(pkName)) {
                setPrimaryKey(column);
            } else {
                columnList.add(column);
            }
        }
        rs = getProvidedConnection().getMetaData().getIndexInfo(null, getOwningSchema().getSchema(), tableName, true, false);
        while (rs.next()) {
            try {
                Column referencedColum = findByName(rs.getString("COLUMN_NAME"));
                referencedColum.setUnique(true);
            } catch (NoSuchElementException e) {
                System.err.println(String.format("%s.%s not found, but referenced as unique key", tableName, rs.getString("COLUMN_NAME")));
            }
        }
        if (primaryKey == null) {
            Iterator<Column> iter = columnList.iterator();
            while (iter.hasNext()) {
                Column column = iter.next();
                if (column.isUnique()) {
                    primaryKey = column;
                    iter.remove();
                    break;
                }
            }
        }
    }

    public Schema getOwningSchema() {
        return owningSchema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableType() {
        return tableType;
    }

    public List<Column> getColumns(boolean includePrimaryKeyColumn) {
        List<Column> returnList = new ArrayList<Column>();
        if (includePrimaryKeyColumn && primaryKey != null) {
            returnList.add(primaryKey);
        }
        returnList.addAll(columnList);
        return returnList;
    }

    public List<Column> getColumns() {
        return getColumns(true);
    }

    public Set<Column> getForeignKeyReferences() {
        Set<Column> referenceColumns = new HashSet<>();
        for (Column column : getColumns()) {
           referenceColumns.addAll(column.getReferencingColumns());
        }
        return referenceColumns;
    }

    public Connection getProvidedConnection() {
        return providedConnection;
    }

    public Column getPrimaryKey() {
        return primaryKey;
    }

    public String getCamelCaseName(boolean firstLetterUpperCase) {
        StringBuffer camelCaseName = new StringBuffer(tableName);
        int idx = -1;
        while ((idx = camelCaseName.indexOf("_")) != -1) {
            if (idx != 0) {
                camelCaseName.setCharAt(idx + 1, Character.toUpperCase(camelCaseName.charAt(idx + 1)));
                camelCaseName.deleteCharAt(idx);
            }
        }
        if (firstLetterUpperCase) {
            camelCaseName.setCharAt(0, Character.toUpperCase(camelCaseName.charAt(0)));
        }
        return camelCaseName.toString();
    }

    public String getRsIdentifier() {
        return null;
    }

    public String getSchema() {
        if (schema == null) {
            schema = getOwningSchema().getSchema();
        }
        return schema;
    }

    @Override
    public String toString() {
        return "Table{" +
                "owningSchema=" + owningSchema.getSchema() +
                ", tableName='" + tableName + '\'' +
                ", tableType='" + tableType + '\'' +
                ", columnList=" + columnList +
                ", providedConnection=" + providedConnection +
                '}';
    }

    public String getCanonicalName() {
        String schema = getSchema();
        if (schema != null) {
            return String.format("%s.%s", schema, getTableName());
        }
        return getTableName();
    }

    public Column findByName(String columnName) {
        for (Column column : getColumns()) {
            if (column.getColumnName().equals(columnName)) {
                return column;
            }
        }
        throw new NoSuchElementException(columnName + " not found for table " + tableName);
    }

    public void setPrimaryKey(Column primaryKey) {
        this.primaryKey = primaryKey;
        for (Column column : getColumns()) {
            column.setIsPrimaryKey(false);
        }
        primaryKey.setIsPrimaryKey(true);
    }
}
