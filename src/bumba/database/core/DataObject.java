package bumba.database.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DataObject {
    public boolean validate();

    public void save(Connection c) throws SQLException, Exception;

    public void load(Connection c, String sql, List columns) throws Exception;

    public List packageVars(boolean addId);

    public boolean isModified();

    public Integer getId();

    public void delete(Connection c) throws Exception;
}
