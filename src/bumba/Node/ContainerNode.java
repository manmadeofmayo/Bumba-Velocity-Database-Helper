package bumba.Node;

import bumba.Node.iface.IContainerNode;
import bumba.Node.iface.INode;

/**
 * Created by emcee on 9/6/13.
 */
public class ContainerNode extends Node implements IContainerNode {
    ContainerNode[] containerNodes;

    public ContainerNode(Type type, String name, String owningPath, ContainerNode[] containerNodes) {
        super(type, name, owningPath);
        this.containerNodes = containerNodes;
    }

    @Override
    public IContainerNode[] getChildren() {
        return new IContainerNode[0];
    }

    @Override
    public INode navigate(String navigationPath) {
        return null;
    }
}
