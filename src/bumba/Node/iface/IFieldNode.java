package bumba.Node.iface;

import bumba.Node.ValidationError;

/**
 * Created by emcee on 9/6/13.
 */
public interface IFieldNode extends INode {
    public Object getValue();
    public Class<?> getValueType();
    public ValidationError getValidationError();
}
