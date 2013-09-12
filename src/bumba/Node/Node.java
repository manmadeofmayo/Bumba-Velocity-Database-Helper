package bumba.Node;

import bumba.Node.iface.INode;

/**
 * Created by emcee on 9/6/13.
 */
public class Node implements INode {
    Type type;
    String name;
    String owningPath;

    protected Node(Type type, String name, String owningPath) {
        this.type = type;
        this.name = name;
        this.owningPath = owningPath;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOwningPath() {
        return null;
    }
}
