package bumba.database.core;

import java.sql.*;
import java.util.List;

/**
 * Created by emcee on 6/28/13.
 */
// TODO: add pooling; use datasource lookups with self-instancing connections as a fallback
// TODO: See http://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html
public class EasyConnector {
    public static final boolean USE_TEST_DATABASE = true;
    Connection connection;
    Statement statement;
    ResultSet rs;

    public EasyConnector(ConnectionType connectionType) {
        setConnection(connectionType, false);
    }

    private void setConnection(ConnectionType connectionType, boolean autoCommit) {
        if (USE_TEST_DATABASE) {
            System.err.println("OPENING DATABASE");
            System.err.println("WARNING: USING TEST AND NOT PRODUCTION DATABASE");
        }
        try {
            switch (connectionType) {
                case ORDERS:
                    connection = DataConnector.getOrdersConnection(USE_TEST_DATABASE);
                    break;
                case LEADS:
                    connection = DataConnector.getLeadgenConnection(USE_TEST_DATABASE);
                    break;
                case ACCESS:
                    connection = DataConnector.getAccessControlConnection(USE_TEST_DATABASE);
                    break;
                case EBP:
                    connection = DataConnector.getEBPConnection(USE_TEST_DATABASE);
                    break;
            }
            connection.setAutoCommit(autoCommit);
        } catch (Exception e) {
            handleError(e);
        }
    }

    public EasyConnector(ConnectionType connectionType, boolean autoCommit) {
        setConnection(connectionType, autoCommit);
    }

    public int update(String sql, List vars) {
        PreparedStatement preparedStatement = prepareStatement(sql, vars);
        if (preparedStatement != null) {
            try {
                return preparedStatement.executeUpdate();
            } catch (Exception e) {
                handleError(e);
            }
        }
        return -1;
    }

    private void handleError(Exception e, boolean closewhendone) {
        // TODO: Actual error handling
        e.printStackTrace();
        System.err.println("Exception: " + e.getMessage());
        // Close everything up
        if (closewhendone) {
            close();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void handleError(Exception e) {
        handleError(e, true);
    }

    public PreparedStatement prepareStatement(String sql, List vars) {
        return prepareStatement(sql, vars, false);
    }

    public PreparedStatement prepareStatement(String sql, List vars, boolean returnKeys) {
        closeResultSet();
        closeStatement();
        PreparedStatement preparedStatement = null;
        try {
            if (returnKeys) {
                preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement(sql);
            }

            statement = preparedStatement;

            for (int idx = 0; idx < vars.size(); idx++) {
                Object var = vars.get(idx);
                if ( var instanceof Object[]) {
                    if (var instanceof Integer[][]) {
                        Array arr = connection.createArrayOf("integer", (Integer[][])var);
                        preparedStatement.setArray(idx + 1, arr);
                    } else if (var instanceof String[]) {
                        // why is varchar the only one that is case-sensitive?  AND it's lower-case when convention is upper?
                        Array arr = connection.createArrayOf("varchar", (String[])var);
                        preparedStatement.setArray(idx + 1, arr);
                    }

                }  else if (var instanceof Integer) {
                    preparedStatement.setInt(idx + 1, (Integer)var);
                } else {
                    preparedStatement.setObject(idx + 1, vars.get(idx));
                }
            }
            System.err.println(sql + "\n" + statement.toString());
            return preparedStatement;
        } catch (Exception e) {
            handleError(e);
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    protected void closeStatement() {
        if (statement != null) {
            try {
                statement.close();
                statement = null;
            } catch (Exception e) {
                handleError(e);
            }
        }
    }

    protected void closeResultSet() {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (Exception e) {
                handleError(e);
                rs = null;
            }
        }
    }

    public int executeAndReturnId(String sql, List vars) {
        PreparedStatement preparedStatement = prepareStatement(sql, vars, true);
            try {
                //preparedStatement.setQueryTimeout(5);
                preparedStatement.execute();
                rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    System.err.println(rs.getInt(1));
                    return rs.getInt(1);
                } else {
                    throw new RuntimeException("Expected return keys and instead got nothing.");
                }
            } catch (Exception e) {
                handleError(e);
            }
        return -1;
    }

    public Object executeAndReturnDefault(String sql, List vars) {
        PreparedStatement preparedStatement = prepareStatement(sql, vars, true);
        try {
            //preparedStatement.setQueryTimeout(5);
            preparedStatement.execute();
            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                System.err.println(rs.getObject(1));
                return rs.getObject(1);
            } else {
                throw new RuntimeException("Expected return keys and instead got nothing.");
            }
        } catch (Exception e) {
            handleError(e);
        }
        return null;
    }

    public boolean execute(String sql, List vars) {
        PreparedStatement preparedStatement = prepareStatement(sql, vars);
        if (preparedStatement != null) {
            try {
                return preparedStatement.execute();
            } catch (Exception e) {
                handleError(e);
            }
        }
        return false;
    }

    public boolean execute(String sql) {
        createStatement();
        try {
            System.err.println(sql);
            return statement.execute(sql);
        } catch (Exception e) {
            handleError(e);
        }
        return false;
    }

    public int update(String sql) {
        try {
            System.err.println(sql);
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            handleError(e);
        }
        return 0;
    }

    private void createStatement() {
        closeResultSet();
        closeStatement();
        try {
            statement = connection.createStatement();
        } catch (Exception e) {
            handleError(e);
        }
    }

    public ResultSet getQuery(String query) {
        try {
            System.err.println(query);
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            handleError(e);
        }
        return rs;
    }

    public ResultSet getQuery(String query, List columns) {
        try {
            System.err.println(query);
            rs = prepareStatement(query, columns).executeQuery();
        } catch (Exception e) {
            handleError(e);
        }
        return rs;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public void close() {
        System.err.println("CLOSING DATABASE");
        try {
            if (connection != null) {
                connection.commit();
            }
        } catch (Exception e) {
            handleError(e, false);
        }
        closeResultSet();
        closeStatement();
        closeConnection();
    }

    protected void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (Exception e) {
                handleError(e, false);
            }
        }
    }

    public enum ConnectionType {
        ORDERS,
        ORDERS_LEGACY,
        LEADS,
        LEADS_LEGACY,
        ACCESS,
        ACCESS_LEGACY,
        EBP,
        EBP_LEGACY
    }
}
