package Samples;

public interface ICommonAttributeGroup <T extends IElement, M> extends IElement<T, M>, IFlowContent<T, M> {

    default T addAttrClass(String var1) {
        ((AbstractElement<T, M>)this).addAttr(new SomeAttribute(null));
        return this.self();
    }

}
