package bumba.database.core;
import java.sql.*;
import java.util.List;


public class DataConnector {
    public static Connection getTestDatabaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "postgres", "password");
        return connection;
    }

    public static ResultSet getEasyResultSet(PreparedStatement preparedStatement, List vars) throws SQLException {

        for (int idx = 0; idx < vars.size(); idx++) {
            Object var = vars.get(idx);
            if ( var instanceof Object[]) {
                /* doesn't work, need access to Connection
                if (var instanceof Integer[][]) {
                    Array arr = c.createArrayOf("integer", (Integer[][])var);
                    preparedStatement.setArray(idx + 1, arr);
                } else if (var instanceof String[]) {
                    // why is varchar the only one that is case-sensitive?  AND it's lower-case when convention is upper?
                    Array arr = c.createArrayOf("varchar", (String[])var);
                    preparedStatement.setArray(idx + 1, arr);
                }*/
                throw new RuntimeException("Arrays not supported");

            }  else if (var instanceof Integer) {
                preparedStatement.setInt(idx + 1, (Integer)var);
            } else {
                preparedStatement.setObject(idx + 1, vars.get(idx));
            }
        }
        return preparedStatement.executeQuery();
    }

    protected DataConnector() {

    }
}