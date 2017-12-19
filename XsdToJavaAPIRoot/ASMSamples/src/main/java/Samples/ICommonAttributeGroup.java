package Samples;

public interface ICommonAttributeGroup <T extends IElement<T>> extends IElement<T> {

    default T addAttrClass(SomeAttribute var1) {
        ((AbstractElement<T>)this).addAttr(var1);
        return this.self();
    }

}
