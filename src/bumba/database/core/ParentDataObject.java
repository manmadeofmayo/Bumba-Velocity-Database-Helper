package bumba.database.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emcee on 9/10/13.
 */
public abstract class ParentDataObject implements DataObject {
    public List<DataObject> getMissingOldObjects(List<DataObject> newItems, List<DataObject> oldItems) {
        List<DataObject> returnList = new ArrayList<>();
        for (DataObject oldItem : oldItems) {
            boolean foundMatch = false;
            for (DataObject newItem : newItems) {
                if (newItem.getId().equals(oldItem.getId())) {
                    foundMatch = true;
                }
            }
            if (foundMatch) {
                returnList.add(oldItem);
            }
        }
        return returnList;
    }
}
