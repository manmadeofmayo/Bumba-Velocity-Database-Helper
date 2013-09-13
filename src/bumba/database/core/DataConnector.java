package bumba.database.core;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class DataConnector {
// -------------------------- STATIC METHODS --------------------------

    public static String fixPrice(String price) {
        String out = price;
        if (out.charAt(out.length() - 2) == '.')
            out = out + "0";
        return out;
    }

    public static String fixPrice(float price) {
        return fixPrice("" + (Math.round(price * 100f) / 100f));
    }

    public static String fixPrice(double price) {
        return fixPrice("" + (Math.round(price * 100f) / 100f));
    }

    public static Connection getTestDatabaseConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/orders_test", "postgres", "password");
        return connection;
    }

    public static Connection getEBPConnection(boolean useTestDatabase)
            throws Exception {
        if (useTestDatabase) {
            return getTestDatabaseConnection();
        }
        javax.sql.DataSource db_datasource = null;
        try {
            javax.naming.Context context = new javax.naming.InitialContext();
            db_datasource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/ebp");
            if (context != null)
                try {
                    context.close();
                } catch (Exception e) {
                }
            context = null;
        } catch (Exception e) {
            throw new Exception("Failed to acquire EBP db datasource : jdbc/ebp", e);
        }

        return db_datasource.getConnection();
    }

    public static Connection getLeadgenConnection(boolean useTestDatabase)
            throws Exception {
        if (useTestDatabase) {
            return getTestDatabaseConnection();
        }
        javax.sql.DataSource db_datasource = null;
        try {
            javax.naming.Context context = new javax.naming.InitialContext();
            db_datasource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/leadgen");
            if (context != null)
                try {
                    context.close();
                } catch (Exception e) {
                }
            context = null;
        } catch (Exception e) {
            throw new Exception("Failed to acquire Leadgen db datasource : jdbc/leadgen", e);
        }

        return db_datasource.getConnection();
    }

    public static Connection getOrdersConnection(boolean useTestDatabase) throws NamingException, SQLException, ClassNotFoundException {
        if (useTestDatabase) {
            return getTestDatabaseConnection();
        }
        javax.sql.DataSource db_datasource = null;
        javax.naming.Context context = new javax.naming.InitialContext();
        db_datasource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/orders");
        if (context != null)
                context.close();
        context = null;

        return db_datasource.getConnection();
    }

    public static Connection getCpaexcelConnection(boolean useTestDatabase)
            throws Exception {
        if (useTestDatabase) {
            return getTestDatabaseConnection();
        }
        javax.sql.DataSource db_datasource = null;
        try {
            javax.naming.Context context = new javax.naming.InitialContext();
            db_datasource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/pv4_cpaexcel");
            if (context != null)
                try {
                    context.close();
                } catch (Exception e) {
                }
            context = null;
        } catch (Exception e) {
            throw new Exception("Failed to acquire Orders db datasource : jdbc/pv4_cpaexcel", e);
        }

        return db_datasource.getConnection();
    }

    public static Connection getAccessControlConnection(boolean useTestDatabase)
            throws Exception {
        if (useTestDatabase) {
            return getTestDatabaseConnection();
        }
        javax.sql.DataSource db_datasource = null;
        try {
            javax.naming.Context context = new javax.naming.InitialContext();
            db_datasource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/access_control");
            if (context != null)
                try {
                    context.close();
                } catch (Exception e) {
                }
            context = null;
        } catch (Exception e) {
            throw new Exception("Failed to acquire EBP db datasource : jdbc/access_control", e);
        }

        return db_datasource.getConnection();
    }

    public static Connection getStrayerConnection(boolean useTestDatabase)
            throws Exception {
        javax.sql.DataSource db_datasource = null;
        try {
            javax.naming.Context context = new javax.naming.InitialContext();
            db_datasource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/strayer");
            if (context != null)
                try {
                    context.close();
                } catch (Exception e) {
                }
            context = null;
        } catch (Exception e) {
            throw new Exception("Failed to acquire Orders db datasource : jdbc/strayer", e);
        }

        return db_datasource.getConnection();
    }

    public static String escapeQuotes(String text) {
        if (text == null)
            return "";
        text.replaceAll("\\\\", "\\\\\\\\");
        return text.replaceAll("'", "''");
    }

    public static final java.text.DateFormat INPUT_DATE_FORMATTER = new java.text.SimpleDateFormat("yyyy-MM-dd");
    public static final java.text.DateFormat DATE_FORMATTER       = new java.text.SimpleDateFormat("yyyy-MM-dd (E)");

    public static String formatDate(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static Date getDateFromInput(String formattedDate) {
        try {
            return INPUT_DATE_FORMATTER.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    protected DataConnector() {

    }
}