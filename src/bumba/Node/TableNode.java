/*
package bumba.Node;

import bumba.Column;
import bumba.EasyConnector;
import bumba.Node.iface.ITableNode;
import bumba.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

*/
/**
 * Created by emcee on 9/6/13.
 */

/*
public class TableNode extends ContainerNode implements ITableNode {
    protected TableNode(Type type, String name, String owningPath, ContainerNode[] containerNodes) {
        super(type, name, owningPath, containerNodes);
    }

    public static TableNode getInstance(String schema, String tableName, Object pk_id) throws Exception {
        EasyConnector ec = new EasyConnector(EasyConnector.ConnectionType.ORDERS);
        Connection c = ec.getConnection();
        String[] types = {"TABLE"};
        ResultSet rs = c.getMetaData().getTables(null, schema, tableName, types);
        Table t = null;
        if (rs.next()) {
            t = new Table(rs, null);
        } else {
            throw new Exception(String.format("Schema.Table '%s'.'%s' not found", schema, tableName));
        }
        Column primaryKey = t.getPrimaryKey();
        if (primaryKey == null) {
            throw new Exception(String.format("primary key not found for %s.%s", schema, tableName));
        }
        rs = ec.getQuery(String.format("SELECT * FROM %s.%s WHERE %s =?", schema, tableName, primaryKey.getColumnName()), Arrays.asList((primaryKey.getJavaClass())pk_id));
        while (rs.next()) {

        }
    }
}
*/
