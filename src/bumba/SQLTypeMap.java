package bumba;

import java.sql.Types;

public class SQLTypeMap {

    /**
     * Translates a data type from an integer (java.sql.Types value) to a string
     * that represents the corresponding class.
     *
     * @param sqlType The java.sql.Types value to convert to a string representation.
     * @return The class name that corresponds to the given java.sql.Types value,
     *         or "java.lang.Object" if the type has no known mapping.
     */
    public static String convert( int sqlType, String columnType) throws ClassNotFoundException {
        String result = null;

        switch( sqlType ) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = "java.lang.String";
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                result = "java.math.BigDecimal";
                break;

            case Types.BIT:
                result = "java.lang.Boolean";
                break;

            case Types.TINYINT:
                result = "java.lang.Byte";
                break;

            case Types.SMALLINT:
                result = "java.lang.Short";
                break;

            case Types.INTEGER:
                result = "java.lang.Integer";
                break;

            case Types.BIGINT:
                result = "java.lang.Long";
                break;

            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
                result = "java.lang.Double";
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = "[Ljava.lang.Byte;";
                break;

            case Types.DATE:
                result = "java.sql.Date";
                break;

            case Types.TIME:
                result = "java.sql.Time";
                break;
            case Types.DISTINCT:
                result = "java.lang.Object";
            case Types.TIMESTAMP:
                result = "java.sql.Timestamp";
                break;
            case Types.ARRAY:
                if (columnType.contains("varchar") || columnType.contains("text")) {
                    result = "[Ljava.lang.String;";
                } else if (columnType.contains("int")) {
                    result = "[[Ljava.lang.Integer;";
                }
        }

        if (result == null) {
            System.err.println("Could not find value for sqlType " + sqlType + " and column type " + columnType);
            result = "java.lang.Object";
            //throw new RuntimeException("Could not find value for sqlType " + sqlType + " and column type " + columnType);
        }

        return result;
    }



    public static String getResultSetIdentifier( int type) {
        String result = "Object";

        switch( type ) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = "String";
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                result = "BigDecimal";
                break;

            case Types.BIT:
                result = "Boolean";
                break;

            case Types.TINYINT:
                result = "Byte";
                break;

            case Types.SMALLINT:
                result = "Short";
                break;

            case Types.INTEGER:
                result = "Int";
                break;

            case Types.BIGINT:
                result = "Long";
                break;

            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
                result = "Double";
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = "Bytes";
                break;

            case Types.DATE:
                result = "Date";
                break;

            case Types.TIME:
                result = "Time";
                break;

            case Types.TIMESTAMP:
                result = "Timestamp";
                break;
            case Types.ARRAY:
                result = "Array";
                break;
        }

        return result;

    }
}