package bumba;

import java.util.List;

/**
 * Created by emcee on 7/24/13.
 * Interface for all data-backed classes providing essential methods
 */
public interface DataObject {
    public boolean validate();

    public boolean save();

    public boolean load(String sql, List columns);

    public List packageVars(boolean addId);

    public boolean isModified();

 /*   public Integer getId();*/

    void delete();
}
