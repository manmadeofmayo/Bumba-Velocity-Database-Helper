package bumba;

import bumba.generated.Bundles;
import bumba.generated.Orders;

/**
 * Created by emcee on 9/6/13.
 */
public class GeneratedSandBox {
    public static void main(String[] args) {
        Orders o = new Orders(106227);
        //Bundles b = new Bundles("4000A");
        //b.setName(b.getName() + "1");
        //b.save();
    }

    public static Bundles getBundle(String sku) {
        return new Bundles(sku);
    }
}
