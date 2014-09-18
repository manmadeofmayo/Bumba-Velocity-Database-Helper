package bumba.database.core;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DataConnector {
    public static Connection getTestDatabaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "postgres", "password");
        return connection;
    }

    protected DataConnector() {

    }
}