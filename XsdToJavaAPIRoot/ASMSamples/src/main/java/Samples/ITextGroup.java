package Samples;

public interface ITextGroup<T extends IElement<T>> extends IElement<T>{

    default T addAttrText(AttrText var1) {
        ((AbstractElement<T>)this).addAttr(var1);
        return this.self();
    }
}
