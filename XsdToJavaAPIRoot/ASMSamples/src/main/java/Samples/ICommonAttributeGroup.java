package Samples;

public interface ICommonAttributeGroup <T extends IElement<T>> extends IElement<T> {

    default T addAttrClass(String var1) {
        ((AbstractElement<T>)this).addAttr(new SomeAttribute(var1));
        return this.self();
    }

}
