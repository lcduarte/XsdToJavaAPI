package Samples.HTML;

public interface ICommonAttributeGroup <T extends IElement<T, P>, P extends IElement> extends IElement<T, P>, IFlowContent<T, P> {

    default T addAttrClass(String var1) {
        return addAttr(new SomeAttribute(null));
    }

}
