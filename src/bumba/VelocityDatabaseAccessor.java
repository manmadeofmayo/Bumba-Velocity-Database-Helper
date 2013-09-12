package bumba;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;

/**
 * Created by emcee on 8/19/13.
 */
public class VelocityDatabaseAccessor {
    protected String tableName;
    protected List<Column> columns;
    DatabaseMetaData metaData;

    public VelocityDatabaseAccessor(String tableName, ResultSet metaData) {
        this.tableName = tableName;
        metaData = metaData;
    }

    public String getJavaTableName() {
        return tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }
}
