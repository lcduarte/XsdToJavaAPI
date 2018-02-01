package Samples.HTML;

public interface ICommonAttributeGroup <T extends IElement> extends IElement<T>, IFlowContent<T> {

    default T addAttrClass(String var1) {
        ((AbstractElement)this).addAttr(new SomeAttribute(null));
        return this.self();
    }

}
