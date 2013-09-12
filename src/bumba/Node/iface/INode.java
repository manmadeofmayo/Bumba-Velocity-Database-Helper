package bumba.Node.iface;

import bumba.Node.Type;

/**
 * Created by emcee on 9/6/13.
 */
public interface INode {
    public Type getType();
    public String getName();
    public String getOwningPath();
}
