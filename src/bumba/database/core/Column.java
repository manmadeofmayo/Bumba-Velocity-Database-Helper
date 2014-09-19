package bumba.database.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by emcee on 8/19/13.
 */

// docs: http://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html
public class Column {
    protected Table owningTable = null;
    protected String columnName = null;
    protected String columnSqlType = null;
    protected Integer javaSqlType = null;
    protected Class javaClass = null;
    protected Integer size = null;
    protected Integer precision = null;
    protected Set<Column> referencedByColumns = new HashSet<>();
    protected Column referencedColumn = null;
    // true, false, and null for unknown
    Boolean nullable = null;
    String defaultValue = "null";
    Boolean autoIncrementing = null;
    Boolean generated = null;

    boolean isUnique = false;

    String refSchema = null;
    String refCatalog = null;
    String refTableName = null;
    Integer refJavaType = null;


    protected FieldType fieldType;
    private boolean isPrimaryKey;

    // expects a resultset from Connection.getMetaData().getColumns(...) in which next() is already called.  Does not close
    public Column(ResultSet metaDataRs, Table owningTable) throws SQLException, ClassNotFoundException {
        this.owningTable = owningTable;
        columnName = metaDataRs.getString("COLUMN_NAME");
        columnSqlType = metaDataRs.getString("TYPE_NAME");
        javaSqlType = metaDataRs.getInt("DATA_TYPE");
        javaClass = Class.forName(SQLTypeMap.convert(javaSqlType, columnSqlType));
        size = metaDataRs.getInt("COLUMN_SIZE");
        precision = (Integer)metaDataRs.getObject("DECIMAL_DIGITS");
        nullable = metaDataRs.getString("IS_NULLABLE").equals("YES") ? Boolean.TRUE : Boolean.FALSE;
        defaultValue = metaDataRs.getString("COLUMN_DEF");
        autoIncrementing = metaDataRs.getString("IS_AUTOINCREMENT").equals("YES") ? Boolean.TRUE : Boolean.FALSE;
        //generated = metaDataRs.getString(24).equals("YES") ? Boolean.TRUE : Boolean.FALSE;
        //generated = metaDataRs.getString("IS_GENERATEDCOLUMN").equals("YES") ? Boolean.TRUE : Boolean.FALSE;
        generated = null;
        refSchema = null;
        refCatalog = null;
        refJavaType = null;
/*        for (int i = 1; i < metaDataRs.getMetaData().getColumnCount(); i++) {
            System.out.println(metaDataRs.getMetaData().getColumnName(i));
        }*/
        try {
            refSchema = metaDataRs.getString("SCOPE_SCHEMA");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        try {
            refCatalog = metaDataRs.getString("SCOPE_CATALOG");
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        try {
            refJavaType = Integer.parseInt(metaDataRs.getString("SOURCE_DATA_TYPE"));
        } catch (NumberFormatException e) {
            //e.printStackTrace();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        setDefaultValue();
    }

    private void setDefaultValue() {
        if (defaultValue == null) {
            defaultValue = "null";
        } if(defaultValue.contains("::")) {
            defaultValue = defaultValue.substring(0,defaultValue.indexOf("::"));
        } if (defaultValue.contains("(")) {
            defaultValue = "null";
        } else if (defaultValue.contains("'")) {
            defaultValue = defaultValue.replaceAll("'", "\"");
        }
    }

    public String getJavaType() {
        return javaClass.getSimpleName();//return javaClass.getCanonicalName();
    }

    public String getCamelCaseName(boolean firstLetterUpperCase) {
        StringBuffer camelCaseName = new StringBuffer(columnName);
        int idx = -1;
        while ((idx = camelCaseName.indexOf("_")) != -1) {
            if (idx != 0 && idx != camelCaseName.length()) {
                camelCaseName.setCharAt(idx+1, Character.toUpperCase(camelCaseName.charAt(idx+1)));
                camelCaseName.deleteCharAt(idx);
            }
        }
        if (firstLetterUpperCase) {
            camelCaseName.setCharAt(0, Character.toUpperCase(camelCaseName.charAt(0)));
        }
        return camelCaseName.toString();
    }

    public String getRsIdentifier() {
        String type = SQLTypeMap.getResultSetIdentifier(javaSqlType);
        String conversion = "";
        String ending = "";
        if (type.equals("Array")) {
            conversion = String.format("(%s)", this.getJavaType());
            ending = ".getArray()";
        }
        return String.format("%srs.get%s(\"%s\")%s", conversion, type, getColumnName(), ending);
    }

    @Override
    public String toString() {
        return "Column{" +
                "owningTable=" + owningTable.getTableName() +
                ", columnName='" + columnName + '\'' +
                ", columnSqlType='" + columnSqlType + '\'' +
                ", javaSqlType=" + javaSqlType +
                ", javaClass=" + javaClass +
                ", size=" + size +
                ", precision=" + precision +
                ", nullable=" + nullable +
                ", defaultValue='" + defaultValue + '\'' +
                ", autoIncrementing=" + autoIncrementing +
                ", generated=" + generated +
                ", refSchema='" + refSchema + '\'' +
                ", refCatalog='" + refCatalog + '\'' +
                ", refTableName='" + refTableName + '\'' +
                ", refJavaType=" + refJavaType +
                ", fieldType=" + fieldType +
                '}';
    }

    public Table getOwningTable() {
        return owningTable;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnSqlType() {
        return columnSqlType;
    }

    public Integer getJavaSqlType() {
        return javaSqlType;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getPrecision() {
        return precision;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Boolean getAutoIncrementing() {
        return autoIncrementing;
    }

    public Boolean getGenerated() {
        return generated;
    }

    public String getRefSchema() {
        return refSchema;
    }

    public String getRefCatalog() {
        return refCatalog;
    }

    public String getRefTableName() {
        return refTableName;
    }

    public Integer getRefJavaType() {
        return refJavaType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void addReferencedBy(Column fkColumn) {
        referencedByColumns.add(fkColumn);

    }

    public void setReferencedColumn(Column pkColumn) {
        referencedColumn = pkColumn;
    }

    public Column getReferencedColumn() {
        return referencedColumn;
    }

    public boolean isReference() {
        return referencedColumn != null;
    }

    public boolean isReferenced() {
        return referencedByColumns.size() != 0;
    }

    public Set<Column> getReferencingColumns() {
        return referencedByColumns;
    }

    public Boolean isUnique() {
        return isUnique;
    }

    public void setUnique(Boolean unique) {
        isUnique = unique;
    }

    public void setIsPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
        if (javaClass.equals(Integer.class)) {
            defaultValue = "-1";
        }
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public String getTableName() {
        return getOwningTable().getTableName();
    }

    public String getCanonicalName() {
        String tableName = getTableName();
        if (tableName != null) {
            return String.format("%s.%s", tableName, getColumnName());
        }
        return getTableName();
    }

    public String getName() {
        return getCamelCaseName(false);
    }

    public String getTableType() {
        return getOwningTable().getCamelCaseName(true);
    }

    public String getRefName() {
        return getOwningTable().getCamelCaseName(false);
    }


}
