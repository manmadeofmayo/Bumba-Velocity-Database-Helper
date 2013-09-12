package bumba;

import bumba.generated.Bundles;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by emcee on 9/8/13.
 */
public class PlayBox {

    public static void main(String[] args) {
/*        try {
            Database db = new Database();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
        Bundles b = new Bundles("4000A");
        Field[] fields = ((Object)b).getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName());
        }
    }

  /*  public static List<Field> getContainerFields(String canonicalPrefix, Object object) {
        List<Field> matchingFields = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.)
        }


    }*/

}
