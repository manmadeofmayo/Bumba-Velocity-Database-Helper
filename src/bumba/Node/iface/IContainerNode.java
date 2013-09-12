package bumba.Node.iface;

/**
 * Created by emcee on 9/6/13.
 */
public interface IContainerNode extends INode {
    public IContainerNode[] getChildren();
    public INode navigate(String navigationPath);
}
